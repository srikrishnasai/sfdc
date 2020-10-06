/**
 * 
 */
package com.tadigital.sfdc_campaign.model;

/**
 * @author nivedha.g
 *
 */
public class CampaignMemberResponse {

	String message;
	String errorCode;
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}
	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CampaignMemberResponse [message=" + message + ", errorCode=" + errorCode + "]";
	}
	
	
}
