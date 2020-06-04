package self.lcw01.miaosha.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 该类的作用是配置消息队列
 */

@Configuration
public class MQConfig {

    public static final String QUEUE = "queue";

    @Bean
    public Queue queue(){
        //返回一个名称是queue的队列
        return new Queue(QUEUE,true);
    }
}
