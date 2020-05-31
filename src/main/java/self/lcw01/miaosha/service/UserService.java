package self.lcw01.miaosha.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import self.lcw01.miaosha.dao.UserDao;
import self.lcw01.miaosha.entity.User;
import self.lcw01.miaosha.exception.GlobalException;
import self.lcw01.miaosha.redis.RedisService;
import self.lcw01.miaosha.redis.UserKey;
import self.lcw01.miaosha.result.CodeMsg;
import self.lcw01.miaosha.util.MD5Util;
import self.lcw01.miaosha.util.UUIDUtil;
import self.lcw01.miaosha.vo.LoginVo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserService {

    public final static String COOKIE_NAME_TOKEN = "token";

    @Autowired
    UserDao userDao;

    @Autowired
    RedisService redisService;

    public User getById(Long id){
        return userDao.getById(id);
    }

    @Transactional
    public boolean tx(){
        User user1 = new User();
        user1.setId(Long.valueOf(2));
        user1.setName("wawa");
        userDao.insert(user1);

        User user2 = new User();
        user2.setId((long)1);
        user2.setName("dada");
        userDao.insert(user2);

        return true;
    }

    /*
    public User login(HttpServletResponse response,LoginVo loginVo){
        if (loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        User user = userDao.getById(Long.parseLong(mobile));
        if (user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        String dbPass = user.getPassword();
        String dbSalt = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(password,dbSalt);
        if (!calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        if(user != null) {
            //生成唯一的token，作为每个登录用户的标识
            String token = UUIDUtil.uuid();
            AddCookie(response, token,user);
        }
        return user;
    }
    */

    public String login(HttpServletResponse response,LoginVo loginVo){
        if (loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        User user = userDao.getById(Long.parseLong(mobile));
        if (user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        String dbPass = user.getPassword();
        String dbSalt = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(password,dbSalt);
        if (!calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        if(user != null) {
            //生成唯一的token，作为每个登录用户的标识
            String token = UUIDUtil.uuid();
            AddCookie(response, token,user);
            return token;
        }
        return null;
    }

    //每次调用该方法都会重新设置一个新的token，然后刷新缓存，redis的生成时间重新设置
    private void AddCookie(HttpServletResponse response, String token,User user) {
        //将token和用户信息user作为键值对存在redis中
        redisService.set(UserKey.token,token,user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(UserKey.token.expireSeconds());
        //设置该cookie的有效路径
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    //从redis中取出user
    public User getByToken(HttpServletResponse response,String token){
        if (StringUtils.isEmpty(token)){
            return null;
        }
        User user = redisService.get(UserKey.token,token,User.class);
        //延长有效期
        if (user != null){
            AddCookie(response,token,user);
        }
        return user;
    }
}
