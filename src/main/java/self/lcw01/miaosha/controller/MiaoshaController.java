package self.lcw01.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import self.lcw01.miaosha.dto.GoodsDto;
import self.lcw01.miaosha.entity.MiaoshaOrder;
import self.lcw01.miaosha.entity.OrderInfo;
import self.lcw01.miaosha.entity.User;
import self.lcw01.miaosha.result.CodeMsg;
import self.lcw01.miaosha.service.GoodsService;
import self.lcw01.miaosha.service.MiaoshaService;
import self.lcw01.miaosha.service.OrderService;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RequestMapping("/do_miaosha")
    public String miaosha(Model model, User user, @RequestParam("goodsId") long goodsId){
        model.addAttribute("user",user);
        if (user == null){
            return "login";
        }
        //判断库存
        GoodsDto goodsDto = goodsService.getGoodsDtoByGoodsId(goodsId);
        int stock = goodsDto.getStockCount();
        if (stock <= 0){
            model.addAttribute("errmsg", CodeMsg.MIAOSHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //该用户是否已经秒杀成功过
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(),goodsId);
        if (miaoshaOrder != null){
            model.addAttribute("errmsg",CodeMsg.MIAOSHA_REPEATE.getMsg());
            return "miaosha_fail";
        }
        //一个事务里边完成：减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user,goodsDto);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goodsDto);
        return "order_detail";
    }
}
