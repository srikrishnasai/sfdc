package com.tadigital.sfdc_campaign.service;

/**
 * This interface has a method which decrypts the bean to its original format and is overridded in its implemented class.
 * @author saikrishna.sp
 *
 */
public interface BeanDecryption {

	public Object decrypt(Object o);
	
}
