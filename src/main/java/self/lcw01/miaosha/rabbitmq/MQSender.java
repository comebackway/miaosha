package self.lcw01.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static Logger logger = LoggerFactory.getLogger(MQSender.class);

    //操作队列的封装类,里边实现了各种方法
    @Autowired
    AmqpTemplate amqpTemplate;

    public void send(Object message){
        logger.info("send message");
        //往队列发送消息
        amqpTemplate.convertAndSend(MQConfig.QUEUE,message.toString());
    }
}
