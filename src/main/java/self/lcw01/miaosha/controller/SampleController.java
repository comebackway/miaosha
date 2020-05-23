package self.lcw01.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import self.lcw01.miaosha.entity.User;
import self.lcw01.miaosha.result.Result;
import self.lcw01.miaosha.service.UserService;

@Controller
@RequestMapping("/demo")
public class SampleController {
    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name","Licw");
        return "hello";
    }


    @Autowired
    UserService userService;

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbget(){
        User user = userService.getById(1);
        return Result.success(user);
    }
    @RequestMapping("/db/tx")
    public Result<Boolean> tx(){
        userService.tx();
        return Result.success(true);
    }
}
