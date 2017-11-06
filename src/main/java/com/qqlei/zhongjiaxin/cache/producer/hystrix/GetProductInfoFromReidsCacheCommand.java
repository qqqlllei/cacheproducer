package com.qqlei.zhongjiaxin.cache.producer.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.qqlei.zhongjiaxin.cache.producer.model.ProductInfo;
import com.qqlei.zhongjiaxin.cache.producer.spring.SpringContext;
import redis.clients.jedis.JedisCluster;

/**
 * Created by 李雷 on 2017/11/6.
 */
public class GetProductInfoFromReidsCacheCommand extends HystrixCommand<ProductInfo>{

    private Long productId;
    public GetProductInfoFromReidsCacheCommand(Long productId){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RedisGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        //timeout超时控制,此处的100ms是根据对于redis的请求的一个统计得出的，例如100个请求，99个都是在100ms内完成
                        .withExecutionTimeoutInMilliseconds(100)
                        //如果设置为20（默认值），那么在一个10秒的滑动窗口内，如果只有19个请求，即使这19个请求都是异常的，也是不会触发开启短路器的
                        .withCircuitBreakerRequestVolumeThreshold(1000)
                        //当异常请求达到这个百分比时，就触发打开短路器，默认是50，也就是50%
                        .withCircuitBreakerErrorThresholdPercentage(70)
                        //需要在多长时间内直接reject请求，然后在这段时间之后，再重新导holf-open状态，尝试允许请求通过以及自动恢复，默认值是5000毫秒
                        .withCircuitBreakerSleepWindowInMilliseconds(5000))
        );
        this.productId = productId;
    }

    @Override
    protected ProductInfo run() throws Exception {
        JedisCluster jedisCluster = (JedisCluster) SpringContext.getApplicationContext()
                .getBean("JedisClusterFactory");
        String key = "product_info_" + productId;
        String json = jedisCluster.get(key);
        if(json != null) {
            return JSONObject.parseObject(json, ProductInfo.class);
        }
        return null;
    }

    @Override
    protected ProductInfo getFallback() {
        return null;
    }
}
