package com.tadigital.sfdc_campaign.view;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tadigital.sfdc_campaign.constants.VaadinConstants;
import com.tadigital.sfdc_campaign.model.AnalyticsConfig;
import com.tadigital.sfdc_campaign.model.SchedulerConfig;
import com.tadigital.sfdc_campaign.repo.AnalyticsConfigRepo;
import com.tadigital.sfdc_campaign.repo.MapMasterRepo;
import com.tadigital.sfdc_campaign.repo.SchedulerRepo;
import com.tadigital.sfdc_campaign.repo.SchedulerRunsLogRepo;
import com.tadigital.sfdc_campaign.service.AdobeAnalytics;
import com.tadigital.sfdc_campaign.service.BeanDecryption;
import com.tadigital.sfdc_campaign.service.QuartzSchedulerInvoke;
import com.tadigital.sfdc_campaign.service.SFDCScheduler;
import com.tadigital.sfdc_campaign.threads.GoogleAnalyticsThread;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

/**
 * @author Srividya.Bobbiti
 *
 */

@Route(value = "scheduler", layout = MainLayout.class)
@StyleSheet("css/mainViewStyles.css")
public class SchedulerView extends VerticalLayout implements BeanDecryption {

	private SchedulerConfig schConfig = new SchedulerConfig();

	Logger logger = LoggerFactory.getLogger(SchedulerView.class);

	private static final long serialVersionUID = 1L;
	private DatePicker startDate = new DatePicker("Start Date : ");
	private DatePicker endDate = new DatePicker("End Date : ");
	private TextField dayTime = new TextField("Every Day At : ");
	RadioButtonGroup<String> hourlyNotification = new RadioButtonGroup<>();
	private TextField monthlyDay = new TextField("Every month on");
	private TextField day = new TextField("After days");
	private String cronexp = "";
	Label syncLabel = new Label("SyncType:");
	Label configType = new Label("Config Type:");
	VerticalLayout rightLayout = new VerticalLayout();
	VerticalLayout headerLayout = new VerticalLayout();
	String[] split;
	String hours;
	String minutes;
	RadioButtonGroup<String> synctype = new RadioButtonGroup<String>();
	RadioButtonGroup<String> analyticsType = new RadioButtonGroup<>();
	Label dataSetType = new Label("Dataset Type:");
	Checkbox lead = new Checkbox("Lead");
	Checkbox contact = new Checkbox("Contact");
	Checkbox opportunities = new Checkbox("Opportunities");
	Checkbox prospects = new Checkbox("Prospects");
	String sfdcData = "";
	ComboBox<String> jobType = new ComboBox<>("Job Type");
	ComboBox<String> analyticsConfigComboBox = new ComboBox<>("Choose Analytics Config");
	ComboBox<String> leadDataSetType = new ComboBox<>("Map Configurations");
	ComboBox<String> contactDataSetType = new ComboBox<>("Map Configurations");
	ComboBox<String> comboBox = new ComboBox<>("Schedule for");
	List<String> analyticsConfigComboBoxList = new ArrayList<>();
	Button campaignSyncSubmit = new Button("submit");
	Button save = new Button("Save");
	HorizontalLayout selection = new HorizontalLayout();
	HorizontalLayout dataSet = new HorizontalLayout();

	@Autowired
	transient SchedulerRepo schRepo;

	@Autowired
	SchedulerRunsLogRepo schLogRepo;

	@Autowired
	transient SFDCScheduler scheduler;

	@Autowired
	transient QuartzSchedulerInvoke qzScheduler;

	transient MapMasterRepo mapMasterRepo;

	transient AnalyticsConfigRepo analyticsConfigRepo;

	transient List<String> mapList;

	public SchedulerView() {
		setPadding(false);
		setMargin(false);
		setHeight("100%");
		mapMasterRepo = BeanUtil.getBean(MapMasterRepo.class);
		analyticsConfigRepo = BeanUtil.getBean(AnalyticsConfigRepo.class);
		int userId = (int) VaadinSession.getCurrent().getAttribute(VaadinConstants.USERID);
		mapList = mapMasterRepo.findConfignameByUseridAndStatus(userId, "Active");
		opportunities.setEnabled(false);
		prospects.setEnabled(false);
		Label headerLbl = new Label("Scheduler - Configuration");
		headerLbl.addClassName("configText");
		headerLayout.add(headerLbl);
		rightLayout.add(headerLayout);
		ComboBox<String> scheduleType = new ComboBox<>("Schedule Type");
		scheduleType.getStyle().set("width", "45%");
		jobType.getStyle().set("width", "45%");
		analyticsConfigComboBox.getStyle().set("width", "45%");
		jobType.setItems("Activity Sync", "Profile Sync");
		scheduleType.setItems("ACS->Salesforce", "Salesforce->ACS", "Bi<->Directional");

		rightLayout.add(jobType, scheduleType);

		HorizontalLayout contentLayout = new HorizontalLayout();
		contentLayout.addClassName("contentLayout");
		rightLayout.addClassName("rightLayout");
		rightLayout.getStyle().set("padding", "0px");
		rightLayout.getStyle().set("margin", "0px");
		rightLayout.getStyle().set("height", "100%");
		rightLayout.getStyle().set("overflow-y", "scroll");
		dataSetType.addClassName("label");
		dataSetType.getStyle().set("font-size", "16px");
		NavigationBarView navBar = new NavigationBarView();
		navBar.scheduler.addClassName("active");
		synctype.setItems("Full Sync", "Last Update");
		dayTime.setLabel("at");
		dayTime.setPlaceholder("In 24 hrs format");
		HorizontalLayout layoutOne = new HorizontalLayout();
		layoutOne.getStyle().set("width", "45%");
		layoutOne.getStyle().set("margin-top", "10px");
		layoutOne.add(startDate);
		layoutOne.add(endDate);

		HorizontalLayout syncNowLayout = new HorizontalLayout();
		HorizontalLayout saveButtonLayout = new HorizontalLayout();
		Checkbox syncNowCheckBox = new Checkbox("Schedule Now");
		syncNowLayout.getStyle().set("width", "45%");
		Button syncButton = new Button("Save");
		syncButton.addClassNames(VaadinConstants.CURSORPOINTER, VaadinConstants.STYLEDBUTTON);
		syncButton.setVisible(false);
		saveButtonLayout.add(syncButton);

		HorizontalLayout configTypeGroup = new HorizontalLayout();
		configTypeGroup.getStyle().set("width", "45%");
		analyticsType.setItems("Google Analytics", "Adobe Analytics");
		configType.addClassName("label");
		configTypeGroup.add(configType, analyticsType);

		configTypeGroup.setVisible(false);
		campaignSyncSubmit.setVisible(false);
		analyticsConfigComboBox.setVisible(false);
		scheduleType.setVisible(false);
		syncNowLayout.setVisible(false);
		saveButtonLayout.setVisible(false);
		layoutOne.setVisible(false);
		selection.setVisible(false);
		dataSet.setVisible(false);
		save.setVisible(false);

		rightLayout.add(configTypeGroup, analyticsConfigComboBox, campaignSyncSubmit);

		campaignSyncSubmit.addClassNames("cursorPointer", VaadinConstants.STYLEDBUTTON);
		analyticsType.addValueChangeListener(e -> {
			analyticsConfigComboBoxList.clear();
			if (analyticsType.getValue().equals("Google Analytics")) {
				List<AnalyticsConfig> analyticsConfigList = analyticsConfigRepo
						.findAllByStatusNotAndAnalyticsConfigTypeAndUserId("Deleted", "Google Analytics", userId);
				for (int i = 0; i < analyticsConfigList.size(); i++) {
					analyticsConfigComboBoxList.add(analyticsConfigList.get(i).getAnalyticsConfigName());
				}
			} else {
				List<AnalyticsConfig> analyticsConfigList = analyticsConfigRepo
						.findAllByStatusNotAndAnalyticsConfigTypeAndUserId("Deleted", "Adobe Analytics", userId);
				for (int i = 0; i < analyticsConfigList.size(); i++) {
					analyticsConfigComboBoxList.add(analyticsConfigList.get(i).getAnalyticsConfigName());
				}
			}
			analyticsConfigComboBox.setItems(analyticsConfigComboBoxList);
		});

		jobType.addValueChangeListener(e -> {
			if (jobType.getValue() != null && jobType.getValue().equals("Activity Sync")) {
				configTypeGroup.setVisible(true);
				campaignSyncSubmit.setVisible(true);
				analyticsConfigComboBox.setVisible(true);
				scheduleType.setVisible(false);
				syncNowLayout.setVisible(false);
				saveButtonLayout.setVisible(false);
				layoutOne.setVisible(false);
				selection.setVisible(false);
				dataSet.setVisible(false);
				save.setVisible(false);
			} else {
				configTypeGroup.setVisible(false);
				analyticsConfigComboBox.setVisible(false);
				campaignSyncSubmit.setVisible(false);
				scheduleType.setVisible(true);
				syncNowLayout.setVisible(true);
				saveButtonLayout.setVisible(true);
				layoutOne.setVisible(true);
				selection.setVisible(true);
				dataSet.setVisible(true);
				save.setVisible(true);
			}
		});

		campaignSyncSubmit.addClickListener(e -> {
			SchedulerConfig sc = new SchedulerConfig();
			sc.setUserid(userId);
			sc.setCronexp(generateCronForSyncNow());
			List<SchedulerConfig> scList = schRepo.findAll();
			if(scList.isEmpty()) {
				sc.setTaskname("schedule0");
			} else {
				sc.setTaskname("schedule" + scList.get(scList.size() - 1).getSchid());
			}
			sc.setScheduletype("ACS->Salesforce");
			
			
			
			AnalyticsConfig analyticsConfig = analyticsConfigRepo
					.findAllByAnalyticsConfigNameAndUserId(analyticsConfigComboBox.getValue().trim(), userId);
			analyticsConfig = (AnalyticsConfig) decrypt(analyticsConfig);
			StringBuilder logResult = new StringBuilder();
			if (analyticsType.getValue().equals("Google Analytics")) {
				Runnable obj = new GoogleAnalyticsThread(new ByteArrayInputStream(analyticsConfig.getJsonFile()),
						analyticsConfig.getSalesforceConfigName(),logResult,scList.size()+1,analyticsConfig.getViewId(),analyticsConfig.getStartDate(),analyticsConfig.getEndDate());
				Thread googleAnalyticsThread = new Thread(obj);
				googleAnalyticsThread.start();
				
				 /*CampaignAnalyticsReporting car = new CampaignAnalyticsReporting();
				 car.report(new ByteArrayInputStream(analyticsConfig.getJsonFile()),
				 analyticsConfig.getSalesforceConfigName(), logResult, scList.size()+1);*/
				 
			} else {
				/*Runnable obj = new AdobeAnalyticsThread(analyticsConfig.getCampaignConfigName(),
						analyticsConfig.getSalesforceConfigName(), analyticsConfig.getRsId(),
						analyticsConfig.getDimensionsId(), logResult, scList.size()+1);
				Thread adobeAnalyticsThread = new Thread(obj);
				adobeAnalyticsThread.start();*/
				
				  AdobeAnalytics adobeAnalytics = new AdobeAnalytics();
				 adobeAnalytics.analyticsReport(analyticsConfig.getCampaignConfigName(),
				  analyticsConfig.getSalesforceConfigName(), analyticsConfig.getRsId(),
				 analyticsConfig.getDimensionsId(), logResult, scList.size()+1,analyticsConfig.getStartDate(),analyticsConfig.getEndDate());
				 
			}
			schRepo.save(sc);
			campaignSyncSubmit.getUI().ifPresent(ui -> ui.navigate("schedules"));
		});
		syncNowCheckBox.addValueChangeListener(e -> {
			if (!syncNowCheckBox.isEmpty()) {
				saveButtonLayout.setVisible(true);
				syncButton.setVisible(true);
				startDate.setVisible(false);
				endDate.setVisible(false);
				comboBox.setVisible(false);
			} else {
				if (!startDate.isVisible() || !endDate.isVisible() || !comboBox.isVisible()) {
					saveButtonLayout.setVisible(false);
					syncButton.setVisible(false);
					startDate.setVisible(true);
					endDate.setVisible(true);
					comboBox.setVisible(true);
				}
			}
		});

		syncButton.addClickListener(e -> {
			String cronExp = generateCronForSyncNow();
			System.out.println(cronExp);
			if (lead.getValue().equals(true) && contact.getValue().equals(true)) {
				sfdcData = lead.getLabel() + "+" + leadDataSetType.getValue() + "," + contact.getLabel() + "+"
						+ contactDataSetType.getValue();
				lead.setValue(false);
				contact.setValue(false);
			} else if (lead.getValue().equals(true)) {
				sfdcData = lead.getLabel() + "+" + leadDataSetType.getValue();
			} else if (contact.getValue().equals(true)) {
				sfdcData = contact.getLabel() + "+" + contactDataSetType.getValue();
			}
			qzScheduler.jobTrigger(cronExp, synctype.getValue(), sfdcData, scheduleType.getValue());
			syncButton.getUI().ifPresent(ui -> ui.navigate("home"));
		});

		syncNowLayout.add(syncNowCheckBox);

		dataSetType.addClassName("label");
		VerticalLayout dataSetsLayout = new VerticalLayout();
		HorizontalLayout dataSetDropdowns = new HorizontalLayout();
		dataSetsLayout.getStyle().set("width", "45%");
		dataSetsLayout.setPadding(false);
		dataSetsLayout.setSpacing(false);

		dataSet.getStyle().set("margin", "5px 0px");
		dataSet.add(dataSetType);
		dataSet.add(lead);
		dataSet.add(contact);
		dataSet.add(opportunities);

		dataSetsLayout.add(dataSet, dataSetDropdowns);
		rightLayout.add(dataSetsLayout, syncNowLayout, saveButtonLayout, layoutOne);

		leadDataSetType.setItems(mapList);
		contactDataSetType.setItems(mapList);

		selection.getStyle().set("width", "45%");
		comboBox.setItems("Every Day", "Every Month", "After Days");
		selection.add(comboBox);
		rightLayout.add(selection);
		HorizontalLayout selectionvalue = new HorizontalLayout();
		save.addClassNames("cursorPointer", VaadinConstants.STYLEDBUTTON);

		lead.addClickListener(e -> {
			if (lead.getValue()) {
				dataSetDropdowns.add(leadDataSetType);
			} else {
				dataSetDropdowns.remove(leadDataSetType);
			}
		});

		contact.addClickListener(e -> {
			if (contact.getValue()) {
				dataSetDropdowns.add(contactDataSetType);
			} else {
				dataSetDropdowns.remove(contactDataSetType);
			}
		});

		comboBox.addValueChangeListener(event -> {

			String[] startDateSplit = startDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).split("-");
			String[] endDateSplit = endDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).split("-");
			if (event.getValue().equalsIgnoreCase("Every Day")) {
				selectionvalue.removeAll();
				HorizontalLayout layoutTwo = new HorizontalLayout();
				layoutTwo.add(dayTime);
				selectionvalue.add(layoutTwo);

			} else if (event.getValue().equalsIgnoreCase("Every Month")) {

				selectionvalue.removeAll();
				selectionvalue.add(monthlyDay);

			} else if (event.getValue().equalsIgnoreCase("After Days")) {

				selectionvalue.removeAll();
				selectionvalue.add(day);

			} else {

				selectionvalue.removeAll();

			}

			selection.add(selectionvalue);
			rightLayout.add(selection);
			if (Integer.parseInt(startDateSplit[2]) < 10) {
				startDateSplit[2] = startDateSplit[2].substring(1);
			}
			if (Integer.parseInt(endDateSplit[2]) < 10) {
				endDateSplit[2] = endDateSplit[2].substring(1);
			}

			save.addClickListener(e -> {
				if (event.getValue().equalsIgnoreCase("Every Day")) {
					split = dayTime.getValue().split(":");
					if (Integer.parseInt(split[1]) < 10) {
						minutes = split[1].substring(1);
					} else {
						minutes = split[1];
					}
					hours = split[0];
					cronexp = "0 " + minutes + " " + hours + " " + startDateSplit[2] + "-" + endDateSplit[2] + " "
							+ startDateSplit[1] + "-" + endDateSplit[1] + " ? " + startDateSplit[0] + "-"
							+ endDateSplit[0];
					schConfig.setCronexp(cronexp);
				} else if (event.getValue().equalsIgnoreCase("After Days")) {
					cronexp = "0 0 12 " + startDateSplit[2] + "-" + endDateSplit[2] + "/" + day.getValue() + " "
							+ startDateSplit[1] + "-" + endDateSplit[1] + " ? " + startDateSplit[0] + "-"
							+ endDateSplit[0];
					schConfig.setCronexp(cronexp);
				}
				if (lead.getValue().equals(true) && contact.getValue().equals(true)) {
					sfdcData = lead.getLabel() + "+" + leadDataSetType.getValue() + "," + contact.getLabel() + "+"
							+ contactDataSetType.getValue();
					lead.setValue(false);
					contact.setValue(false);
				} else if (lead.getValue().equals(true)) {
					sfdcData = lead.getLabel() + "+" + leadDataSetType.getValue();
				} else if (contact.getValue().equals(true)) {
					sfdcData = contact.getLabel() + "+" + contactDataSetType.getValue();
				}
				qzScheduler.jobTrigger(cronexp, synctype.getValue(), sfdcData, scheduleType.getValue());
				save.getUI().ifPresent(ui -> ui.navigate("home"));
			});
			rightLayout.add(save);
		});
		setAlignItems(Alignment.CENTER);
		contentLayout.add(navBar.sidenav, rightLayout);
		add(contentLayout);
	}

	public String generateCronForSyncNow() {
		String strDateFormat = "yyyy:MM:dd:HH:mm:ss";
		DateFormat dtf = new SimpleDateFormat(strDateFormat);
		Calendar now = Calendar.getInstance();
		TimeZone timeZone = now.getTimeZone();
		dtf.setTimeZone(timeZone);
		Date d1 = new Date();
		String currTime = dtf.format(d1);
		System.out.println("currentTime:" + currTime);
		String[] currTimeSplit = currTime.split(":");
		String year, month = "", day = "", hour = "", minutes = "";
		year = currTimeSplit[0];
		month = currTimeSplit[1];
		day = currTimeSplit[2];
		hour = currTimeSplit[3];
		minutes = currTimeSplit[4];
		if (Integer.parseInt(currTimeSplit[1]) < 10) {
			month = currTimeSplit[1].substring(1);
		}
		if (Integer.parseInt(currTimeSplit[2]) < 10) {
			day = currTimeSplit[2].substring(1);
		}
		if (Integer.parseInt(currTimeSplit[3]) < 10) {
			hour = currTimeSplit[3].substring(1);
		}
		if (Integer.parseInt(currTimeSplit[4]) < 10) {
			minutes = currTimeSplit[4].substring(1);
		}
		int sec = Integer.parseInt(currTimeSplit[5]) + 60;
		if (sec > 60) {
			sec = sec - 60;
		}
		return sec + " " + minutes + " " + hour + " " + day + "-" + day + " " + month + "-" + month + " ? " + year + "-"
				+ year;
	}

	@Override
	public Object decrypt(Object analyticsObj) {
		AnalyticsConfig analyticsConfig = new AnalyticsConfig();
		analyticsConfig.setUserId(((AnalyticsConfig) analyticsObj).getUserId());
		analyticsConfig.setStatus(((AnalyticsConfig) analyticsObj).getStatus());
		analyticsConfig.setAnalyticsConfigId(((AnalyticsConfig) analyticsObj).getAnalyticsConfigId());
		analyticsConfig.setAnalyticsConfigName(((AnalyticsConfig) analyticsObj).getAnalyticsConfigName());
		analyticsConfig.setAnalyticsConfigType(((AnalyticsConfig) analyticsObj).getAnalyticsConfigType());
		analyticsConfig.setCampaignConfigId(((AnalyticsConfig) analyticsObj).getCampaignConfigId());
		analyticsConfig.setCampaignConfigName(((AnalyticsConfig) analyticsObj).getCampaignConfigName());
		analyticsConfig.setSalesforceConfigId(((AnalyticsConfig) analyticsObj).getSalesforceConfigId());
		analyticsConfig.setSalesforceConfigName(((AnalyticsConfig) analyticsObj).getSalesforceConfigName());
		analyticsConfig.setViewId(((AnalyticsConfig) analyticsObj).getViewId());
		analyticsConfig.setStartDate(((AnalyticsConfig) analyticsObj).getStartDate());
		analyticsConfig.setEndDate(((AnalyticsConfig) analyticsObj).getEndDate());
		

		try {
			AnalyticsConfig analyticsConfigured = (AnalyticsConfig) analyticsObj;
			if (analyticsConfigured.getRsId() != null && analyticsConfigured.getDimensionsId() != null) {
				analyticsConfig.setRsId(new String(Base64.getDecoder().decode(analyticsConfigured.getRsId()),
						VaadinConstants.ENCODINGFORMAT));
				analyticsConfig
						.setDimensionsId(new String(Base64.getDecoder().decode(analyticsConfigured.getDimensionsId()),
								VaadinConstants.ENCODINGFORMAT));
			}
			if (analyticsConfigured.getJsonFile() != null) {
				analyticsConfig.setJsonFile(((AnalyticsConfig) analyticsObj).getJsonFile());
			}

		} catch (UnsupportedEncodingException e) {
			logger.info("Exception is ::{}", e);
		}

		return analyticsConfig;
	}
}