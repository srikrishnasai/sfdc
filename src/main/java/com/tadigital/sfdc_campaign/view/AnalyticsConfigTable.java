package com.tadigital.sfdc_campaign.view;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tadigital.sfdc_campaign.constants.VaadinConstants;
import com.tadigital.sfdc_campaign.model.AnalyticsConfig;
import com.tadigital.sfdc_campaign.repo.AnalyticsConfigRepo;
import com.tadigital.sfdc_campaign.service.BeanDecryption;
import com.tadigital.sfdc_campaign.service.BeanEncryption;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;

/*
@author akhilreddy.b
*
*/
@Route(value = "analyticsconfig", layout = MainLayout.class)
@StyleSheet("css/mainViewStyles.css")
public class AnalyticsConfigTable extends VerticalLayout implements RouterLayout, BeanDecryption, BeanEncryption {

	private static final long serialVersionUID = 1L;

	VerticalLayout rightLayout = new VerticalLayout();
	VerticalLayout headerLayout = new VerticalLayout();
	HorizontalLayout contentLayout = new HorizontalLayout();
	transient Logger log = LoggerFactory.getLogger(AnalyticsConfigTable.class);
	transient AnalyticsConfigRepo analyticsConfigRepo;

	public AnalyticsConfigTable() {
		setPadding(false);
		setHeight("100%");

		rightLayout.addClassName("rightLayout");
		rightLayout.setSizeFull();
		contentLayout.addClassName("contentLayout");

		NavigationBarView navBar = new NavigationBarView();
		navBar.campaignSync.addClassName("active");

		Label headerLbl = new Label("Analytics - Configuration");
		headerLbl.addClassName("configText");

		Button createConfig = new Button("Create");
		createConfig.addClassNames("configButton", VaadinConstants.STYLEDBUTTON);
		
		VaadinSession.getCurrent().setAttribute("editAnalyticsConfigId", null);
		
		analyticsConfigRepo = BeanUtil.getBean(AnalyticsConfigRepo.class);
		List<AnalyticsConfig> decryptedList = new ArrayList<>();
		List<AnalyticsConfig> analyticsConfigList = analyticsConfigRepo.findAllByStatusNotAndUserId("Deleted",
				(int) VaadinSession.getCurrent().getAttribute(VaadinConstants.USERID));
		for (int i = 0; i < analyticsConfigList.size(); i++) {
			AnalyticsConfig analyticsConfig = new AnalyticsConfig();
			analyticsConfig = (AnalyticsConfig) decrypt(analyticsConfigList.get(i));
			decryptedList.add(analyticsConfig);
		}

		Grid<AnalyticsConfig> analyticsConfigTable = new Grid<>();
		analyticsConfigTable.addClassName("gridStyle");
		analyticsConfigTable.addColumn(AnalyticsConfig::getAnalyticsConfigName).setHeader("Name").setResizable(true);
		analyticsConfigTable.addColumn(AnalyticsConfig::getAnalyticsConfigType).setHeader("Type").setResizable(true);
		analyticsConfigTable.addColumn(AnalyticsConfig::getStatus).setHeader("Status");
		analyticsConfigTable.addColumn(new TAButtonRenderer<>("Edit", item -> {
			AnalyticsConfig analyticsConfig = (AnalyticsConfig) item;
			VaadinSession.getCurrent().setAttribute("editAnalyticsConfigId", analyticsConfig.getAnalyticsConfigId());
			analyticsConfigTable.getUI().ifPresent(ui -> ui.navigate("analyticsconfigview"));
		}));
		
		analyticsConfigTable.addColumn(new TAButtonRenderer<>("Delete", item -> {
			AnalyticsConfig analyticsConfig = (AnalyticsConfig) item;
			analyticsConfig.setStatus("Deleted");
			analyticsConfigRepo.save((AnalyticsConfig) encrypt(analyticsConfig));
			decryptedList.remove(analyticsConfig);
			analyticsConfigTable.setItems(decryptedList);
		}));
		
		decryptedList.sort((analyticsConfig1, analyticsConfig2) -> analyticsConfig1.getStatus()
				.compareToIgnoreCase(analyticsConfig2.getStatus()));
		analyticsConfigTable.setItems(decryptedList);
		analyticsConfigTable.setHeightByRows(false);
		createConfig.addClickListener(e -> createConfig.getUI().ifPresent(ui -> ui.navigate("analyticsconfigview")));
		rightLayout.add(headerLbl, createConfig, analyticsConfigTable);

		contentLayout.add(navBar.sidenav, rightLayout);
		add(contentLayout);

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
			log.info("Exception is ::{}", e);
		}

		return analyticsConfig;
	}

	@Override
	public Object encrypt(Object analyticsObj) {
		AnalyticsConfig analyticsConfig = new AnalyticsConfig();
		try {
			AnalyticsConfig analyticsConfigured = (AnalyticsConfig) analyticsObj;

			analyticsConfig.setStatus(analyticsConfigured.getStatus());
			analyticsConfig.setUserId(analyticsConfigured.getUserId());
			analyticsConfig.setAnalyticsConfigId(analyticsConfigured.getAnalyticsConfigId());
			analyticsConfig.setAnalyticsConfigName(analyticsConfigured.getAnalyticsConfigName());
			analyticsConfig.setAnalyticsConfigType(analyticsConfigured.getAnalyticsConfigType());
			analyticsConfig.setCampaignConfigId((analyticsConfigured.getCampaignConfigId()));
			analyticsConfig.setCampaignConfigName((analyticsConfigured.getCampaignConfigName()));
			
			if (analyticsConfigured.getJsonFile() != null) {
				analyticsConfig.setJsonFile(analyticsConfigured.getJsonFile());
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
