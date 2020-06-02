package self.lcw01.miaosha.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import self.lcw01.miaosha.dto.GoodsDetailDto;
import self.lcw01.miaosha.dto.GoodsDto;
import self.lcw01.miaosha.entity.User;
import self.lcw01.miaosha.redis.GoodsKey;
import self.lcw01.miaosha.redis.RedisService;
import self.lcw01.miaosha.result.Result;
import self.lcw01.miaosha.service.GoodsService;
import self.lcw01.miaosha.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    UserService userService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    //渲染thymeleaf页面的框架
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;
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
    /*
    此处使用了页面缓存技术，是为了防止瞬间的并发   没有做前后端分离
    注意页面缓存需要做生存时间，一般60秒以内，因为这样用户最多也就看到60秒前的页面而已
     */

    // produces 指定返回的内容是html
    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String list(Model model, User user, HttpServletResponse response, HttpServletRequest request){
        model.addAttribute("user",user);

        //取缓存中的页面
        String html = redisService.get(GoodsKey.getGoodsList,"",String.class);
        if (!StringUtils.isEmpty(html)){
            return html;
        }
        
        List<GoodsDto> goodsDtoList = goodsService.listGoodsDto();
        model.addAttribute("goodsList",goodsDtoList);
        //return "goods_list";

        //手工渲染页面
        //因为实现了接口IContext的SpringWebContext类已经过时，所以使用IWebContext
        IWebContext context = new WebContext(request,response,request.getServletContext(),
                //其中model就是要传入的数据
                request.getLocale(),model.asMap());
        //使用thymeleaf的引擎，将数据和页面模板作为参数传值
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", context);
        if (!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsList,"",html);
        }
        return html;
    }

    /*
    未使用静态化页面（前后端分离）技术的原始版本
    @RequestMapping(value = "/to_detail/{goodsId}",produces = "text/html")
    @ResponseBody
    public String detail(Model model, User user,
                         //取url路径中的值的方法
                         @PathVariable("goodsId")long goodsId,
                         HttpServletRequest request,HttpServletResponse response){
        model.addAttribute("user",user);
        GoodsDto goods = goodsService.getGoodsDtoByGoodsId(goodsId);
        model.addAttribute("goods",goods);
        //对秒杀时间进行判断
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        //秒杀状态
        int miaoshaStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        if (now<startAt){
            miaoshaStatus = 0;
            remainSeconds = (int)(startAt - now)/1000;
        }else if (now>endAt){
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else{
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        //return "goods_detail";
        //取缓存中的页面
        String html = redisService.get(GoodsKey.getGoodsDetail,""+goodsId,String.class);
        if (!StringUtils.isEmpty(html)){
            return html;
        }

        //手工渲染页面
        //因为实现了接口IContext的SpringWebContext类已经过时，所以使用IWebContext
        IWebContext context = new WebContext(request,response,request.getServletContext(),
                //其中model就是要传入的数据
                request.getLocale(),model.asMap());
        //使用thymeleaf的引擎，将数据和页面模板作为参数传值
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", context);
        if (!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsDetail,""+goodsId,html);
        }
        return html;
    }

     */

    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailDto> detail(Model model, User user,
                                         //取url路径中的值的方法
                                         @PathVariable("goodsId")long goodsId,
                                         HttpServletRequest request, HttpServletResponse response){

        GoodsDto goods = goodsService.getGoodsDtoByGoodsId(goodsId);

        //对秒杀时间进行判断
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        //秒杀状态
        int miaoshaStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        if (now<startAt){
            miaoshaStatus = 0;
            remainSeconds = (int)(startAt - now)/1000;
        }else if (now>endAt){
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else{
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailDto goodsDetailDto = new GoodsDetailDto();
        goodsDetailDto.setGoodsDto(goods);
        goodsDetailDto.setUser(user);
        goodsDetailDto.setRemainSeconds(remainSeconds);
        goodsDetailDto.setMiaoshaStatus(miaoshaStatus);
        return Result.success(goodsDetailDto);
    }
}
