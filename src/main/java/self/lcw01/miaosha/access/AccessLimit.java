package self.lcw01.miaosha.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/**
 * 定义自定义注解，该注解的作用是作为拦截器校验，跟下边的jsr303校验中的自定义注解校验类似
 */
public @interface AccessLimit {
    int seconds();
    int maxCount();
    boolean needLogin() default true;
}
