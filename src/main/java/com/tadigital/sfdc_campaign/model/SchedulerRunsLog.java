/**
 * 
 */
package com.tadigital.sfdc_campaign.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author srividya.b
 *
 */

@Table(name = "scheduler_run_log")
@Entity
public class SchedulerRunsLog {
	
	@Id
	@Column(name = "log_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int logId;
	
	@Column(name = "run_id")
	int runId;
	
	@Column(name = "log_data")
	String logData;
	
	@Column(name = "endstatus")
	String endstatus;

	public int getLogId() {
		return logId;
	}

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public int getRunId() {
		return runId;
	}

	public void setRunId(int runId) {
		this.runId = runId;
	}

	public String getLogData() {
		return logData;
	}

	public void setLogData(String logData) {
		this.logData = logData;
	}

	public String getEndstatus() {
		return endstatus;
	}

	public void setEndstatus(String endstatus) {
		this.endstatus = endstatus;
	}
	
	
	
}
