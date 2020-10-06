package com.tadigital.sfdc_campaign.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
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
import com.tadigital.sfdc_campaign.constants.StringConstants;
import com.tadigital.sfdc_campaign.model.ACSConfig;
import com.tadigital.sfdc_campaign.model.ACSProfile;
import com.tadigital.sfdc_campaign.model.SchedulerRunsLog;
import com.tadigital.sfdc_campaign.repo.ACSConfigRepo;
import com.tadigital.sfdc_campaign.utils.JWTManager;
import com.tadigital.sfdc_campaign.utils.SchLogSave;

/**
 * @author nivedha.g
 *
 */
@Service
public class ACSConnector implements BeanDecryption {

	Logger logger = LoggerFactory.getLogger(ACSConnector.class);

	HttpClient client;

	Gson gson;

	@Autowired
	private ACSConfigRepo acsRepo;

	ACSConfig acsDb;
	StringBuilder logResult;
	int runId;

	public ACSConnector() {
		client = HttpClientBuilder.create().build();
		gson = new Gson();
	}

	/**
	 * @param userid
	 * @param sfLead
	 */

	SchedulerRunsLog schLog = new SchedulerRunsLog();
	SchLogSave schLogSave = new SchLogSave();

	public boolean acsProfileCreate(String acsConfigName, int userid, ACSProfile profile, StringBuilder logresult,
			int runid) {
		logResult = logresult;
		runId = runid;
		boolean status = true;
		try {
			acsDb = acsRepo.findByConfigname(acsConfigName);
			acsDb = (ACSConfig) decrypt(acsDb);

			status = checkIfExists(profile);
			logger.debug("Status ::{}", status);
			if (!status) {
				HttpPost post = new HttpPost(StringConstants.ACSCREATEPROFILEURL);
				logResult.append(profile.getFirstName() + " ");
				logResult.append("Profile does not exists in ACS, so creating the required profile");
				logResult.append("<br>");
				post.setHeader("Content-Type", "application/json");
				post.setHeader("X-Api-Key", acsDb.getClientId());
				post.setHeader("Cache-Control", "no-cache");
				String accessToken = new JWTManager().create(acsDb.getClientId(), acsDb.getClientSecret(),
						acsDb.getOrganizationId(), acsDb.getTechAccountId());
				if (accessToken != null)
					post.setHeader("Authorization", "Bearer " + accessToken);
				else {
					schLogSave.saveEndStatus("Failure", runId);
					throw new Exception("AccessToken Not Valid");

				}
				logger.debug("salesforce response ::{}", gson.toJson(profile));
				StringEntity params = new StringEntity(gson.toJson(profile));
				post.setEntity(params);
				HttpResponse responsePost = client.execute(post);
				HttpEntity resEntityGet = responsePost.getEntity();
				String responseString = EntityUtils.toString(resEntityGet);
				logger.debug("this is response::{} ", responseString);
				logResult.append(profile.getFirstName() + " " + profile.getLastName() + " ");
				logResult.append("Profile created successfully in ACS <br>");
			}

		} catch (Exception e) {
			schLogSave.saveEndStatus("Failure", runId);
			logger.error("Exception Occurred ::{}", e);
			logger.info("Exception is::{}", e.getMessage());
		}
		schLogSave.saveLogResult(logResult.toString(), runId);
		return status;
	}

	/**
	 * @param sfLead
	 */

	private boolean checkIfExists(ACSProfile profile) {
		try {

			String getURL = StringConstants.ACSFETCHPROFILEURL + profile.getFirstName() + "&lastName_parameter="
					+ profile.getLastName();
			logResult.append("Connecting to ACS and fetching profiles" + "<br>");
			logResult.append("Checking if " + profile.getFirstName() + " profile already exists" + "<br>");
			HttpGet get = new HttpGet(getURL);
			get.setHeader("Content-Type", "application/json");
			get.setHeader("X-Api-Key", acsDb.getClientId());
			String accessToken = new JWTManager().create(acsDb.getClientId(), acsDb.getClientSecret(),
					acsDb.getOrganizationId(), acsDb.getTechAccountId());
			if (accessToken != null)
				get.setHeader("Authorization", "Bearer " + accessToken);
			else {
				schLogSave.saveEndStatus("Failure", runId);
				throw new Exception("AccessToken Not Valid");
			}
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			String responseString = EntityUtils.toString(resEntityGet);
			logger.debug("responseString ::{}", responseString);
			JSONObject json = new JSONObject(responseString);
			JSONArray docs = json.getJSONArray("content");
			if (docs.length() > 0) {
				String pKey = (String) docs.getJSONObject(0).get("PKey");
				logResult.append(profile.getFirstName() + " " + profile.getLastName() + " ");
				logResult.append("Profile already exists in ACS, so updating the existing profile" + "<br>");
				updateIfExists(profile, pKey);
				return true;
			}
		} catch (Exception e) {
			logger.error("Exception Occurred ::{}", e);
			logger.info("Exception is::{}", e.getMessage());
		}
		schLogSave.saveLogResult(logResult.toString(), runId);
		return false;
	}

	/**
	 * @param sfLead
	 * @throws IOException
	 * @throws ClientProtocolException
	 */

	public void updateIfExists(ACSProfile profile, String pKey) throws IOException {
		try {

			HttpPatch patch = new HttpPatch(StringConstants.ACSUPDATEPROFILEURL + pKey);
			patch.setHeader("Content-Type", "application/json");
			String accessToken = new JWTManager().create(acsDb.getClientId(), acsDb.getClientSecret(),
					acsDb.getOrganizationId(), acsDb.getTechAccountId());
			if (accessToken != null)
				patch.setHeader("Authorization", "Bearer " + accessToken);
			else {
				schLogSave.saveEndStatus("Failure", runId);
				throw new Exception("AccessToken Not Valid");
			}
			patch.setHeader("X-Api-Key", acsDb.getClientId());
			StringEntity params;
			JSONObject profileObj = new JSONObject(gson.toJson(profile));
			params = new StringEntity(profileObj.toString());
			patch.setEntity(params);
			HttpResponse patchResponse = client.execute(patch);
			HttpEntity resEntityGet = patchResponse.getEntity();
			String responseString = EntityUtils.toString(resEntityGet);
			logger.debug("this is patch response ::{}", responseString);
			logResult.append("Successfully updated the existing " + profile.getFirstName() + " " + profile.getLastName()
					+ " profile <br>");

		} catch (Exception e) {
			schLogSave.saveEndStatus("Failure", runId);
			logger.error("Exception Occurred ::{}", e);
			logger.info("Exception is::{}", e.getMessage());
		}
		schLogSave.saveLogResult(logResult.toString(), runId);
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
