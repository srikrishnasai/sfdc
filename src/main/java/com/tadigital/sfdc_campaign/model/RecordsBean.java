package com.tadigital.sfdc_campaign.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author nivedha.g
 *
 */
public class RecordsBean {
	@SerializedName("Id")
	String id;
	
	/**
	 * @return the accountId
	 */
	public String getAccountId() {
		return AccountId;
	}

	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(String accountId) {
		AccountId = accountId;
	}

	@SerializedName("AccountId")
	String AccountId;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

}
