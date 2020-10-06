/**
 * 
 */
package com.tadigital.sfdc_campaign.threads;

import com.tadigital.sfdc_campaign.service.AdobeAnalytics;

/**
 * @author nivedha.g
 *
 */
public class AdobeAnalyticsThread implements Runnable{
	String acsConfigName;
	String salesforceConfigName;
    StringBuilder logresult;
    int logid;
    String rsid;
    String dimensionId;
    String startDate;
    String endDate;
	/**
	 * 
	 */
	public AdobeAnalyticsThread(String acsConfigName, String salesforceConfigName, String rsid, String dimensionId, StringBuilder logresult, int logid,String startDate, String endDate) {
		this.acsConfigName = acsConfigName;
		this.salesforceConfigName = salesforceConfigName;
		this.logresult = logresult;
		this.logid = logid;
		this.rsid = rsid;
		this.dimensionId = dimensionId;
		this.startDate = startDate;
		this.endDate = endDate;
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		AdobeAnalytics aa = new AdobeAnalytics();
		 aa.analyticsReport(acsConfigName,salesforceConfigName,rsid,dimensionId,logresult,logid,startDate,endDate);
	}

}
