package com.tadigital.sfdc_campaign.constants;

/**
 * @author nivedha.g
 *
 */
public class StringConstants {
	
	public static final String SALESFORCELOGINURL = "https://login.salesforce.com";
	public static final String SALESFORCEGRANTSERVICE = "/services/oauth2/token?grant_type=password";
	public static final String SALESFORCERESTENDPOINT = "/services/data";
	public static final String SALESFORCEAPIVERSION = "/v30.0";
	public static final String ACSCREATEPROFILEURL = "https://mc.adobe.io/acs322us.campaign-sandbox.adobe.com/campaign/profileAndServicesExt/profile";
	//public static final String ACSFETCHPROFILEURL = "https://mc.adobe.io/acs322us.campaign-sandbox.adobe.com/campaign/profileAndServicesExt/profile/byEmail?email=";
	public static final String ACSFETCHPROFILEURL = "https://mc.adobe.io/acs322us.campaign-sandbox.adobe.com/campaign/profileAndServicesExt/profile/byFirstname/byLastname?firstName_parameter=";
	public static final String ACSUPDATEPROFILEURL = "https://mc.adobe.io/acs322us.campaign-sandbox.adobe.com/campaign/profileAndServicesExt/profile/";
	public static final String ADOBEIOACCESSTOKENURL = "ims-na1.adobelogin.com";
	public static final String ADOBEIOJWTTOKENURL = "/ims/exchange/jwt/";
	public static final String ACSSFTYPEFETCHURL = "https://mc.adobe.io/acs322us.campaign-sandbox.adobe.com/campaign/profileAndServicesExt/profile/bySftype?cusSfdcCustomType_parameter=";
	public static final String ACSFETCHFIELDSURL = "https://mc.adobe.io/acs322us.campaign-sandbox.adobe.com/campaign/profileAndServicesExt/resourceType/profile";
	public static final String SFLEADFIELDSURL = "https://ap4.salesforce.com/services/data/v30.0/sobjects/Lead/describe";
	public static final String SFCONTACTFIELDSURL = "https://ap4.salesforce.com/services/data/v30.0/sobjects/Contact/describe";
	public static final String SFOPPORTUNITYFIELDSURL = "https://ap4.salesforce.com/services/data/v30.0/sobjects/Opportunity/describe";
	public static final String STATUS_ACTIVE = "Active";
	public static final String STATUS_INACTIVE = "Inactive";
	public static final String ACSWORKFLOWURL = "https://mc.adobe.io/acs322us.campaign-sandbox.adobe.com/campaign/workflow/execution/srk_camp_test/commands";
	public static final String CAMPAIGN = "https://ap4.salesforce.com/services/data/v39.0/sobjects/campaign";
}

