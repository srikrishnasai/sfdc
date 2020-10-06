package com.tadigital.sfdc_campaign.service;

import java.util.Calendar;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tadigital.sfdc_campaign.model.SchedulerRuns;
import com.tadigital.sfdc_campaign.repo.SchedulerRunsRepo;
import com.vaadin.flow.server.VaadinSession;


/**
 * @author Srividya.Bobbiti
 *
 */

@Component
public class SFDCThreadSchedulerService implements Runnable {

	@Autowired
	SchedulerRunsRepo schrun;
	
	//private int userId;
	
	@Override
	public void run() {
		SchedulerRuns schrunConfig = new SchedulerRuns();
		Calendar cal = Calendar.getInstance();
		schrunConfig.setLastrun(cal.getTime());
		schrunConfig.setPid(Thread.currentThread().getId());
		schrunConfig.setUserid((int) VaadinSession.getCurrent().getAttribute("userid"));
		schrun.save(schrunConfig);
		//String resultStr = sfdcSync.fetchData(userId);
		System.out.println("hello");
	

	}

	public void stop(long id) {
		Set<Thread> setOfThread = Thread.getAllStackTraces().keySet();

		// Iterate over set to find yours
		for (Thread thread : setOfThread) {
			if (thread.getId() == id) {
				thread.interrupt();
			}
		}
	}

}
