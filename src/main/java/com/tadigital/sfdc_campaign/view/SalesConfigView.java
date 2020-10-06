/**
 * The Class SalesConfigView generates a form where Salesforce details are configured and saved.
 */
package com.tadigital.sfdc_campaign.view;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tadigital.sfdc_campaign.constants.VaadinConstants;
import com.tadigital.sfdc_campaign.model.FieldsBean;
import com.tadigital.sfdc_campaign.model.MapMasterBean;
import com.tadigital.sfdc_campaign.model.SFDCConfig;
import com.tadigital.sfdc_campaign.repo.FieldsRepo;
import com.tadigital.sfdc_campaign.repo.MapMasterRepo;
import com.tadigital.sfdc_campaign.repo.SFDCConfigRepo;
import com.tadigital.sfdc_campaign.service.BeanDecryption;
import com.tadigital.sfdc_campaign.service.BeanEncryption;
import com.tadigital.sfdc_campaign.service.FieldsPersistance;
import com.tadigital.sfdc_campaign.threads.SalesMetadataThread;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;

/**
 * 
 * @author saikrishna.sp
 *
 */
@Route(value = "salesview", layout = MainLayout.class)
@StyleSheet("css/mainViewStyles.css")
public class SalesConfigView extends VerticalLayout implements RouterLayout, BeanEncryption, BeanDecryption {

	private static final long serialVersionUID = 1L;
	
	transient Logger logger = LoggerFactory.getLogger(SalesConfigView.class);
	
	transient SFDCConfig salesConfig = new SFDCConfig();
	transient Optional<SFDCConfig> salesConfigOptional;
	TextField configname = new TextField();
	TextField clientId = new TextField();
	TextField clientSecret = new TextField();
	TextField userName = new TextField();
	PasswordField sfdcPassword = new PasswordField();
	PasswordField secretToken = new PasswordField();
	Checkbox lead = new Checkbox("Lead");
	Checkbox contact = new Checkbox("Contact");
	Button submit = new Button("Submit");
	Button reset = new Button("Reset");
	String sfdcData = "";
	String data = "";
	RadioButtonGroup<String> checkStatus = new RadioButtonGroup<>();
	RadioButtonGroup<String> syncType = new RadioButtonGroup<>();
	Label status = new Label("Status:");
	Label type = new Label("Sync Type:");
	Label dataSetType = new Label("Dataset Type:");
	transient List<SFDCConfig> salesList;
	transient ListIterator<SFDCConfig> iterator;
	VerticalLayout rightLayout = new VerticalLayout();
	transient FieldsPersistance fieldPersistance = new FieldsPersistance();
	transient List<FieldsBean> fieldsList;
	transient FieldsRepo fieldsRepo;
	transient SFDCConfig salesObj;
	transient MapMasterRepo mapMasterRepo;
	transient List<MapMasterBean> mappingList;
	transient ListIterator<MapMasterBean> mapIterator;
	
	public SalesConfigView(@Autowired SFDCConfigRepo sfdcRepo) {
        this.fieldsRepo = BeanUtil.getBean(FieldsRepo.class);
        this.mapMasterRepo = BeanUtil.getBean(MapMasterRepo.class);
		setPadding(false);
		setMargin(false);
		setHeight("100%");
		status.addClassName("label");
		status.getStyle().set("width", "18%");
		type.addClassName("label");
		type.getStyle().set("width", "18%");
		dataSetType.addClassName("label");
		HorizontalLayout headerLblContainer = new HorizontalLayout();
		Label headerLbl = new Label("Sales Force - Configuration");
		headerLbl.addClassName("configText");
		headerLblContainer.add(headerLbl);
		Binder<SFDCConfig> binder = new BeanValidationBinder<>(SFDCConfig.class);
		checkStatus.setItems("Active", "Inactive");
		syncType.setItems("Full Sync", "Last Update");
		binder.bindInstanceFields(this);
		Long salesConfigId = (Long) VaadinSession.getCurrent().getAttribute("editSalesId");
		if (salesConfigId != null && salesConfigId != 0) {
			salesConfigOptional = sfdcRepo
					.findByConfigId((long) VaadinSession.getCurrent().getAttribute("editSalesId"));
			if (salesConfigOptional.isPresent()) {
				configname.setEnabled(false);
				salesConfig = salesConfigOptional.get();
				salesConfig = (SFDCConfig) decrypt(salesConfig);
				data = salesConfig.getSfdcData();
				String[] splitData = data.split(",");
				for (int i = 0; i < splitData.length; i++) {
					if (splitData[i].equals("Lead")) {
						lead.setValue(true);
					}
					if (splitData[i].equals("Contact")) {
						contact.setValue(true);
					}
				}
			}
		} else {
			salesConfig = new SFDCConfig();
		}
		binder.setBean(salesConfig);
		configname.setRequired(true);
		clientId.setRequired(true);
		clientSecret.setRequired(true);
		userName.setRequired(true);
		sfdcPassword.setRequired(true);
		secretToken.setRequired(true);

		MyCustomLayout layout = new MyCustomLayout();
		layout.getContent().addClassName("salesForceCustomFormLayout");
		layout.getContent().getStyle().set("margin-left", "25%");
		layout.getContent().setResponsiveSteps(new ResponsiveStep("0", 1));
		layout.addItemWithLabel("Conifguration name:", configname);
		layout.addItemWithLabel("Client Id:", clientId);
		layout.addItemWithLabel("Client Secret:", clientSecret);
		layout.addItemWithLabel("User Name:", userName);
		layout.addItemWithLabel("Password:", sfdcPassword);
		layout.addItemWithLabel("Secret Token:", secretToken);
		
		HorizontalLayout dataLayout = new HorizontalLayout();
		HorizontalLayout contentLayout = new HorizontalLayout();
		contentLayout.addClassName("contentLayout");
		rightLayout.addClassName("rightLayout");
		rightLayout.addClassName("config");
		rightLayout.getStyle().set("padding-top", "0px");
		HorizontalLayout buttonLayout = new HorizontalLayout(submit, reset);
		HorizontalLayout checkStatusGroup = new HorizontalLayout();
		checkStatusGroup.add(status, checkStatus);
		checkStatusGroup.getStyle().set("width", "40%");
		HorizontalLayout typeLayout = new HorizontalLayout();
		typeLayout.add(type, syncType);
		typeLayout.getStyle().set("width", "40%");
		dataLayout.getStyle().set("width", "40%");
		NavigationBarView navBar = new NavigationBarView();
		navBar.salesForceConfig.addClassName("active");
		dataLayout.add(dataSetType);
		dataSetType.getStyle().set("font-size", "16px");
		dataLayout.add(lead, contact);
		
		rightLayout.add(headerLblContainer, layout, typeLayout, checkStatusGroup,
				buttonLayout);
		setAlignItems(Alignment.CENTER);
		contentLayout.add(navBar.sidenav, rightLayout);
		add(contentLayout);
		
		submit.addClassNames("cursorPointer", VaadinConstants.STYLEDBUTTON);
		reset.addClassNames("cursorPointer", VaadinConstants.STYLEDBUTTON);
		
		submit.addClickListener(e -> {
			if (!(configname.getValue().isEmpty() || clientId.getValue().isEmpty() || clientSecret.getValue().isEmpty() || userName.getValue().isEmpty()
					|| sfdcPassword.getValue().isEmpty() || secretToken.getValue().isEmpty() || syncType.isEmpty() || checkStatus.isEmpty())) {
				salesConfig.setUserid((int) VaadinSession.getCurrent().getAttribute("userid"));
				if (lead.getValue().equals(true) && contact.getValue().equals(true)) {
					sfdcData = lead.getLabel() + "," + contact.getLabel();
					lead.setValue(false);
					contact.setValue(false);
				} else if (lead.getValue().equals(true)) {
					sfdcData = lead.getLabel();
				} else if (contact.getValue().equals(true)) {
					sfdcData = contact.getLabel();
				}
				salesConfig.setSfdcData(sfdcData);
				salesList = sfdcRepo.findAllByUserid((int) VaadinSession.getCurrent().getAttribute("userid"));
				SFDCConfig name = salesList.stream().filter(x -> configname.getValue().trim().equals(x.getConfigname())).findAny()
						.orElse(null);
				if(name == null) {
					
				/*Enters into this when user is saving the details for the first time*/	
					
			    salesConfig.setConfigname(configname.getValue().trim());
				salesConfig = (SFDCConfig) encrypt(salesConfig);
				sfdcRepo.save(salesConfig);
				salesObj = (SFDCConfig) decrypt(salesConfig);
				if((long) VaadinSession.getCurrent().getAttribute("editSalesId") == 0L) {
					Runnable obj = new SalesMetadataThread(salesObj);
					Thread salesMetadataThread = new Thread(obj);
					salesMetadataThread.start();
				}
				if (salesConfig.getCheckstatus().equals("Inactive")) {
					mappingList = mapMasterRepo.findAllBySalesconfigid(salesConfig.getConfigId());
						mapIterator = mappingList.listIterator();
						while (mapIterator.hasNext()) {
							MapMasterBean current = mapIterator.next();
							current.setStatus("Deleted");
							mapMasterRepo.save(current);
						}
				}
				if (salesConfig.getCheckstatus().equals("Active")) {
					mappingList = mapMasterRepo.findAllBySalesconfigid(salesConfig.getConfigId());
						mapIterator = mappingList.listIterator();
						while (mapIterator.hasNext()) {
							MapMasterBean current = mapIterator.next();
							current.setStatus("Active");
							mapMasterRepo.save(current);
						}
				}
				VaadinSession.getCurrent().setAttribute("editSalesId", 0L);
				submit.getUI().ifPresent(ui -> ui.navigate("salestable"));
				} else if(name.getConfigId().equals(salesConfigId)) {
					
					/* Enters into this when user wants to update his details*/
					
					salesConfig.setConfigname(configname.getValue().trim());
					salesConfig = (SFDCConfig) encrypt(salesConfig);
					salesConfig.setConfigId(salesConfigId);
					sfdcRepo.save(salesConfig);
					if (salesConfig.getCheckstatus().equals("Inactive")) {
						mappingList = mapMasterRepo.findAllBySalesconfigid(salesConfig.getConfigId());
							mapIterator = mappingList.listIterator();
							while (mapIterator.hasNext()) {
								MapMasterBean current = mapIterator.next();
								current.setStatus("Deleted");
								mapMasterRepo.save(current);
							}
					}
					if (salesConfig.getCheckstatus().equals("Active")) {
						mappingList = mapMasterRepo.findAllBySalesconfigid(salesConfig.getConfigId());
							mapIterator = mappingList.listIterator();
							while (mapIterator.hasNext()) {
								MapMasterBean current = mapIterator.next();
								current.setStatus("Active");
								mapMasterRepo.save(current);
							}
					}
					submit.getUI().ifPresent(ui -> ui.navigate("salestable"));
				} else {
					Notification.show("Configuration with this name exists", 5000, Position.TOP_CENTER);
				}
			} else {
				Notification.show("Please fill the required fields", 5000, Position.TOP_CENTER);
			}
		});
		reset.addClickListener(e -> {
			configname.clear();
			clientId.clear();
			clientSecret.clear();
			userName.clear();
			sfdcPassword.clear();
			syncType.clear();
			checkStatus.clear();
		});
	}

	@Override
	public Object encrypt(Object salesObj) {
		SFDCConfig salesConfig = new SFDCConfig();

		try {
			salesConfig.setConfigId(((SFDCConfig) salesObj).getConfigId());
			salesConfig.setUserid(((SFDCConfig) salesObj).getUserid());
			salesConfig.setCheckstatus(((SFDCConfig) salesObj).getCheckstatus());
			salesConfig.setSfdcData(((SFDCConfig) salesObj).getSfdcData());
			salesConfig.setSyncType(((SFDCConfig) salesObj).getSyncType());
			salesConfig.setConfigname(((SFDCConfig) salesObj).getConfigname());
			salesConfig.setClientId(
					Base64.getEncoder().encodeToString(((SFDCConfig) salesObj).getClientId().getBytes("utf-8")));
			salesConfig.setClientSecret(
					Base64.getEncoder().encodeToString(((SFDCConfig) salesObj).getClientSecret().getBytes("utf-8")));
			salesConfig.setUserName(
					Base64.getEncoder().encodeToString(((SFDCConfig) salesObj).getUserName().getBytes("utf-8")));
			salesConfig.setSfdcPassword(
					Base64.getEncoder().encodeToString(((SFDCConfig) salesObj).getSfdcPassword().getBytes("utf-8")));
			salesConfig.setSecretToken(
					Base64.getEncoder().encodeToString(((SFDCConfig) salesObj).getSecretToken().getBytes("utf-8")));
			return salesConfig;
		} catch (UnsupportedEncodingException e) {
			logger.info("Exception is ::{}",e);
		}

		return null;
	}

	@Override
	public Object decrypt(Object salesObj) {
		SFDCConfig salesConfig = new SFDCConfig();
		salesConfig.setConfigId(((SFDCConfig) salesObj).getConfigId());
		salesConfig.setUserid(((SFDCConfig) salesObj).getUserid());
		salesConfig.setCheckstatus(((SFDCConfig) salesObj).getCheckstatus());
		salesConfig.setSfdcData(((SFDCConfig) salesObj).getSfdcData());
		salesConfig.setSyncType(((SFDCConfig) salesObj).getSyncType());
		salesConfig.setConfigname(((SFDCConfig) salesObj).getConfigname());
		try {
			salesConfig.setClientId(
					new String(Base64.getDecoder().decode(((SFDCConfig) salesObj).getClientId()), "utf-8"));
			salesConfig.setClientSecret(
					new String(Base64.getDecoder().decode(((SFDCConfig) salesObj).getClientSecret()), "utf-8"));
			salesConfig.setUserName(
					new String(Base64.getDecoder().decode(((SFDCConfig) salesObj).getUserName()), "utf-8"));
			salesConfig.setSfdcPassword(
					new String(Base64.getDecoder().decode(((SFDCConfig) salesObj).getSfdcPassword()), "utf-8"));
			salesConfig.setSecretToken(
					new String(Base64.getDecoder().decode(((SFDCConfig) salesObj).getSecretToken()), "utf-8"));
			return salesConfig;
		} catch (UnsupportedEncodingException e) {
			logger.info("Exception is ::{}",e);
		}

		return null;
	}

}
