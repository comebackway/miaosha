package self.lcw01.miaosha.redis;

public class MiaoshaKey extends BasePrefix {
    public MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60,"gp");
    public static MiaoshaKey verifyCode = new MiaoshaKey(60,"vcode");
}
