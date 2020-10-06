/**
* The class MainView is used to show the schedules of the logged in user. 
*/
package com.tadigital.sfdc_campaign.view;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tadigital.sfdc_campaign.constants.VaadinConstants;
import com.tadigital.sfdc_campaign.model.ACSConfig;
import com.tadigital.sfdc_campaign.model.AnalyticsConfig;
import com.tadigital.sfdc_campaign.model.SFDCConfig;
import com.tadigital.sfdc_campaign.repo.ACSConfigRepo;
import com.tadigital.sfdc_campaign.repo.AnalyticsConfigRepo;
import com.tadigital.sfdc_campaign.repo.SFDCConfigRepo;
import com.tadigital.sfdc_campaign.service.BeanDecryption;
import com.tadigital.sfdc_campaign.service.BeanEncryption;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;

/*
@author akhilreddy.b
*
*/
@Route(value = "analyticsconfigview", layout = MainLayout.class)
@StyleSheet("css/mainViewStyles.css")
public class AnalyticsConfigView extends VerticalLayout implements RouterLayout, BeanDecryption, BeanEncryption {

	private static final long serialVersionUID = 1L;

	VerticalLayout rightLayout = new VerticalLayout();
	VerticalLayout headerLayout = new VerticalLayout();
	HorizontalLayout contentLayout = new HorizontalLayout();
	RadioButtonGroup<String> analyticsType = new RadioButtonGroup<>();
	Label configType = new Label("Config Type:");
	RadioButtonGroup<String> checkStatus = new RadioButtonGroup<>();
	Label status = new Label("Status:");
	TextField configName = new TextField("Config Name:");
	TextField rsId = new TextField("RS Id");
	TextField dimensionsId = new TextField("Dimensions Id");
	Paragraph label = new Paragraph();
	TextField viewId = new TextField("View Id");
	TextField campaignStartDate = new TextField("Start Date:");
	TextField campaignEndDate = new TextField("End Date");
	transient Logger log = LoggerFactory.getLogger(AnalyticsConfigTable.class);
	transient AnalyticsConfigRepo analyticsConfigRepo;
	transient ACSConfigRepo acsConfigRepo;
	transient SFDCConfigRepo sfdcConfigRepo;
	transient AnalyticsConfig analyticsConfig = new AnalyticsConfig();
	transient List<AnalyticsConfig> analyticsConfigList;
	HorizontalLayout configsLayout = new HorizontalLayout();
	HorizontalLayout datesLayout = new HorizontalLayout();
	ComboBox<String> campaignConfigs = new ComboBox<>("Choose the Campaign config: ");
	ComboBox<String> salesforceConfigs = new ComboBox<>("Choose the Sales-Force config: ");
	String fileName = "";
	HorizontalLayout fileContent = new HorizontalLayout();
	Button changeFile = new Button("change file");
	Button submit = new Button("submit");

	public AnalyticsConfigView() {
		setPadding(false);
		setHeight("100%");
        campaignStartDate.setPlaceholder("yyyy-mm-dd");
        campaignEndDate.setPlaceholder("yyyy-mm-dd");
		int userid = (int) VaadinSession.getCurrent().getAttribute(VaadinConstants.USERID);
		analyticsConfigRepo = BeanUtil.getBean(AnalyticsConfigRepo.class);
		acsConfigRepo = BeanUtil.getBean(ACSConfigRepo.class);
		sfdcConfigRepo = BeanUtil.getBean(SFDCConfigRepo.class);
		analyticsConfig.setUserId(userid);
		rightLayout.addClassName("rightLayout");
		rightLayout.setSizeFull();
		contentLayout.addClassName("contentLayout");
		changeFile.setClassName(VaadinConstants.STYLEDBUTTON);
        viewId.setVisible(false);
		NavigationBarView navBar = new NavigationBarView();
		navBar.campaignSync.addClassName("active");
        datesLayout.add(campaignStartDate, campaignEndDate);
		HorizontalLayout checkStatusGroup = new HorizontalLayout();
		checkStatus.setItems("Active", "Inactive");
		checkStatus.setValue("Inactive");
		checkStatusGroup.add(status, checkStatus);

		HorizontalLayout configTypeGroup = new HorizontalLayout();
		analyticsType.setItems("Google Analytics", "Adobe Analytics");
		configTypeGroup.add(configType, analyticsType);

		MemoryBuffer buffer = new MemoryBuffer();
		Upload upload = new Upload(buffer);

		upload.setVisible(false);
		rsId.setVisible(false);
		dimensionsId.setVisible(false);
		fileContent.setVisible(false);
		changeFile.setVisible(false);
		campaignConfigs.setVisible(false);

		fileContent.getStyle().set("width", "42%");
		configName.getStyle().set("width", "42%");
		rsId.getStyle().set("width", "42%");
		dimensionsId.getStyle().set("width", "42%");
		configTypeGroup.getStyle().set("width", "42%");
		checkStatusGroup.getStyle().set("width", "42%");
		datesLayout.getStyle().set("width", "42%");
		viewId.getStyle().set("width", "42%");
		upload.getStyle().set("width", "42%");
		configsLayout.getStyle().set("width", "42%");
		status.getStyle().set("margin-right", "54px");
		status.addClassName("label");
		configType.addClassName("label");
		label.addClassName("label");

		List<ACSConfig> acsConfigList = acsConfigRepo.findAllByCheckstatusNotAndUserid("Deleted",
				(int) VaadinSession.getCurrent().getAttribute("userid"));
		List<String> acsConfigNames = new ArrayList<>();
		for (int i = 0; i < acsConfigList.size(); i++) {
			ACSConfig acsConfig = acsConfigList.get(i);
			acsConfigNames.add(acsConfig.getConfigname());
		}

		List<SFDCConfig> salesConfigList = sfdcConfigRepo.findAllByCheckstatusNotAndUserid("Deleted",
				(int) VaadinSession.getCurrent().getAttribute("userid"));
		List<String> salesConfigNames = new ArrayList<>();
		for (int i = 0; i < salesConfigList.size(); i++) {
			SFDCConfig sfdcConfig = salesConfigList.get(i);
			salesConfigNames.add(sfdcConfig.getConfigname());
		}

		campaignConfigs.setItems(acsConfigNames);
		salesforceConfigs.setItems(salesConfigNames);
		configsLayout.add(campaignConfigs, salesforceConfigs);

		if (VaadinSession.getCurrent().getAttribute("editAnalyticsConfigId") != null) {
			analyticsConfig = analyticsConfigRepo
					.findByAnalyticsConfigId((int) VaadinSession.getCurrent().getAttribute("editAnalyticsConfigId"));
			if (analyticsConfig != null) {
				analyticsConfig = (AnalyticsConfig) decrypt(analyticsConfig);
				configName.setValue(analyticsConfig.getAnalyticsConfigName());
				checkStatus.setValue(analyticsConfig.getStatus());
				campaignStartDate.setValue(analyticsConfig.getStartDate());
				campaignEndDate.setValue(analyticsConfig.getEndDate());
				if (analyticsConfig.getAnalyticsConfigType().equals("Google Analytics")) {
					rsId.setVisible(false);
					dimensionsId.setVisible(false);
					analyticsType.setValue("Google Analytics");
					fileContent.setVisible(true);
					fileContent.removeAll();
					viewId.setVisible(true);
					viewId.setValue(analyticsConfig.getViewId());
					label.setText(analyticsConfig.getFileName());
					fileName = analyticsConfig.getFileName();
					changeFile.setVisible(true);
					fileContent.add(label, changeFile);
					salesforceConfigs.setValue(analyticsConfig.getSalesforceConfigName());
				} else {
					upload.setVisible(false);
					rsId.setVisible(true);
					configsLayout.setVisible(true);
					campaignConfigs.setValue(analyticsConfig.getCampaignConfigName());
					salesforceConfigs.setValue(analyticsConfig.getSalesforceConfigName());
					dimensionsId.setVisible(true);
					rsId.setValue(analyticsConfig.getRsId());
					dimensionsId.setValue(analyticsConfig.getDimensionsId());
					analyticsType.setValue("Adobe Analytics");
				}
			}
		}
		analyticsType.addValueChangeListener(e -> {
			if (analyticsType.getValue().equalsIgnoreCase("Google Analytics")) {
				viewId.setVisible(true);
				upload.setVisible(true);
				configsLayout.setVisible(true);
				campaignConfigs.setVisible(false);
				rsId.setVisible(false);
				dimensionsId.setVisible(false);
			} else {
				viewId.setVisible(false);
				upload.setVisible(false);
				configsLayout.setVisible(true);
				campaignConfigs.setVisible(true);
				rsId.setVisible(true);
				dimensionsId.setVisible(true);
			}
		});

		submit.addClassNames("cursorPointer", VaadinConstants.STYLEDBUTTON);

		upload.addSucceededListener(event -> {
			try {
				
				byte[] bytes = IOUtils.toByteArray(buffer.getInputStream());
				analyticsConfig.setJsonFile(bytes);
				fileName = event.getFileName();
				
			} catch (IOException e) {
				log.error("IOException in reading the json file::{}", e);
			}
		});

		changeFile.addClickListener(e -> {
			upload.setVisible(true);
			fileContent.removeAll();
			fileName = "";
		});

		submit.addClickListener(e -> {
			analyticsConfigList = analyticsConfigRepo
					.findByUserId((int) VaadinSession.getCurrent().getAttribute("userid"));
			AnalyticsConfig name = analyticsConfigList.stream()
					.filter(x -> configName.getValue().trim().equals(x.getAnalyticsConfigName())).findAny()
					.orElse(null);
			if (VaadinSession.getCurrent().getAttribute("editAnalyticsConfigId") == null && name != null) {
				Notification.show("Configuration with the same name exists", 3000, Position.TOP_CENTER);
			} else {

				if ((configName.getValue() != null || configName.getValue().trim().isEmpty())
						&& (analyticsType.getValue() != null)) {
					analyticsConfig.setAnalyticsConfigName(configName.getValue());
					analyticsConfig.setAnalyticsConfigType(analyticsType.getValue());
					analyticsConfig.setStatus(checkStatus.getValue());
					analyticsConfig.setFileName(fileName);
					analyticsConfig.setStartDate(campaignStartDate.getValue());
					analyticsConfig.setEndDate(campaignEndDate.getValue());
					SFDCConfig sfdcConfig = sfdcConfigRepo.findByConfignameAndUserid(salesforceConfigs.getValue().trim(),
							userid);
					if (null != sfdcConfig) {
						analyticsConfig.setSalesforceConfigId(sfdcConfig.getConfigId());
						analyticsConfig.setSalesforceConfigName(sfdcConfig.getConfigname());
					}

					if (analyticsType.getValue().equals("Google Analytics") && !fileName.isEmpty()) {
						analyticsConfig.setViewId(viewId.getValue());
						analyticsConfigRepo.save(analyticsConfig);
						submit.getUI().ifPresent(ui -> ui.navigate("analyticsconfig"));
					} else if (analyticsType.getValue().equals("Adobe Analytics")
							&& (rsId.getValue() != null && !rsId.getValue().trim().isEmpty())
							&& (dimensionsId.getValue() != null && !dimensionsId.getValue().trim().isEmpty())
							&& (campaignConfigs.getValue() != null && !campaignConfigs.getValue().trim().isEmpty())) {
						ACSConfig acsConfig = acsConfigRepo.findByConfignameAndUserid(campaignConfigs.getValue().trim(),
								userid);
						if (null != acsConfig) {
							analyticsConfig.setCampaignConfigId(acsConfig.getConfigId());
							analyticsConfig.setCampaignConfigName(acsConfig.getConfigname());
						}
						analyticsConfig.setRsId(rsId.getValue().trim());
						analyticsConfig.setDimensionsId(dimensionsId.getValue().trim());
						encrypt(analyticsConfig);
						analyticsConfigRepo.save(analyticsConfig);
						submit.getUI().ifPresent(ui -> ui.navigate("analyticsconfig"));
					} else {
						Notification.show("Please fill the required fields", 3000, Position.TOP_CENTER);
					}
				} else {
					Notification.show("Please fill the required fields", 3000, Position.TOP_CENTER);
				}
			}
		});

		rightLayout.add(configName, configTypeGroup, checkStatusGroup,viewId,datesLayout, configsLayout, upload, rsId, dimensionsId
				,fileContent, submit);
		contentLayout.add(navBar.sidenav, rightLayout);
		add(contentLayout);
	}

	@Override
	public Object decrypt(Object analyticsObj) {
		analyticsConfig.setUserId(((AnalyticsConfig) analyticsObj).getUserId());
		analyticsConfig.setStatus(((AnalyticsConfig) analyticsObj).getStatus());
		analyticsConfig.setAnalyticsConfigId(((AnalyticsConfig) analyticsObj).getAnalyticsConfigId());
		analyticsConfig.setAnalyticsConfigName(((AnalyticsConfig) analyticsObj).getAnalyticsConfigName());
		analyticsConfig.setAnalyticsConfigType(((AnalyticsConfig) analyticsObj).getAnalyticsConfigType());
		analyticsConfig.setCampaignConfigId(((AnalyticsConfig) analyticsObj).getCampaignConfigId());
		analyticsConfig.setCampaignConfigName(((AnalyticsConfig) analyticsObj).getCampaignConfigName());
		analyticsConfig.setSalesforceConfigId(((AnalyticsConfig) analyticsObj).getSalesforceConfigId());
		analyticsConfig.setSalesforceConfigName(((AnalyticsConfig) analyticsObj).getSalesforceConfigName());

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
				analyticsConfig.setFileName(((AnalyticsConfig) analyticsObj).getFileName());
			}

		} catch (UnsupportedEncodingException e) {
			log.info("Exception is ::{}", e);
		}

		return analyticsConfig;
	}

	@Override
	public Object encrypt(Object analyticsObj) {
		try {
			AnalyticsConfig analyticsConfigured = (AnalyticsConfig) analyticsObj;

			analyticsConfig.setStatus(analyticsConfigured.getStatus());
			analyticsConfig.setUserId(analyticsConfigured.getUserId());
			analyticsConfig.setAnalyticsConfigId(analyticsConfigured.getAnalyticsConfigId());
			analyticsConfig.setAnalyticsConfigName(analyticsConfigured.getAnalyticsConfigName());
			analyticsConfig.setAnalyticsConfigType(analyticsConfigured.getAnalyticsConfigType());
			analyticsConfig.setCampaignConfigId((analyticsConfigured.getCampaignConfigId()));
			analyticsConfig.setCampaignConfigName((analyticsConfigured.getCampaignConfigName()));
			analyticsConfig.setSalesforceConfigId((analyticsConfigured.getSalesforceConfigId()));
			analyticsConfig.setSalesforceConfigName((analyticsConfigured.getSalesforceConfigName()));
			analyticsConfig.setViewId(analyticsConfigured.getViewId());
			analyticsConfig.setStartDate(analyticsConfigured.getStartDate());
			analyticsConfig.setEndDate(analyticsConfigured.getEndDate());

			if (analyticsConfigured.getJsonFile() != null) {
				analyticsConfig.setJsonFile(analyticsConfigured.getJsonFile());
				analyticsConfig.setFileName(((AnalyticsConfig) analyticsObj).getFileName());
			}
			if (analyticsConfigured.getRsId() != null && analyticsConfigured.getDimensionsId() != null) {
				analyticsConfig.setRsId(Base64.getEncoder()
						.encodeToString(analyticsConfigured.getRsId().getBytes(VaadinConstants.ENCODINGFORMAT)));
				analyticsConfig.setDimensionsId(Base64.getEncoder().encodeToString(
						analyticsConfigured.getDimensionsId().getBytes(VaadinConstants.ENCODINGFORMAT)));
			}

			return analyticsConfig;
		} catch (UnsupportedEncodingException e) {
			log.info("Exception is ::{}", e);
		}

		return null;
	}

}
