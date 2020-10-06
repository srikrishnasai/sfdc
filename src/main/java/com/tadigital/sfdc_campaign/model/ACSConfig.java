package com.tadigital.sfdc_campaign.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author nivedha.g
 *
 */
@Table(name = "sfdcacs.acs_config")
@Entity
public class ACSConfig {

	@Id
	@Column(name = "idacs_config")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long configId;

	@Column(name = "clientid")
	String clientId;

	@Column(name = "client_secret")
	String clientSecret;

	@Column(name = "organization_id")
	String organizationId;

	@Column(name = "tech_account_id")
	String techAccountId;

	@Column(name = "userid")
	int userid;

	@Column(name = "checkstatus")
	String checkstatus;
	
	@Column(name = "config_name")
	String configname;
	
	
	public String getConfigname() {
		return configname;
	}

	public void setConfigname(String configname) {
		this.configname = configname;
	}

	public String getCheckstatus() {
		return checkstatus;
	}

	public void setCheckstatus(String checkstatus) {
		this.checkstatus = checkstatus;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public Long getConfigId() {
		return configId;
	}

	public void setConfigId(Long configId) {
		this.configId = configId;
	}

	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return the clientSecret
	 */
	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * @param clientSecret the clientSecret to set
	 */
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	/**
	 * @return the organizationId
	 */
	public String getOrganizationId() {
		return organizationId;
	}

	/**
	 * @param organizationId the organizationId to set
	 */
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	/**
	 * @return the techAccountId
	 */
	public String getTechAccountId() {
		return techAccountId;
	}

	/**
	 * @param techAccountId the techAccountId to set
	 */
	public void setTechAccountId(String techAccountId) {
		this.techAccountId = techAccountId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ACSConfig [configId=" + configId + ", clentId=" + clientId + ", clientSecret=" + clientSecret
				+ ", organizationId=" + organizationId + ", techAccountId=" + techAccountId + "]";
	}

}
