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
public class SaveProductInfo2ReidsCacheCommand extends HystrixCommand<Boolean> {

    private ProductInfo productInfo;
    public SaveProductInfo2ReidsCacheCommand(ProductInfo productInfo){
        super(HystrixCommandGroupKey.Factory.asKey("RedisGroup"));
        this.productInfo = productInfo;
    }
    @Override
    protected Boolean run() throws Exception {
        JedisCluster jedisCluster = (JedisCluster) SpringContext.getApplicationContext()
                .getBean("JedisClusterFactory");
        String key = "product_info_" + productInfo.getId();
        jedisCluster.set(key, JSONObject.toJSONString(productInfo));
        return true;
    }
}
