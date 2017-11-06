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
public class SaveShopInfo2ReidsCacheCommand extends HystrixCommand<Boolean> {

    private ShopInfo shopInfo;
    public SaveShopInfo2ReidsCacheCommand(ShopInfo shopInfo){
        super(HystrixCommandGroupKey.Factory.asKey("RedisGroup"));
        this.shopInfo = shopInfo;
    }

    @Override
    protected Boolean run() throws Exception {
        JedisCluster jedisCluster = (JedisCluster) SpringContext.getApplicationContext()
                .getBean("JedisClusterFactory");
        String key = "shop_info_" + shopInfo.getId();
        jedisCluster.set(key, JSONObject.toJSONString(shopInfo));
        return true;
    }

    @Override
    protected Boolean getFallback() {
        return false;
    }
}
