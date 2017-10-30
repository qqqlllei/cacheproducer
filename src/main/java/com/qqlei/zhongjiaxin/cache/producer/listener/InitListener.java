package com.qqlei.zhongjiaxin.cache.producer.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.qqlei.zhongjiaxin.cache.producer.kafka.KafkaConsumer;
import com.qqlei.zhongjiaxin.cache.producer.rebuild.RebuildCacheThread;
import com.qqlei.zhongjiaxin.cache.producer.spring.SpringContext;
import com.qqlei.zhongjiaxin.cache.producer.zk.ZooKeeperSession;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 系统初始化的监听器
 * @author Administrator
 *
 */
public class InitListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext sc = sce.getServletContext();
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(sc);
		SpringContext.setApplicationContext(context);
		
		new Thread(new KafkaConsumer("cache-message")).start();
		new Thread(new RebuildCacheThread()).start();
		ZooKeeperSession.init();
	}
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}

}
