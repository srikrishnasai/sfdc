/**
 * 
 */
package com.tadigital.sfdc_campaign.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.tadigital.sfdc_campaign.constants.StringConstants;
import com.tadigital.sfdc_campaign.model.Campaign;
import com.tadigital.sfdc_campaign.model.CampaignMember;
import com.tadigital.sfdc_campaign.model.SFContact;
import com.tadigital.sfdc_campaign.model.SFDCConfig;
import com.tadigital.sfdc_campaign.model.SFLead;
import com.tadigital.sfdc_campaign.model.SchedulerRunsLog;
import com.tadigital.sfdc_campaign.repo.SFDCConfigRepo;
import com.tadigital.sfdc_campaign.repo.SchedulerRepo;
import com.tadigital.sfdc_campaign.repo.SchedulerRunsLogRepo;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.tadigital.sfdc_campaign.utils.ReadPropertiesFile;
import com.tadigital.sfdc_campaign.utils.SchLogSave;

/**
 * @author nivedha.g
 *
 */

@Service
public class SFDCConnector implements BeanDecryption {

	/** The log. */
	Logger logger = LoggerFactory.getLogger(SFDCConnector.class);

	SFDCConfigRepo sfdcRepo;
	SchedulerRepo schRepo;
	SchedulerRunsLogRepo schLogRepo;
	SchedulerRunsLog schLog = new SchedulerRunsLog();

	private static String baseUri;
	private static BasicHeader oauthHeader;
	private static BasicHeader prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");
	String responseString = "";
	JSONObject jsonLead;
	JsonElement element;
	String resp = "";
	String campaignId = null;
	HttpClient httpClient = HttpClientBuilder.create().build();
	ReadPropertiesFile readString = new ReadPropertiesFile();
	Gson gson = new Gson();
	StringBuilder logResult;
	int runId;
	String connectionDetails = "";
	SchLogSave schLogSave = new SchLogSave();

	public void createLead(int userid, SFLead sflead, StringBuilder logresult, int runid, String salesConfig)
			throws IOException {

		logResult = logresult;
		runId = runid;
		this.schRepo = BeanUtil.getBean(SchedulerRepo.class);
		this.sfdcRepo = BeanUtil.getBean(SFDCConfigRepo.class);
		this.schLogRepo = BeanUtil.getBean(SchedulerRunsLogRepo.class);

		SFDCConfig sfdcdb = sfdcRepo.findByConfigname(salesConfig);
		sfdcdb = (SFDCConfig) decrypt(sfdcdb);
		connectionDetails = salesConnection(sfdcdb);

		baseUri = connectionDetails.split("\\+")[0];
		oauthHeader = new BasicHeader("Authorization", "OAuth " + connectionDetails.split("\\+")[1]);

		boolean status = checkIfLeadExists(sflead, userid, connectionDetails);
		if (!status) {

			try {
				String createLeadUrl = baseUri + readString.getProperty("CreateLead");
				logResult.append("URL to create lead in salesforce" + createLeadUrl + "<br>");
				HttpPost post = new HttpPost(createLeadUrl);
				post.setHeader(oauthHeader);
				JSONObject leadObj = new JSONObject(gson.toJson(sflead));
				leadObj.remove("type");
				StringEntity params = new StringEntity(leadObj.toString());
				post.setEntity(params);
				post.setHeader("Content-Type", "application/json");
				HttpResponse createLeadResponse = httpClient.execute(post);
				HttpEntity leadResEntity = createLeadResponse.getEntity();
				String responseString = EntityUtils.toString(leadResEntity);
				logger.debug("this is lead creation response ::{}", responseString);
				logResult.append(sflead.getFirstName() + " " + sflead.getLastName() + " ");
				logResult.append("Lead created successfully in salesforce" + "<br>");
			} catch (Exception e) {
				schLogSave.saveEndStatus("Failure", runId);
				logger.error("Exception occured is ::{}", e);
			}
			schLogSave.saveLogResult(logResult.toString(), runId);

		}
	}

	public boolean checkIfLeadExists(SFLead sflead, int userid, String connectionDetails) throws IOException {

		baseUri = connectionDetails.split("\\+")[0];
		oauthHeader = new BasicHeader("Authorization", "OAuth " + connectionDetails.split("\\+")[1]);

		String checkLeadUrl = baseUri + readString.getProperty("CheckLeadQuery") + sflead.getEmail();
		HttpGet get = new HttpGet(checkLeadUrl);
		logResult.append("URL to fetch lead data by Email" + checkLeadUrl + "<br>");
		get.addHeader(oauthHeader);
		get.addHeader(prettyPrintHeader);

		HttpResponse leadExistsResponse = httpClient.execute(get);
		HttpEntity leadResEntity = leadExistsResponse.getEntity();
		String responseString = EntityUtils.toString(leadResEntity);
		logger.debug("responseString ::{}", responseString);
		logResult.append("responseString->" + responseString + "<br>");
		Object data = new JSONTokener(responseString).nextValue();
		if (data instanceof JSONObject) {
			logger.debug("data ::{}", data);
			logResult.append(sflead.getFirstName() + " " + sflead.getLastName() + " ");
			logResult.append("Lead already exists, so updating the existing Lead" + "<br>");
			String id = ((JSONObject) data).get("Id").toString();
			updateIfLeadExists(sflead, userid, id, connectionDetails);
			return true;
		}
		if (data instanceof JSONArray) {
			logger.debug("data ::{}", data);
			logResult.append(sflead.getFirstName() + " " + sflead.getLastName() + " ");
			logResult.append("Lead does not exixts so creating the required Lead" + "<br>");
			return false;
		}
		schLogSave.saveLogResult(logResult.toString(), runId);
		return false;
	}

	public void updateIfLeadExists(SFLead sflead, int userid, String id, String connectionDetails)
			throws UnsupportedEncodingException {

		baseUri = connectionDetails.split("\\+")[0];
		oauthHeader = new BasicHeader("Authorization", "OAuth " + connectionDetails.split("\\+")[1]);

		String updateLeadUrl = baseUri + readString.getProperty("LeadData") + id;
		logResult.append("URL for updating lead" + updateLeadUrl + "<br>");
		HttpPatch patch = new HttpPatch(updateLeadUrl);
		patch.setHeader("Content-Type", "application/json");
		patch.setHeader(oauthHeader);
		StringEntity params;
		JSONObject leadObj = new JSONObject(gson.toJson(sflead));
		leadObj.remove("type");
		params = new StringEntity(leadObj.toString());
		patch.setEntity(params);
		HttpResponse patchResponse;
		try {
			patchResponse = httpClient.execute(patch);
			/*
			 * HttpEntity resEntityGet = patchResponse.getEntity(); String responseString =
			 * EntityUtils.toString(resEntityGet);
			 */
			logger.debug("this is lead update response ::{}", responseString);
			logResult.append(
					"Updated " + sflead.getFirstName() + " " + sflead.getLastName() + " lead successfully" + "<br>");
		} catch (IOException e) {
			schLogSave.saveEndStatus("Failure", runId);
			logger.error("IOException occured ::{}", e);
		}
		schLogSave.saveLogResult(logResult.toString(), runId);

	}

	public void createContact(int userid, SFContact sfContact, StringBuilder logresult, int runid, String salesConfig)
			throws IOException {

		logResult = logresult;
		runId = runid;
		this.schRepo = BeanUtil.getBean(SchedulerRepo.class);
		this.sfdcRepo = BeanUtil.getBean(SFDCConfigRepo.class);

		SFDCConfig sfdcdb = sfdcRepo.findByConfigname(salesConfig);
		sfdcdb = (SFDCConfig) decrypt(sfdcdb);
		connectionDetails = salesConnection(sfdcdb);

		baseUri = connectionDetails.split("\\+")[0];
		oauthHeader = new BasicHeader("Authorization", "OAuth " + connectionDetails.split("\\+")[1]);
		boolean status = checkIfContactExists(sfContact, userid, connectionDetails);
		if (!status) {
			try {
				String createContactUrl = baseUri + readString.getProperty("CreateContact");
				HttpPost post = new HttpPost(createContactUrl);
				logResult.append(sfContact.getFirstName() + " ");
				logResult.append("URL to create contact in salesforce " + createContactUrl + "<br>");
				post.setHeader(oauthHeader);
				JSONObject contactObj = new JSONObject(gson.toJson(sfContact));
				contactObj.remove("type");
				StringEntity params = new StringEntity(contactObj.toString());
				post.setEntity(params);
				post.setHeader("Content-Type", "application/json");
				HttpResponse createContactResponse = httpClient.execute(post);
				HttpEntity leadResEntity = createContactResponse.getEntity();
				String responseString = EntityUtils.toString(leadResEntity);
				logger.debug("this is contact creation response ::{}", responseString);
				logResult.append("successfully created " + sfContact.getFirstName() + " " + sfContact.getLastName()
						+ " contact in salesforce" + "<br>");
			} catch (Exception e) {
				schLogSave.saveEndStatus("Failure", runId);
				logger.error("Exception occured is ::{}", e);
			}
			schLogSave.saveLogResult(logResult.toString(), runId);
		}

	}

	public boolean checkIfContactExists(SFContact sfContact, int userid, String connectionDetails) throws IOException {

		baseUri = connectionDetails.split("\\+")[0];
		oauthHeader = new BasicHeader("Authorization", "OAuth " + connectionDetails.split("\\+")[1]);
		String checkContactUrl = baseUri + readString.getProperty("CheckContactQuery") + sfContact.getEmail();
		HttpGet get = new HttpGet(checkContactUrl);
		logResult.append("URL to fetch contact in salesforce by Email " + checkContactUrl + "<br>");
		get.addHeader(oauthHeader);
		get.addHeader(prettyPrintHeader);

		HttpResponse contactExistsResponse = httpClient.execute(get);
		HttpEntity leadResEntity = contactExistsResponse.getEntity();
		String responseString = EntityUtils.toString(leadResEntity);
		logger.debug("responseString ::{}", responseString);
		Object data = new JSONTokener(responseString).nextValue();
		if (data instanceof JSONObject) {
			logger.debug("data ::{}", data);
			logResult.append(sfContact.getFirstName() + " " + sfContact.getLastName() + " ");
			logResult.append("Contact already exists so updating the existing Contact in salesforce" + "<br>");
			String id = ((JSONObject) data).get("Id").toString();
			updateIfContactExists(sfContact, userid, id, connectionDetails);
			return true;
		}
		if (data instanceof JSONArray) {
			logger.debug("data ::{}", data);
			logResult.append(sfContact.getFirstName() + " " + sfContact.getLastName() + " ");
			logResult.append("contact does not exists so creating the required contact in salesforce" + "<br>");
			return false;
		}
		schLogSave.saveLogResult(logResult.toString(), runId);
		return false;
	}

	public void updateIfContactExists(SFContact sfContact, int userid, String id, String connectionDetails)
			throws UnsupportedEncodingException {

		baseUri = connectionDetails.split("\\+")[0];
		oauthHeader = new BasicHeader("Authorization", "OAuth " + connectionDetails.split("\\+")[1]);
		String updateContactUrl = baseUri + readString.getProperty("ContactData") + id;
		logResult.append(sfContact.getFirstName() + " " + sfContact.getLastName() + " ");
		logResult.append("URL to update contact in salesforce" + updateContactUrl + "<br>");
		HttpPatch patch = new HttpPatch(updateContactUrl);
		patch.setHeader("Content-Type", "application/json");
		patch.setHeader(oauthHeader);
		JSONObject contactObj = new JSONObject(gson.toJson(sfContact));
		contactObj.remove("type");
		StringEntity params = new StringEntity(contactObj.toString());
		patch.setEntity(params);
		HttpResponse patchResponse;
		try {
			patchResponse = httpClient.execute(patch);
			/*
			 * HttpEntity resEntityGet = patchResponse.getEntity(); String responseString =
			 * EntityUtils.toString(resEntityGet);
			 */
			logger.debug("this is contact update response ::{}", responseString);
			logResult.append(sfContact.getFirstName() + " " + sfContact.getLastName() + " ");
			logResult.append("successfully updated the existing contact" + "<br>");
		} catch (IOException e) {
			schLogSave.saveEndStatus("Failure", runId);
			logger.error("IOException occured is ::{}", e);
		}
		schLogSave.saveLogResult(logResult.toString(), runId);

	}

	public String creatCampaign(Campaign campaign, String salesConfig) throws IOException {

		this.schRepo = BeanUtil.getBean(SchedulerRepo.class);
		this.sfdcRepo = BeanUtil.getBean(SFDCConfigRepo.class);
		this.schLogRepo = BeanUtil.getBean(SchedulerRunsLogRepo.class);


		SFDCConfig sfdcdb = sfdcRepo.findByConfigname(salesConfig);
		sfdcdb = (SFDCConfig) decrypt(sfdcdb);
		connectionDetails = salesConnection(sfdcdb);
		baseUri = connectionDetails.split("\\+")[0];
		oauthHeader = new BasicHeader("Authorization", "OAuth " + connectionDetails.split("\\+")[1]);

		boolean status = checkIfCampaignExists(campaign, connectionDetails);
		if (!status) {
			try {
				String createCampaignUrl = baseUri + readString.getProperty("CreateCampaign");
				HttpPost post = new HttpPost(createCampaignUrl);
				post.setHeader(oauthHeader);
				JSONObject campaignObj = new JSONObject(gson.toJson(campaign));
				StringEntity params = new StringEntity(campaignObj.toString());
				post.setEntity(params);
				post.setHeader("Content-Type", "application/json");
				HttpResponse createCampaignResponse = httpClient.execute(post);
				HttpEntity CampaignResEntity = createCampaignResponse.getEntity();
				String responseString = EntityUtils.toString(CampaignResEntity);
				JSONObject json = new JSONObject(responseString);
				campaignId = json.getString("Id");

			} catch (Exception e) {
				// schLogSave.saveEndStatus("Failure", runId);
				// logger.error("Exception occured is ::{}", e);
			}
			// schLogSave.saveLogResult(logResult.toString(), runId);
		}
		return campaignId;
	}

	public boolean checkIfCampaignExists(Campaign campaign, String connectionDetails) throws IOException {

		baseUri = connectionDetails.split("\\+")[0];
		oauthHeader = new BasicHeader("Authorization", "OAuth " + connectionDetails.split("\\+")[1]);
		String campaignName = campaign.getName();
		String checkCampaignUrl = baseUri + readString.getProperty("CheckCampaignQuery") + URLEncoder.encode(campaignName,"UTF-8"); 
		System.out.println(checkCampaignUrl);
		HttpGet get = new HttpGet(checkCampaignUrl);
		get.addHeader(oauthHeader);
		get.addHeader(prettyPrintHeader);

		HttpResponse campaignExistsResponse = httpClient.execute(get);
		HttpEntity campaignResEntity = campaignExistsResponse.getEntity();
		String responseString = EntityUtils.toString(campaignResEntity);
		// logger.debug("responseString ::{}",responseString);
		// logResult.append("responseString->" + responseString+"<br>");
		Object data = new JSONTokener(responseString).nextValue();
		if (data instanceof JSONObject) {
			// logger.debug("data ::{}", data);
			campaignId = ((JSONObject) data).get("Id").toString();
			return true;
		}
		if (data instanceof JSONArray) {
			// logger.debug("data ::{}", data);
			return false;
		}
		return false;
	}

	public void createCampaignMember(List<String> campaignMembersEmailIds, CampaignMember campaignMember,
			String salesConfig, String email) {

		this.schRepo = BeanUtil.getBean(SchedulerRepo.class);
		this.sfdcRepo = BeanUtil.getBean(SFDCConfigRepo.class);
		this.schLogRepo = BeanUtil.getBean(SchedulerRunsLogRepo.class);

		SFDCConfig sfdcdb = sfdcRepo.findByConfigname(salesConfig);
		sfdcdb = (SFDCConfig) decrypt(sfdcdb);
		connectionDetails = salesConnection(sfdcdb);
		baseUri = connectionDetails.split("\\+")[0];
		oauthHeader = new BasicHeader("Authorization", "OAuth " + connectionDetails.split("\\+")[1]);
		try {

			String createCampaignMemberUrl = baseUri + readString.getProperty("CreateCampaignMember");
			HttpPost campaignMemberPost = new HttpPost(createCampaignMemberUrl);
			campaignMemberPost.setHeader(oauthHeader);
			JSONObject campaignObj = new JSONObject(gson.toJson(campaignMember));
			StringEntity params = new StringEntity(campaignObj.toString());
			campaignMemberPost.setEntity(params);
			campaignMemberPost.setHeader("Content-Type", "application/json");
			HttpResponse createCampaignResponse = httpClient.execute(campaignMemberPost);
			HttpEntity CampaignResEntity = createCampaignResponse.getEntity();
			String campaignResponseString = EntityUtils.toString(CampaignResEntity);
			JSONArray campaignResult = new JSONArray(campaignResponseString);
			JSONObject json = new JSONObject(campaignResult.get(0).toString());
			String errorMessage = json.get("message").toString();
			if (errorMessage.equals("Already a campaign member.")) {
				CampaignMember campMember = new CampaignMember();
				campMember.setStatus(campaignMember.getStatus());

				CampaignMemberIdRetreival cmir = new CampaignMemberIdRetreival();
				String campaignMemberId = cmir.getCampaignMemberId(email,sfdcdb);
				updateCampaignMember(salesConfig, campaignMemberId, campMember);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateCampaignMember(String salesConfig, String campaignMemberId, CampaignMember campaignMember) {

		this.schRepo = BeanUtil.getBean(SchedulerRepo.class);
		this.sfdcRepo = BeanUtil.getBean(SFDCConfigRepo.class);
		this.schLogRepo = BeanUtil.getBean(SchedulerRunsLogRepo.class);

		SFDCConfig sfdcdb = sfdcRepo.findByConfigname(salesConfig);
		sfdcdb = (SFDCConfig) decrypt(sfdcdb);
		connectionDetails = salesConnection(sfdcdb);
		baseUri = connectionDetails.split("\\+")[0];
		oauthHeader = new BasicHeader("Authorization", "OAuth " + connectionDetails.split("\\+")[1]);
		try {
			String updateCampaignMemberUrl = baseUri + readString.getProperty("UpdateCampaignMember")
					+ campaignMemberId;
			HttpPatch campaignMemberPatch = new HttpPatch(updateCampaignMemberUrl);
			campaignMemberPatch.setHeader(oauthHeader);
			JSONObject campaignObj = new JSONObject(gson.toJson(campaignMember));
			StringEntity params = new StringEntity(campaignObj.toString());
			campaignMemberPatch.setEntity(params);
			campaignMemberPatch.setHeader("Content-Type", "application/json");
			HttpResponse updateCampaignResponse = httpClient.execute(campaignMemberPatch);
			HttpEntity CampaignResEntity = updateCampaignResponse.getEntity();
			String campaignResponseString = EntityUtils.toString(CampaignResEntity);

		} catch (Exception e) {
			// schLogSave.saveEndStatus("Failure", runId);
			// logger.error("Exception occured is ::{}", e);
		}
	}

	public String checkIfLeadExistsForCampaign(SFLead sflead, int userid, String salesConfig) throws IOException {
		this.schRepo = BeanUtil.getBean(SchedulerRepo.class);
		this.sfdcRepo = BeanUtil.getBean(SFDCConfigRepo.class);
		this.schLogRepo = BeanUtil.getBean(SchedulerRunsLogRepo.class);

		SFDCConfig sfdcdb = sfdcRepo.findByConfigname(salesConfig);
		sfdcdb = (SFDCConfig) decrypt(sfdcdb);
		connectionDetails = salesConnection(sfdcdb);
		baseUri = connectionDetails.split("\\+")[0];
		oauthHeader = new BasicHeader("Authorization", "OAuth " + connectionDetails.split("\\+")[1]);

		String checkLeadUrl = baseUri + readString.getProperty("CheckLeadQuery") + sflead.getEmail();
		HttpGet get = new HttpGet(checkLeadUrl);

		get.addHeader(oauthHeader);
		get.addHeader(prettyPrintHeader);

		HttpResponse leadExistsResponse = httpClient.execute(get);
		HttpEntity leadResEntity = leadExistsResponse.getEntity();
		String responseString = EntityUtils.toString(leadResEntity);
		Object data = new JSONTokener(responseString).nextValue();
		if (data instanceof JSONObject) {
			String id = ((JSONObject) data).get("Id").toString();
			return id;
		}
		if (data instanceof JSONArray) {
			return null;
		}
		return null;
	}

	public String salesConnection(SFDCConfig sfdcdb) {

		// Assemble the login request URL
		String loginURL = StringConstants.SALESFORCELOGINURL + StringConstants.SALESFORCEGRANTSERVICE + "&client_id="
				+ sfdcdb.getClientId() + "&client_secret=" + sfdcdb.getClientSecret() + "&username="
				+ sfdcdb.getUserName() + "&password=" + sfdcdb.getSfdcPassword() + sfdcdb.getSecretToken();
		logger.debug("loginURL::{}", loginURL);
		// logResult.append("Salesforce loginURL:: " +
		// StringConstants.SALESFORCELOGINURL + "<br>");
		// Login requests must be POSTs
		HttpPost httpPost = new HttpPost(loginURL);
		HttpResponse loginResponse = null;

		try {
			loginResponse = httpClient.execute(httpPost);
			logger.debug("login reponse ::{}", loginResponse);

		} catch (ClientProtocolException cpException) {
			schLogSave.saveEndStatus("Failure", runId);
			logger.error("ClientProtocol Exception occured is ::{}", cpException);
		} catch (IOException ioException) {
			schLogSave.saveEndStatus("Failure", runId);
			logger.info("IOException occured is ::{}", ioException);
		}

		// verify response is HTTP OK

		String getResult = null;
		try {
			HttpEntity httpEntity = null != loginResponse ? loginResponse.getEntity() : null;
			getResult = EntityUtils.toString(httpEntity);
		} catch (IOException ioException) {
			schLogSave.saveEndStatus("Failure", runId);
			logger.error("IOException occured is ::{}", ioException);
		}
		JSONObject jsonObject = null;
		String loginAccessToken = null;
		String loginInstanceUrl = null;
		try {
			jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
			logger.debug("RESPONSE----{}", jsonObject);

			loginInstanceUrl = jsonObject.getString("instance_url");
			loginAccessToken = jsonObject.getString(("access_token"));
		} catch (Exception jsonException) {
			schLogSave.saveEndStatus("Failure", runId);
			logger.error("Exception occurred is ::{}", jsonException);
		}

		baseUri = loginInstanceUrl + StringConstants.SALESFORCERESTENDPOINT + StringConstants.SALESFORCEAPIVERSION;
		oauthHeader = new BasicHeader("Authorization", "OAuth " + loginAccessToken);

		// logResult.append(" Successful login " + "<br>");
		// logResult.append(" instance URL: " + loginInstanceUrl + "<br>");
		// release connection
		httpPost.releaseConnection();
		return baseUri + "+" + loginAccessToken;
	}

	@Override
	public Object decrypt(Object salesObj) {
		SFDCConfig salesConfig = new SFDCConfig();
		salesConfig.setConfigId(((SFDCConfig) salesObj).getConfigId());
		salesConfig.setUserid(((SFDCConfig) salesObj).getUserid());
		salesConfig.setCheckstatus(((SFDCConfig) salesObj).getCheckstatus());
		salesConfig.setSfdcData(((SFDCConfig) salesObj).getSfdcData());
		salesConfig.setSyncType(((SFDCConfig) salesObj).getSyncType());
		salesConfig.setConfigname(((SFDCConfig) salesObj).getConfigname());
		try {
			salesConfig.setClientId(
					new String(Base64.getDecoder().decode(((SFDCConfig) salesObj).getClientId()), "utf-8"));
			salesConfig.setClientSecret(
					new String(Base64.getDecoder().decode(((SFDCConfig) salesObj).getClientSecret()), "utf-8"));
			salesConfig.setUserName(
					new String(Base64.getDecoder().decode(((SFDCConfig) salesObj).getUserName()), "utf-8"));
			salesConfig.setSfdcPassword(
					new String(Base64.getDecoder().decode(((SFDCConfig) salesObj).getSfdcPassword()), "utf-8"));
			salesConfig.setSecretToken(
					new String(Base64.getDecoder().decode(((SFDCConfig) salesObj).getSecretToken()), "utf-8"));
			return salesConfig;
		} catch (UnsupportedEncodingException e) {
			logger.error("Unsupported Exception is ::{}", e);
		}
		return null;
	}

}
