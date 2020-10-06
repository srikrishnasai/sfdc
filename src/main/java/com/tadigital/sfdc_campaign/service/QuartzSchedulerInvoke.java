package com.tadigital.sfdc_campaign.service;

import java.util.List;
import java.util.Objects;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tadigital.sfdc_campaign.model.SchedulerConfig;
import com.tadigital.sfdc_campaign.model.SchedulerRunsLog;
import com.tadigital.sfdc_campaign.repo.SchedulerRepo;
import com.tadigital.sfdc_campaign.repo.SchedulerRunsLogRepo;
import com.tadigital.sfdc_campaign.repo.SchedulerRunsRepo;
import com.vaadin.flow.server.VaadinSession;

/**
 * @author Srividya.Bobbiti
 *
 */
@Service
public class QuartzSchedulerInvoke {

	@Autowired
	SchedulerRunsRepo schrun;

	@Autowired
	SchedulerRepo schRepo;
	
	@Autowired
	SchedulerRunsLogRepo schLogRepo;
	
	Logger logger = LoggerFactory.getLogger(ACSSync.class);

	public void jobTrigger(String cronExp, String synctype,String dataset,String scheduletype) {
		
		SchedulerConfig schConfig = new SchedulerConfig();
		String jobKey;
		List<SchedulerConfig> list = schRepo.findAll();
		if (!list.isEmpty()) {
			jobKey = "schedule" + (list.get(list.size() - 1).getSchid());
		} else {
			jobKey = "schedule" + 0;
		}

		JobDetail job = JobBuilder.newJob(ScheduleJob.class).withIdentity(jobKey, "group1").build();
		job.getJobDataMap().putIfAbsent("userId", (int) VaadinSession.getCurrent().getAttribute("userid"));
		job.getJobDataMap().putIfAbsent("logId", list.size()+1);
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobKey, "group1")
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExp)).build();
		
		schConfig.setCronexp(cronExp);
		schConfig.setUserid((int) VaadinSession.getCurrent().getAttribute("userid"));
		schConfig.setTaskname(job.getKey().getName());
		schConfig.setSynctype(synctype);
		schConfig.setDataset(dataset);
		schConfig.setScheduletype(scheduletype);
		schRepo.save(schConfig);
		
		SchedulerRunsLog schRunLog = new SchedulerRunsLog();
		schRunLog.setRunId(list.size()+1);
		schLogRepo.save(schRunLog);
		
		Scheduler scheduler;
		try {
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(job, trigger);
			searchJob(job.getKey().getName());
		} catch (SchedulerException e) {
			logger.info("Could not schedule a job::{}",e);
		}

	}

	public void searchJob(String jobName) {
		try {
			Scheduler sch = new StdSchedulerFactory().getScheduler();
			for (String groupName : sch.getJobGroupNames()) {
				for (JobKey jobKey : sch.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
					if (Objects.equals(jobName, jobKey.getName())) {
						sch.interrupt(jobKey);
					}
				}
			}
		} catch (SchedulerException e) {
			logger.info("could not find a job in search::{}",e);
		}

	}
}
