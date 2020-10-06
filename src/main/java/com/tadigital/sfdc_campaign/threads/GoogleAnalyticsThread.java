/**
 * 
 */
package com.tadigital.sfdc_campaign.threads;

import java.io.InputStream;

import com.tadigital.sfdc_campaign.service.CampaignAnalyticsReporting;

/**
 * @author nivedha.g
 *
 */
public class GoogleAnalyticsThread implements Runnable{
	InputStream in;
    String salesConfigName;
    StringBuilder logresult;
    int logid;
    String viewId;
    String startDate;
    String endDate;
	/**
	 * 
	 */
	public GoogleAnalyticsThread(InputStream client_secret_file, String salesConfigName, StringBuilder logresult, int logid,String viewId,String startDate,String endDate) {
		this.in = client_secret_file;
		this.salesConfigName = salesConfigName;
		this.logresult = logresult;
		this.logid = logid;
		this.endDate = endDate;
		this.startDate = startDate;
		this.viewId = viewId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		CampaignAnalyticsReporting car = new CampaignAnalyticsReporting();
		 car.report(in,salesConfigName, logresult, logid,viewId,startDate,endDate);
	}

}
