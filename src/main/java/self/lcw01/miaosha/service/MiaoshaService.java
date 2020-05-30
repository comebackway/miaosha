package self.lcw01.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import self.lcw01.miaosha.dto.GoodsDto;
import self.lcw01.miaosha.entity.OrderInfo;
import self.lcw01.miaosha.entity.User;

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

    @Transactional
    public OrderInfo miaosha(User user, GoodsDto goodsDto){
        //减库存
        goodsService.reduceStock(goodsDto);
        //下订单  and  写秒杀订单
        OrderInfo orderInfo = orderService.createOrder(user,goodsDto);
        return orderInfo;
    }
}
