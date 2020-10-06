/**
 * The Class ACSConfigView generates a form where Adobe Campaign details are configured and saved.
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
import com.tadigital.sfdc_campaign.model.ACSConfig;
import com.tadigital.sfdc_campaign.model.FieldsBean;
import com.tadigital.sfdc_campaign.model.MapMasterBean;
import com.tadigital.sfdc_campaign.repo.ACSConfigRepo;
import com.tadigital.sfdc_campaign.repo.FieldsRepo;
import com.tadigital.sfdc_campaign.repo.MapMasterRepo;
import com.tadigital.sfdc_campaign.service.BeanDecryption;
import com.tadigital.sfdc_campaign.service.BeanEncryption;
import com.tadigital.sfdc_campaign.service.FieldsPersistance;
import com.tadigital.sfdc_campaign.threads.AcsMetadataThread;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;

/**
 * View Class responsible for rendering Adobe Campaign Standard
 * 
 * @author Ravi.sangubotla
 *
 */
@Route(value = "acsview", layout = MainLayout.class)
@StyleSheet("css/mainViewStyles.css")
public class ACSConfigView extends VerticalLayout implements RouterLayout, BeanEncryption, BeanDecryption {

	private static final long serialVersionUID = 913896580869521324L;

	transient Logger logger = LoggerFactory.getLogger(ACSConfigView.class);
	
	transient ACSConfig acsConfig = new ACSConfig();
	transient Optional<ACSConfig> acsConfigOptional;
	TextField configname = new TextField();
	TextField clientId = new TextField();
	TextField clientSecret = new TextField();
	TextField organizationId = new TextField();
	TextField techAccountId = new TextField();
	Button submit = new Button("Submit");
	Button reset = new Button("Reset");
	VerticalLayout rightLayout = new VerticalLayout();
	RadioButtonGroup<String> checkStatus = new RadioButtonGroup<>();
	Label status = new Label("Status:");
	transient List<ACSConfig> acsList;
	transient ListIterator<ACSConfig> iterator;
	transient FieldsPersistance fieldsPersistance = new FieldsPersistance();
	transient List<FieldsBean> fieldsList;
	transient FieldsRepo fieldsRepo;
	transient List<MapMasterBean> mappingList;
	transient ListIterator<MapMasterBean> mapIterator;
	transient MapMasterRepo mapMasterRepo;

	public ACSConfigView(@Autowired ACSConfigRepo acsRepo) {
		this.mapMasterRepo = BeanUtil.getBean(MapMasterRepo.class);
		this.fieldsRepo = BeanUtil.getBean(FieldsRepo.class);
		
		setPadding(false);
		setMargin(false);
		setHeight("100%");
		Binder<ACSConfig> binder = new BeanValidationBinder<>(ACSConfig.class);
		binder.bindInstanceFields(this);
		Long acsConfigId = (Long) VaadinSession.getCurrent().getAttribute("editConfigId");
		status.addClassName("label");
		checkStatus.setItems("Active", "Inactive");
		HorizontalLayout buttonLayout = new HorizontalLayout(submit, reset);
		HorizontalLayout headerLblContainer = new HorizontalLayout();
		Label headerLbl = new Label("Adobe Campaign Standard - Configuration");
		headerLbl.addClassName("configText");
		headerLblContainer.add(headerLbl);
		
		if (acsConfigId != null && acsConfigId != 0) {
			acsConfigOptional = acsRepo.findByConfigId((long) VaadinSession.getCurrent().getAttribute("editConfigId"));
			if (acsConfigOptional.isPresent()) {
				configname.setEnabled(false);
				acsConfig = acsConfigOptional.get();
				acsConfig = (ACSConfig) decrypt(acsConfig);
			}
		} else {
			acsConfig = new ACSConfig();
		}
		binder.setBean(acsConfig);
		configname.setRequired(true);
		clientId.setRequired(true);
		clientSecret.setRequired(true);
		organizationId.setRequired(true);
		techAccountId.setRequired(true);
	
		MyCustomLayout layout = new MyCustomLayout();
		layout.getContent().addClassName("acsCustomFormLayout");
		layout.getContent().getStyle().set("margin-left", "25%");
		layout.getContent().setResponsiveSteps(new ResponsiveStep("0", 1));
		layout.addItemWithLabel("Conifguration name:", configname);
		layout.addItemWithLabel("Client Id:", clientId);
		layout.addItemWithLabel("Client Secret:", clientSecret);
		layout.addItemWithLabel("Organization Id:", organizationId);
		layout.addItemWithLabel("Tech Account Id:", techAccountId);

		HorizontalLayout contentLayout = new HorizontalLayout();
		contentLayout.addClassName("contentLayout");
		rightLayout.addClassName("rightLayout");
		rightLayout.getStyle().set("display", "grid");
		rightLayout.getStyle().set("justify-items", "center");
		rightLayout.getStyle().set("padding-top", "0px");

		NavigationBarView navBar = new NavigationBarView();
		navBar.acsConfig.addClassName("active");
		HorizontalLayout checkStatusGroup = new HorizontalLayout();
		checkStatusGroup.setWidth("30%");
		checkStatusGroup.add(status, checkStatus);
		rightLayout.add(headerLblContainer, layout, checkStatusGroup,
				buttonLayout);
		contentLayout.add(navBar.sidenav, rightLayout);
		add(contentLayout);
		
		submit.addClassNames("cursorPointer", VaadinConstants.STYLEDBUTTON);
		reset.addClassNames("cursorPointer", VaadinConstants.STYLEDBUTTON);
		
		submit.addClickListener(e -> {
			if (!(configname.getValue().isEmpty() || clientId.getValue().isEmpty() || clientSecret.getValue().isEmpty()
					|| organizationId.getValue().isEmpty() || techAccountId.getValue().isEmpty() || checkStatus.isEmpty())) {
				
				acsConfig.setUserid((int) VaadinSession.getCurrent().getAttribute("userid"));
				acsList = acsRepo.findAllByUserid(acsConfig.getUserid());
				ACSConfig name = acsList.stream().filter(x -> configname.getValue().trim().equals(x.getConfigname())).findAny()
						.orElse(null);
				if (name == null) {
					
					/* Enters into this when user wants to save new configurations*/
					
					acsConfig.setConfigname(configname.getValue().trim());
					acsConfig = (ACSConfig) encrypt(acsConfig);
					acsRepo.save(acsConfig);
					ACSConfig acsObj = (ACSConfig) decrypt(acsConfig);
					if((long) VaadinSession.getCurrent().getAttribute("editConfigId") == 0L) {
						Runnable obj = new AcsMetadataThread(acsObj);
						Thread acsMetadataThread = new Thread(obj);
						acsMetadataThread.start();
					}
					if (acsConfig.getCheckstatus().equals("Inactive")) {
						mappingList = mapMasterRepo.findAllByAcsconfigid(acsConfig.getConfigId());
						mapIterator = mappingList.listIterator();
						while (mapIterator.hasNext()) {
							MapMasterBean current = mapIterator.next();
							current.setStatus("Deleted");
							mapMasterRepo.save(current);
						}
					}
					if (acsConfig.getCheckstatus().equals("Active")) {
						mappingList = mapMasterRepo.findAllByAcsconfigid(acsConfig.getConfigId());
						mapIterator = mappingList.listIterator();
						while (mapIterator.hasNext()) {
							MapMasterBean current = mapIterator.next();
							current.setStatus("Active");
							mapMasterRepo.save(current);
						}
					}
					VaadinSession.getCurrent().setAttribute("editConfigId", 0L);
					submit.getUI().ifPresent(ui -> ui.navigate("acstable"));

				} else if (name.getConfigId().equals(acsConfigId)) {
					
					/* Enters into this method when user wants to update details*/
					
					acsConfig.setConfigId(acsConfigId);
					acsConfig.setConfigname(configname.getValue().trim());
					acsConfig = (ACSConfig) encrypt(acsConfig);
					acsRepo.save(acsConfig);
					if (acsConfig.getCheckstatus().equals("Inactive")) {
						mappingList = mapMasterRepo.findAllByAcsconfigid(acsConfig.getConfigId());
						mapIterator = mappingList.listIterator();
						while (mapIterator.hasNext()) {
							MapMasterBean current = mapIterator.next();
							current.setStatus("Deleted");
							mapMasterRepo.save(current);
						}
					}
					if (acsConfig.getCheckstatus().equals("Active")) {
						mappingList = mapMasterRepo.findAllByAcsconfigid(acsConfig.getConfigId());
						mapIterator = mappingList.listIterator();
						while (mapIterator.hasNext()) {
							MapMasterBean current = mapIterator.next();
							current.setStatus("Active");
							mapMasterRepo.save(current);
						}
					}
					submit.getUI().ifPresent(ui -> ui.navigate("acstable"));
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
			clientSecret.clear();
			techAccountId.clear();
			checkStatus.clear();
		});

	}

	@Override
	public Object decrypt(Object acsObj) {
		ACSConfig acsConfig = new ACSConfig();
		acsConfig.setUserid(((ACSConfig) acsObj).getUserid());
		acsConfig.setCheckstatus(((ACSConfig) acsObj).getCheckstatus());
		acsConfig.setConfigId(((ACSConfig) acsObj).getConfigId());
		acsConfig.setConfigname(((ACSConfig) acsObj).getConfigname());
		try {
			acsConfig.setClientId(new String(Base64.getDecoder().decode(((ACSConfig) acsObj).getClientId()), "utf-8"));
			acsConfig.setClientSecret(
					new String(Base64.getDecoder().decode(((ACSConfig) acsObj).getClientSecret()), "utf-8"));
			acsConfig.setOrganizationId(
					new String(Base64.getDecoder().decode(((ACSConfig) acsObj).getOrganizationId()), "utf-8"));
			acsConfig.setTechAccountId(
					new String(Base64.getDecoder().decode(((ACSConfig) acsObj).getTechAccountId()), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.info("Exception is ::{}",e);
		}

		return acsConfig;
	}

	@Override
	public Object encrypt(Object acsObj) {
		ACSConfig acsConfig = new ACSConfig();
		try {
			acsConfig.setClientId(
					Base64.getEncoder().encodeToString(((ACSConfig) acsObj).getClientId().getBytes("utf-8")));
			acsConfig.setClientSecret(
					Base64.getEncoder().encodeToString(((ACSConfig) acsObj).getClientSecret().getBytes("utf-8")));
			acsConfig.setOrganizationId(
					Base64.getEncoder().encodeToString(((ACSConfig) acsObj).getOrganizationId().getBytes("utf-8")));
			acsConfig.setTechAccountId(
					Base64.getEncoder().encodeToString(((ACSConfig) acsObj).getTechAccountId().getBytes("utf-8")));
			acsConfig.setCheckstatus(((ACSConfig) acsObj).getCheckstatus());
			acsConfig.setUserid(((ACSConfig) acsObj).getUserid());
			acsConfig.setConfigId(((ACSConfig) acsObj).getConfigId());
			acsConfig.setConfigname(((ACSConfig) acsObj).getConfigname());
			return acsConfig;
		} catch (UnsupportedEncodingException e) {
			logger.info("Exception is ::{}",e);
		}

		return null;
	}

}
