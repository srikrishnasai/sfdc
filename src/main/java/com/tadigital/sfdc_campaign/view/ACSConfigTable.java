/**
 * The Class ACSConfigTable generates list of all the Adobe Campaign Configuration details which can be edited and a new configuration can also be added.
 */
package com.tadigital.sfdc_campaign.view;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tadigital.sfdc_campaign.constants.VaadinConstants;
import com.tadigital.sfdc_campaign.model.ACSConfig;
import com.tadigital.sfdc_campaign.model.MapMasterBean;
import com.tadigital.sfdc_campaign.repo.ACSConfigRepo;
import com.tadigital.sfdc_campaign.repo.MapMasterRepo;
import com.tadigital.sfdc_campaign.service.BeanDecryption;
import com.tadigital.sfdc_campaign.service.BeanEncryption;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;

/**
 * 
 * @author saikrishna.sp
 *
 */

@Route(value = "acstable", layout= MainLayout.class)
@StyleSheet("css/mainViewStyles.css")
public class ACSConfigTable extends Div implements RouterLayout,BeanDecryption, BeanEncryption{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	transient Logger logger = LoggerFactory.getLogger(ACSConfigTable.class);
	
	VerticalLayout rightLayout = new VerticalLayout();

	transient MapMasterRepo mapMasterRepo;
	transient List<MapMasterBean> mappingList;
	transient ListIterator<MapMasterBean> mapIterator;
    
	public ACSConfigTable(@Autowired ACSConfigRepo acsRepo) {
		VaadinSession.getCurrent().setAttribute("editConfigId", 0L);
	    this.mapMasterRepo = BeanUtil.getBean(MapMasterRepo.class);
		List<ACSConfig> decryptedList = new ArrayList<>();
		List<ACSConfig> acsConfigList = acsRepo.findAllByCheckstatusNotAndUserid("Deleted",(int)VaadinSession.getCurrent().getAttribute("userid"));
		for(int i=0;i<acsConfigList.size();i++) {
			ACSConfig config = new ACSConfig();
			config = (ACSConfig) decrypt(acsConfigList.get(i));
			decryptedList.add(config);
		}
		acsConfigList.sort((acsConfig1, acsConfig2) -> acsConfig1.getCheckstatus().compareToIgnoreCase(acsConfig2.getCheckstatus()));
		Label headerLbl = new Label("Adobe Campaign Standard - Configuration");
		headerLbl.addClassName("configText");
		Button createConfig = new Button("Create");
		createConfig.addClassNames("configButton", VaadinConstants.STYLEDBUTTON);
		HorizontalLayout contentLayout = new HorizontalLayout();
		contentLayout.addClassName("contentLayout");
		rightLayout.addClassName("rightLayout");
		NavigationBarView navBar = new NavigationBarView();
		navBar.acsConfig.addClassName("active");
		Grid<ACSConfig> acsTable = new Grid<>();
		acsTable.addClassName("gridStyle");
		acsTable.addColumn(ACSConfig::getConfigname).setHeader("Name").setResizable(true);
		acsTable.addColumn(ACSConfig::getClientId).setHeader("Client Id").setResizable(true);
		acsTable.addColumn(ACSConfig::getClientSecret).setHeader("Client Secret").setResizable(true);
		acsTable.addColumn(ACSConfig::getOrganizationId).setHeader("Organization Id").setResizable(true);
		acsTable.addColumn(ACSConfig::getTechAccountId).setHeader("Tech Account Id").setResizable(true);
		acsTable.addColumn(ACSConfig::getCheckstatus).setHeader("Status");
		 acsTable.addColumn(new TAButtonRenderer<>("Edit", item ->  {
				ACSConfig ac = (ACSConfig)item;
				VaadinSession.getCurrent().setAttribute("editConfigId", ac.getConfigId());
				acsTable.getUI().ifPresent(ui -> ui.navigate("acsview"));
			}));
		 acsTable.addColumn(new TAButtonRenderer<>("Delete", item ->  {
				ACSConfig ac = (ACSConfig)item;
				ac.setCheckstatus("Deleted");
				acsRepo.save((ACSConfig) encrypt(ac));
				decryptedList.remove(ac);
				acsTable.setItems(decryptedList);
				mappingList = mapMasterRepo.findAllByAcsconfigid(ac.getConfigId());
				mapIterator = mappingList.listIterator();
				while (mapIterator.hasNext()) {
					MapMasterBean current = mapIterator.next();
					current.setStatus("Deleted");
					mapMasterRepo.save(current);
				}
			}));
		decryptedList.sort((ACSConfig1, ACSConfig2) -> ACSConfig1.getCheckstatus().compareToIgnoreCase(ACSConfig2.getCheckstatus()));
		acsTable.setItems(decryptedList);
		acsTable.setHeightByRows(false);
		createConfig.addClickListener(e -> createConfig.getUI().ifPresent(ui -> ui.navigate("acsview")));
		rightLayout.add(headerLbl,createConfig, acsTable);
		contentLayout.add(navBar.sidenav, rightLayout);
		add(contentLayout);
		
		setSizeFull();
		
	}

	@Override
	public Object decrypt(Object acsObj) {
		ACSConfig acsConfig = new ACSConfig();
		acsConfig.setUserid(((ACSConfig) acsObj).getUserid());
		acsConfig.setCheckstatus(((ACSConfig) acsObj).getCheckstatus());
		acsConfig.setConfigId(((ACSConfig) acsObj).getConfigId());
		acsConfig.setConfigname(((ACSConfig) acsObj).getConfigname());
		try {
			acsConfig.setClientId(new String(Base64.getDecoder().decode(((ACSConfig) acsObj).getClientId()), VaadinConstants.ENCODINGFORMAT));
			acsConfig.setClientSecret(
					new String(Base64.getDecoder().decode(((ACSConfig) acsObj).getClientSecret()), VaadinConstants.ENCODINGFORMAT));
			acsConfig.setOrganizationId(
					new String(Base64.getDecoder().decode(((ACSConfig) acsObj).getOrganizationId()), VaadinConstants.ENCODINGFORMAT));
			acsConfig.setTechAccountId(
					new String(Base64.getDecoder().decode(((ACSConfig) acsObj).getTechAccountId()), VaadinConstants.ENCODINGFORMAT));
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
					Base64.getEncoder().encodeToString(((ACSConfig) acsObj).getClientId().getBytes(VaadinConstants.ENCODINGFORMAT)));
			acsConfig.setClientSecret(
					Base64.getEncoder().encodeToString(((ACSConfig) acsObj).getClientSecret().getBytes(VaadinConstants.ENCODINGFORMAT)));
			acsConfig.setOrganizationId(
					Base64.getEncoder().encodeToString(((ACSConfig) acsObj).getOrganizationId().getBytes(VaadinConstants.ENCODINGFORMAT)));
			acsConfig.setTechAccountId(
					Base64.getEncoder().encodeToString(((ACSConfig) acsObj).getTechAccountId().getBytes(VaadinConstants.ENCODINGFORMAT)));
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
