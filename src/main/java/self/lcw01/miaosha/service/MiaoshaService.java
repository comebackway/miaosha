package self.lcw01.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import self.lcw01.miaosha.dto.GoodsDto;
import self.lcw01.miaosha.entity.MiaoshaOrder;
import self.lcw01.miaosha.entity.OrderInfo;
import self.lcw01.miaosha.entity.User;
import self.lcw01.miaosha.redis.GoodsKey;
import self.lcw01.miaosha.redis.MiaoshaKey;
import self.lcw01.miaosha.redis.RedisService;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class MiaoshaService {
    /*
    不提倡在自己的service里边引用其他类的dao，如果想用其他类的dao则引入他们对应的service再调用其dao
    @Autowired
    GoodsDao goodsDao
     */

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo miaosha(User user, GoodsDto goodsDto){
        if (user == null){
            return null;
        }
        //减库存
        boolean success = goodsService.reduceStock(goodsDto);
        if (success){
            //下订单  and  写秒杀订单
            OrderInfo orderInfo = orderService.createOrder(user,goodsDto);
            return orderInfo;
        }else{
            /*
            减库存失败，商品已无库存。设定该商品已无库存标记，以便下边轮询结果时区分等待中还是秒杀失败状态
            因为这是数据库里边的库存 是最准确的 一旦没有了库存可以直接设置库存不足状态，在队列里边的订单也不可能秒杀成功
            所以下边在轮询状态时，一旦遇到这个库存不足的状态可以直接返回秒杀失败
             */
            setGoodsOver(goodsDto.getId());
            return null;
        }
    }


    /**
     * 查询秒杀结果
     * @param userId
     * @param goodsId
     * @return
     */
    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(userId,goodsId);
        if (miaoshaOrder != null){
            return miaoshaOrder.getOrderId();
        }else{
            boolean isOver = getGoodsOver(goodsId);
            if (isOver){
                return -1;
            }else{
                return 0;
            }
        }
    }

    private void setGoodsOver(long goodsId) {
        redisService.set(GoodsKey.getGoodsIsOver,""+goodsId,true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(GoodsKey.getGoodsIsOver,""+goodsId);
    }

    public boolean checkPath(User user, long goodsId, String path) {
        if (user == null || path == null){
            return false;
        }
        String path_r = redisService.get(MiaoshaKey.getMiaoshaPath,""+user.getId()+"_"+goodsId,String.class);
        return path.equals(path_r);
    }


    /**
     * 生成bufferedimage图片（为了生成验证码）
     * @param user
     * @param goodsId
     * @return
     */
    public BufferedImage createVerifyCode(User user,long goodsId){
        int width = 80;
        int height = 32;
        //在内存中生成图片
        BufferedImage bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        //拿到这个对象后就可以在图片上画东西了
        Graphics graphics = bufferedImage.getGraphics();
        //设置背景颜色
        graphics.setColor(new Color(0xDCDCDC));
        //填充背景颜色
        graphics.fillRect(0,0,width,height);
        //设置画笔颜色
        graphics.setColor(Color.BLACK);
        //画个黑色的边框
        graphics.drawRect(0,0,width-1,height-1);

        //随机生成五十个干扰的点
        Random random = new Random();
        for (int i=0;i<50;i++){
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            graphics.drawOval(x,y,0,0);
        }

        //画验证码最后关闭画笔
        //这里的传参是借用了上边的random，这样可以减少内存开支消耗
        String verifyCode = createCode(random);
        graphics.setColor(new Color(0,100,0));
        graphics.setFont(new Font("Candara",Font.BOLD,24));
        //画字符串内容
        graphics.drawString(verifyCode,8,24);
        graphics.dispose();

        //把验证码存到redis  cacl:计算verifyCode公式的值
        int rnd = cacl(verifyCode);
        //传入goodsId是为了适应 一个客户打开了多个不同商品页面的情况，所以要以goodsId作为key条件
        redisService.set(MiaoshaKey.verifyCode,""+user.getId()+"_"+goodsId,rnd);
        return bufferedImage;
    }


    private String createCode(Random random) {
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);
        char[] ops = new char[] {'+','-','*'};
        char op1 = ops[random.nextInt(3)];
        char op2 = ops[random.nextInt(3)];
        String exp = "" + num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    /**
     * 使用了scriptEngine引擎动态执行脚本语言，下边使用了常用的javascript引擎 动态执行javas
     * @param verifyCode
     * @return
     */
    private int cacl(String verifyCode) {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine engine = engineManager.getEngineByName("JavaScript");
        try {
            return (Integer)engine.eval(verifyCode);
        } catch (ScriptException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean checkVerifyCode(User user, long goodsId, int verifyCode) {
        Integer codeRight = redisService.get(MiaoshaKey.verifyCode,""+user.getId()+"_"+goodsId,Integer.class);
        if (codeRight == null || codeRight-verifyCode != 0){
            return false;
        }
        //校验正确后要删掉验证码
        redisService.del(MiaoshaKey.verifyCode,""+user.getId()+"_"+goodsId);
        return true;
    }
}
