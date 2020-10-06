package com.tadigital.sfdc_campaign.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tadigital.sfdc_campaign.model.ACSConfig;
import com.tadigital.sfdc_campaign.service.FieldsPersistance;

public class AcsMetadataThread implements Runnable{

	Logger logger = LoggerFactory.getLogger(AcsMetadataThread.class);
	private ACSConfig acsConfig;
	
	public AcsMetadataThread(ACSConfig acsConfig) {
		
		this.acsConfig = acsConfig;
		
	}
	
	@Override
	public void run() {
		FieldsPersistance fieldsPersistance = new FieldsPersistance();
		fieldsPersistance.getAndSaveCampaignFields(acsConfig);
		
	}

}
