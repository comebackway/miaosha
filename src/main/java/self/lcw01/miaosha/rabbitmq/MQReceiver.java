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
        logger.info("receive message" + message);
    }
}
