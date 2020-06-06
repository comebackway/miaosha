package self.lcw01.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import self.lcw01.miaosha.redis.RedisService;

@Service
public class MQSender {

    private static Logger logger = LoggerFactory.getLogger(MQSender.class);

    //操作队列的封装类,里边实现了各种方法
    @Autowired
    AmqpTemplate amqpTemplate;

    /*

        以下四种基础方法是测试用

    public void send(Object message){
        logger.info("send message");
        //往队列发送消息
        amqpTemplate.convertAndSend(MQConfig.QUEUE,message.toString());
    }

    public void sendTopic(Object message){
        logger.info("send topic message");
        amqpTemplate.convertAndSend(MQConfig.Topic_EXCHANGE,MQConfig.ROUTING_KEY1,message.toString()+"1");
        amqpTemplate.convertAndSend(MQConfig.Topic_EXCHANGE,MQConfig.ROUTING_KEY2,message.toString()+"2");
    }

    public void sendFanout(Object message){
        logger.info("send fanout message");
        amqpTemplate.convertAndSend(MQConfig.Fanout_EXCHANGE,"",message.toString());
    }

    public void sendHeaders(Object message){
        logger.info("send headers message");
        //需要先新建一个MessageProperties对象 以设置该消息的参数（键值对）
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("c","c1");
        //messageProperties.setHeader("header21","value2");
        //新建Message对象 以便设置sender中要传的值
        //参数一：要送的值 要以bytes数组的形式传参
        //参数二：MessageProperties对象 以标明这消息的参数，以便交换机作为条件where匹配
        Message obj = new Message(message.toString().getBytes(),messageProperties);
        amqpTemplate.convertAndSend(MQConfig.Headers_EXCHANGE,"",obj);
    }

    */


    /**
     * 真正的业务方法
     */
    public void sendMiaoshaMessage(MQMiaoshaMessage mqMiaoshaMessage) {
        //这里是借用了redis的beantostring方法而已，不是真的要操作redis
        String msg = RedisService.beanToString(mqMiaoshaMessage);
        logger.info("send message：" + msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE,msg);
    }
}
