package self.lcw01.miaosha.dto;

import self.lcw01.miaosha.entity.OrderInfo;

public class OrderDetailDto {
    private GoodsDto goodsDto;
    private OrderInfo orderInfo;

    public GoodsDto getGoodsDto() {
        return goodsDto;
    }

    public void setGoodsDto(GoodsDto goodsDto) {
        this.goodsDto = goodsDto;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }
}
