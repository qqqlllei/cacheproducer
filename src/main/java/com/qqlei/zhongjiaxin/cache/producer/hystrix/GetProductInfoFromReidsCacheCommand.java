package com.qqlei.zhongjiaxin.cache.producer.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.qqlei.zhongjiaxin.cache.producer.model.ProductInfo;
import com.qqlei.zhongjiaxin.cache.producer.spring.SpringContext;
import redis.clients.jedis.JedisCluster;

/**
 * Created by 李雷 on 2017/11/6.
 */
public class GetProductInfoFromReidsCacheCommand extends HystrixCommand<ProductInfo>{

    private Long productId;
    public GetProductInfoFromReidsCacheCommand(Long productId){
        super(HystrixCommandGroupKey.Factory.asKey("RedisGroup"));
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
}
