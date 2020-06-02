package self.lcw01.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisService {

    @Autowired
    RedisConfig redisConfig;

    @Autowired
    JedisPool jedisPool;

    /**
     * 获取单个对象
     * @param keyPrefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    //获取redis中key对应的value值并转换为T类型的对象
    public <T> T get(KeyPrefix keyPrefix,String key,Class<T> clazz){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            String str = jedis.get(realKey);
            T t = stringToBean(str,clazz);
            return t;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    /**
     * 设置对象
     * @param keyPrefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    //将value对象以string类型的方式保存在redis中
    public <T> boolean set(KeyPrefix keyPrefix,String key,T value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            String str = beanToString(value);
            //如果转换失败则返回false，保存失败
            if (str == null || str.length() <= 0){
                return false;
            }
            int second = keyPrefix.expireSeconds();
            if (second <= 0){
                jedis.set(realKey,str);
            }else {
                jedis.setex(realKey,second,str);
            }
            return true;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    /**
     * 判断是否存在该key
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean exists(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realkey = prefix.getPrefix() + key;
            return jedis.exists(realkey);
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    /**
     * 将key中存储的数字值增一并返回增加后的值
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realkey = prefix.getPrefix() + key;
            return jedis.incr(realkey);
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    /**
     * 将key中存储的数字值减一并返回减少后的值
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long decr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realkey = prefix.getPrefix() + key;
            return jedis.decr(realkey);
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    /**
     * 删除key
     * @param prefix
     * @param key
     * @return
     */
    public boolean del(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realkey = prefix.getPrefix() + key;
            long ret = jedis.del(realkey);
            return ret>0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }


    //当对象为空或者对象的类型为数字类型会转换失败,所以要特殊处理
    private <T> String beanToString(T value) {
        if (value == null){
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class){
            return ""+value;
        }else if (clazz == String.class){
            return (String)value;
        }else if(clazz == long.class || clazz ==  Long.class){
            return ""+value;
        }else {
            return JSON.toJSONString(value);
        }
    }

    private <T> T stringToBean(String str,Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null){
            return null;
        }
        if (clazz == int.class || clazz == Integer.class){
            return (T)Integer.valueOf(str);
        }else if (clazz == String.class){
            return (T)str;
        }else if(clazz == long.class || clazz == Long.class){
            return (T)Long.valueOf(str);
        }else {
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
        }
    }

    /*
    将这个方法  jedispool作为一个bean加载到springboot中，以便其他程序调用
     */
    @Bean
    public JedisPool JedisPoolFactory(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        jedisPoolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        jedisPoolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait() * 1000);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig,redisConfig.getHost(),redisConfig.getPort(),
                redisConfig.getTimeout()*1000);
        return jedisPool;
    }
}
