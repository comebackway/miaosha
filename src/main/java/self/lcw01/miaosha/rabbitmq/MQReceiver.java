package self.lcw01.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import self.lcw01.miaosha.dto.GoodsDto;
import self.lcw01.miaosha.entity.MiaoshaOrder;
import self.lcw01.miaosha.entity.User;
import self.lcw01.miaosha.redis.RedisService;
import self.lcw01.miaosha.service.GoodsService;
import self.lcw01.miaosha.service.MiaoshaService;
import self.lcw01.miaosha.service.OrderService;

@Service
public class MQReceiver {

    private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    /*
    以下四个方法是测试用的基础方法

    //指定监听的队列的名称
    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message){
        logger.info("receive message " + message);
    }

    @RabbitListener(queues = MQConfig.Topic_QUEUE1)
    public void receive1(String message){
        logger.info("receive topic1 message " + message);
    }

    @RabbitListener(queues = MQConfig.Topic_QUEUE2)
    public void receive2(String message){
        logger.info("receive topic2 message " + message);
    }
    */
    /*
     * 注意headers方式的listener 接收参数的类型时bytes数组
     * @param message
     */
    /*
    @RabbitListener(queues = MQConfig.HEADERS_QUEUE)
    public void receiveHeaderQueue(byte [] message){
        logger.info("header queue message " + new String(message));
    }
    */



    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(String message){
        logger.info("receive message"+ message);
        //将字符串还原成类
        MQMiaoshaMessage mqMiaoshaMessage = RedisService.stringToBean(message,MQMiaoshaMessage.class);
        //取得相应信息
        User user = mqMiaoshaMessage.getUser();
        long goodsId = mqMiaoshaMessage.getGoodId();
        //判断库存
        GoodsDto goodsDto = goodsService.getGoodsDtoByGoodsId(goodsId);
        int stock = goodsDto.getStockCount();
        if (stock <= 0){
            return;
        }
        //该用户是否已经秒杀成功过
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(),goodsId);
        if (miaoshaOrder != null){
            return;
        }
        //一个事务里边完成：减库存 下订单 写入秒杀订单
        miaoshaService.miaosha(user,goodsDto);
    }


}
