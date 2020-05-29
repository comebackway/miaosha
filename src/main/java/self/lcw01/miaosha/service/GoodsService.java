package self.lcw01.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import self.lcw01.miaosha.dao.GoodsDao;
import self.lcw01.miaosha.dto.GoodsDto;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsDto> listGoodsDto(){
        return goodsDao.listGoodsDtoList();
    }
}
