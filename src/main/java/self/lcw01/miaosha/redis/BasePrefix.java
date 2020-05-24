package self.lcw01.miaosha.redis;

public abstract class BasePrefix implements KeyPrefix{

    private int expireSeconds;

    private String prefix;

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    //默认0代表永不过期
    public BasePrefix(String prefix) {
        this.expireSeconds = 0;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        //通过获取继承了该父类的子类的类名，去作为key名字的区分
        /*
        如UserKey和OrderKey分别作为该抽象类的子类，则各自获得对应的类名
         */
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }
}
