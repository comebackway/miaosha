package self.lcw01.miaosha.redis;

public class MiaoshaKey extends BasePrefix {
    public MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60,"gp");
    public static MiaoshaKey verifyCode = new MiaoshaKey(60,"vcode");

    //这是固定过期时间的限流redis
    public static MiaoshaKey accessNum = new MiaoshaKey(60,"an");
    //这是自定义时间的限流redis
    public static MiaoshaKey withExpire(int expireSeconds){
        return new MiaoshaKey(expireSeconds,"an");
    }
}
