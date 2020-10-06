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
@Table(name="sfdcacs.sfdc_config")
@Entity
public class SFDCConfig {
	
	@Id
	@Column(name="idsfdc_config")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long configId;
	
	@Column(name="clientid")
	String clientId;
	
	@Column(name="client_secret")
	String clientSecret;
	
	@Column(name="username")
	String userName;
	
	@Column(name="sfdcpassword")
	String sfdcPassword;
	
	@Column(name="secret_token")
	String secretToken;
	
	@Column(name="dataset")
	String sfdcData;
	
	@Column(name = "userid")
	int userid;
	
	@Column(name = "checkstatus")
	String checkstatus;
	
	@Column(name = "synctype")
	String syncType;
	
	@Column(name = "config_name")
	String configname;
	
	
	public String getConfigname() {
		return configname;
	}

	public void setConfigname(String configname) {
		this.configname = configname;
	}
	
	
	public String getSyncType() {
		return syncType;
	}

	public void setSyncType(String syncType) {
		this.syncType = syncType;
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

	/**
	 * @return the sfdcData
	 */
	public String getSfdcData() {
		return sfdcData;
	}

	/**
	 * @param sfdcData the sfdcData to set
	 */
	public void setSfdcData(String sfdcData) {
		this.sfdcData = sfdcData;
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
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the sfdcPassword
	 */
	public String getSfdcPassword() {
		return sfdcPassword;
	}

	/**
	 * @param sfdcPassword the sfdcPassword to set
	 */
	public void setSfdcPassword(String sfdcPassword) {
		this.sfdcPassword = sfdcPassword;
	}

	/**
	 * @return the secretToken
	 */
	public String getSecretToken() {
		return secretToken;
	}

	/**
	 * @param secretToken the secretToken to set
	 */
	public void setSecretToken(String secretToken) {
		this.secretToken = secretToken;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SFDCConfig [configId=" + configId + ", clientId=" + clientId + ", clientSecret=" + clientSecret
				+ ", userName=" + userName + ", sfdcPassword=" + sfdcPassword + ", secretToken=" + secretToken
				+ ", sfdcData=" + sfdcData + ", userid=" + userid + ", checkstatus=" + checkstatus + ", syncType="
				+ syncType + ", configname=" + configname + "]";
	}


	

	/**
	 * @return the idsfdc_config
	 */
	
	
}
