package com.tadigital.sfdc_campaign.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author akhilreddy.b
 *
 */
@Table(name = "sfdcacs.dashboard_data")
@Entity
public class DashboardData {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;

	@Column(name = "userid")
	int userid;

	@Column(name = "datatype")
	String datatype;

	@Column(name = "datacount")
	int datacount;
	
	@Column(name = "date")
	String date;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	
	
	public int getDatacount() {
		return datacount;
	}

	public void setDatacount(int datacount) {
		this.datacount = datacount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "DashboardData [id=" + id + ", userid=" + userid + ", datatype=" + datatype + ", datacount=" + datacount
				+ ", date=" + date + "]";
	}

	
	
	
}
