/**
 * The Class SalesConfigTable generates list of all the Salesforce Configuration details which can be edited and a new configuration can also be added.
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
import com.tadigital.sfdc_campaign.model.MapMasterBean;
import com.tadigital.sfdc_campaign.model.SFDCConfig;
import com.tadigital.sfdc_campaign.repo.MapMasterRepo;
import com.tadigital.sfdc_campaign.repo.SFDCConfigRepo;
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
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;

/**
 * 
 * @author saikrishna.sp
 *
 */
@Route(value="salestable", layout = MainLayout.class)
@StyleSheet("css/mainViewStyles.css")
public class SalesConfigTable extends Div implements RouterLayout,BeanDecryption,BeanEncryption{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	transient Logger logger = LoggerFactory.getLogger(SalesConfigTable.class);
	
	VerticalLayout rightLayout = new VerticalLayout();
	transient MapMasterRepo mapMasterRepo;
	transient List<MapMasterBean> mappingList;
	transient ListIterator<MapMasterBean> mapIterator;
    
	public SalesConfigTable(@Autowired SFDCConfigRepo sfdcRepo) {
		VaadinSession.getCurrent().setAttribute("editSalesId", 0L);
		setHeight("100%");
		this.mapMasterRepo = BeanUtil.getBean(MapMasterRepo.class);
		List<SFDCConfig> decryptedList = new ArrayList<>();
		List<SFDCConfig> salesConfigList = sfdcRepo.findAllByCheckstatusNotAndUserid("Deleted",(int)VaadinSession.getCurrent().getAttribute("userid"));
		for(int i=0;i<salesConfigList.size();i++) {
			SFDCConfig config = new SFDCConfig();
			config = (SFDCConfig) decrypt(salesConfigList.get(i));
			decryptedList.add(config);
		}
		salesConfigList.sort((sfdcConfig1, sfdcConfig2) -> sfdcConfig1.getCheckstatus().compareToIgnoreCase(sfdcConfig2.getCheckstatus()));
		HorizontalLayout contentLayout = new HorizontalLayout();
		contentLayout.addClassName("contentLayout");
		rightLayout.addClassName("rightLayout");
		NavigationBarView navBar = new NavigationBarView();
		navBar.salesForceConfig.addClassName("active");
		Label headerLbl = new Label("Salesforce - Configuration");
		headerLbl.addClassName("configText");
		Button createConfig = new Button("Create");
		createConfig.addClassNames("configButton", VaadinConstants.STYLEDBUTTON);
		Grid<SFDCConfig> salesTable = new Grid<>();
		salesTable.addClassName("gridStyle");
		salesTable.addColumn(SFDCConfig::getConfigname,"configname").setHeader("Name").setResizable(true);
		salesTable.addColumn(SFDCConfig::getClientId, "clientId").setHeader("Client Id").setResizable(true);
		salesTable.addColumn(SFDCConfig::getClientSecret, "clientSecret").setHeader("Client Secret").setResizable(true);
		salesTable.addColumn(SFDCConfig::getUserName, "userName").setHeader("User Name").setResizable(true);
		salesTable.addColumn(SFDCConfig::getCheckstatus, "checkstatus").setHeader("Status").setResizable(true);
		salesTable.addColumn(new NativeButtonRenderer<>("Edit", item ->  {
			SFDCConfig sd = (SFDCConfig)item;
			VaadinSession.getCurrent().setAttribute("editSalesId", sd.getConfigId());
			salesTable.getUI().ifPresent(ui -> ui.navigate("salesview"));
		}));
		salesTable.addColumn(new NativeButtonRenderer<>("Delete", item ->  {
			SFDCConfig sd = (SFDCConfig)item;
			sd.setCheckstatus("Deleted");
			sfdcRepo.save((SFDCConfig) encrypt(sd));
			decryptedList.remove(sd);
			salesTable.setItems(decryptedList);
			mappingList = mapMasterRepo.findAllBySalesconfigid(sd.getConfigId());
			mapIterator = mappingList.listIterator();
			while (mapIterator.hasNext()) {
				MapMasterBean current = mapIterator.next();
				current.setStatus("Deleted");
				mapMasterRepo.save(current);
			}
		}));
		decryptedList.sort((sfdcConfig1, sfdcConfig2) -> sfdcConfig1.getCheckstatus().compareToIgnoreCase(sfdcConfig2.getCheckstatus()));
		salesTable.setItems(decryptedList);
		salesTable.setHeightByRows(false);
		createConfig.addClickListener(e -> createConfig.getUI().ifPresent(ui -> ui.navigate("salesview")));
		rightLayout.add(headerLbl,createConfig, salesTable);
		contentLayout.add(navBar.sidenav, rightLayout);
		add(contentLayout);
	}

	@Override
	public Object decrypt(Object salesObj) {
		SFDCConfig salesConfig = new SFDCConfig();
		salesConfig.setConfigname(((SFDCConfig) salesObj).getConfigname());
		salesConfig.setConfigId(((SFDCConfig) salesObj).getConfigId());
		salesConfig.setUserid(((SFDCConfig) salesObj).getUserid());
		salesConfig.setCheckstatus(((SFDCConfig) salesObj).getCheckstatus());
		salesConfig.setSfdcData(((SFDCConfig) salesObj).getSfdcData());
		salesConfig.setSyncType(((SFDCConfig) salesObj).getSyncType());
		try {
			salesConfig.setClientId(
					new String(Base64.getDecoder().decode(((SFDCConfig) salesObj).getClientId()), VaadinConstants.ENCODINGFORMAT));
			salesConfig.setClientSecret(
					new String(Base64.getDecoder().decode(((SFDCConfig) salesObj).getClientSecret()), VaadinConstants.ENCODINGFORMAT));
			salesConfig.setUserName(
					new String(Base64.getDecoder().decode(((SFDCConfig) salesObj).getUserName()), VaadinConstants.ENCODINGFORMAT));
			salesConfig.setSfdcPassword(
					new String(Base64.getDecoder().decode(((SFDCConfig) salesObj).getSfdcPassword()), VaadinConstants.ENCODINGFORMAT));
			salesConfig.setSecretToken(
					new String(Base64.getDecoder().decode(((SFDCConfig) salesObj).getSecretToken()), VaadinConstants.ENCODINGFORMAT));
			return salesConfig;
		} catch (UnsupportedEncodingException e) {
			logger.info("Exception is ::{}",e);
		}

		return null;
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
					Base64.getEncoder().encodeToString(((SFDCConfig) salesObj).getClientId().getBytes(VaadinConstants.ENCODINGFORMAT)));
			salesConfig.setClientSecret(
					Base64.getEncoder().encodeToString(((SFDCConfig) salesObj).getClientSecret().getBytes(VaadinConstants.ENCODINGFORMAT)));
			salesConfig.setUserName(
					Base64.getEncoder().encodeToString(((SFDCConfig) salesObj).getUserName().getBytes(VaadinConstants.ENCODINGFORMAT)));
			salesConfig.setSfdcPassword(
					Base64.getEncoder().encodeToString(((SFDCConfig) salesObj).getSfdcPassword().getBytes(VaadinConstants.ENCODINGFORMAT)));
			salesConfig.setSecretToken(
					Base64.getEncoder().encodeToString(((SFDCConfig) salesObj).getSecretToken().getBytes(VaadinConstants.ENCODINGFORMAT)));
			return salesConfig;
		} catch (UnsupportedEncodingException e) {
			logger.info("Exception is ::{}",e);
		}

		return null;
	}

}
