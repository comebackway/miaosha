package self.lcw01.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import self.lcw01.miaosha.dto.GoodsDto;
import self.lcw01.miaosha.dto.OrderDetailDto;
import self.lcw01.miaosha.entity.Goods;
import self.lcw01.miaosha.entity.OrderInfo;
import self.lcw01.miaosha.entity.User;
import self.lcw01.miaosha.result.CodeMsg;
import self.lcw01.miaosha.result.Result;
import self.lcw01.miaosha.service.GoodsService;
import self.lcw01.miaosha.service.OrderService;

@Controller
@RequestMapping(value = "/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailDto> info(User user, @RequestParam("orderId") long orderId){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo orderInfo = orderService.getOrderById(orderId);
        if (orderInfo == null){
            return Result.error(CodeMsg.ORDER_NULL);
        }
        long goodsId = orderInfo.getGoodsId();
        GoodsDto goods = goodsService.getGoodsDtoByGoodsId(goodsId);
        OrderDetailDto orderDetailDto = new OrderDetailDto();
        orderDetailDto.setGoodsDto(goods);
        orderDetailDto.setOrderInfo(orderInfo);
        return Result.success(orderDetailDto);
    }
}
