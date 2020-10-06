package com.tadigital.sfdc_campaign.model;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author akhilreddy.b
 *
 */

@Table(name = "sfdcacs.analytics_config")
@Entity
public class AnalyticsConfig {

	@Id
	@Column(name = "analytics_config_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int analyticsConfigId;

	@Column(name = "user_id")
	int userId;

	@Column(name = "analytics_config_name")
	String analyticsConfigName;

	@Column(name = "analytics_config_type")
	String analyticsConfigType;

	@Column(name = "rs_id")
	String rsId;

	@Column(name = "dimensions_id")
	String dimensionsId;

	@Column(name = "json_file")
	byte[] jsonFile;

	@Column(name = "file_name")
	String fileName;

	@Column(name = "status")
	String status;
	
	@Column(name = "view_id")
	String viewId;
	
	@Column(name = "start_date")
	String startDate;
	
	@Column(name = "end_date")
	String endDate;

	/**
	 * @return the viewId
	 */
	public String getViewId() {
		return viewId;
	}

	/**
	 * @param viewId the viewId to set
	 */
	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	@Column(name = "campaign_config_id")
	Long campaignConfigId;

	@Column(name = "campaign_config_name")
	String campaignConfigName;

	@Column(name = "salesforce_config_id")
	Long salesforceConfigId;

	@Column(name = "salesforce_config_name")
	String salesforceConfigName;

	/**
	 * @return the analyticsConfigId
	 */
	public int getAnalyticsConfigId() {
		return analyticsConfigId;
	}

	/**
	 * @param analyticsConfigId
	 *            the analyticsConfigId to set
	 */
	public void setAnalyticsConfigId(int analyticsConfigId) {
		this.analyticsConfigId = analyticsConfigId;
	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * @return the analyticsConfigName
	 */
	public String getAnalyticsConfigName() {
		return analyticsConfigName;
	}

	/**
	 * @param analyticsConfigName
	 *            the analyticsConfigName to set
	 */
	public void setAnalyticsConfigName(String analyticsConfigName) {
		this.analyticsConfigName = analyticsConfigName;
	}

	/**
	 * @return the analyticsConfigType
	 */
	public String getAnalyticsConfigType() {
		return analyticsConfigType;
	}

	/**
	 * @param analyticsConfigType
	 *            the analyticsConfigType to set
	 */
	public void setAnalyticsConfigType(String analyticsConfigType) {
		this.analyticsConfigType = analyticsConfigType;
	}

	/**
	 * @return the rsId
	 */
	public String getRsId() {
		return rsId;
	}

	/**
	 * @param rsId
	 *            the rsId to set
	 */
	public void setRsId(String rsId) {
		this.rsId = rsId;
	}

	/**
	 * @return the dimensionsId
	 */
	public String getDimensionsId() {
		return dimensionsId;
	}

	/**
	 * @param dimensionsId
	 *            the dimensionsId to set
	 */
	public void setDimensionsId(String dimensionsId) {
		this.dimensionsId = dimensionsId;
	}

	/**
	 * @return the jsonFile
	 */
	public byte[] getJsonFile() {
		return jsonFile;
	}

	/**
	 * @param bytes
	 *            the jsonFile to set the google analytics configurations
	 */
	public void setJsonFile(byte[] bytes) {
		this.jsonFile = bytes;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the campaignConfigId
	 */
	public Long getCampaignConfigId() {
		return campaignConfigId;
	}

	/**
	 * @param campaignConfigId
	 *            the campaignConfigId to set
	 */
	public void setCampaignConfigId(Long campaignConfigId) {
		this.campaignConfigId = campaignConfigId;
	}

	/**
	 * @return the campaignConfigName
	 */
	public String getCampaignConfigName() {
		return campaignConfigName;
	}

	/**
	 * @param campaignConfigName
	 *            the campaignConfigName to set
	 */
	public void setCampaignConfigName(String campaignConfigName) {
		this.campaignConfigName = campaignConfigName;
	}

	/**
	 * @return the salesforceConfigId
	 */
	public Long getSalesforceConfigId() {
		return salesforceConfigId;
	}

	/**
	 * @param salesforceConfigId
	 *            the salesforceConfigId to set
	 */
	public void setSalesforceConfigId(Long salesforceConfigId) {
		this.salesforceConfigId = salesforceConfigId;
	}

	/**
	 * @return the salesforceConfigName
	 */
	public String getSalesforceConfigName() {
		return salesforceConfigName;
	}

	/**
	 * @param salesforceConfigName
	 *            the salesforceConfigName to set
	 */
	public void setSalesforceConfigName(String salesforceConfigName) {
		this.salesforceConfigName = salesforceConfigName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AnalyticsConfig [analyticsConfigId=" + analyticsConfigId + ", userId=" + userId
				+ ", analyticsConfigName=" + analyticsConfigName + ", analyticsConfigType=" + analyticsConfigType
				+ ", rsId=" + rsId + ", dimensionsId=" + dimensionsId + ", jsonFile=" + Arrays.toString(jsonFile)
				+ ", fileName=" + fileName + ", status=" + status + ", campaignConfigId=" + campaignConfigId
				+ ", campaignConfigName=" + campaignConfigName + ", salesforceConfigId=" + salesforceConfigId
				+ ", salesforceConfigName=" + salesforceConfigName + "]";
	}

}
