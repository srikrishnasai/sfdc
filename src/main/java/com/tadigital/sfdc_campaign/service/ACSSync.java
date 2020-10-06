/**
 * 
 */
package com.tadigital.sfdc_campaign.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
import com.tadigital.sfdc_campaign.model.MapMasterBean;
import com.tadigital.sfdc_campaign.model.SFContact;
import com.tadigital.sfdc_campaign.model.SFDCConfig;
import com.tadigital.sfdc_campaign.model.SFLead;
import com.tadigital.sfdc_campaign.model.SchedulerRunsLog;
import com.tadigital.sfdc_campaign.repo.ACSConfigRepo;
import com.tadigital.sfdc_campaign.repo.MapConfigRepo;
import com.tadigital.sfdc_campaign.repo.MapMasterRepo;
import com.tadigital.sfdc_campaign.repo.SFDCConfigRepo;
import com.tadigital.sfdc_campaign.repo.SchedulerRunsLogRepo;
import com.tadigital.sfdc_campaign.utils.JWTManager;
import com.tadigital.sfdc_campaign.utils.SFTOACSConverter;
import com.tadigital.sfdc_campaign.utils.SchLogSave;

/**
 * @author nivedha.g
 *
 */
@Service
public class ACSSync implements BeanDecryption {

    public ACSSync() {
		client = HttpClientBuilder.create().build();
		gson = new Gson();
	}

	Logger logger = LoggerFactory.getLogger(ACSSync.class);

	HttpClient client;
	StringBuilder contacts = new StringBuilder();
	Gson gson;

	SFTOACSConverter converter = new SFTOACSConverter();

	@Autowired
	private ACSConfigRepo acsRepo;

	@Autowired
	MapMasterRepo mapMasterRepo;

	@Autowired
	MapConfigRepo mapConfigRepo;

	@Autowired
	SchedulerRunsLogRepo schLogRepo;

	@Autowired
	private SFDCConfigRepo sfdcRepo;

	Optional<ACSConfig> acsConfig;
	SFDCConfig sfdcConfig;
	ACSConfig acsDb;
	List<String> mappedPairs;
	StringBuilder logResult;
	int runId;

	SFDCConnector sfdcConnector = new SFDCConnector();
	SchLogSave schLogSave = new SchLogSave();
	SchedulerRunsLog schLog = new SchedulerRunsLog();

	/**
	 * 
	 * @param dataType
	 * @param userid
	 * @param logResult
	 * @return
	 */
	public String acsData(String dataType, int userid, StringBuilder logresult, int runid) {
		this.logResult = logresult;
		runId = runid;
		String[] data = dataType.split(",");
		for (int i = 0; i < data.length; i++) {
			if (data[i].split("\\+")[0].equalsIgnoreCase("lead")) {
				contacts = syncLead(data[i].split("\\+")[0], userid, data[i].split("\\+")[1]);
			}

			if (data[i].split("\\+")[0].equalsIgnoreCase("contact")) {
				contacts = syncContact(data[i].split("\\+")[0], userid, data[i].split("\\+")[1]);
			}
		}
		String endStatus = schLogSave.getEndStatus(runId);
		if (endStatus == null) {
			schLogSave.saveEndStatus("Success", runId);
		}
		return contacts.toString();
	}

	/**
	 * @return
	 */
	private StringBuilder syncContact(String data, int userid, String mapConfig) {
		logger.debug("Entered into syncContact method::{}", data);
		logResult.append("Fetching contact data from ACS" + data + "<br>");

		MapMasterBean mapMasterBean = mapMasterRepo.findByConfigname(mapConfig);
		acsConfig = acsRepo.findByConfigId(mapMasterBean.getAcsconfigid());
		if (acsConfig.isPresent()) {
			acsDb = (ACSConfig) decrypt(acsConfig.get());
		}
		List<SFContact> sfContacts = new ArrayList<>();
		try {

			String getURL = StringConstants.ACSSFTYPEFETCHURL + data;
			HttpGet get = new HttpGet(getURL);
			logResult.append("ACS URL to fetch data using filters" + StringConstants.ACSSFTYPEFETCHURL+"<br>");
			get.setHeader("Content-Type", "application/json");
			get.setHeader("X-Api-Key", acsDb.getClientId());
			String accessToken = new JWTManager().create(acsDb.getClientId(), acsDb.getClientSecret(),
					acsDb.getOrganizationId(), acsDb.getTechAccountId());
			if (accessToken != null)
				get.setHeader("Authorization", "Bearer " + accessToken);
			else
				throw new Exception("AccessToken Not Valid");
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			String responseString = EntityUtils.toString(resEntityGet);
			logger.debug("responseString::{}", responseString);

			JSONObject json = new JSONObject(responseString);
			JSONArray docs = json.getJSONArray("content");
			mappedPairs = mapConfigRepo.findMappedPairsByUseridAndMapconfigidAndPairBelongsTo(userid,
					mapMasterBean.getMapconfigid(), "Contact");

			Optional<SFDCConfig> sfdcobj = sfdcRepo.findByConfigId(mapMasterBean.getSalesconfigid());
			sfdcConfig = sfdcobj.isPresent() ? sfdcobj.get() : null;
            String configName = null != sfdcConfig ? sfdcConfig.getConfigname() : "";
			for (int i = 0; i < docs.length(); i++) {
				JSONObject jsonObj = docs.getJSONObject(i);
				logger.debug("Adobe Campaign Response::{}", docs.getJSONObject(i));
				SFContact sfc = converter.profileToContact(jsonObj, mappedPairs);
				sfContacts.add(sfc);
				sfdcConnector.createContact(userid, sfc, logResult, runId, configName);
				contacts.append(sfc.toString());
			}
			logger.debug("SFContacts List to campaign::{}", sfContacts);
			logResult.append("SFContacts List from campaign" + sfContacts.toString()+"<br>");

		} catch (Exception e) {
			schLogSave.saveEndStatus("Failure", runId);
			logger.error("Exception Occurred ::{}",e);

		}
		schLogSave.saveLogResult(logResult.toString(), runId);

		return contacts;
	}

	/**
	 * @return
	 */
	private StringBuilder syncLead(String data, int userid, String mapConfig) {

		logger.debug("Entered into syncLead method::{}", data);
		logResult.append("Fetching contact data from ACS" + data+"<br>");
		MapMasterBean mapMasterBean = mapMasterRepo.findByConfigname(mapConfig);

		acsConfig = acsRepo.findByConfigId(mapMasterBean.getAcsconfigid());
		if (acsConfig.isPresent()) {
			acsDb = (ACSConfig) decrypt(acsConfig.get());
		}
		List<SFLead> sfleads = new ArrayList<>();
		try {

			String getURL = StringConstants.ACSSFTYPEFETCHURL + data;
			HttpGet get = new HttpGet(getURL);
			logResult.append("ACS URL to fetch data using filters" + StringConstants.ACSSFTYPEFETCHURL+"<br>");
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
			logger.debug("responseString::{}", responseString);

			JSONObject json = new JSONObject(responseString);
			JSONArray docs = json.getJSONArray("content");
			mappedPairs = mapConfigRepo.findMappedPairsByUseridAndMapconfigidAndPairBelongsTo(userid,
					mapMasterBean.getMapconfigid(), "Lead");

			Optional<SFDCConfig> sfdcobj = sfdcRepo.findByConfigId(mapMasterBean.getSalesconfigid());
			sfdcConfig = sfdcobj.isPresent() ? sfdcobj.get() : null;
			String configName = null != sfdcConfig ? sfdcConfig.getConfigname() : "";
			for (int i = 0; i < docs.length(); i++) {
				JSONObject jsonObj = docs.getJSONObject(i);
				logger.debug("Adobe Campaign Response::{}", docs.get(i));
				SFLead sfl = converter.profileToLead(jsonObj, mappedPairs);
				sfleads.add(sfl);
				sfdcConnector.createLead(userid, sfl, logResult, runId, configName);
				contacts.append(sfl.toString());
			}
			logger.debug("SFLeads from campaign::{}", sfleads);
			logResult.append("SFLeads list from campaign" + sfleads.toString()+"<br>");
		} catch (Exception e) {
			schLogSave.saveEndStatus("Failure", runId);
			logger.error("Exception Occurred ::{}",e);
		}

		schLogSave.saveLogResult(logResult.toString(), runId);
		return contacts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tadigital.sfdc_campaign.service.BeanDecryption#decrypt(java.lang.Object)
	 */

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
			logger.error("Unsupported Exception is ::{}",e);
		}

		return acsConfig;
	}

}

