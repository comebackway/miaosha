package self.lcw01.miaosha.ThreadLocalContext;

import self.lcw01.miaosha.entity.User;

public class UserContext {
    /**
     * ThreadLocal的作用：是保证线程安全的
     * 每个线程里边，ThreadLocal的属性是独立的，所以在多线程里边，可以用ThreadLocal保证线程间属性值的独立性
     */
    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<User>();

    public static void setUserThreadLocal(User user){
        userThreadLocal.set(user);
    }

    public static User getUserThreadLocal(){
        return userThreadLocal.get();
    }
}
