package self.lcw01.miaosha.rabbitmq;

import self.lcw01.miaosha.entity.User;

public class MQMiaoshaMessage {
    private User user;
    private long goodId;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getGoodId() {
        return goodId;
    }

    public void setGoodId(long goodId) {
        this.goodId = goodId;
    }
}
