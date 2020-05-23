package self.lcw01.miaosha.dao;

import org.apache.ibatis.annotations.Mapper;
import self.lcw01.miaosha.entity.User;

@Mapper
public interface UserDao {
    public User getById(int id);

    public int insert(User user);
}
