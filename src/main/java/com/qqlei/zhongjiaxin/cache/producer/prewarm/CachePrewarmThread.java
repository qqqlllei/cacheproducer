package com.qqlei.zhongjiaxin.cache.producer.prewarm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qqlei.zhongjiaxin.cache.producer.model.ProductInfo;
import com.qqlei.zhongjiaxin.cache.producer.service.CacheService;
import com.qqlei.zhongjiaxin.cache.producer.spring.SpringContext;
import com.qqlei.zhongjiaxin.cache.producer.zk.ZooKeeperSession;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by 李雷 on 2017/10/30.
 */
public class CachePrewarmThread implements Runnable{
    @Override
    public void run() {
        CacheService cacheService = (CacheService) SpringContext.
                getApplicationContext().getBean("cacheService");
        ZooKeeperSession zooKeeperSession =  ZooKeeperSession.getInstance();

        // 获取storm taskid列表
       String taskidList =   zooKeeperSession.getNodeData("/taskid-list");

       if(StringUtils.isNotBlank(taskidList)){
           String[] taskidListSplited = taskidList.split(",");
           for (String taskId:taskidListSplited) {
               String taskidLockPath = "/taskid-lock-" + taskId;
               boolean result = zooKeeperSession.acquireFastFailedDistributedLock(taskidLockPath);
               if(!result) {
                   continue;
               }

               String taskidStatusLockPath = "/taskid-status-lock-" + taskId;
               zooKeeperSession.acquireDistributedLock(taskidStatusLockPath);

               String taskidStatus = zooKeeperSession.getNodeData("/taskid-status-" + taskId);

               if(StringUtils.isBlank(taskidStatus)){
                   String productidList = zooKeeperSession.getNodeData("/task-hot-product-list-" + taskId);

                   if(StringUtils.isNotBlank(productidList)){
                       JSONArray productidJSONArray = JSONArray.parseArray(productidList);
                       for(int i = 0; i < productidJSONArray.size(); i++) {
                           JSONObject jsonObject = productidJSONArray.getJSONObject(i);
                           long productId = jsonObject.getLong("key");
                           String productInfoJSON = "{\"id\": " + productId + ", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"modifiedTime\": \"2017-01-01 12:00:00\"}";
                           ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);
                           cacheService.saveProductInfo2LocalCache(productInfo);
                           cacheService.saveProductInfo2ReidsCache(productInfo);
                       }
                   }

                   zooKeeperSession.setNodeData("/taskid-status-"+taskId, "success");
               }

               zooKeeperSession.releaseDistributedLock(taskidStatusLockPath);

               zooKeeperSession.releaseDistributedLock(taskidLockPath);
           }
       }

    }
}
