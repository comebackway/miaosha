package self.lcw01.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import self.lcw01.miaosha.dao.OrderDao;
import self.lcw01.miaosha.dto.GoodsDto;
import self.lcw01.miaosha.entity.MiaoshaOrder;
import self.lcw01.miaosha.entity.OrderInfo;
import self.lcw01.miaosha.entity.User;
import self.lcw01.miaosha.redis.OrderKey;
import self.lcw01.miaosha.redis.RedisService;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    public MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(long userId,long goodsId){
        //return orderDao.getMiaoshaOrderByUserIdAndGoodsId(userId,goodsId);
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid,""+userId+"_"+goodsId,MiaoshaOrder.class);
    }

    @Transactional
    public OrderInfo createOrder(User user, GoodsDto goodsDto) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsDto.getId());
        orderInfo.setGoodsName(goodsDto.getGoodsName());
        orderInfo.setGoodsPrice(goodsDto.getMiaoshaPrice());
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());

        orderDao.insertOrderInfo(orderInfo);

        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goodsDto.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());

        orderDao.insertMiaoShaOrder(miaoshaOrder);
        redisService.set(OrderKey.getMiaoshaOrderByUidGid,""+user.getId()+"_"+goodsDto.getId(),miaoshaOrder);
        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }
}
