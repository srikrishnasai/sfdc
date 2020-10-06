package com.tadigital.sfdc_campaign.service;

/**
 * This interface has a method which encrypts the bean from its original format and is overridded in its implemented class.
 * @author saikrishna.sp
 *
 */
public interface BeanEncryption {

	public Object encrypt(Object o);
	
}
