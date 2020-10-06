package com.tadigital.sfdc_campaign.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author nivedha.g
 *
 */
public class SFLead {
	
	
	@SerializedName("type")
	String type;

	@SerializedName("FirstName")
	String firstName;
	
	@SerializedName("LastName")
	String lastName;
	
	@SerializedName("Title")
	String title;
	
	@SerializedName("Company")
	String company;
	
	@SerializedName("Phone")
	String phone;
	
	@SerializedName("MobilePhone")
	String mobilePhone;
	
	@SerializedName("Fax")
	String fax;
	
	@SerializedName("Email")
	String email;
	
	@SerializedName("LastModifiedDate")
	String lastModifiedDate;
	
	@SerializedName("Salutation")
	String salutation;

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the mobilePhone
	 */
	public String getMobilePhone() {
		return mobilePhone;
	}

	/**
	 * @param mobilePhone the mobilePhone to set
	 */
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	/**
	 * @return the fax
	 */
	public String getFax() {
		return fax;
	}

	/**
	 * @param fax the fax to set
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the lastModifiedDate
	 */
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * @return the salutation
	 */
	public String getSalutation() {
		return salutation;
	}

	/**
	 * @param salutation the salutation to set
	 */
	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SFLead [type=" + type + ", firstName=" + firstName + ", lastName=" + lastName + ", title=" + title
				+ ", company=" + company + ", phone=" + phone + ", mobilePhone=" + mobilePhone + ", fax=" + fax
				+ ", email=" + email + ", lastModifiedDate=" + lastModifiedDate + ", salutation=" + salutation + "]";
	}
	
	
	
}
