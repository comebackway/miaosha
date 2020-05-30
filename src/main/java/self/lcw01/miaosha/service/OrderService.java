package self.lcw01.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import self.lcw01.miaosha.dao.OrderDao;
import self.lcw01.miaosha.dto.GoodsDto;
import self.lcw01.miaosha.entity.MiaoshaOrder;
import self.lcw01.miaosha.entity.OrderInfo;
import self.lcw01.miaosha.entity.User;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    public MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(long userId,long goodsId){
        return orderDao.getMiaoshaOrderByUserIdAndGoodsId(userId,goodsId);
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

        long orderId = orderDao.insertOrderInfo(orderInfo);

        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goodsDto.getId());
        miaoshaOrder.setOrderId(orderId);
        miaoshaOrder.setUserId(user.getId());

        orderDao.insertMiaoShaOrder(miaoshaOrder);

        return orderInfo;
    }
}
