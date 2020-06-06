package self.lcw01.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);

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

    /**
     * 注意headers方式的listener 接收参数的类型时bytes数组
     * @param message
     */
    @RabbitListener(queues = MQConfig.HEADERS_QUEUE)
    public void receiveHeaderQueue(byte [] message){
        logger.info("header queue message " + new String(message));
    }
}
