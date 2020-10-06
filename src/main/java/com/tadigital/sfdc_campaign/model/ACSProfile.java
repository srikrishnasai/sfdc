package com.tadigital.sfdc_campaign.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Ravi.sangubotla
 *
 */
public class ACSProfile {

	@SerializedName("cusSfdcCustomType")
	String cusSfdcCustomType;

	@SerializedName("firstName")
	String firstName;

	@SerializedName("lastName")
	String lastName;

	@SerializedName("title")
	String title;

	@SerializedName("cusCompany")
	String cusCompany;

	@SerializedName("phone")
	String phone;

	@SerializedName("mobilePhone")
	String mobilePhone;

	@SerializedName("fax")
	String fax;

	@SerializedName("email")
	String email;

	@SerializedName("lastModified")
	String lastModified;

	@SerializedName("salutation")
	String salutation;
	
	@SerializedName("birthDate")
	String birthDate;

	/**
	 * @return the birthDate
	 */
	public String getBirthDate() {
		return birthDate;
	}

	/**
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @return the cusSfdcCustomType
	 */
	public String getCusSfdcCustomType() {
		return cusSfdcCustomType;
	}

	/**
	 * @param cusSfdcCustomType
	 *            the cusSfdcCustomType to set
	 */
	public void setCusSfdcCustomType(String cusSfdcCustomType) {
		this.cusSfdcCustomType = cusSfdcCustomType;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
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
	 * @param lastName
	 *            the lastName to set
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
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the cusCompany
	 */
	public String getCusCompany() {
		return cusCompany;
	}

	/**
	 * @param cusCompany
	 *            the cusCompany to set
	 */
	public void setCusCompany(String cusCompany) {
		this.cusCompany = cusCompany;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
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
	 * @param mobilePhone
	 *            the mobilePhone to set
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
	 * @param fax
	 *            the fax to set
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
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the lastModified
	 */
	public String getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified
	 *            the lastModified to set
	 */
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @return the salutation
	 */
	public String getSalutation() {
		return salutation;
	}

	/**
	 * @param salutation
	 *            the salutation to set
	 */
	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ACSProfile [cusSfdcCustomType=" + cusSfdcCustomType + ", firstName=" + firstName + ", lastName="
				+ lastName + ", title=" + title + ", cusCompany=" + cusCompany + ", phone=" + phone + ", mobilePhone="
				+ mobilePhone + ", fax=" + fax + ", email=" + email + ", lastModified=" + lastModified + ", salutation="
				+ salutation + "]";
	}

}
