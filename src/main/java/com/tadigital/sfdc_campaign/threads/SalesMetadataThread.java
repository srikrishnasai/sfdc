package com.tadigital.sfdc_campaign.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tadigital.sfdc_campaign.model.SFDCConfig;
import com.tadigital.sfdc_campaign.service.FieldsPersistance;

public class SalesMetadataThread implements Runnable{

	Logger LOG = LoggerFactory.getLogger(SalesMetadataThread.class);
	private SFDCConfig sfdcConfig;
	
	public SalesMetadataThread(SFDCConfig sfdcConfig) {
		
		this.sfdcConfig = sfdcConfig;
		
	}
	
	
	@Override
	public void run() {
		System.out.println("Entered into thread "+sfdcConfig.toString());
		FieldsPersistance fieldPersistance = new FieldsPersistance();
		fieldPersistance.getSalesFields(sfdcConfig);
		
	}

}
