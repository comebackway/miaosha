package self.lcw01.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import self.lcw01.miaosha.entity.User;
import self.lcw01.miaosha.redis.RedisService;
import self.lcw01.miaosha.redis.UserKey;
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
    @Autowired
    RedisService redisService;

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbget(){
        User user = userService.getById((long)1);
        return Result.success(user);
    }
    @RequestMapping("/db/tx")
    public Result<Boolean> tx(){
        userService.tx();
        return Result.success(true);
    }

    /*
    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> reidsset(){
        boolean v1 = redisService.set("key2","wawawa");
        return Result.success(v1);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<String> reidsget(){
        String v1 = redisService.get("key2",String.class);
        return Result.success(v1);
    }
    */
    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> reidsget(){
        User v1 = redisService.get(UserKey.getById,""+3,User.class);
        return Result.success(v1);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> reidsset(){
        User user = new User();
        user.setId((long)3);
        user.setName("333");
        boolean v1 = redisService.set(UserKey.getById,""+3,user);
        return Result.success(v1);
    }
}
