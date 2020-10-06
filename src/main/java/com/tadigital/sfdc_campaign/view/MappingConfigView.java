package com.tadigital.sfdc_campaign.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.tadigital.sfdc_campaign.constants.StringConstants;
import com.tadigital.sfdc_campaign.constants.VaadinConstants;
import com.tadigital.sfdc_campaign.model.ACSConfig;
import com.tadigital.sfdc_campaign.model.MapConfig;
import com.tadigital.sfdc_campaign.model.MapMasterBean;
import com.tadigital.sfdc_campaign.model.SFDCConfig;
import com.tadigital.sfdc_campaign.repo.ACSConfigRepo;
import com.tadigital.sfdc_campaign.repo.FieldsRepo;
import com.tadigital.sfdc_campaign.repo.MapConfigRepo;
import com.tadigital.sfdc_campaign.repo.MapMasterRepo;
import com.tadigital.sfdc_campaign.repo.SFDCConfigRepo;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;

@Route(value = "map", layout = MainLayout.class)
@StyleSheet("css/mainViewStyles.css")
public class MappingConfigView extends VerticalLayout implements RouterLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	VerticalLayout salesLayout = new VerticalLayout();
	VerticalLayout acsLayout = new VerticalLayout();
	VerticalLayout mapLayout = new VerticalLayout();
	VerticalLayout buttonLayout = new VerticalLayout();
	HorizontalLayout leftLayout = new HorizontalLayout();
	HorizontalLayout rightLayout = new HorizontalLayout();
	HorizontalLayout hortiLayout = new HorizontalLayout();
	HorizontalLayout contentLayout = new HorizontalLayout();
	VerticalLayout vertiLayout = new VerticalLayout();
	HorizontalLayout configLayout = new HorizontalLayout();

	ListBox<String> leadListBox = new ListBox<>();
	ListBox<String> contactListBox = new ListBox<>();
	ListBox<String> opportunityListBox = new ListBox<>();
	ListBox<String> acsListBox = new ListBox<>();
	ListBox<String> mapLeadListBox = new ListBox<>();
	ListBox<String> mapContactListBox = new ListBox<>();
	ListBox<String> mapOpportunityListBox = new ListBox<>();

	Button pairButton = new Button(new Icon(VaadinIcon.ANGLE_RIGHT));
	Button unpairButton = new Button(new Icon(VaadinIcon.ANGLE_LEFT));

	ComboBox<String> dataSetType = new ComboBox<>("DataSet Type");
	HorizontalLayout dataLayout = new HorizontalLayout();

	ComboBox<String> acsConfigurations = new ComboBox<>("ACS Configurations");
	ComboBox<String> salesConfigurations = new ComboBox<>("Sales Configurations");

	TextField configName = new TextField();
	RadioButtonGroup<String> checkStatus = new RadioButtonGroup<>();
	Label status = new Label("Status:");
	Button submit = new Button("Submit");

	transient FieldsRepo fieldsRepo;
	transient MapMasterRepo mapMasterRepo;
	transient MapConfigRepo mapRepo;
	transient SFDCConfigRepo sfdcRepo;
	transient ACSConfigRepo acsRepo;

	transient List<String> mapLeadList = new ArrayList<>();
	transient List<String> mapContactList = new ArrayList<>();
	transient List<String> mapOpportunityList = new ArrayList<>();
	transient List<String> acsConfigurationList = new ArrayList<>();
	transient List<String> salesConfigurationList = new ArrayList<>();

	transient List<String> leadList = new ArrayList<>();
	transient List<String> contactList = new ArrayList<>();
	transient List<String> opportunityList = new ArrayList<>();
	transient List<String> acsList = new ArrayList<>();

	long salesConfigid;
	long acsConfigid;

	String acsName = "";
	String salesName = "";

	transient Optional<ACSConfig> acsConfigBean;
	transient Optional<SFDCConfig> sfdcConfigBean;

	transient List<MapMasterBean> mapMasterList;

	public MappingConfigView() {
		this.fieldsRepo = BeanUtil.getBean(FieldsRepo.class);
		this.mapRepo = BeanUtil.getBean(MapConfigRepo.class);
		this.mapMasterRepo = BeanUtil.getBean(MapMasterRepo.class);
		this.sfdcRepo = BeanUtil.getBean(SFDCConfigRepo.class);
		this.acsRepo = BeanUtil.getBean(ACSConfigRepo.class);
		setPadding(false);
		setHeight("100%");
		status.addClassName("label");
		int userid = (int) VaadinSession.getCurrent().getAttribute("userid");
		String mapConfigName = (String) VaadinSession.getCurrent().getAttribute("configname");
		String configStatus = (String) VaadinSession.getCurrent().getAttribute("configstatus");
		long acsConfigId = (long) VaadinSession.getCurrent().getAttribute("acsconfigid");
		long salesConfigId = (long) VaadinSession.getCurrent().getAttribute("salesconfigid");
		HorizontalLayout headerLblContainer = new HorizontalLayout();
		Label headerLbl = new Label("Mapping - Configurations");
		headerLbl.addClassName("mappingConfigViewLabel");
		headerLblContainer.add(headerLbl);

		acsConfigBean = acsRepo.findByConfigId(acsConfigId);
		sfdcConfigBean = sfdcRepo.findByConfigId(salesConfigId);
		if (acsConfigId != 0 && salesConfigId != 0) {
			acsName = acsConfigBean.isPresent() ? acsConfigBean.get().getConfigname() : null;
			salesName = sfdcConfigBean.isPresent() ? sfdcConfigBean.get().getConfigname() : null;
		}
		configName.setPlaceholder("Enter a name");
		configName.setRequired(true);

		if (!mapConfigName.isEmpty()) {
			configName.setValue((String) VaadinSession.getCurrent().getAttribute("configname"));
			mapLeadList = mapRepo.findMappedpairsByMapconfigidAndBelongs(
					(Integer) VaadinSession.getCurrent().getAttribute("configid"), "Lead");
			mapLeadListBox.setItems(mapLeadList);
			mapContactList = mapRepo.findMappedpairsByMapconfigidAndBelongs(
					(Integer) VaadinSession.getCurrent().getAttribute("configid"), "Contact");
			mapContactListBox.setItems(mapContactList);
			mapOpportunityList = mapRepo.findMappedpairsByMapconfigidAndBelongs(
					(Integer) VaadinSession.getCurrent().getAttribute("configid"), "Opportunity");
			mapOpportunityListBox.setItems(mapOpportunityList);
		}
		checkStatus.setItems(StringConstants.STATUS_ACTIVE, StringConstants.STATUS_INACTIVE);
		if (!configStatus.isEmpty()) {
			checkStatus.setValue("" + VaadinSession.getCurrent().getAttribute("configstatus"));
		} else {
			checkStatus.setValue("Inactive");
		}
		NavigationBarView navBar = new NavigationBarView();
		navBar.mapConfig.addClassName("active");
		submit.addClassNames("cursorPointer", VaadinConstants.STYLEDBUTTON);
		contentLayout.addClassName("contentLayout");
		contentLayout.setMargin(false);
		contentLayout.setPadding(false);
		vertiLayout.addClassName("mappingViewRightLayout");
		HorizontalLayout checkStatusGroup = new HorizontalLayout();
		checkStatusGroup.add(status, checkStatus);
		acsConfigurationList = acsRepo.findAllByUseridAndCheckstatus(userid, "Active");
		acsConfigurations.setItems(acsConfigurationList);
		salesConfigurationList = sfdcRepo.findAllByUseridAndCheckstatus(userid, "Active");
		salesConfigurations.setItems(salesConfigurationList);

		if (null != acsName && !acsName.isEmpty()) {
			acsConfigurations.setValue(acsName);
		}

		if (null != salesName && !salesName.isEmpty()) {
			salesConfigurations.setValue(salesName);
		}

		dataSetType.setItems("Lead", "Contact", "Opportunity");
		configLayout.add(salesConfigurations, acsConfigurations, dataSetType);
		salesLayout.add(new H5("SalesForce Fields"));
		salesLayout.getStyle().set("padding-left", "0px");
		checkStatus.getStyle().set("margin-left", "8%");
		acsLayout.add(new H5("Campaign Fields"));
		mapLayout.add(new H5("Mapped Fields"));
		rightLayout.add(mapLayout);
		leftLayout.getStyle().set("margin-left", "0px");
		leftLayout.add(salesLayout, acsLayout);
		buttonLayout.add(pairButton, unpairButton);
		buttonLayout.getStyle().set("align-self", "center");
		buttonLayout.setWidth("");
		hortiLayout.add(leftLayout, buttonLayout, rightLayout);

		MyCustomLayout layout = new MyCustomLayout();
		layout.getContent().addClassName("mappingViewCustomFormLayout");
		layout.getContent().setResponsiveSteps(new ResponsiveStep("0", 1));
		layout.addItemWithLabel("Conifguration name:", configName);
		layout.addItemWithoutLabel(checkStatusGroup);
	
		vertiLayout.add(headerLblContainer, layout, configLayout, hortiLayout, submit);
		contentLayout.add(navBar.sidenav, vertiLayout);
		add(contentLayout);
		leadListBox.setId("salesList");
		contactListBox.setId("salesList");
		opportunityListBox.setId("salesList");
		acsListBox.setId("acsList");
		mapLeadListBox.setId("acsList");
		mapContactListBox.setId("acsList");
		mapOpportunityListBox.setId("acsList");
		dataSetType.addValueChangeListener(value -> {

			if (dataSetType.getValue() != null && dataSetType.getValue().equals("Lead")) {
				if (salesConfigurations.getValue() != null && acsConfigurations.getValue() != null) {
					salesConfigid = sfdcRepo.findByConfigname(salesConfigurations.getValue()).getConfigId();
					acsConfigid = acsRepo.findByConfigname(acsConfigurations.getValue()).getConfigId();
				} else {
					Notification.show("Please Select the configurations", 5000, Position.TOP_CENTER);
				}

				leadList = fieldsRepo.findBySalesidAndUserIdAndBelongs(salesConfigid, userid, "Lead");
				Collections.sort(leadList);
				acsList = fieldsRepo.findByAcsidAndUserIdAndBelongs(acsConfigid, userid, "acs");
				Collections.sort(acsList);
				leadListBox.setItems(leadList);
				salesLayout.removeAll();
				salesLayout.add(new H5("SalesForce Fields"));
				mapLayout.removeAll();
				mapLayout.add(new H5("Mapped Fields"));
				if (mapLeadList != null) {
					mapLeadListBox.setItems(mapLeadList);
					mapLayout.add(mapLeadListBox);
				}
				salesLayout.add(leadListBox);
				acsListBox.setItems(acsList);
				acsLayout.add(acsListBox);

				pairButton.addClickListener(e -> {
					if (leadListBox.getValue() != null && acsListBox.getValue() != null) {

						if (mapLeadList.contains(leadListBox.getValue() + "+" + acsListBox.getValue())) {
							Notification.show("Already Paired");
						} else {
							mapLeadList.add(leadListBox.getValue() + "+" + acsListBox.getValue());
							mapLeadListBox.setItems(mapLeadList);

							mapLayout.add(mapLeadListBox);
						}

						leadListBox.clear();
						acsListBox.clear();
					}
				});

				unpairButton.addClickListener(e -> {

					if ((Integer) VaadinSession.getCurrent().getAttribute("configid") != null) {
						MapConfig mapConfig = new MapConfig();
						mapConfig = mapRepo.findByMappedpairAndMapconfigidAndPairBelongsTo(mapLeadListBox.getValue(),
								(Integer) VaadinSession.getCurrent().getAttribute("configid"), "Lead");
						if (mapConfig != null) {
							mapRepo.delete(mapConfig);
						}
					}

					if (mapLeadListBox.getValue() != null) {

						mapLeadList.remove(mapLeadListBox.getValue());
						mapLeadListBox.setItems(mapLeadList);
						mapLayout.add(mapLeadListBox);
						mapLeadListBox.clear();
					}

				});

			}

			if (dataSetType.getValue() != null && dataSetType.getValue().equals("Contact")) {
				if (salesConfigurations.getValue() != null && acsConfigurations.getValue() != null) {
					salesConfigid = sfdcRepo.findByConfigname(salesConfigurations.getValue()).getConfigId();
					acsConfigid = acsRepo.findByConfigname(acsConfigurations.getValue()).getConfigId();
				} else {
					Notification.show("Please Select the configurations", 5000, Position.TOP_CENTER);
				}

				contactList = fieldsRepo.findBySalesidAndUserIdAndBelongs(salesConfigid, userid, "Contact");
				Collections.sort(contactList);
				acsList = fieldsRepo.findByAcsidAndUserIdAndBelongs(acsConfigid, userid, "acs");
				Collections.sort(acsList);
				contactListBox.setItems(contactList);
				salesLayout.removeAll();
				salesLayout.add(new H5("SalesForce Fields"));
				mapLayout.removeAll();
				mapLayout.add(new H5("Mapped Fields"));
				if (mapContactList != null) {
					mapContactListBox.setItems(mapContactList);
					mapLayout.add(mapContactListBox);
				}
				salesLayout.add(contactListBox);
				acsListBox.setItems(acsList);
				acsLayout.add(acsListBox);

				pairButton.addClickListener(e -> {
					if (contactListBox.getValue() != null && acsListBox.getValue() != null) {

						if (mapContactList.contains(contactListBox.getValue() + "+" + acsListBox.getValue())) {
							Notification.show("Already Paired");
						} else {
							mapContactList.add(contactListBox.getValue() + "+" + acsListBox.getValue());
							mapContactListBox.setItems(mapContactList);

							mapLayout.add(mapContactListBox);
						}

						contactListBox.clear();
						acsListBox.clear();
					}
				});

				unpairButton.addClickListener(e -> {

					if ((Integer) VaadinSession.getCurrent().getAttribute("configid") != null) {
						MapConfig mapConfig = new MapConfig();
						mapConfig = mapRepo.findByMappedpairAndMapconfigidAndPairBelongsTo(mapContactListBox.getValue(),
								(Integer) VaadinSession.getCurrent().getAttribute("configid"), "Contact");
						if (mapConfig != null) {
							mapRepo.delete(mapConfig);
						}
					}

					if (mapContactListBox.getValue() != null) {

						mapContactList.remove(mapContactListBox.getValue());
						mapContactListBox.setItems(mapContactList);
						mapLayout.add(mapContactListBox);
						mapContactListBox.clear();

					}
				});
			}

			if (dataSetType.getValue() != null && dataSetType.getValue().equals("Opportunity")) {
				if (salesConfigurations.getValue() != null && acsConfigurations.getValue() != null) {
					salesConfigid = sfdcRepo.findByConfigname(salesConfigurations.getValue()).getConfigId();
					acsConfigid = acsRepo.findByConfigname(acsConfigurations.getValue()).getConfigId();
				} else {
					Notification.show("Please Select the configurations", 5000, Position.TOP_CENTER);
				}

				opportunityList = fieldsRepo.findBySalesidAndUserIdAndBelongs(salesConfigid, userid, "Opportunity");
				Collections.sort(opportunityList);
				acsList = fieldsRepo.findByAcsidAndUserIdAndBelongs(acsConfigid, userid, "acs");
				Collections.sort(acsList);
				opportunityListBox.setItems(opportunityList);
				salesLayout.removeAll();
				salesLayout.add(new H5("SalesForce Fields"));
				mapLayout.removeAll();
				mapLayout.add(new H5("Mapped Fields"));
				if (mapOpportunityList != null) {
					mapOpportunityListBox.setItems(mapOpportunityList);
					mapLayout.add(mapOpportunityListBox);
				}
				salesLayout.add(opportunityListBox);
				acsListBox.setItems(acsList);
				acsLayout.add(acsListBox);

				pairButton.addClickListener(e -> {
					if (opportunityListBox.getValue() != null && acsListBox.getValue() != null) {

						if (mapOpportunityList.contains(opportunityListBox.getValue() + "+" + acsListBox.getValue())) {
							Notification.show("Already Paired");
						} else {
							mapOpportunityList.add(opportunityListBox.getValue() + "+" + acsListBox.getValue());
							mapOpportunityListBox.setItems(mapOpportunityList);

							mapLayout.add(mapOpportunityListBox);
						}

						opportunityListBox.clear();
						acsListBox.clear();
					}
				});

				unpairButton.addClickListener(e -> {

					if ((Integer) VaadinSession.getCurrent().getAttribute("configid") != null) {
						MapConfig mapConfig = new MapConfig();
						mapConfig = mapRepo.findByMappedpairAndMapconfigidAndPairBelongsTo(
								mapOpportunityListBox.getValue(),
								(Integer) VaadinSession.getCurrent().getAttribute("configid"), "Opportunity");
						if (mapConfig != null) {
							mapRepo.delete(mapConfig);
						}
					}

					if (mapOpportunityListBox.getValue() != null) {

						mapOpportunityList.remove(mapOpportunityListBox.getValue());
						mapOpportunityListBox.setItems(mapOpportunityList);
						mapLayout.add(mapOpportunityListBox);
						mapOpportunityListBox.clear();
					}
				});
			}

		});

		submit.addClickListener(e -> {
			if (!configName.getValue().isEmpty() && salesConfigurations.getValue() != null
					&& acsConfigurations.getValue() != null) {

				MapMasterBean tableItem = new MapMasterBean();
				tableItem.setConfigname(configName.getValue().trim());
				tableItem.setStatus(checkStatus.getValue());
				tableItem.setUserid(userid);
				tableItem.setAcsconfigid(acsRepo.findByConfigname(acsConfigurations.getValue()).getConfigId());
				tableItem.setSalesconfigid(sfdcRepo.findByConfigname(salesConfigurations.getValue()).getConfigId());

				if (VaadinSession.getCurrent().getAttribute("configid") != null) {
					tableItem.setMapconfigid((int) VaadinSession.getCurrent().getAttribute("configid"));
				}

				mapMasterList = mapMasterRepo.findAllByUserid((int) VaadinSession.getCurrent().getAttribute("userid"));
				MapMasterBean name = mapMasterList.stream()
						.filter(x -> configName.getValue().trim().equals(x.getConfigname())).findAny().orElse(null);

				if (name == null) {
					mapMasterRepo.save(tableItem);
					saveMappingConfigurations(mapLeadList, mapContactList, mapOpportunityList,
							tableItem.getMapconfigid());
					VaadinSession.getCurrent().setAttribute("configname", "");
					VaadinSession.getCurrent().setAttribute("configstatus", "");
					VaadinSession.getCurrent().setAttribute("configid", null);
					VaadinSession.getCurrent().setAttribute("salesconfiguration", "");
					VaadinSession.getCurrent().setAttribute("acsconfiguration", "");
					VaadinSession.getCurrent().setAttribute("acsconfigid", null);
					VaadinSession.getCurrent().setAttribute("salesconfigid", null);
					submit.getUI().ifPresent(ui -> ui.navigate("maptable"));
				} else if (name.getMapconfigid() == (Integer) VaadinSession.getCurrent().getAttribute("configid")) {
					tableItem.setConfigname(configName.getValue().trim());
					tableItem.setMapconfigid((Integer) VaadinSession.getCurrent().getAttribute("configid"));
					mapMasterRepo.save(tableItem);
					saveMappingConfigurations(mapLeadList, mapContactList, mapOpportunityList,
							tableItem.getMapconfigid());
					submit.getUI().ifPresent(ui -> ui.navigate("maptable"));
				} else {
					Notification.show("Configuration with this name already exists", 5000, Position.TOP_CENTER);
				}
			} else {
				Notification.show("Please enter the configurations", 5000, Position.TOP_CENTER);
			}

		});

	}

	private void saveMappingConfigurations(List<String> leadItems, List<String> contactItems,
			List<String> opportunityItems, int mapconfigid) {

		for (int i = 0; i < leadItems.size(); i++) {
			if (mapRepo.findByMappedpairAndMapconfigidAndPairBelongsTo(leadItems.get(i), mapconfigid, "Lead") == null) {
				MapConfig mapConfig = new MapConfig();
				mapConfig.setUserid((Integer) VaadinSession.getCurrent().getAttribute("userid"));
				mapConfig.setMapconfigid(mapconfigid);
				mapConfig.setMappedpair(leadItems.get(i));
				mapConfig.setPairBelongsTo("Lead");
				mapRepo.save(mapConfig);
			}
		}

		for (int i = 0; i < contactItems.size(); i++) {
			if (mapRepo.findByMappedpairAndMapconfigidAndPairBelongsTo(contactItems.get(i), mapconfigid,
					"Contact") == null) {
				MapConfig mapConfig = new MapConfig();
				mapConfig.setUserid((Integer) VaadinSession.getCurrent().getAttribute("userid"));
				mapConfig.setMapconfigid(mapconfigid);
				mapConfig.setMappedpair(contactItems.get(i));
				mapConfig.setPairBelongsTo("Contact");
				mapRepo.save(mapConfig);
			}
		}

		for (int i = 0; i < opportunityItems.size(); i++) {
			if (mapRepo.findByMappedpairAndMapconfigidAndPairBelongsTo(opportunityItems.get(i), mapconfigid,
					"Opportunity") == null) {
				MapConfig mapConfig = new MapConfig();
				mapConfig.setUserid((Integer) VaadinSession.getCurrent().getAttribute("userid"));
				mapConfig.setMapconfigid(mapconfigid);
				mapConfig.setMappedpair(opportunityItems.get(i));
				mapConfig.setPairBelongsTo("Opportunity");
				mapRepo.save(mapConfig);
			}
		}
	}

}
