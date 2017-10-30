package com.qqlei.zhongjiaxin.cache.producer.rebuild;

import com.qqlei.zhongjiaxin.cache.producer.model.ProductInfo;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 重建缓存内存队列
 * Created by 李雷 on 2017/10/23.
 */
public class RebuildCacheQueue {

    private ArrayBlockingQueue<ProductInfo> queue  = new ArrayBlockingQueue<>(1000);


    public void putProductInfo(ProductInfo productInfo){
        try {
            queue.put(productInfo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ProductInfo takeProductInfo(){
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  null;
    }

    /**
     * 单例
     */
    private static class Singleton{
        private static RebuildCacheQueue instance;
        static {
            instance = new RebuildCacheQueue();
        }

        public static RebuildCacheQueue getInstance(){
            return instance;
        }
    }

    public static RebuildCacheQueue getInstance(){
        return Singleton.getInstance();
    }

    public static void init(){
        getInstance();
    }

}
