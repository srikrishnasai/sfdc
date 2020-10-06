package com.tadigital.sfdc_campaign.utils;

import java.util.Optional;

import com.tadigital.sfdc_campaign.model.SchedulerRunsLog;
import com.tadigital.sfdc_campaign.repo.SchedulerRunsLogRepo;

public class SchLogSave {

	SchedulerRunsLogRepo schLogRepo;
	SchedulerRunsLog schLog = new SchedulerRunsLog();

	public void saveLogResult(String logResult, int runId) {

		this.schLogRepo = BeanUtil.getBean(SchedulerRunsLogRepo.class);
		schLog.setLogData(logResult);
		schLog.setRunId(runId);
		if(schLogRepo == null) {
			System.out.println("repo value is null");
		}
		Optional<SchedulerRunsLog> srl = schLogRepo.findByRunId(runId);
		if (srl.isPresent()) {
			System.out.println("entered into if condition");
			schLog.setLogId(srl.get().getLogId());
			schLog.setEndstatus(srl.get().getEndstatus());
			schLogRepo.save(schLog);
		} else {
			schLogRepo.save(schLog);
		}

	}

	public void saveEndStatus(String endResult, int runId) {
		this.schLogRepo = BeanUtil.getBean(SchedulerRunsLogRepo.class);
		schLog.setEndstatus(endResult);
		schLog.setRunId(runId);
		Optional<SchedulerRunsLog> srl = schLogRepo.findByRunId(runId);
		if (srl.isPresent()) {
			schLog.setLogId(srl.get().getLogId());
			schLog.setLogData(srl.get().getLogData());
			schLogRepo.save(schLog);
		} else {
			schLogRepo.save(schLog);
		}
	}

	public String getEndStatus(int runId) {
		this.schLogRepo = BeanUtil.getBean(SchedulerRunsLogRepo.class); 
		Optional<SchedulerRunsLog> srl = schLogRepo.findByRunId(runId);
		return srl.isPresent() ? srl.get().getEndstatus() : null;
		
	}
}