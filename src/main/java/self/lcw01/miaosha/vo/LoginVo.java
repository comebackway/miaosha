package self.lcw01.miaosha.vo;

public class LoginVo {
    private String password;
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
