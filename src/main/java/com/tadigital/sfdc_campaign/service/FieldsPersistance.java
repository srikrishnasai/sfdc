package com.tadigital.sfdc_campaign.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tadigital.sfdc_campaign.constants.StringConstants;
import com.tadigital.sfdc_campaign.model.ACSConfig;
import com.tadigital.sfdc_campaign.model.FieldsBean;
import com.tadigital.sfdc_campaign.model.SFDCConfig;
import com.tadigital.sfdc_campaign.repo.FieldsRepo;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.tadigital.sfdc_campaign.utils.JWTManager;

/**
 * 
 * @author saikrishna.sp
 *
 */
@Service
public class FieldsPersistance implements BeanDecryption {

	/** The log. */
	Logger logger = LoggerFactory.getLogger(SFDCConnector.class);

	HttpClient client;
	FieldsBean fieldBean;
	FieldsRepo fieldRepo;
	String baseUri;
	BasicHeader oauthHeader;
	BasicHeader prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");
	SFDCConfig sfObj;
	HttpEntity httpEntity;

	public FieldsPersistance() {

		client = HttpClientBuilder.create().build();

	}

	public void getSalesFields(SFDCConfig salesConfig) {

		this.fieldRepo = BeanUtil.getBean(FieldsRepo.class);
		sfObj = salesConfig;
		String loginURL = StringConstants.SALESFORCELOGINURL + StringConstants.SALESFORCEGRANTSERVICE + "&client_id="
				+ sfObj.getClientId() + "&client_secret=" + sfObj.getClientSecret() + "&username=" + sfObj.getUserName()
				+ "&password=" + sfObj.getSfdcPassword() + sfObj.getSecretToken();
		logger.debug("loginURL::{}" , loginURL);

		// Login requests must be POSTs
		HttpPost httpPost = new HttpPost(loginURL);
		HttpResponse loginResponse = null;

		try {
			loginResponse = client.execute(httpPost);
			logger.debug("login reponse ::{}", loginResponse);
		} catch (ClientProtocolException cpException) {
			
			logger.info("Exception is::{}",cpException.getMessage());
		} catch (IOException ioException) {
			
			logger.info("Exception is::{}",ioException.getMessage());
		}

		// verify response is HTTP OK
		
		

		String getResult = null;
		try {
			httpEntity = null != loginResponse ? loginResponse.getEntity() : null;
			getResult = null != httpEntity ? EntityUtils.toString(httpEntity) : "";
		} catch (IOException ioException) {
			logger.info("Exception is::{}",ioException.getMessage());
		}
		JSONObject jsonObject = null;
		String loginAccessToken = null;
		String loginInstanceUrl = null;
		try {
			jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
			logger.debug("RESPONSE----::{}", jsonObject);
			loginAccessToken = jsonObject.getString("access_token");
			loginInstanceUrl = jsonObject.getString("instance_url");
		} catch (Exception jsonException) {
			
			logger.info("Exception is::{}",jsonException.getMessage());
		}

		baseUri = loginInstanceUrl + StringConstants.SALESFORCERESTENDPOINT + StringConstants.SALESFORCEAPIVERSION;
		oauthHeader = new BasicHeader("Authorization", "OAuth " + loginAccessToken);

		logger.debug("oauthHeader1:{} " ,oauthHeader);
		logger.debug("Successful login");
		logger.debug(" instance URL: {}" ,loginInstanceUrl);
		logger.debug("  access token/session ID:{} " , loginAccessToken);
		logger.debug("baseUri: {}" , baseUri);

		getLeadFields();
		getContactFields();
		getOpportunityFields();

	}

	public void getLeadFields() {

		try {
			String getURL = StringConstants.SFLEADFIELDSURL;

			HttpGet get = new HttpGet(getURL);
			get.addHeader(oauthHeader);
			get.addHeader(prettyPrintHeader);
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			String leadFieldResponse = EntityUtils.toString(resEntityGet);
			JSONObject respObj = new JSONObject(leadFieldResponse);
			JSONArray fieldArray = respObj.getJSONArray("fields");
			for (int i = 0; i < fieldArray.length(); i++) {
				JSONObject current = fieldArray.getJSONObject(i);
				
				fieldBean = new FieldsBean();
				fieldBean.setFieldName(current.get("name").toString());
				fieldBean.setFieldType(current.get("type").toString());
				fieldBean.setRepoType("sf");
				fieldBean.setUserId(sfObj.getUserid());
				
				fieldBean.setBelongs("Lead");
                fieldBean.setSalesconfigid(sfObj.getConfigId());
				fieldRepo.save(fieldBean);
			}
			fieldBean = new FieldsBean();
			fieldBean.setFieldName("type");
			fieldBean.setFieldType("String");
			fieldBean.setRepoType("sf");
			fieldBean.setUserId(sfObj.getUserid());
			
			fieldBean.setBelongs("Lead");
			fieldBean.setSalesconfigid(sfObj.getConfigId());
			fieldRepo.save(fieldBean);
			

		} catch (Exception e) {

			logger.info("Exception is::{}",e.getMessage());
		}
	}

	public void getContactFields() {

		try {
			String getURL = StringConstants.SFCONTACTFIELDSURL;
			HttpGet get = new HttpGet(getURL);
			get.addHeader(oauthHeader);
			get.addHeader(prettyPrintHeader);
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			String contactFieldResponse = EntityUtils.toString(resEntityGet);
			JSONObject respObj = new JSONObject(contactFieldResponse);
			JSONArray fieldArray = respObj.getJSONArray("fields");
			for (int i = 0; i < fieldArray.length(); i++) {
				JSONObject current = fieldArray.getJSONObject(i);
				
				fieldBean = new FieldsBean();
				fieldBean.setFieldName(current.get("name").toString());
				fieldBean.setFieldType(current.get("type").toString());
				fieldBean.setRepoType("sf");
				fieldBean.setUserId(sfObj.getUserid());
				
				fieldBean.setBelongs("Contact");
				fieldBean.setSalesconfigid(sfObj.getConfigId());
				fieldRepo.save(fieldBean);
			}
			fieldBean = new FieldsBean();
			fieldBean.setFieldName("type");
			fieldBean.setFieldType("String");
			fieldBean.setRepoType("sf");
			fieldBean.setUserId(sfObj.getUserid());
			
			fieldBean.setBelongs("Contact");
			fieldBean.setSalesconfigid(sfObj.getConfigId());
			fieldRepo.save(fieldBean);

		} catch (Exception e) {

			logger.info("Exception is::{}",e.getMessage());
		}
	}

	public void getOpportunityFields() {

		try {
			String getURL = StringConstants.SFOPPORTUNITYFIELDSURL;
			HttpGet get = new HttpGet(getURL);
			get.addHeader(oauthHeader);
			get.addHeader(prettyPrintHeader);
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			String opportunityFieldResponse = EntityUtils.toString(resEntityGet);
			JSONObject respObj = new JSONObject(opportunityFieldResponse);
			JSONArray fieldArray = respObj.getJSONArray("fields");
			for (int i = 0; i < fieldArray.length(); i++) {
				JSONObject current = fieldArray.getJSONObject(i);
				fieldBean = new FieldsBean();
				fieldBean.setFieldName(current.get("name").toString());
				fieldBean.setFieldType(current.get("type").toString());
				fieldBean.setRepoType("sf");
				fieldBean.setUserId(sfObj.getUserid());
				fieldBean.setBelongs("Opportunity");
				fieldBean.setSalesconfigid(sfObj.getConfigId());
				fieldRepo.save(fieldBean);
			}

		} catch (Exception e) {
			logger.info("Exception is::{}",e.getMessage());
		}
	}

	public void getAndSaveCampaignFields(ACSConfig acsObj) {
		
		try {
			String getURL = StringConstants.ACSFETCHFIELDSURL;
			HttpGet get = new HttpGet(getURL);
			get.setHeader("Content-Type", "application/json");
			get.setHeader("X-Api-Key", acsObj.getClientId());
			String accessToken = new JWTManager().create(acsObj.getClientId(), acsObj.getClientSecret(),
					acsObj.getOrganizationId(), acsObj.getTechAccountId());
			if (accessToken != null)
				get.setHeader("Authorization", "Bearer " + accessToken);
			else
				throw new Exception("AccessToken Not Valid");
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			String responseString = EntityUtils.toString(resEntityGet);
			JSONObject respObj = new JSONObject(responseString);
			JSONObject contentObj = respObj.getJSONObject("content");
			Iterator<String> keys = contentObj.keys();
			this.fieldRepo = BeanUtil.getBean(FieldsRepo.class);
			while (keys.hasNext()) {
				fieldBean = new FieldsBean();
				String key = keys.next();
				JSONObject current = contentObj.getJSONObject(key);
				fieldBean.setFieldName(key);
				fieldBean.setFieldType(current.get("type").toString());
				fieldBean.setRepoType("acs");
				fieldBean.setUserId(acsObj.getUserid());
				fieldBean.setBelongs("acs");
				fieldBean.setAcsconfigid(acsObj.getConfigId());
				fieldRepo.save(fieldBean);

			}

		} catch (Exception e) {

			logger.info("Exception is::{}",e.getMessage());
		}

	}

	@Override
	public Object decrypt(Object acsObj) {
		ACSConfig acsConfig = new ACSConfig();
		acsConfig.setUserid(((ACSConfig) acsObj).getUserid());
		acsConfig.setCheckstatus(((ACSConfig) acsObj).getCheckstatus());
		acsConfig.setConfigId(((ACSConfig) acsObj).getConfigId());
		try {
			acsConfig.setClientId(new String(Base64.getDecoder().decode(((ACSConfig) acsObj).getClientId()), "utf-8"));
			acsConfig.setClientSecret(
					new String(Base64.getDecoder().decode(((ACSConfig) acsObj).getClientSecret()), "utf-8"));
			acsConfig.setOrganizationId(
					new String(Base64.getDecoder().decode(((ACSConfig) acsObj).getOrganizationId()), "utf-8"));
			acsConfig.setTechAccountId(
					new String(Base64.getDecoder().decode(((ACSConfig) acsObj).getTechAccountId()), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.info("Exception is::{}",e.getMessage());
		}

		return acsConfig;
	}

}
