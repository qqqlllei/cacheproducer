package com.qqlei.zhongjiaxin.cache.producer.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.qqlei.zhongjiaxin.cache.producer.model.ShopInfo;
import com.qqlei.zhongjiaxin.cache.producer.spring.SpringContext;
import redis.clients.jedis.JedisCluster;

/**
 * Created by 李雷 on 2017/11/6.
 */
public class GetShopInfoFromReidsCacheCommand extends HystrixCommand<ShopInfo> {

    private Long shopId;
    public GetShopInfoFromReidsCacheCommand(Long shopId){
        super(HystrixCommandGroupKey.Factory.asKey("RedisGroup"));
        this.shopId = shopId;
    }

    @Override
    protected ShopInfo run() throws Exception {
        JedisCluster jedisCluster = (JedisCluster) SpringContext.getApplicationContext()
                .getBean("JedisClusterFactory");
        String key = "shop_info_" + shopId;
        String json = jedisCluster.get(key);
        if(json != null) {
            return JSONObject.parseObject(json, ShopInfo.class);
        }
        return null;
    }

    @Override
    protected ShopInfo getFallback() {
        return null;
    }
}
