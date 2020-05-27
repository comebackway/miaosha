package self.lcw01.miaosha.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.lang.reflect.Method;

@Target({ElementType.METHOD,ElementType.FIELD,ElementType.ANNOTATION_TYPE,ElementType.CONSTRUCTOR,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
//该注解作用是，下边自定义注解调用该类进行实质校验，类里边定义校验方法
@Constraint(validatedBy = {IsMobileValidator.class})
public @interface IsMobile {
    /*
    注意自定义注解里边，定义的参数的方式  需要后边加()  比较奇怪
     */


    //默认该注解使用后需要执行检验方法进行校验
    boolean required() default true;

    //校验不通过返回的默认的信息
    String message() default "手机号码格式有误";

    /*
    下边两个好像是必须存在的，先不管
     */
    Class<?> [] groups() default {};

    Class<? extends Payload> [] payload() default {};
}
