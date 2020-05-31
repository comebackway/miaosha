package self.lcw01.miaosha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import self.lcw01.miaosha.entity.User;
import self.lcw01.miaosha.result.Result;


@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/info")
    @ResponseBody
    public Result<User> userInfo(User user){
        return Result.success(user);
    }
}
