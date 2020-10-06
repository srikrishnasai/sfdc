package com.tadigital.sfdc_campaign.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.tadigital.sfdc_campaign.model.SchedulerConfig;
import com.tadigital.sfdc_campaign.repo.SchedulerRepo;


/**
 * @author Srividya.Bobbiti
 *
 */

@Configuration
@EnableScheduling
public class SFDCThreadScheduler {
	
	@Autowired 
	SFDCThreadSchedulerService task; 
	
	@Autowired
	SchedulerRepo schRepo;
	
	ThreadPoolTaskScheduler tps;
	
	CronTrigger trigger;

	int pid = 0;
	
	Set<ThreadPoolTaskScheduler> threadPool = new HashSet<>();
	
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler tps = new ThreadPoolTaskScheduler();
		tps.setThreadNamePrefix("job");
		return tps;
	}

	public void changeTrigger(String cronExpression,int userid) {
		System.out.println("change trigger to: " + cronExpression);
		trigger = new CronTrigger(cronExpression);
		start(cronExpression,userid);
	}

	public void start(String cronExpression,int userid) {
		
		SchedulerConfig schConfig = new SchedulerConfig();
		tps = new ThreadPoolTaskScheduler();
		threadPool.add(tps);
		schConfig.setCronexp(cronExpression);
		schConfig.setUserid(userid);
		tps.initialize();
		task = new SFDCThreadSchedulerService();	
		List<SchedulerConfig> list = schRepo.findAll();
		if(list.size() != 0) {
			tps.setThreadNamePrefix("schedule"+(list.get(list.size()-1).getSchid()));
		}else {
			tps.setThreadNamePrefix("schedule"+0);
		}
		//schConfig.setTaskname(tps.getThreadNamePrefix());
		schRepo.save(schConfig);
		tps.schedule(task, trigger);
		
	}
	
	public void stop() {
		
		for(ThreadPoolTaskScheduler tps : threadPool) {
			System.out.println(tps.getThreadNamePrefix());
		}
		
	}
}
