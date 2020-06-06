package self.lcw01.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import self.lcw01.miaosha.dto.GoodsDto;
import self.lcw01.miaosha.entity.MiaoshaOrder;
import self.lcw01.miaosha.entity.OrderInfo;
import self.lcw01.miaosha.entity.User;
import self.lcw01.miaosha.redis.GoodsKey;
import self.lcw01.miaosha.redis.RedisService;

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
}
