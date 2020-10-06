package com.tadigital.sfdc_campaign.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.tadigital.sfdc_campaign.constants.StringConstants;
import com.tadigital.sfdc_campaign.model.ACSConfig;
import com.tadigital.sfdc_campaign.model.ACSProfile;
import com.tadigital.sfdc_campaign.model.DashboardData;
import com.tadigital.sfdc_campaign.model.MapMasterBean;
import com.tadigital.sfdc_campaign.model.RecordsBean;
import com.tadigital.sfdc_campaign.model.SFContact;
import com.tadigital.sfdc_campaign.model.SFDCConfig;
import com.tadigital.sfdc_campaign.model.SFLead;
import com.tadigital.sfdc_campaign.model.SchedulerConfig;
import com.tadigital.sfdc_campaign.model.SchedulerRunsLog;
import com.tadigital.sfdc_campaign.repo.ACSConfigRepo;
import com.tadigital.sfdc_campaign.repo.DashboardDataRepo;
import com.tadigital.sfdc_campaign.repo.MapConfigRepo;
import com.tadigital.sfdc_campaign.repo.MapMasterRepo;
import com.tadigital.sfdc_campaign.repo.SFDCConfigRepo;
import com.tadigital.sfdc_campaign.repo.SchedulerRepo;
import com.tadigital.sfdc_campaign.repo.SchedulerRunsLogRepo;
import com.tadigital.sfdc_campaign.repo.SchedulerRunsRepo;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.tadigital.sfdc_campaign.utils.ReadPropertiesFile;
import com.tadigital.sfdc_campaign.utils.SFTOACSConverter;
import com.tadigital.sfdc_campaign.utils.SchLogSave;

/**
 * @author Ravi.sangubotla
 *
 */
@Service
public class SFDCSync implements BeanDecryption {

	/** The log. */
	Logger logger = LoggerFactory.getLogger(SFDCSync.class);

	@Autowired
	private SFDCConfigRepo sfdcRepo;

	@Autowired
	private ACSConfigRepo acsRepo;

	@Autowired
	private SchedulerRepo schRepo;

	@Autowired
	SchedulerRunsRepo schRun;

	@Autowired
	ACSConnector acsConnector;

	@Autowired
	MapMasterRepo mapMasterRepo;

	@Autowired
	MapConfigRepo mapConfigRepo;

	@Autowired
	SchedulerRunsLogRepo schLogRepo;

	private static String baseUri;
	private static BasicHeader oauthHeader;
	private static BasicHeader prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");
	String responseString = "";
	JSONObject jsonLead;
	JsonElement element;
	String resp = "";
	HttpClient httpClient = HttpClientBuilder.create().build();
	ReadPropertiesFile readString = new ReadPropertiesFile();
	Gson gson = new Gson();
	StringBuilder contacts = new StringBuilder();
	MapMasterBean mapMasterBean;
	List<String> mappedPairs;
	StringBuilder logResult;
	String connectionDetails = "";
	int runId;
	SFTOACSConverter converter = new SFTOACSConverter();
	SchedulerRunsLog schLog = new SchedulerRunsLog();

	DashboardData dashboardData;
	DashboardDataRepo dashboardDataRepo;

	Date currentDate = new Date();
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	String now;
	SchLogSave schLogSave = new SchLogSave();

	List<ACSProfile> updatedLeadList = new ArrayList<>();
	List<ACSProfile> createdLeadList = new ArrayList<>();
	List<ACSProfile> updatedContactList = new ArrayList<>();
	List<ACSProfile> createdContactList = new ArrayList<>();

	public String fetchData(int userid, StringBuilder logresult, int runid) {
		this.logResult = logresult;
		runId = runid;

		List<SchedulerConfig> schConfigs = schRepo.findByUserId(userid);
		SchedulerConfig schdb = schConfigs.get(0);

		this.now = dateFormat.format(currentDate);

		try {
			String data[] = schdb.getDataset().split(",");

			for (int i = 0; i < data.length; i++) {
				mapMasterBean = mapMasterRepo.findByConfigname(data[i].split("\\+")[1]);
				mappedPairs = mapConfigRepo.findMappedPairsByUseridAndMapconfigidAndPairBelongsTo(userid,
						mapMasterBean.getMapconfigid(), data[i].split("\\+")[0]);

				SFDCConfig sfdcdb = sfdcRepo.findByConfigId(mapMasterBean.getSalesconfigid()).get();
				sfdcdb = (SFDCConfig) decrypt(sfdcdb);
				connectionDetails = salesConnection(sfdcdb);
				ACSConfig acsdb = acsRepo.findByConfigId(mapMasterBean.getAcsconfigid()).get();
				if (data[i].split("\\+")[0].equalsIgnoreCase("lead")) {
					if (sfdcdb.getSyncType().equalsIgnoreCase("full sync")) {
						logResult.append("Retrieving Full Leads Data from Salesforce" + "<br>");
						contacts = fetchFullLeadData(connectionDetails, acsdb.getConfigname(), data[i].split("\\+")[0],
								userid, mappedPairs);
					} else {
						logResult.append("Retrieving Updated Leads Data from Salesforce" + "<br>");
						contacts = fetchUpdatedLeadData(connectionDetails, acsdb.getConfigname(),
								data[i].split("\\+")[0], userid, mappedPairs);
					}
				}

				if (data[i].split("\\+")[0].equalsIgnoreCase("contact")) {
					if (sfdcdb.getSyncType().equalsIgnoreCase("full sync")) {
						logResult.append("Retrieving Full Contacts Data from Salesforce" + "<br>");
						contacts = fetchFullContactData(connectionDetails, acsdb.getConfigname(),
								data[i].split("\\+")[0], userid, mappedPairs);
					} else {
						logResult.append("Retrieving Updated Contacts Data from Salesforce" + "<br>");
						contacts = fetchUpdatedContactData(connectionDetails, acsdb.getConfigname(),
								data[i].split("\\+")[0], userid, mappedPairs);
					}
				}

			}

		} catch (Exception e) {
			schLogSave.saveEndStatus("Failure", runId);
			logger.error("Exception Occurred ::", e);
		}

		if (!updatedLeadList.isEmpty()) {
			logResult.append("Finally Leads Synced List  " + "<br>");
			for (int i = 0; i < updatedLeadList.size(); i++) {
				logResult.append(
						updatedLeadList.get(i).getFirstName() + "  " + updatedLeadList.get(i).getLastName() + "<br>");
			}
		}

		/*
		 * if (!createdLeadList.isEmpty()) {
		 * logResult.append("Finally  newly created Lead List  " + "<br>"); for (int i =
		 * 0; i < createdLeadList.size(); i++) { logResult.append(
		 * createdLeadList.get(i).getFirstName() + "  " +
		 * createdLeadList.get(i).getLastName() + "<br>"); } }
		 */

		if (!updatedContactList.isEmpty()) {
			logResult.append("Finally updated Contact List  " + "<br>");
			for (int i = 0; i < updatedContactList.size(); i++) {
				logResult.append(updatedContactList.get(i).getFirstName() + "  "
						+ updatedContactList.get(i).getLastName() + "<br>");
			}
		}

		/*
		 * if (!createdContactList.isEmpty()) {
		 * logResult.append("Finally newly created Contacts List :- " + "<br>"); for
		 * (int i = 0; i < updatedContactList.size(); i++) {
		 * logResult.append(updatedContactList.get(i).getFirstName() + "  " +
		 * updatedContactList.get(i).getLastName() + "<br>"); } }
		 */

		String endStatus = schLogSave.getEndStatus(runId);
		if (endStatus == null) {
			schLogSave.saveEndStatus("Success", runId);
		}
		schLogSave.saveLogResult(logResult.toString(), runId);

		return contacts.toString();
	}

	/**
	 * @param sfdcdb
	 * @return
	 */
	private String salesConnection(SFDCConfig sfdcdb) {

		// Assemble the login request URL
		String loginURL = StringConstants.SALESFORCELOGINURL + StringConstants.SALESFORCEGRANTSERVICE + "&client_id="
				+ sfdcdb.getClientId() + "&client_secret=" + sfdcdb.getClientSecret() + "&username="
				+ sfdcdb.getUserName() + "&password=" + sfdcdb.getSfdcPassword() + sfdcdb.getSecretToken();
		logger.debug("loginURL::{}", loginURL);
		logResult.append("Salesforce loginURL::" + StringConstants.SALESFORCELOGINURL);
		// Login requests must be POSTs
		HttpPost httpPost = new HttpPost(loginURL);
		HttpResponse loginResponse = null;

		try {
			loginResponse = httpClient.execute(httpPost);
			logger.debug("login reponse ::{}", loginResponse);

		} catch (IOException ioException) {
			schLogSave.saveEndStatus("Failure", runId);
			logger.error("IOException occurred ::{}", ioException);
		}

		String getResult = null;
		try {
			HttpEntity httpEntity = null != loginResponse ? loginResponse.getEntity() : null;
			getResult = EntityUtils.toString(httpEntity);
		} catch (IOException ioException) {
			schLogSave.saveEndStatus("Failure", runId);
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
			schLogSave.saveEndStatus("Failure", runId);
		}

		baseUri = loginInstanceUrl + StringConstants.SALESFORCERESTENDPOINT + StringConstants.SALESFORCEAPIVERSION;
		oauthHeader = new BasicHeader("Authorization", "OAuth " + loginAccessToken);
		logResult.append("   Salesforce Successful login" + "<br>");
		logResult.append(" instance URL: " + loginInstanceUrl + "<br>");
		// release connection
		httpPost.releaseConnection();
		return baseUri + "+" + loginAccessToken;
	}

	/**
	 * @param leadConfig
	 * @param string
	 */
	private StringBuilder fetchUpdatedLeadData(String connectionDetails, String acsConfigName, String dataitem,
			int userid, List<String> mappedPairs) {
		this.dashboardDataRepo = BeanUtil.getBean(DashboardDataRepo.class);
		dashboardData = new DashboardData();
		SFLead sfLead = new SFLead();
		baseUri = connectionDetails.split("\\+")[0];
		oauthHeader = new BasicHeader("Authorization", "OAuth " + connectionDetails.split("\\+")[1]);
		String uri = baseUri + readString.getProperty(dataitem);
		Date lastRunDate;
		lastRunDate = (Date) schRun.fetchLastRun(userid).get(0).getLastrun();
		Long currentTime = System.currentTimeMillis();
		currentTime += 86400 * 60;
		Date currentDate = new Date(currentTime);

		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);

		c.add(Calendar.DATE, 1);
		Date nextDate = c.getTime();

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss+hh:mm");
		TimeZone zone = TimeZone.getTimeZone("UTC");
		df.setTimeZone(zone);
		String next = df.format(nextDate);
		String lastRunDateTime = df.format(lastRunDate);
		logger.debug("currentdatetime ::{}", next + "\n");
		logger.debug("lastRunDateTime::{}", lastRunDateTime);

		try {
			String encodedstartdate = URLEncoder.encode(lastRunDateTime, "UTF-8");
			String encodedenddate = URLEncoder.encode(next, "UTF-8");
			logger.debug("URIb :: {}", uri);
			String leadURL = "?start=" + encodedstartdate + "&end=" + encodedenddate;
			uri += leadURL;
			logger.debug("Query URL: {}", uri);
			HttpGet httpGet = new HttpGet(uri);
			httpGet.addHeader(oauthHeader);
			httpGet.addHeader(prettyPrintHeader);

			// Make the request.
			HttpResponse leadId = httpClient.execute(httpGet);

			// Process the result
			int statusCode = leadId.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				responseString = EntityUtils.toString(leadId.getEntity());
				JSONObject json = new JSONObject(responseString);
				logger.debug("get response ::{}", json);

				JSONArray docs = json.getJSONArray("ids");
				logger.debug("JSON result of Query:\n{}", docs);

				for (int i = 0; i < docs.length(); i++) {
					logger.debug("id: {}", docs.getString(i));
					String leadUri = baseUri + readString.getProperty("LeadData") + docs.getString(i);
					HttpGet get = new HttpGet(leadUri);
					get.addHeader(oauthHeader);
					get.addHeader(prettyPrintHeader);
					HttpResponse leadData = httpClient.execute(get);

					int leadStatusCode = leadData.getStatusLine().getStatusCode();
					if (leadStatusCode == 200) {
						responseString = EntityUtils.toString(leadData.getEntity());
						jsonLead = new JSONObject(responseString);
						logger.debug("lead response" + jsonLead);
						element = gson.fromJson(jsonLead.toString(), JsonElement.class);
					}
					sfLead = gson.fromJson(element, SFLead.class);
					logger.debug("String fromm sfdc object ::{}", gson.toJson(sfLead));

					// if (sfLead.getEmail() != null) {
					ACSProfile profile = converter.leadToProfile(jsonLead, mappedPairs);
					boolean status = acsConnector.acsProfileCreate(acsConfigName, userid, profile, logResult, runId);
					contacts.append(sfLead.toString());
					if (status) {
						updatedLeadList.add(profile);
					} else {
						createdLeadList.add(profile);
					}
					// }
				}
				dashboardData.setUserid(userid);
				dashboardData.setDatatype("Lead");
				dashboardData.setDatacount(docs.length());
				dashboardData.setDate(now);
				dashboardDataRepo.save(dashboardData);
			}
		} catch (Exception e) {
			schLogSave.saveEndStatus("Failure", runId);
			logger.error(e.getMessage());

		}
		schLogSave.saveLogResult(logResult.toString(), runId);
		return contacts;

	}

	private StringBuilder fetchUpdatedContactData(String connectionDetails, String acsConfigName, String dataitem,
			int userid, List<String> mappedPairs) {
		this.dashboardDataRepo = BeanUtil.getBean(DashboardDataRepo.class);
		dashboardData = new DashboardData();
		SFContact sfContact = new SFContact();
		baseUri = connectionDetails.split("\\+")[0];
		oauthHeader = new BasicHeader("Authorization", "OAuth " + connectionDetails.split("\\+")[1]);
		String uri = baseUri + readString.getProperty(dataitem);
		Date lastRunDate;
		lastRunDate = (Date) schRun.fetchLastRun(userid).get(0).getLastrun();
		Long currentTime = System.currentTimeMillis();
		currentTime += 86400 * 60;
		Date currentDate = new Date(currentTime);

		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);

		c.add(Calendar.DATE, 1);
		Date nextDate = c.getTime();

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss+hh:mm");
		TimeZone zone = TimeZone.getTimeZone("UTC");
		df.setTimeZone(zone);
		String next = df.format(nextDate);
		String lastRunDateTime = df.format(lastRunDate);
		logger.debug("currentdatetime ::{}", next);
		logger.debug("lastRunDateTime ::{}", lastRunDateTime);
		try {
			String encodedstartdate = URLEncoder.encode(lastRunDateTime, "UTF-8");
			String encodedenddate = URLEncoder.encode(next, "UTF-8");
			logger.debug("URIb :: {}", uri);
			String contactURL = "?start=" + encodedstartdate + "&end=" + encodedenddate;
			uri += contactURL;
			logger.debug("Query URL: {}", uri);
			HttpGet httpGet = new HttpGet(uri);
			logger.debug("oauthHeader2: {}", oauthHeader);
			httpGet.addHeader(oauthHeader);
			httpGet.addHeader(prettyPrintHeader);

			// Make the request.
			HttpResponse contactId = httpClient.execute(httpGet);

			// Process the result
			int statusCode = contactId.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				responseString = EntityUtils.toString(contactId.getEntity());
				JSONObject json = new JSONObject(responseString);
				logger.debug("get response ::{}", json);
				JSONArray docs = json.getJSONArray("ids");
				logger.debug("JSON result of Query:\n{}", docs);
				for (int i = 0; i < docs.length(); i++) {
					logger.debug("id: {}", docs.getString(i));
					String contactUri = baseUri + readString.getProperty("ContactData") + docs.getString(i);
					HttpGet get = new HttpGet(contactUri);
					get.addHeader(oauthHeader);
					get.addHeader(prettyPrintHeader);
					HttpResponse leadData = httpClient.execute(get);

					int leadStatusCode = leadData.getStatusLine().getStatusCode();
					if (leadStatusCode == 200) {
						responseString = EntityUtils.toString(leadData.getEntity());
						jsonLead = new JSONObject(responseString);
						logger.debug("lead response ::{}", jsonLead);
						element = gson.fromJson(jsonLead.toString(), JsonElement.class);
					}
					sfContact = gson.fromJson(element, SFContact.class);
					logger.debug("String fromm sfdc object ::{}", gson.toJson(sfContact));

					// if (sfContact.getEmail() != null) {
					ACSProfile profile = converter.contactToProfile(jsonLead, mappedPairs);
					boolean status = acsConnector.acsProfileCreate(acsConfigName, userid, profile, logResult, runId);
					contacts.append(sfContact.toString());
					if (status) {
						updatedContactList.add(profile);
					} else {
						createdContactList.add(profile);
					}
					// }
				}
				dashboardData.setUserid(userid);
				dashboardData.setDatatype("Contact");
				dashboardData.setDatacount(docs.length());
				dashboardData.setDate(now);
				dashboardDataRepo.save(dashboardData);
			}
		} catch (Exception e) {
			schLogSave.saveEndStatus("Failure", runId);
			logger.error(e.getMessage());

		}
		schLogSave.saveLogResult(logResult.toString(), runId);
		return contacts;

	}

	private StringBuilder fetchFullLeadData(String connectionDetails, String acsConfigName, String dataitem, int userid,
			List<String> mappedPairs) {
		this.dashboardDataRepo = BeanUtil.getBean(DashboardDataRepo.class);
		dashboardData = new DashboardData();
		SFLead sfLead = new SFLead();
		baseUri = connectionDetails.split("\\+")[0];
		oauthHeader = new BasicHeader("Authorization", "OAuth " + connectionDetails.split("\\+")[1]);
		String uri = baseUri + readString.getProperty("LeadQuery");
		try {
			logger.debug("Query URL: {}", uri);
			HttpGet httpGet = new HttpGet(uri);
			logger.debug("oauthHeader2: {}", oauthHeader);
			httpGet.addHeader(oauthHeader);
			httpGet.addHeader(prettyPrintHeader);

			// Make the request.
			HttpResponse leadId = httpClient.execute(httpGet);

			// Process the result
			int statusCode = leadId.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				responseString = EntityUtils.toString(leadId.getEntity());
				JSONObject json = new JSONObject(responseString);
				logger.debug("get response  ::{}", json);
				JSONArray docs = json.getJSONArray("records");
				logger.debug("JSON result of Query:\n::{}", docs);
				for (int i = 0; i < docs.length(); i++) {
					RecordsBean rb = gson.fromJson(docs.get(i).toString(), RecordsBean.class);
					String leadUri = baseUri + readString.getProperty("LeadData") + rb.getId();
					HttpGet get = new HttpGet(leadUri);
					get.addHeader(oauthHeader);
					get.addHeader(prettyPrintHeader);
					HttpResponse leadData = httpClient.execute(get);

					int leadStatusCode = leadData.getStatusLine().getStatusCode();
					if (leadStatusCode == 200) {
						responseString = EntityUtils.toString(leadData.getEntity());
						jsonLead = new JSONObject(responseString);
						logger.debug("lead response ::{}", jsonLead);
						element = gson.fromJson(jsonLead.toString(), JsonElement.class);
					}
					sfLead = gson.fromJson(element, SFLead.class);
					logger.debug("String fromm sfdc object ::{}", gson.toJson(sfLead));

					// if (sfLead.getEmail() != null) {
					ACSProfile profile = converter.leadToProfile(jsonLead, mappedPairs);
					boolean status = acsConnector.acsProfileCreate(acsConfigName, userid, profile, logResult, runId);
					contacts.append(sfLead.toString());
					if (status) {
						updatedLeadList.add(profile);
					} else {
						createdLeadList.add(profile);
					}
					// }
				}
				dashboardData.setUserid(userid);
				dashboardData.setDatatype("Lead");
				dashboardData.setDatacount(docs.length());
				dashboardData.setDate(now);
				dashboardDataRepo.save(dashboardData);
			}
		} catch (Exception e) {
			schLogSave.saveEndStatus("Failure", runId);
			logger.error(e.getMessage());

		}
		schLogSave.saveLogResult(logResult.toString(), runId);
		return contacts;

	}

	private StringBuilder fetchFullContactData(String connectionDetails, String acsConfigName, String dataitem,
			int userid, List<String> mappedPairs) {
		this.dashboardDataRepo = BeanUtil.getBean(DashboardDataRepo.class);
		dashboardData = new DashboardData();
		SFContact sfContact = new SFContact();

		baseUri = connectionDetails.split("\\+")[0];
		oauthHeader = new BasicHeader("Authorization", "OAuth " + connectionDetails.split("\\+")[1]);
		String uri = baseUri + readString.getProperty("ContactQuery");
		try {
			logger.debug("Query URL: {}", uri);
			HttpGet httpGet = new HttpGet(uri);
			logger.debug("oauthHeader2: {}", oauthHeader);
			httpGet.addHeader(oauthHeader);
			httpGet.addHeader(prettyPrintHeader);

			// Make the request.
			HttpResponse contactID = httpClient.execute(httpGet);

			// Process the result
			int statusCode = contactID.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				responseString = EntityUtils.toString(contactID.getEntity());
				JSONObject json = new JSONObject(responseString);
				logger.debug("get response ::{}", json);
				JSONArray docs = json.getJSONArray("records");
				logger.debug("JSON result of Query:\n::{}", docs);
				for (int i = 0; i < docs.length(); i++) {
					RecordsBean rb = gson.fromJson(docs.get(i).toString(), RecordsBean.class);
					String contactUri = baseUri + readString.getProperty("ContactData") + rb.getId();
					HttpGet get = new HttpGet(contactUri);
					get.addHeader(oauthHeader);
					get.addHeader(prettyPrintHeader);
					HttpResponse leadData = httpClient.execute(get);

					int contactStatusCode = leadData.getStatusLine().getStatusCode();
					if (contactStatusCode == 200) {
						responseString = EntityUtils.toString(leadData.getEntity());
						jsonLead = new JSONObject(responseString);
						logger.debug("Contact response ::{}", jsonLead);
						element = gson.fromJson(jsonLead.toString(), JsonElement.class);
					}
					sfContact = gson.fromJson(element, SFContact.class);
					// if (sfContact.getEmail() != null) {
					ACSProfile profile = converter.contactToProfile(jsonLead, mappedPairs);
					boolean status = acsConnector.acsProfileCreate(acsConfigName, userid, profile, logResult, runId);
					contacts.append(sfContact.toString());
					if (status) {
						updatedContactList.add(profile);
					} else {
						createdContactList.add(profile);
					}
					// }
				}
				dashboardData.setUserid(userid);
				dashboardData.setDatatype("Contact");
				dashboardData.setDatacount(docs.length());
				dashboardData.setDate(now);
				dashboardDataRepo.save(dashboardData);
			}
		} catch (Exception e) {
			schLogSave.saveEndStatus("Failure", runId);
			logger.error(e.getMessage());
		}
		schLogSave.saveLogResult(logResult.toString(), runId);
		return contacts;
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
