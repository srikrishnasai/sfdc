/**
 * 
 */
package com.tadigital.sfdc_campaign.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.sobject.CampaignMember;
import com.sforce.soap.enterprise.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.tadigital.sfdc_campaign.model.SFDCConfig;

/**
 * @author nivedha.g
 *
 */
public class CampaignMemberIdRetreival {

	EnterpriseConnection connection;
	Logger logger = LoggerFactory.getLogger(CampaignMemberIdRetreival.class);
	public static final String USERNAME = "nivedhag@tadigital.com";
	public static final String SECRETTOKEN = "TEChaspect@02Vwa3EP9UytaiB90bzkMHEwyY8";
	public static final String AUTHENDPOINT = "https://login.salesforce.com/services/Soap/c/37.0/";

	private boolean login(SFDCConfig sfdcdb) {
		boolean success = false;

		try {
			ConnectorConfig config = new ConnectorConfig();
			config.setUsername(sfdcdb.getUserName());
			config.setPassword(sfdcdb.getSfdcPassword()+sfdcdb.getSecretToken());

			config.setAuthEndpoint(AUTHENDPOINT);

			connection = new EnterpriseConnection(config);

			success = true;
		} catch (ConnectionException ce) {
			logger.error("Exception in CampaignMemberIdRetreival login()::{}", ce);
		}

		return success;
	}

	private void logout() {
		try {
			connection.logout();
		} catch (ConnectionException ce) {
			logger.error("Exception in CampaignMemberIdRetreival logout()::{}", ce);
		}
	}

	public String getCampaignMemberId(String email,SFDCConfig sfdcdb) {
		Boolean loginStatus = login(sfdcdb);
		if (loginStatus) {

			String soqlQuery = "SELECT Id FROM CampaignMember WHERE Email='" + email + "'";
			try {
				QueryResult qr = connection.query(soqlQuery);

				if (qr.getSize() == 1) {

					SObject[] records = qr.getRecords();

					CampaignMember campaignMember = (CampaignMember) records[0];
					if (campaignMember != null) {
						return campaignMember.getId();
					}
				} else {
					logger.info("No campaign id found with the mail id::{}", email);
				}
			} catch (ConnectionException ce) {
				logger.error("Exception in CampaignMemberIdRetreival getCampaignMemberId()::{}", ce);
			}
		}
		logout();
		return null;
	}

}
