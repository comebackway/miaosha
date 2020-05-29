package self.lcw01.miaosha.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import self.lcw01.miaosha.dto.GoodsDto;

import java.util.List;

@Mapper
public interface GoodsDao {
    public List<GoodsDto> listGoodsDtoList();
}
