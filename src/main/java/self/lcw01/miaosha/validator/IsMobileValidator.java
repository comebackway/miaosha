package self.lcw01.miaosha.validator;

import org.apache.commons.lang3.StringUtils;
import self.lcw01.miaosha.util.ValidatorUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/*
该类要实现ConstraintValidator接口才能实现注解校验功能
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {
    private boolean required = false;

    /*
    初始化方法，可以拿到注解的信息
     */
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    /*
    要校验的方法，能获取到注解要校验的值的信息
     */
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (required){
            //必须要校验
            return ValidatorUtil.isMobile(s);
        }else {
            //非必须校验，则空的时候直接放过，不为空的时候还是要校验
            if (StringUtils.isEmpty(s)){
                return true;
            }else{
                return ValidatorUtil.isMobile(s);
            }
        }
    }
}
