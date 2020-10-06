package com.tadigital.sfdc_campaign.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author srividya.b
 *
 */
@Service
public class BeanUtil implements ApplicationContextAware {

	private static ApplicationContext context;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationContextAware#setApplicationContext(org
	 * .springframework.context.ApplicationContext)
	 */
	
	@Override
	public synchronized void setApplicationContext(ApplicationContext applicationContext){
		context = applicationContext;
	}

	public static <T> T getBean(Class<T> beanClass) {
		return context.getBean(beanClass);
	}

}
