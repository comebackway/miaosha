package self.lcw01.miaosha.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import self.lcw01.miaosha.entity.User;
import self.lcw01.miaosha.service.UserService;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    UserService userService;

    /*
    版本一：调用了@CookieValue和@RequestParam注解
     */
    /*
    @RequestMapping("/to_list")
    public String toLogin(Model model,
                          //取cookie中值为token的内容,非必须内容,可以为空
                          @CookieValue(value = UserService.COOKIE_NAME_TOKEN,required = false) String cookieToken,
                          //因为部分手机端是不存cookie的，为了兼容手机端，把token也作为参数去取，非必须内容，可以为空
                          @RequestParam(value = UserService.COOKIE_NAME_TOKEN,required = false) String paramToken){
        //cookie和参数中的token均为空，返回登录页面
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return "login";
        }
        //优先取参数中的token
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        User user = userService.getByToken(token);
        model.addAttribute("user",user);
        return "goods_list";
    }
    */


    /*
    版本二：使用自定义config
     */
    @RequestMapping("/to_list")
    public String toLogin(Model model,User user){
        model.addAttribute("user",user);
        return "goods_list";
    }
}
