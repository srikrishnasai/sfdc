package com.tadigital.sfdc_campaign.service;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.gson.Gson;
import com.tadigital.sfdc_campaign.model.Campaign;
import com.tadigital.sfdc_campaign.model.CampaignMember;
import com.tadigital.sfdc_campaign.model.SFDCConfig;
import com.tadigital.sfdc_campaign.model.SFLead;
import com.tadigital.sfdc_campaign.model.SchedulerRunsLog;
import com.tadigital.sfdc_campaign.repo.SFDCConfigRepo;
import com.tadigital.sfdc_campaign.repo.SchedulerRepo;
import com.tadigital.sfdc_campaign.repo.SchedulerRunsLogRepo;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.tadigital.sfdc_campaign.utils.ReadPropertiesFile;

public class SFCampaign {

	private static String baseUri;
	private static BasicHeader oauthHeader;
	String connectionDetails = "";
	ReadPropertiesFile readString = new ReadPropertiesFile();
	Gson gson = new Gson();
	JSONObject jsonCampaign;
	HttpClient httpClient = HttpClientBuilder.create().build();
	SFDCConfigRepo sfdcRepo;
	SchedulerRepo schRepo;
	SchedulerRunsLogRepo schLogRepo;
	SchedulerRunsLog schLog = new SchedulerRunsLog();
	SFDCConnector sfdc = new SFDCConnector();
	private static BasicHeader prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");

	public void creatCampaign(Campaign campaign, String salesConfig) throws IOException {

		this.schRepo = BeanUtil.getBean(SchedulerRepo.class);
		this.sfdcRepo = BeanUtil.getBean(SFDCConfigRepo.class);
		this.schLogRepo = BeanUtil.getBean(SchedulerRunsLogRepo.class);

		SFDCConfig sfdcdb = sfdcRepo.findByConfigname(salesConfig);
		connectionDetails = this.sfdc.salesConnection(sfdcdb);
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

			} catch (Exception e) {
				// schLogSave.saveEndStatus("Failure", runId);
				// logger.error("Exception occured is ::{}", e);
			}
			// schLogSave.saveLogResult(logResult.toString(), runId);
		}
	}

	public boolean checkIfCampaignExists(Campaign campaign, String connectionDetails) throws IOException {

		baseUri = connectionDetails.split("\\+")[0];
		oauthHeader = new BasicHeader("Authorization", "OAuth " + connectionDetails.split("\\+")[1]);

		String checkCampaignUrl = baseUri + readString.getProperty("CheckCampaignQuery") + campaign.getName();
		HttpGet get = new HttpGet(checkCampaignUrl);
		get.addHeader(oauthHeader);

		HttpResponse campaignExistsResponse = httpClient.execute(get);
		HttpEntity campaignResEntity = campaignExistsResponse.getEntity();
		String responseString = EntityUtils.toString(campaignResEntity);
		// logger.debug("responseString ::{}",responseString);
		// logResult.append("responseString->" + responseString+"<br>");
		Object data = new JSONTokener(responseString).nextValue();
		if (data instanceof JSONObject) {
			// logger.debug("data ::{}", data);
			String id = ((JSONObject) data).get("Id").toString();
			return true;
		}
		if (data instanceof JSONArray) {
			// logger.debug("data ::{}", data);
			return false;
		}
		return false;
	}

	public void createCampaignMember(CampaignMember campaignMember, String salesConfig) {

		this.schRepo = BeanUtil.getBean(SchedulerRepo.class);
		this.sfdcRepo = BeanUtil.getBean(SFDCConfigRepo.class);
		this.schLogRepo = BeanUtil.getBean(SchedulerRunsLogRepo.class);

		SFDCConfig sfdcdb = sfdcRepo.findByConfigname(salesConfig);
		connectionDetails = this.sfdc.salesConnection(sfdcdb);
		baseUri = connectionDetails.split("\\+")[0];
		oauthHeader = new BasicHeader("Authorization", "OAuth " + connectionDetails.split("\\+")[1]);

		/*String checkCampaignUrl = baseUri + readString.getProperty("CheckCampaignQuery") + campaign.getName();
		HttpGet campaignGet = new HttpGet(checkCampaignUrl);
		campaignGet.addHeader(oauthHeader);*/

		try {
			/*HttpResponse campaignExistsResponse = httpClient.execute(campaignGet);
			HttpEntity campaignResEntity = campaignExistsResponse.getEntity();
			String responseString = EntityUtils.toString(campaignResEntity);
			Object data = new JSONTokener(responseString).nextValue();
			String id = ((JSONObject) data).get("Id").toString();*/

			/*CampaignMember campaignMember = new CampaignMember();
			campaignMember.setCampaignId(id);
			campaignMember.setLcId("0036F00002wZGE5QAO");
			campaignMember.setStatus("Sent");*/

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

		} catch (Exception e) {
			// schLogSave.saveEndStatus("Failure", runId);
			// logger.error("Exception occured is ::{}", e);
		}

	}

	public String checkIfLeadExists(SFLead sflead, int userid, String salesConfig) throws IOException {
		this.schRepo = BeanUtil.getBean(SchedulerRepo.class);
		this.sfdcRepo = BeanUtil.getBean(SFDCConfigRepo.class);
		this.schLogRepo = BeanUtil.getBean(SchedulerRunsLogRepo.class);

		SFDCConfig sfdcdb = sfdcRepo.findByConfigname(salesConfig);
		connectionDetails = this.sfdc.salesConnection(sfdcdb);
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

}
