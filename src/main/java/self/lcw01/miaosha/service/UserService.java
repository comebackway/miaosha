package self.lcw01.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import self.lcw01.miaosha.dao.UserDao;
import self.lcw01.miaosha.entity.User;
import self.lcw01.miaosha.result.CodeMsg;
import self.lcw01.miaosha.util.MD5Util;
import self.lcw01.miaosha.vo.LoginVo;

@Service
public class UserService {
    @Autowired
    UserDao userDao;

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

    public CodeMsg login(LoginVo loginVo){
        if (loginVo == null){
            return CodeMsg.SERVER_ERROR;
        }
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        User user = userDao.getById(Long.parseLong(mobile));
        if (user == null){
            return CodeMsg.MOBILE_NOT_EXIST;
        }
        String dbPass = user.getPassword();
        String dbSalt = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(password,dbSalt);
        if (!calcPass.equals(dbPass)){
            return CodeMsg.PASSWORD_ERROR;
        }
        return CodeMsg.SUCCESS;
    }
}
