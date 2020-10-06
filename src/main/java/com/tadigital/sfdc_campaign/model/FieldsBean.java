package com.tadigital.sfdc_campaign.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author saikrishna.sp
 *
 */

@Table(name = "sfdcacs.field_repo")
@Entity
public class FieldsBean {

	
	@Id
	@Column(name = "field_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int fieldId;
	
	@Column(name = "repo_type")
	String repoType;
	
	@Column(name = "field_name")
	String fieldName;
	
	@Column(name = "field_type")
	String fieldType;
	
	@Column(name = "user_id")
	int userId;
	
	@Column(name = "belongs_to")
	String belongs;
	
	
	@Column(name = "salesconfigid")
	Long salesconfigid;
	
	@Column(name = "acsconfigid")
	Long acsconfigid;
	
	
	
	/*@Column(name = "sales_name")
	String salesname;
	
	@Column(name = "acs_name")
	String acsname;

	public String getSalesname() {
		return salesname;
	}

	public void setSalesname(String salesname) {
		this.salesname = salesname;
	}

	public String getAcsname() {
		return acsname;
	}

	public void setAcsname(String acsname) {
		this.acsname = acsname;
	}*/

	public Long getSalesconfigid() {
		return salesconfigid;
	}

	public void setSalesconfigid(Long salesconfigid) {
		this.salesconfigid = salesconfigid;
	}

	public Long getAcsconfigid() {
		return acsconfigid;
	}

	public void setAcsconfigid(Long acsconfigid) {
		this.acsconfigid = acsconfigid;
	}

	/**
	 * @return the belongs
	 */
	public String getBelongs() {
		return belongs;
	}

	/**
	 * @param belongs the belongs to set
	 */
	public void setBelongs(String belongs) {
		this.belongs = belongs;
	}

	public int getFieldId() {
		return fieldId;
	}

	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}

	public String getRepoType() {
		return repoType;
	}

	public void setRepoType(String repoType) {
		this.repoType = repoType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "FieldsBean [fieldId=" + fieldId + ", repoType=" + repoType + ", fieldName=" + fieldName + ", fieldType="
				+ fieldType + ", userId=" + userId + ", belongs=" + belongs + ", salesconfigid=" + salesconfigid
				+ ", acsconfigid=" + acsconfigid + "]";
	}

	

	
	
	
}
