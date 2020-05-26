package self.lcw01.miaosha.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import self.lcw01.miaosha.entity.User;

@Mapper
public interface UserDao {
    public User getById(@Param("id") long id);

    public int insert(User user);
}
