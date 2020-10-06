/**
 * 
 */
package com.tadigital.sfdc_campaign.model;

/**
 * @author nivedha.g
 *
 */
public class AnalyticsJsonModel {
	
	String rsid;
	
	String dimensioniId;
	
	String metricId;
	

	/**
	 * @return the rsid
	 */
	public String getRsid() {
		return rsid;
	}

	/**
	 * @param rsid the rsid to set
	 */
	public String setRsid(String rsid) {
		this.rsid = "geo1xxpnwtatraining";
		return rsid;
	}

	/**
	 * @return the dimensioniId
	 */
	public String getDimensioniId() {
		return dimensioniId;
	}

	/**
	 * @param dimensioniId the dimensioniId to set
	 */
	public String setDimensioniId(String dimensioniId) {
		this.dimensioniId = "variables/prop5";
		return dimensioniId;
	}

	/**
	 * @return the metricId
	 */
	public String getMetricId() {
		return metricId;
	}

	/**
	 * @param metricId the metricId to set
	 */
	public String setMetricId(String metricId) {
		this.metricId = "metrics/occurrences";
		return metricId;
	}

	
	 
	
	
	/*{
"rsid": "geo1xxpnwtatraining",
"globalFilters": [
{
"type": "dateRange",
"dateRange": "2019-05-01T00:00:00.000/2019-06-01T00:00:00.000"
}
],
"metricContainer": {
"metrics": [
{
"columnId": "0",
"id": "metrics/occurrences",
"sort": "desc"
}
]
},
"dimension": "variables/prop5",
"settings": {
"countRepeatInstances": true,
"limit": 50,
"page": 0
}
}*/

}
