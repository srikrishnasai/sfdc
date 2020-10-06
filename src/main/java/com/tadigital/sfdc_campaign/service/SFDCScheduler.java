package com.tadigital.sfdc_campaign.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ListIterator;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.vaadin.klaudeta.PaginatedGrid;

import com.tadigital.sfdc_campaign.model.SchedulerRuns;
import com.tadigital.sfdc_campaign.model.SchedulerRunsLog;
import com.tadigital.sfdc_campaign.model.Schedules;
import com.tadigital.sfdc_campaign.repo.SchedulerRunsLogRepo;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.server.VaadinSession;


/**
 * @author Srividya.Bobbiti
 *
 */

@Component
public class SFDCScheduler implements Runnable {
	
	transient Logger log = LoggerFactory.getLogger(SFDCScheduler.class);

	@SuppressWarnings("rawtypes")
	ScheduledFuture scheduledFuture;

	TaskScheduler taskScheduler;
	
	PaginatedGrid grid;
    
	public void schedule(String cronExpressionStr) {
		VaadinSession.getCurrent().lock();
		grid = VaadinSession.getCurrent().getAttribute(PaginatedGrid.class);
		if (taskScheduler == null) {
			this.taskScheduler = new ConcurrentTaskScheduler();
		}
		if (this.scheduledFuture != null) {
			this.scheduledFuture.cancel(true);
		}
		scheduledFuture = this.taskScheduler.schedule(this, new CronTrigger(cronExpressionStr));
	}

	@Override
	public void run() {
		SchedulerRuns schrunConfig = new SchedulerRuns();
		ListDataProvider<Schedules> s= (ListDataProvider<Schedules>) grid.getDataProvider();
		ArrayList<Schedules> al = (ArrayList<Schedules>) s.getItems();
		ListIterator<Schedules> it = al.listIterator();
		while(it.hasNext()) {
			Schedules sd = it.next();
			System.out.println(sd.getEndStatus());
			setStatus(sd);
			grid.getDataProvider().refreshAll();
		}
	}
	public void setStatus(Schedules sd) {
		SchedulerRunsLogRepo schLogRepo = BeanUtil.getBean(SchedulerRunsLogRepo.class);
		ListIterator<SchedulerRunsLog> it = schLogRepo.findAll().listIterator();
		Calendar cal = Calendar.getInstance();
		while (it.hasNext()) {
			SchedulerRunsLog schLog = it.next();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

			if (schLog.getRunId() == sd.getSchId()) {
				String currentTimeString = simpleDateFormat.format(cal.getTime());
				Date currDate = null;
				if (currentTimeString.charAt(0) == '0') {
					String currentTime;
					currentTime = currentTimeString.substring(1, currentTimeString.length());
					try {
						currDate = simpleDateFormat.parse(currentTime);
					} catch (ParseException e) {
						log.error("ParserException occured is ::{}",e);
					}

				} else {
					try {
						currDate = simpleDateFormat.parse(currentTimeString);
					} catch (ParseException e) {
						log.error("ParserException occured is ::{}",e);
					}
				}
				if (schLog.getEndstatus() == null) {
					if ((currDate.compareTo(sd.getStartDate()) >= 0)) {
						sd.setEndStatus("Running");
						break;
					} else {
						sd.setEndStatus("Yet to run");
						break;
					}
				} else if (schLog.getEndstatus() != null) {
					sd.setEndStatus(schLog.getEndstatus());
					break;
				}
			}
		}
	}


}
