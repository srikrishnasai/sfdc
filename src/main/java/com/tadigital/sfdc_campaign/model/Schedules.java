package com.tadigital.sfdc_campaign.model;

import java.util.Date;

public class Schedules {
	Date startDate;
	Date endDate;
	String status;
	long schId;
	String logData;
	String endStatus;
	
	public Schedules() {

	}
	public String getEndStatus() {
		return endStatus;
	}

	public void setEndStatus(String endStatus) {
		this.endStatus = endStatus;
	}


	public String getLogData() {
		return logData;
	}

	public void setLogData(String logData) {
		this.logData = logData;
	}

	public long getSchId() {
		return schId;
	}

	public void setSchId(long schId) {
		this.schId = schId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Schedules [startDate=" + startDate + ", endDate=" + endDate + ", status=" + status + "]";
	}

	public Schedules(Date startDate, Date endDate, String status, long schId) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = status;
		this.schId = schId;
	}
}
