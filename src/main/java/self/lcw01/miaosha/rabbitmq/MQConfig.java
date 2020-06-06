package self.lcw01.miaosha.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * 该类的作用是配置消息队列
 */

@Configuration
public class MQConfig {

    public static final String QUEUE = "queue";
    public static final String Topic_QUEUE1 = "topic.queue1";
    public static final String Topic_QUEUE2 = "topic.queue2";
    public static final String HEADERS_QUEUE = "headers.queue";

    public static final String Topic_EXCHANGE = "topicExchange";
    public static final String Fanout_EXCHANGE = "fanoutExchange";
    public static final String Headers_EXCHANGE = "headersExchange";

    public static final String ROUTING_KEY1 = "topic.key1";
    //这里的# 表示匹配所有
    public static final String ROUTING_KEY2 = "topic.#";


    public static final String MIAOSHA_QUEUE = "queue_1";


    @Bean
    public Queue miaoshaqueue(){
        //返回一个名称是queue的队列
        return new Queue(MIAOSHA_QUEUE,true);
    }





    /**
     * Direct模式 交换机Exchange（使用了默认的交换机）
     * @return
     */
    @Bean
    public Queue queue(){
        //返回一个名称是queue的队列
        return new Queue(QUEUE,true);
    }



    /**
     * Topic模式 交换机Exchange,需要自定义exchange
     * 规则：消息先放到exchange交换机中 再用exchange把消息发送到队列queue中
     * 原理：通过绑定binding 将交换机和队列绑定 然后通过key区分交换机中绑定的队列 当然key和queue不是一一对应的关系  key也可以使用通配符匹配多个key值
     * 使用过程：sender发消息时会指定交换机，key和message，然后通过相关交换机对应的key规则将message发送给相应的queue。在listener中 通过其监听的queue 然后获得对应的信息
     * @return
     */
    @Bean
    public Queue topicQueue1(){
        return new Queue(Topic_QUEUE1,true);
    }

    @Bean
    public Queue topicQueue2(){
        return new Queue(Topic_QUEUE2,true);
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(Topic_EXCHANGE);
    }

    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(ROUTING_KEY1);
    }

    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(ROUTING_KEY2);
    }



    /**
     * Fanout模式 交换机Exchange
     * 规则：广播，不需要key作为区分
     * 使用过程：跟topic类似 但不需要传入key值
     */
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(Fanout_EXCHANGE);
    }

    //这里为了方便 使用topicqueue队列进行绑定
    @Bean
    public Binding fanoutBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }



    /**
     * headers模式 交换机exchange
     * 规则：以map作为条件，只有符合map条件的消息才能发送到对应的队列中
     * 使用过程：交换机里边设定对应map 以作为该交换机的where条件作为匹配。sender中要新建MessageProperties以设置其的map值供其传入的交换机做条件判断。
     *          sender还需要新建Message对应，以传入其需要传输的参数以及messageproperties的map条件，另外参数要以bytes数组类型传输
     *          receiver中则需要接收bytes数组作为形参 接收sender的消息
     * 注意：好像有bug whereall和whereany在这里都是只匹配一个队列即可接收到消息
     */
    @Bean
    public Queue headersQueue(){
        return new Queue(HEADERS_QUEUE,true);
    }

    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(Headers_EXCHANGE);
    }

    @Bean
    public Binding headerBinding(){
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("c","c1");
        map.put("b","b1");
        map.put("x-match","all");
        //whereAll(map) 只有符合map里边所有key value键值对 队列queue才能接收到消息
        return BindingBuilder.bind(headersQueue()).to(headersExchange()).whereAll(map).match();
    }

}
