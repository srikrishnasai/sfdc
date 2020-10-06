package com.tadigital.sfdc_campaign.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * @author Srividya.Bobbiti
 *
 */

@Table(name = "sfdcacs.sch_config")
@Entity

public class SchedulerConfig {

	@Id
	@Column(name = "schid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long schid;

	@Column(name = "userid")
	Integer userid;

	@Column(name = "cronexp")
	String cronexp;
	
	@Column(name = "taskname")
	String taskname;
	
	@Column(name = "synctype")
	String synctype;

	@Column(name = "dataset")
	String dataset;
	
	@Column(name = "scheduletype")
	String scheduletype;
	
	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public String getScheduletype() {
		return scheduletype;
	}

	public void setScheduletype(String scheduletype) {
		this.scheduletype = scheduletype;
	}

	/**
	 * @return the synctype
	 */
	public String getSynctype() {
		return synctype;
	}

	/**
	 * @param synctype the synctype to set
	 */
	public void setSynctype(String synctype) {
		this.synctype = synctype;
	}

	public String getTaskname() {
		return taskname;
	}

	public void setTaskname(String taskname) {
		this.taskname = taskname;
	}

	public long getSchid() {
		return schid;
	}

	public void setSchid(long schid) {
		this.schid = schid;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public String getCronexp() {
		return cronexp;
	}

	public void setCronexp(String cronexp) {
		this.cronexp = cronexp;
	}

	

}
