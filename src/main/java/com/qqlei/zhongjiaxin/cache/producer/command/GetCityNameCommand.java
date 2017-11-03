package com.qqlei.zhongjiaxin.cache.producer.command;

import com.netflix.hystrix.*;

/**
 * Created by 李雷 on 2017/11/2.
 */
public class GetCityNameCommand extends HystrixCommand<String>{

    private Long cityId;

    public GetCityNameCommand(Long cityId){
        //定义线程组，Hystrix是通过command group来定义一个线程池
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetCityNameGroup"))
                //用来细粒度的区分调用线程
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetCityNameCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetCityNamePool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        //基于信号量的资源隔离
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(15)));

        this.cityId = cityId;
    }

    @Override
    protected String run() throws Exception {
        return "北京";
    }
}
