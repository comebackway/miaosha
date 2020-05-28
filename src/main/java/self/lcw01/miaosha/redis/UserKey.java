package self.lcw01.miaosha.redis;

public class UserKey extends BasePrefix {
    //token默认的有效期
    private static final int TOKEN_EXPIRE = 3600 *24 *2;

    public UserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public UserKey(String prefix) {
        super(prefix);
    }

    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");
    public static UserKey token = new UserKey(TOKEN_EXPIRE,"tk");
}
