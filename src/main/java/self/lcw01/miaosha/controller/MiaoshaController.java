package self.lcw01.miaosha.controller;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import self.lcw01.miaosha.dto.GoodsDto;
import self.lcw01.miaosha.entity.MiaoshaOrder;
import self.lcw01.miaosha.entity.OrderInfo;
import self.lcw01.miaosha.entity.User;
import self.lcw01.miaosha.rabbitmq.MQMiaoshaMessage;
import self.lcw01.miaosha.rabbitmq.MQSender;
import self.lcw01.miaosha.redis.GoodsKey;
import self.lcw01.miaosha.redis.RedisService;
import self.lcw01.miaosha.result.CodeMsg;
import self.lcw01.miaosha.result.Result;
import self.lcw01.miaosha.service.GoodsService;
import self.lcw01.miaosha.service.MiaoshaService;
import self.lcw01.miaosha.service.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    private Map<Long,Boolean> localOverMap = new HashMap<Long, Boolean>();
    /*
    该方法的作用是：在系统初始化时会调用该方法
    业务场景：在系统初始化时将商品秒杀的库存放到redis中。并初始化商品库存是否已经被秒杀完标志
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsDto> goodsDtoList = goodsService.listGoodsDto();
        if (goodsDtoList == null){
            return;
        }
        for (GoodsDto goodsDto:goodsDtoList){
            redisService.set(GoodsKey.getGoodsStock,""+goodsDto.getId(),goodsDto.getStockCount());
            //设定商品是否已被秒杀完标志
            localOverMap.put(goodsDto.getId(),false);
        }
    }


    //限定只能是post方式提交
    /*
    GET和POST区别：
    GET是幂等的，也就是不会对服务端数据有影响 比如只是查询
    POST是对服务端数据有影响的 比如新增修改删除
     */
    @RequestMapping(value = "/do_miaosha",method = RequestMethod.POST)
    @ResponseBody
    //将返回类型从Result<OrderInfo> 变成 Result<Integer>  返回状态（1：表示排队中）
    public Result<Integer> miaosha(Model model, User user, @RequestParam("goodsId") long goodsId){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        /*
        V1.0 直接在数据库里边对数量进行判断

        //判断库存
        GoodsDto goodsDto = goodsService.getGoodsDtoByGoodsId(goodsId);
        int stock = goodsDto.getStockCount();
        if (stock <= 0){
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //该用户是否已经秒杀成功过
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(),goodsId);
        if (miaoshaOrder != null){
            return Result.error(CodeMsg.MIAOSHA_REPEATE);
        }
        //一个事务里边完成：减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user,goodsDto);
        return Result.success(orderInfo);

        */


        /*
        v2.0 将库存放在redis中执行，使用rabbitmq对请求进行异步处理
         */
        //在预减库存前先查询是否已秒杀完毕标志,尽可能减少性能开销
        boolean over = localOverMap.get(goodsId);
        if (over){
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //预减库存
        long stock = redisService.decr(GoodsKey.getGoodsStock,""+goodsId);
        if (stock < 0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //判断是否已秒杀
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(),goodsId);
        if (miaoshaOrder != null){
            return Result.error(CodeMsg.MIAOSHA_REPEATE);
        }
        //入队操作
        MQMiaoshaMessage mqMiaoshaMessage = new MQMiaoshaMessage();
        mqMiaoshaMessage.setGoodId(goodsId);
        mqMiaoshaMessage.setUser(user);
        mqSender.sendMiaoshaMessage(mqMiaoshaMessage);
        //返回排队中提示
        return Result.success(0);
    }


    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    /**
     * orderId  成功
     * -1      失败
     *  0      排队中
     */
    public Result<Long> miaoshaResult(Model model, User user, @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }

}
