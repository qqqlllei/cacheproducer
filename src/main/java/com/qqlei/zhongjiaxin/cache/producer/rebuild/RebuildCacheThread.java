package com.qqlei.zhongjiaxin.cache.producer.rebuild;

import com.qqlei.zhongjiaxin.cache.producer.model.ProductInfo;
import com.qqlei.zhongjiaxin.cache.producer.service.CacheService;
import com.qqlei.zhongjiaxin.cache.producer.spring.SpringContext;
import com.qqlei.zhongjiaxin.cache.producer.zk.ZooKeeperSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 缓存重建线程
 * Created by 李雷 on 2017/10/23.
 */
public class RebuildCacheThread implements Runnable{
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public void run() {
        RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();
        ZooKeeperSession zkSession = ZooKeeperSession.getInstance();
        CacheService cacheService = (CacheService) SpringContext.getApplicationContext()
                .getBean("cacheService");
        while (true){

            //不断的从重建队列中获取数据
            ProductInfo productInfo =  rebuildCacheQueue.takeProductInfo();

            zkSession.acquireDistributedLock(productInfo.getId());

            ProductInfo existedProductInfo = cacheService.getProductInfoFromReidsCache(productInfo.getId());

            if(existedProductInfo == null){
                System.out.println("existed product info is null......");
            }

            if(existedProductInfo !=null){
                try {
                    Date date = sdf.parse(productInfo.getModifiedTime());
                    Date existedDate = sdf.parse(existedProductInfo.getModifiedTime());
                    if(date.before(existedDate)){
                        System.out.println("current date[" + productInfo.getModifiedTime() + "] is before existed date[" + existedProductInfo.getModifiedTime() + "]");
                        continue;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                System.out.println("current date[" + productInfo.getModifiedTime() + "] is after existed date[" + existedProductInfo.getModifiedTime() + "]");
            }
            cacheService.saveProductInfo2LocalCache(productInfo);
            cacheService.saveProductInfo2ReidsCache(productInfo);
            zkSession.releaseDistributedLock(productInfo.getId());
        }
    }
}
