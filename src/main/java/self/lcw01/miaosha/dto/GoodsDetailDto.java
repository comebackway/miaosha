package self.lcw01.miaosha.dto;

import self.lcw01.miaosha.entity.User;

public class GoodsDetailDto {
    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodsDto goodsDto;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getMiaoshaStatus() {
        return miaoshaStatus;
    }

    public void setMiaoshaStatus(int miaoshaStatus) {
        this.miaoshaStatus = miaoshaStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public GoodsDto getGoodsDto() {
        return goodsDto;
    }

    public void setGoodsDto(GoodsDto goodsDto) {
        this.goodsDto = goodsDto;
    }
}
