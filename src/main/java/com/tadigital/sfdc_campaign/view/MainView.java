/**
* The class MainView is used to show the schedules of the logged in user. 
*/
package com.tadigital.sfdc_campaign.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;

import org.davidmoten.text.utils.WordWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.klaudeta.PaginatedGrid;

import com.tadigital.sfdc_campaign.constants.VaadinConstants;
import com.tadigital.sfdc_campaign.model.SchedulerConfig;
import com.tadigital.sfdc_campaign.model.SchedulerRunsLog;
import com.tadigital.sfdc_campaign.model.Schedules;
import com.tadigital.sfdc_campaign.repo.ACSConfigRepo;
import com.tadigital.sfdc_campaign.repo.SchedulerRepo;
import com.tadigital.sfdc_campaign.repo.SchedulerRunsLogRepo;
import com.tadigital.sfdc_campaign.service.ACSConfigServiceImpl;
import com.tadigital.sfdc_campaign.service.SFDCConfigServiceImpl;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.communication.PushMode;

/*
@author akhilreddy.b
@author srividya.b
*
*/
@Route(value = "schedules", layout = MainLayout.class)
@StyleSheet("css/mainViewStyles.css")
public class MainView extends VerticalLayout implements RouterLayout {

	private static final long serialVersionUID = 1L;

	VerticalLayout rightLayout = new VerticalLayout();
	VerticalLayout headerLayout = new VerticalLayout();
	transient Logger log = LoggerFactory.getLogger(MainView.class);

	/* The acs service */
	@Autowired
	transient ACSConfigServiceImpl acsService;

	/* The sales service */
	@Autowired
	transient SFDCConfigServiceImpl salesService;

	@Autowired
	transient ACSConfigRepo acsConfigRepo;

	PaginatedGrid<Schedules> grid = new PaginatedGrid<>();
	transient List<Schedules> schedules = new ArrayList<>();
	ListDataProvider<Schedules> dataProvider = new ListDataProvider<>(schedules);

	Checkbox refreshCheckbox = new Checkbox("Auto Refresh For Every");
	ComboBox<String> timerBox = new ComboBox<>();

	public MainView(@Autowired SchedulerRepo schedulerRepo) {
		setPadding(false);
		setHeight("100%");
		VaadinSession.getCurrent();
		HorizontalLayout contentLayout = new HorizontalLayout();
		Div buttonsContainer = new Div();
		
		buttonsContainer.addClassName("buttonsContainer");
		contentLayout.addClassName("contentLayout");
		rightLayout.addClassName("rightLayout");
		rightLayout.setSizeFull();
		Label rightLayoutLabel = new Label("Scheduled Jobs Monitor");
		rightLayoutLabel.addClassName("configText");

		Button dashBoard = new Button("Dashboard");

		Button refreshGrid = new Button();
		refreshGrid.setIcon(new Icon(VaadinIcon.REFRESH));
		refreshGrid.addClassNames(VaadinConstants.FLOATRIGHT);

		refreshGrid.addClassName(VaadinConstants.STYLEGROUP);
		refreshGrid.getStyle().set("margin-top", "4px");
		timerBox.addClassName(VaadinConstants.STYLEGROUP);
		refreshCheckbox.addClassName(VaadinConstants.STYLEGROUP);
		refreshCheckbox.getStyle().set("padding-top", "6px");
		
		timerBox.setEnabled(false);

		dashBoard.addClassNames("dashBoardButton", VaadinConstants.STYLEDBUTTON);
		dashBoard.addClickListener(e -> {
			log.info("Navigating from schedules view to home view");
			dashBoard.getUI().ifPresent(ui -> ui.navigate("home"));
		});

		refreshGrid.addClickListener(e -> {
			try {
				showSchedules(schedulerRepo, false);
			} catch (ParseException pe) {
				log.error("MainView showSchedules Exception::{}", pe);
			}
		});

		refreshCheckbox.addClickListener(e -> {
			if (refreshCheckbox.getValue()) {
				timerBox.setEnabled(true);
			} else {
				timerBox.setEnabled(false);
			}

		});

		String[] timervalues = { "1 Minutes", "2 Minutes", "3 Minutes", "4 Minutes", "5 Minutes", "10 Minutes",
				"15 Minutes" };
		timerBox.setItems(timervalues);
		buttonsContainer.add(refreshGrid, timerBox, refreshCheckbox, dashBoard);
		rightLayout.add(rightLayoutLabel, buttonsContainer);

		timerBox.addValueChangeListener(e -> {
			if (null != timerBox.getValue()) {
				int value = Integer.parseInt(timerBox.getValue().split(" ")[0]) * 60000;
				UI.getCurrent().setPollInterval(value);
			} else {
				UI.getCurrent().setPollInterval(-1);
			}
		});

		UI.getCurrent().addPollListener(run -> {
			if (getUI().isPresent()) {
				getUI().get().access(() -> {
					try {
						showSchedules(schedulerRepo, false);
					} catch (ParseException e1) {
						log.info("MainView init Method::{}", e1.getMessage());
					}
				});
			}

		});

		try {
			showSchedules(schedulerRepo, true);
			grid.setPage(5);
			grid.setPageSize(8);
			rightLayout.add(grid);
		} catch (ParseException pe) {
			log.info("MainView init method::{}", pe.getMessage());
		}
		NavigationBarView navBar = new NavigationBarView();
		navBar.home.addClassName(VaadinConstants.ACTIVE);

		contentLayout.add(navBar.sidenav, rightLayout);
		add(contentLayout);

	}

	/*
	 * showSchedules method adds the schedules grid to the right layout.
	 * 
	 * @param schedulerRepo used to retrieve all the schedules.
	 * 
	 */
	public void showSchedules(SchedulerRepo schedulerRepo, Boolean status) throws ParseException {
		if (!status) {
			schedules.clear();
		}
		SchedulerRunsLogRepo schLogRepo = BeanUtil.getBean(SchedulerRunsLogRepo.class);
		ListIterator<SchedulerConfig> iterator = schedulerRepo
				.findByUserId((int) VaadinSession.getCurrent().getAttribute("userid")).listIterator(); 

		while (iterator.hasNext()) {
			SchedulerConfig sc = iterator.next();
			if (sc.getUserid() == VaadinSession.getCurrent().getAttribute("userid")) {
				String[] cron = sc.getCronexp().split(" ");
				int min = Integer.parseInt(cron[1]) + 1;
				int hour = Integer.parseInt(cron[2]);
				if(min > 60) {
					min -= 60; 
					hour += 1;
				}
				String startDate = cron[3].split("-")[0] + "-" + cron[4].split("-")[0] + "-" + cron[6].split("-")[0]
						+ " " + cron[2] + ":" + cron[1];
				String endDate = cron[3].split("-")[1] + "-" + cron[4].split("-")[1] + "-" + cron[6].split("-")[1] + " "
						+ hour + ":" + min;
				Schedules schedule = new Schedules();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(VaadinConstants.SIMPLEDATEFORMAT);
				Calendar now = Calendar.getInstance();
				TimeZone timeZone = now.getTimeZone();
				simpleDateFormat.setTimeZone(timeZone);
				Date start = simpleDateFormat.parse(startDate);
				Date end = simpleDateFormat.parse(endDate);
				schedule.setStartDate(start);
				schedule.setEndDate(end);
				schedule.setSchId(sc.getSchid());
				String current = simpleDateFormat.format(new Date());
				Date currentDate = simpleDateFormat.parse(current);
				if (currentDate.compareTo(end) <= 0 && currentDate.compareTo(start) >= 0) {
					schedule.setStatus("Active");
				} else {
					schedule.setStatus("Inactive");
				}
				setStatus(schedule);
				schedules.add(schedule);
			}
		}

		schedules.sort((schedules1, schedules2) -> schedules1.getStatus().compareToIgnoreCase(schedules2.getStatus()));
		if (status) {
			addGridToLayout(schLogRepo);
		} else {
			grid.setDataProvider(dataProvider);
			grid.getDataProvider().refreshAll();
			grid.setEnabled(true);

			if (grid.getUI().isPresent()) {
				grid.getUI().get().getPushConfiguration().setPushMode(PushMode.MANUAL);
				grid.getUI().get().push();
			}
		}
	}

	/*
	 * showStatus method updates the status of the schedule in grid.
	 * 
	 * @param schedule status is set to Active/Inactive.
	 * 
	 */

	private void addGridToLayout(SchedulerRunsLogRepo schLogRepo) {
		Dialog dialog = new Dialog();
		dialog.setWidth("600px");
		grid.addColumn(Schedules::getStartDate).setHeader("StartDate / Time").setWidth("200px").setResizable(true);
		grid.addColumn(Schedules::getEndDate).setHeader("EndDate / Time").setWidth("200px").setResizable(true);
		grid.addColumn(Schedules::getStatus).setHeader("Active / Inactive").setResizable(true);
		grid.addColumn(Schedules::getEndStatus).setHeader("Progress").setResizable(true);
		grid.addColumn(new TAButtonRenderer<>("View Logs", item -> {
			dialog.removeAll();
			Schedules sd = (Schedules) item;
			String logData = sd.getLogData();
			long schId = sd.getSchId();
			ListIterator<SchedulerRunsLog> it = schLogRepo.findAll().listIterator();
			while (it.hasNext()) {
				SchedulerRunsLog schLog = it.next();
				if (schLog.getRunId() == schId) {
					logData = schLog.getLogData();
					break;
				}
			}

			if (logData != null) {
				String wrapped = WordWrap.from(logData).maxWidth(40).insertHyphens(true).wrap();
				Label wrappedTxt = new Label();
				wrappedTxt.getElement().setProperty("innerHTML", wrapped);
				dialog.add(wrappedTxt);
			}
			dialog.open();
		})).setHeader("Logs").setResizable(true);

		grid.setDataProvider(dataProvider);
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
					} catch (ParseException pe) {
						log.error("ParseException in MainView setStatus method::{}", pe.getMessage());
					}

				} else {
					try {
						currDate = simpleDateFormat.parse(currentTimeString);
					} catch (ParseException pe) {
						log.error("ParseException in MainView setStatus method::{}", pe.getMessage());
					}
				}
				if (schLog.getEndstatus() == null) {
					sd.setEndStatus("Running");
					if (currDate != null && (currDate.compareTo(sd.getStartDate()) >= 0)) {
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
