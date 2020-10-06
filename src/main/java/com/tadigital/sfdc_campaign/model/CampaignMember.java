package com.tadigital.sfdc_campaign.model;

import com.google.gson.annotations.SerializedName;

public class CampaignMember {

	@SerializedName("CampaignId")
	String campaignId;
	
	@SerializedName("LeadId")
	String leadId;
	
	@SerializedName("Status")
	String status;
	
	public String getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	public String getleadId() {
		return leadId;
	}
	public void setLeadId(String leadId) {
		this.leadId = leadId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
