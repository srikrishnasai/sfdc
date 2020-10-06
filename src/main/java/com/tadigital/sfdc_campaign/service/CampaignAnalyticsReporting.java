package com.tadigital.sfdc_campaign.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.AnalyticsReportingScopes;
import com.google.api.services.analyticsreporting.v4.model.ColumnHeader;
import com.google.api.services.analyticsreporting.v4.model.DateRange;
import com.google.api.services.analyticsreporting.v4.model.DateRangeValues;
import com.google.api.services.analyticsreporting.v4.model.Dimension;
import com.google.api.services.analyticsreporting.v4.model.GetReportsRequest;
import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;
import com.google.api.services.analyticsreporting.v4.model.Metric;
import com.google.api.services.analyticsreporting.v4.model.MetricHeaderEntry;
import com.google.api.services.analyticsreporting.v4.model.Report;
import com.google.api.services.analyticsreporting.v4.model.ReportRequest;
import com.google.api.services.analyticsreporting.v4.model.ReportRow;
import com.tadigital.sfdc_campaign.model.Campaign;
import com.tadigital.sfdc_campaign.model.CampaignMember;
import com.tadigital.sfdc_campaign.model.SFLead;
import com.tadigital.sfdc_campaign.model.SchedulerRunsLog;
import com.tadigital.sfdc_campaign.repo.SchedulerRunsLogRepo;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.tadigital.sfdc_campaign.utils.SchLogSave;

@Service
public class CampaignAnalyticsReporting {
	private static final String APPLICATION_NAME = "Hello Analytics Reporting";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String VIEW_ID = "193760103";
	static SFDCConnector sfdcConnector;
	static List<String> campaignMembersEmailIds = new ArrayList<>();

	StringBuilder logResult;
	int logId;
	String endStatus;

	SchLogSave schLogSave = new SchLogSave();

	public String report(InputStream client_secret_file, String salesConfigName, StringBuilder logresult, int logid, String viewId, String startDate, String endDate) {

		logResult = logresult;
		logId = logid;
		GetReportsResponse response = null;
		try {
			
			
			AnalyticsReporting service = initializeAnalyticsReporting(client_secret_file);
			response = getReport(service,startDate,endDate,viewId);
			logResult.append("Connected to google account"+ "<br>");
			printResponse(response, salesConfigName);
			
		} catch (Exception e) {
			schLogSave.saveEndStatus("Failure", logId);
			e.printStackTrace();
		}
		
		String endStatus = schLogSave.getEndStatus(logId);
		if (endStatus == null) {
			schLogSave.saveEndStatus("Success", logId);
		}
		schLogSave.saveLogResult(logResult.toString(), logId);
		
		return response.toString();
	}

	/**
	 * Initializes an Analytics Reporting API V4 service object.
	 *
	 * @return An authorized Analytics Reporting API V4 service object.
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	private AnalyticsReporting initializeAnalyticsReporting(InputStream client_secret_file)

			throws GeneralSecurityException, IOException {

		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

		GoogleCredential credential = GoogleCredential.fromStream(client_secret_file)
				.createScoped(AnalyticsReportingScopes.all());
		logResult.append("Connecting to google account and initializing analytics reporting"+ "<br>");
		// Construct the Analytics Reporting service object.
		return new AnalyticsReporting.Builder(httpTransport, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
	}

	/**
	 * Queries the Analytics Reporting API V4.
	 *
	 * @param service
	 *            An authorized Analytics Reporting API V4 service object.
	 * @return GetReportResponse The Analytics Reporting API V4 response.
	 * @throws IOException
	 */
	private GetReportsResponse getReport(AnalyticsReporting service,String startDate, String endDate,String viewId) throws IOException {
		// Create the DateRange object.
		DateRange dateRange = new DateRange();
		dateRange.setStartDate(startDate);
		dateRange.setEndDate(endDate);

		// Create the Metrics object.
		Metric sessions = new Metric().setExpression("ga:totalEvents").setAlias("TotalEvents");

		Dimension pageTitle = new Dimension().setName("ga:eventAction");
		Dimension pageDetails = new Dimension().setName("ga:eventLabel");
		Dimension pageInfo = new Dimension().setName("ga:eventCategory");

		// Create the ReportRequest object.
		ReportRequest request = new ReportRequest().setViewId(viewId).setDateRanges(Arrays.asList(dateRange))
				.setMetrics(Arrays.asList(sessions)).setDimensions(Arrays.asList(pageInfo, pageDetails, pageTitle));

		ArrayList<ReportRequest> requests = new ArrayList<ReportRequest>();
		requests.add(request);

		// Create the GetReportsRequest object.
		GetReportsRequest getReport = new GetReportsRequest().setReportRequests(requests);

		// Call the batchGet method.
		GetReportsResponse response = service.reports().batchGet(getReport).execute();

		// Return the response.
		return response;
	}

	/**
	 * Parses and prints the Analytics Reporting API V4 response.
	 *
	 * @param response
	 *            An Analytics Reporting API V4 response.
	 * @throws IOException
	 */
	private void printResponse(GetReportsResponse response, String salesConfigName) {
		sfdcConnector = BeanUtil.getBean(SFDCConnector.class);
		Set<String> responded = new HashSet<>();
		Set<String> sent = new HashSet<>();
		String campaignId = null;
		for (Report report : response.getReports()) {
			ColumnHeader header = report.getColumnHeader();
			List<String> dimensionHeaders = header.getDimensions();
			Map<String, String> idMailMap = new HashMap<String, String>();
			List<ReportRow> rows = report.getData().getRows();

			if (rows == null) {
				System.out.println("No data found for " + VIEW_ID);
				logResult.append("No Data found for the given time period of campaign"+ "<br>");
				return;
			}

			for (ReportRow row : rows) {
				List<String> dimensions = row.getDimensions();
				String leadId = null;
				String value = "responded";
				for (int i = 0; i < dimensionHeaders.size() && i < dimensions.size(); i++) {
					if (dimensionHeaders.get(i).equals("ga:eventCategory")) {
						Campaign campaignObj = new Campaign();
						campaignObj.setName(dimensions.get(i));
						try {
							campaignId = sfdcConnector.creatCampaign(campaignObj, salesConfigName);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							schLogSave.saveEndStatus("Failure", logId);
							e.printStackTrace();
						}

					}
					if (dimensionHeaders.get(i).equals("ga:eventLabel")) {
						SFLead sfLead = new SFLead();
						sfLead.setEmail(dimensions.get(i));
						campaignMembersEmailIds.add(sfLead.getEmail());
						try {
							leadId = sfdcConnector.checkIfLeadExistsForCampaign(sfLead, 1, salesConfigName);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							schLogSave.saveEndStatus("Failure", logId);
							e.printStackTrace();
						}
						idMailMap.put(leadId, sfLead.getEmail());
					}
					if (dimensionHeaders.get(i).equals("ga:eventAction")) {
						if (!dimensions.get(i).equals("clickonimage")) {
							value = "sent";
						}
					}

				}
				if (value.equals("responded")) {
					responded.add(leadId);
				} else {
					sent.add(leadId);
				}
			}

			for (String leadId : responded) {
				CampaignMember campMember = new CampaignMember();
				campMember.setCampaignId(campaignId);
				campMember.setLeadId(leadId);
				campMember.setStatus("responded");
				sfdcConnector.createCampaignMember(campaignMembersEmailIds, campMember, salesConfigName,
						idMailMap.get(leadId));

			}

			for (String leadId : sent) {
				if (!responded.contains(leadId)) {
					CampaignMember campMember = new CampaignMember();
					campMember.setCampaignId(campaignId);
					campMember.setLeadId(leadId);
					campMember.setStatus("sent");
					sfdcConnector.createCampaignMember(campaignMembersEmailIds, campMember, salesConfigName,
							idMailMap.get(leadId));
				}
			}
			logResult.append("Connected to salesforce and updated campaigns data accordingly"+ "<br>");
		}
	}
}