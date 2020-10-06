package com.tadigital.sfdc_campaign.service;

import java.util.Calendar;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.tadigital.sfdc_campaign.model.SchedulerConfig;
import com.tadigital.sfdc_campaign.model.SchedulerRuns;
import com.tadigital.sfdc_campaign.repo.SchedulerRepo;
import com.tadigital.sfdc_campaign.repo.SchedulerRunsRepo;
import com.tadigital.sfdc_campaign.utils.BeanUtil;

/**
 * @author Srividya.Bobbiti
 *
 */
@Component
public class ScheduleJob implements Job {

	private SchedulerRunsRepo schRunRepo;
	private SchedulerRepo schRepo;
	String resultStr = "";
	private SFDCSync sfdcSync;
	private ACSSync acsSync;

	public void execute(JobExecutionContext context) throws JobExecutionException {
		int userid = (int) context.getMergedJobDataMap().get("userId");
		int logId = (int) context.getMergedJobDataMap().get("logId");
		this.schRunRepo = BeanUtil.getBean(SchedulerRunsRepo.class);
		this.sfdcSync = BeanUtil.getBean(SFDCSync.class);
		this.schRepo = BeanUtil.getBean(SchedulerRepo.class);
		this.acsSync = BeanUtil.getBean(ACSSync.class);
		List<SchedulerConfig> schConfigs = schRepo.findByUserId(userid);
		SchedulerConfig schdb = schConfigs.get(0);
		SchedulerRuns schrunConfig = new SchedulerRuns();
		Calendar cal = Calendar.getInstance();
		schrunConfig.setLastrun(cal.getTime());
		schrunConfig.setUserid((int) context.getMergedJobDataMap().get("userId"));
		schrunConfig.setPid(Thread.currentThread().getId());

		StringBuilder logResult = new StringBuilder();

		schRunRepo.save(schrunConfig);

		if (schdb.getScheduletype().equals("ACS->Salesforce")) {
			resultStr = acsSync.acsData(schdb.getDataset(), userid, logResult, logId);
		} else if (schdb.getScheduletype().equals("Salesforce->ACS")) {
			resultStr = sfdcSync.fetchData(userid, logResult, logId);
		} else if (schdb.getScheduletype().equals("Bi<->Directional")) {
			resultStr = sfdcSync.fetchData(userid, logResult, logId);
			resultStr = resultStr + acsSync.acsData(schdb.getDataset(), userid, logResult, logId);
		}

		System.out.println(resultStr);

	}

}