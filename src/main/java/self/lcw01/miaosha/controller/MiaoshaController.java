package self.lcw01.miaosha.controller;

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
import self.lcw01.miaosha.result.CodeMsg;
import self.lcw01.miaosha.result.Result;
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

    //限定只能是post方式提交
    /*
    GET和POST区别：
    GET是幂等的，也就是不会对服务端数据有影响 比如只是查询
    POST是对服务端数据有影响的 比如新增修改删除
     */
    @RequestMapping(value = "/do_miaosha",method = RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> miaosha(Model model, User user, @RequestParam("goodsId") long goodsId){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
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
    }
}
