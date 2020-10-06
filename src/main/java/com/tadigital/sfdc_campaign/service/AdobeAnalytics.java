/**
 * 
 */
package com.tadigital.sfdc_campaign.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.tadigital.sfdc_campaign.constants.VaadinConstants;
import com.tadigital.sfdc_campaign.model.ACSConfig;
import com.tadigital.sfdc_campaign.model.Campaign;
import com.tadigital.sfdc_campaign.model.CampaignMember;
import com.tadigital.sfdc_campaign.model.SFLead;
import com.tadigital.sfdc_campaign.repo.ACSConfigRepo;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.tadigital.sfdc_campaign.utils.JWTManager;
import com.tadigital.sfdc_campaign.utils.SchLogSave;
import com.vaadin.flow.server.VaadinSession;

/**
 * @author nivedha.g
 *
 */
@Service
public class AdobeAnalytics implements BeanDecryption {
	Logger logger = LoggerFactory.getLogger(AdobeAnalytics.class);

	@Autowired
	private ACSConfigRepo acsRepo;
	Gson gson;
	HttpResponse analyticsResponse;

	static SFDCConnector sfdcConnector;
	
	StringBuilder logResult;
	int logId;
	String endStatus;
	
	SchLogSave schLogSave = new SchLogSave();

	ACSConfig acsDb;
	HttpClient client = HttpClientBuilder.create().build();

	String openBracket = "{";
	String closeBracket = "}";
	String openSquareBracket = "[";
	String closeSquareBracket = "]";
	/*
	 * String rsid = "geo1xxpnwtatraining"; String dimensionId = "variables/prop11";
	 */
	String metricId = "metrics/occurrences";

	static List<String> campaignMembersEmailIds = new ArrayList<>();

	public String getJson(String rsid, String dimensionId,String startDate, String endDate) {
		System.out.println("inside get json  ");
		StringBuilder builder = new StringBuilder();
		String dateRange = startDate+"T00:00:00.000/"+endDate+"T00:00:00.000" ;

		builder.append(openBracket + " \"rsid\" : " + " \"" + rsid + "\" ,");
		// builder.append(openBracket+"\"rsid\":"+"\""+rsid+"\",");
		builder.append("\"globalFilters\" :" + openSquareBracket);
		builder.append(openBracket);
		builder.append(" \"type\": \"dateRange\" ,");
		builder.append(" \"dateRange\":"+"\""+dateRange+"\"");
		builder.append(closeBracket);
		builder.append(closeSquareBracket);
		builder.append(",");
		builder.append(" \"metricContainer\" : " + openBracket);
		builder.append(" \"metrics\" : " + openSquareBracket);
		builder.append(openBracket);
		builder.append(" \"columnId\" : \"0\" ,");
		builder.append(" \"id\" : \"" + metricId + "\" ,");
		builder.append(" \"sort\" : \"desc \"");
		builder.append(closeBracket);
		builder.append(closeSquareBracket);
		builder.append(closeBracket);
		builder.append(",");
		builder.append(" \"dimension\" : \"" + dimensionId + "\" ,");
		builder.append(" \"search\" : " + openBracket);
		builder.append(" \"clause\" : \" ( CONTAINS '@' )\" ");
		builder.append(closeBracket);
		builder.append(",");
		builder.append(" \"settings\" : " + openBracket);
		builder.append(" \"countRepeatInstances\" : \"true\" ,");
		builder.append(" \"limit\" : \"100\" ,");
		builder.append(" \"page\" : \"0\" ");
		builder.append(closeBracket);
		builder.append(closeBracket);
		System.out.println(builder);
		return builder.toString();
	}

	public String analyticsReport(String acsConfigName, String salesforceConfigName, String rsid, String dimensionId, StringBuilder logresult, int logid,String startDate, String endDate) {
		sfdcConnector = BeanUtil.getBean(SFDCConnector.class);
		logResult = logresult;
		logId = logid;
		acsRepo = BeanUtil.getBean(ACSConfigRepo.class);
		acsDb = acsRepo.findByConfignameAndUserid(acsConfigName,
				(int) VaadinSession.getCurrent().getAttribute(VaadinConstants.USERID));
		acsDb = (ACSConfig) decrypt(acsDb);
		
		logResult.append("Connecting to Adobe analytics to fetch the report data "+"<br>");
		
		final String analyticsReportUrl = "https://analytics.adobe.io/api/techas3/reports";
		HttpPost post = new HttpPost(analyticsReportUrl);
		post.setHeader("Content-Type", "application/json");
		post.setHeader("X-Proxy-global-company-Id", "techas3");
		post.setHeader("X-Api-Key", acsDb.getClientId());

		String accessToken;
		try {
			accessToken = new JWTManager().create(acsDb.getClientId(), acsDb.getClientSecret(),
					acsDb.getOrganizationId(), acsDb.getTechAccountId());

			if (accessToken != null)
				post.setHeader("Authorization", "Bearer " + accessToken);
			else {
				schLogSave.saveEndStatus("Failure", logId);
				throw new Exception("AccessToken Not Valid");
				

			}
			StringEntity params = new StringEntity(getJson(rsid, dimensionId,startDate,endDate));
			post.setEntity(params);
			analyticsResponse = client.execute(post);
			HttpEntity resEntityGet = analyticsResponse.getEntity();
			String responseString = EntityUtils.toString(resEntityGet);
			System.out.println(responseString);
			JSONObject json = new JSONObject(responseString);
			JSONArray result = json.getJSONArray("rows");
			String leadId = null;
			String status = "responded";
			logResult.append("Connected to adobe analytics and fetched the required data "+"<br>");
			Map<String, String> idMailMap = new HashMap<String, String>();
			CampaignMember camp = new CampaignMember();
			for (int i = 0; i < result.length(); i++) {
				String values = result.getJSONObject(i).getString("value");
				String category = values.split("\\:")[1];
				String campaignName = values.split("\\:")[2];
				if (category.equals("emailopen")) {
					status = "sent";
				}
				String email = values.split("\\:")[0];
				SFLead sfLead = new SFLead();
				sfLead.setEmail(email);
				Campaign campaignObj = new Campaign();
				campaignObj.setName(campaignName);
				String campaignId = sfdcConnector.creatCampaign(campaignObj, salesforceConfigName);
				campaignMembersEmailIds.add(sfLead.getEmail());
				leadId = sfdcConnector.checkIfLeadExistsForCampaign(sfLead, 1, salesforceConfigName);
				camp.setCampaignId(campaignId);
				camp.setStatus(status);
				camp.setLeadId(leadId);
				idMailMap.put(leadId, email);
				sfdcConnector.createCampaignMember(campaignMembersEmailIds, camp, salesforceConfigName,
						idMailMap.get(leadId));	
			}
			logResult.append("Connected to salesforce and updated campaigns data accordingly"+"<br>");

		} catch (Exception e) {
			schLogSave.saveEndStatus("Failure", logId);
			e.printStackTrace();
		}
		
		String endStatus = schLogSave.getEndStatus(logId);
		if (endStatus == null) {
			schLogSave.saveEndStatus("Success", logId);
		}
		schLogSave.saveLogResult(logResult.toString(), logId);
		
		return analyticsResponse.toString();
	}

	@Override
	public Object decrypt(Object acsObj) {
		ACSConfig acsConfig = new ACSConfig();
		acsConfig.setUserid(((ACSConfig) acsObj).getUserid());
		acsConfig.setCheckstatus(((ACSConfig) acsObj).getCheckstatus());
		acsConfig.setConfigId(((ACSConfig) acsObj).getConfigId());
		acsConfig.setConfigname(((ACSConfig) acsObj).getConfigname());
		try {
			acsConfig.setClientId(new String(Base64.getDecoder().decode(((ACSConfig) acsObj).getClientId()), "utf-8"));
			acsConfig.setClientSecret(
					new String(Base64.getDecoder().decode(((ACSConfig) acsObj).getClientSecret()), "utf-8"));
			acsConfig.setOrganizationId(
					new String(Base64.getDecoder().decode(((ACSConfig) acsObj).getOrganizationId()), "utf-8"));
			acsConfig.setTechAccountId(
					new String(Base64.getDecoder().decode(((ACSConfig) acsObj).getTechAccountId()), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.info("Exception is::{}", e.getMessage());
		}

		return acsConfig;
	}

}
