package com.qqlei.zhongjiaxin.cache.producer.command;

import com.netflix.hystrix.*;

/**
 * Created by 李雷 on 2017/11/2.
 */
public class GetBrandNameCommand extends HystrixCommand<String>{

    private Long brandId;

    public GetBrandNameCommand(Long brandId){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetBrandNameGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetBrandNameCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetBrandInfoPool"))
                //hystrix.threadpool.default.coreSize 并发执行的最大线程数，默认10
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(15)
                        //hystrix.threadpool.default.queueSizeRejectionThreshold 即使maxQueueSize没有达到，
                        // 达到queueSizeRejectionThreshold该值后，请求也会被拒绝。
                        // 因为maxQueueSize不能被动态修改，这个参数将允许我们动态设置该值。if maxQueueSize == -1，该字段将不起作用
                        .withQueueSizeRejectionThreshold(10))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        ////使用信号量隔离时，命令fallback(降级)调用最大的并发数,默认:10
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(15)));

        this.brandId = brandId;
    }
    @Override
    protected String run() throws Exception {
        // 调用一个品牌服务的接口
        // 如果调用失败了，报错了，那么就会去调用fallback降级机制
        throw new Exception();
    }

    @Override
    protected String getFallback() {
        System.out.println("从本地缓存获取过期的品牌数据，brandId=" + brandId);
        return "NIKE";
    }
}
