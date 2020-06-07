package self.lcw01.miaosha.Interceptor;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import self.lcw01.miaosha.ThreadLocalContext.UserContext;
import self.lcw01.miaosha.access.AccessLimit;
import self.lcw01.miaosha.entity.User;
import self.lcw01.miaosha.redis.MiaoshaKey;
import self.lcw01.miaosha.redis.RedisService;
import self.lcw01.miaosha.result.CodeMsg;
import self.lcw01.miaosha.result.Result;
import self.lcw01.miaosha.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Service
//1.拦截器的定义 要先继承HandlerInterceptorAdapter类
public class AccessLimitInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //handler是controller下的一个个方法
        if (handler instanceof HandlerMethod){
            User user = getUser(request, response);
            /*
            因为在spring中 先执行拦截器再执行后边的参数处理器的
            所以在同一个线程里边（一次访问），我们可以先在拦截器里边使用ThreadLocal保存变量
            然后在参数处理器中再get出来
             */
            UserContext.setUserThreadLocal(user);

            //将其转为HandlerMethod后 可以拿到该方法的很多东西 包括其方法上的注解
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            //如果方法上没有该注解，直接放过
            if (accessLimit == null){
                return true;
            }
            //获得方法的注解上的属性值
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            String key = request.getRequestURI();
            //开始校验注解的属性值
            if (needLogin){
                if (user == null){
                    //给客户端提示的方法
                    //直接在方法中使用outputstream将错误信息写出
                    render(response, CodeMsg.SESSION_ERROR);
                    //然后再关闭该次进程
                    return false;
                }
                key = user.getId() + "_" + key;
            }else{
                //do nothing
            }
            Integer accessnum = redisService.get(MiaoshaKey.withExpire(seconds),""+user.getId()+"_"+request.getRequestURL(),Integer.class);
            if(accessnum == null){
                //如果是第一次访问
                redisService.set(MiaoshaKey.withExpire(seconds),""+user.getId()+"_"+request.getRequestURL(),1);
            }else if (accessnum < maxCount){
                //少于访问次数限制
                redisService.incr(MiaoshaKey.accessNum,""+user.getId()+"_"+request.getRequestURL());
            }else{
                render(response, CodeMsg.ACCESS_LIMIT);
                return false;
            }
        }
        return true;
    }


    private void render(HttpServletResponse response, CodeMsg sessionError) throws IOException {
        //不设定这个可能会出现乱码
        response.setContentType("application/json;charset=UTF-8");
        OutputStream outputStream = response.getOutputStream();
        //1.先转换成String
        String res = JSON.toJSONString(Result.error(sessionError));
        //2.再用bytes数组写出
        outputStream.write(res.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }


    private User getUser(HttpServletRequest request, HttpServletResponse response){
        String paramToken = request.getParameter(UserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request,UserService.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return null;
        }
        //优先取参数中的token
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        return userService.getByToken(response,token);
    }

    private String getCookieValue(HttpServletRequest request,String cookieName){
        //取出所有的cookie做循环
        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length<=0){
            return null;
        }
        for (Cookie cookie:cookies){
            if (cookie.getName().equals(cookieName)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
