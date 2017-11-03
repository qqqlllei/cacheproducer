package com.qqlei.zhongjiaxin.cache.producer.filter;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

import javax.servlet.*;
import java.io.IOException;

/**
 * hystrix请求上下文过滤器
 * Created by 李雷 on 2017/11/2.
 */
public class HystrixRequestContextFilter implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            filterChain.doFilter(servletRequest,servletResponse);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            context.shutdown();
        }


    }

    @Override
    public void destroy() {

    }
}
