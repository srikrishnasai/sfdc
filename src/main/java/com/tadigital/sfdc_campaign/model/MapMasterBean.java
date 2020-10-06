package com.tadigital.sfdc_campaign.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * @author akhilreddy.b
 *
 */

@Table(name = "sfdcacs.map_master")
@Entity

public class MapMasterBean {

	@Id
	@Column(name = "map_config_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int mapconfigid;

	@Column(name = "user_id")
	int userid;

	@Column(name = "config_name")
	String configname;
	
	@Column(name = "status")
	String status;
	
	String salesconfig;
	
	String acsconfig;

	@Column(name = "salesconfigid")
	long salesconfigid;
	
	@Column(name = "acsconfigid")
	long acsconfigid;
	
	

	public String getSalesconfig() {
		return salesconfig;
	}

	public void setSalesconfig(String salesconfig) {
		this.salesconfig = salesconfig;
	}

	public String getAcsconfig() {
		return acsconfig;
	}

	public void setAcsconfig(String acsconfig) {
		this.acsconfig = acsconfig;
	}

	public int getMapconfigid() {
		return mapconfigid;
	}

	public void setMapconfigid(int mapconfigid) {
		this.mapconfigid = mapconfigid;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getConfigname() {
		return configname;
	}

	public void setConfigname(String configname) {
		this.configname = configname;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getSalesconfigid() {
		return salesconfigid;
	}

	public void setSalesconfigid(long salesconfigid) {
		this.salesconfigid = salesconfigid;
	}

	public long getAcsconfigid() {
		return acsconfigid;
	}

	public void setAcsconfigid(long acsconfigid) {
		this.acsconfigid = acsconfigid;
	}

	@Override
	public String toString() {
		return "MapMasterBean [mapconfigid=" + mapconfigid + ", userid=" + userid + ", configname=" + configname
				+ ", status=" + status + ", salesconfigid=" + salesconfigid + ", acsconfigid=" + acsconfigid + "]";
	}
	
	
	
	
}
