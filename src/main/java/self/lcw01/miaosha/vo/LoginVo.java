package self.lcw01.miaosha.vo;

import org.hibernate.validator.constraints.Length;
import self.lcw01.miaosha.validator.IsMobile;

import javax.validation.constraints.NotNull;

public class LoginVo {
    /*
    @NotNull和@Length是框架原生的校验
    */
    @NotNull
    @Length(min=6)
    private String password;
    @NotNull
    //使用自定义的注解校验
    @IsMobile
    private String mobile;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "LoginVo{" +
                "password='" + password + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}
