package com.tadigital.sfdc_campaign.model;

import java.util.Date;

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


@Table(name = "sfdcacs.scheduler_runs")
@Entity

public class SchedulerRuns {
	@Id
	@Column(name = "runid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int runid;
	
	@Column
	int userid;
	
	@Column
	Date lastrun;
	
	@Column
	long pid;
	
	
	public long getPid() {
		return pid;
	}
	public void setPid(long pid) {
		this.pid = pid;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public Date getLastrun() {
		return lastrun;
	}
	public void setLastrun(Date lastrun) {
		this.lastrun = lastrun;
	}
}
