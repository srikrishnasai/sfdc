package com.tadigital.sfdc_campaign.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Ravi.sangubotla
 *
 */
@Table(name = "sfdcacs.logins")
@Entity
public class TAUser {

	@Id
	@Column(name = "userid")
	Integer userid;

	@Column(name = "username")
	String userName;

	@Column(name = "password")
	String password;
	
	@Column(name = "firstname")
	String firstname;

	@Column(name = "lastname")
	String lastname;

	@Column(name = "subscribed")
	String subscribed;
	
	@Column(name = "otp")
	String otp;

	public TAUser() {
	}

	/**
	 * @return the userid
	 */
	public int getUserid() {
		return userid;
	}

	/**
	 * @param userid the userid to set
	 */
	public void setUserid(int userid) {
		this.userid = userid;
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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getSubscribed() {
		return subscribed;
	}

	public void setSubscribed(String subscribed) {
		this.subscribed = subscribed;
	}
	
	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	@Override
	public String toString() {
		return "TAUser [userid=" + userid + ", userName=" + userName + ", password=" + password + ", firstname="
				+ firstname + ", lastname=" + lastname + ", subscribed=" + subscribed + ", otp=" + otp + "]";
	}

	
}
