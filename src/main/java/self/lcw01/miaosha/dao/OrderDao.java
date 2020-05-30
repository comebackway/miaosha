package self.lcw01.miaosha.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import self.lcw01.miaosha.entity.MiaoshaOrder;
import self.lcw01.miaosha.entity.OrderInfo;

@Mapper
public interface OrderDao {
    public MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(@Param("userId") long userId,@Param("goodsId") long goodsId);

    public long insertOrderInfo(OrderInfo orderInfo);

    public int insertMiaoShaOrder(MiaoshaOrder miaoshaOrder);
}
