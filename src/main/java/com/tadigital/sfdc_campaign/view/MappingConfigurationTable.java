/**
 *  The class MainView is used to show the schedules of the logged in user. 
 */
package com.tadigital.sfdc_campaign.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tadigital.sfdc_campaign.constants.VaadinConstants;
import com.tadigital.sfdc_campaign.model.ACSConfig;
import com.tadigital.sfdc_campaign.model.MapMasterBean;
import com.tadigital.sfdc_campaign.model.SFDCConfig;
import com.tadigital.sfdc_campaign.repo.ACSConfigRepo;
import com.tadigital.sfdc_campaign.repo.MapMasterRepo;
import com.tadigital.sfdc_campaign.repo.SFDCConfigRepo;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;


/**
 * @author akhilreddy.b
 *
 */
@Route(value="maptable", layout = MainLayout.class)
@StyleSheet("css/mainViewStyles.css")
public class MappingConfigurationTable extends VerticalLayout implements RouterLayout{

	private static final long serialVersionUID = 1L;
	
	VerticalLayout rightLayout = new VerticalLayout();
	
	transient MapMasterRepo mapMasterRepo;
	
	transient ACSConfigRepo acsRepo;
	transient SFDCConfigRepo sfdcRepo;
	transient List<MapMasterBean> mappingConfigurations = new ArrayList<>();
	transient Optional<ACSConfig> acsConfigBean;
	transient Optional<SFDCConfig> sfdcConfigBean;
	
	public MappingConfigurationTable() {	
		setPadding(false);
		setHeight("100%");
		HorizontalLayout contentLayout = new HorizontalLayout();
		contentLayout.addClassName("contentLayout");
		rightLayout.addClassName("rightLayout");
		rightLayout.setSizeFull();
		
		Label headerLbl = new Label("Field Mapping - Configuration");
		headerLbl.addClassName("configText");
		Button createConfig = new Button("Create");
		createConfig.addClassNames("configButton", VaadinConstants.STYLEDBUTTON);
		int userid = (int) VaadinSession.getCurrent().getAttribute("userid");
		NavigationBarView navBar = new NavigationBarView();
		navBar.mapConfig.addClassName("active");
		rightLayout.add(headerLbl,createConfig);
		add(contentLayout);
		this.mapMasterRepo = BeanUtil.getBean(MapMasterRepo.class);
		this.acsRepo = BeanUtil.getBean(ACSConfigRepo.class);
		this.sfdcRepo = BeanUtil.getBean(SFDCConfigRepo.class);
		VaadinSession.getCurrent().setAttribute(VaadinConstants.CONFIGNAME, "");
		VaadinSession.getCurrent().setAttribute("configstatus", "");
		VaadinSession.getCurrent().setAttribute("configid", 0);
		VaadinSession.getCurrent().setAttribute(VaadinConstants.SALESCONFIGURATION, "");
		VaadinSession.getCurrent().setAttribute(VaadinConstants.ACSCONFIGURATION, "");
		VaadinSession.getCurrent().setAttribute("acsconfigid", 0L);
		VaadinSession.getCurrent().setAttribute("salesconfigid", 0L);
		createConfig.addClickListener(e -> createConfig.getUI().ifPresent(ui -> ui.navigate("map")));
		
		
		mappingConfigurations = mapMasterRepo.findAllByStatusNotAndUserid("Deleted",userid);
		mappingConfigurations.sort((mapTableBean1, mapTableBean2) -> mapTableBean1.getStatus().compareToIgnoreCase(mapTableBean2.getStatus()));
		
		for(int i=0;i<mappingConfigurations.size();i++) {
			MapMasterBean mb = mappingConfigurations.get(i);
			sfdcConfigBean = sfdcRepo.findByConfigId(mb.getSalesconfigid());
			acsConfigBean = acsRepo.findByConfigId(mb.getAcsconfigid());
			SFDCConfig sfdcdb = sfdcConfigBean.isPresent() ? sfdcConfigBean.get() : null;
			ACSConfig acsdb = acsConfigBean.isPresent() ? acsConfigBean.get() : null;
			if(acsdb != null) {
				mb.setAcsconfig(acsdb.getConfigname());
			}
			if(sfdcdb != null) {
			mb.setSalesconfig(sfdcdb.getConfigname());
			}
		}
		
		Grid<MapMasterBean> grid = new Grid<>();
		grid.addClassName("gridStyle");
		grid.addColumn(MapMasterBean::getConfigname, "configname").setHeader("Name").setResizable(true);
		grid.addColumn(MapMasterBean::getAcsconfig,"acsconfiguration").setHeader("Acs Configuration").setResizable(true);
		grid.addColumn(MapMasterBean::getSalesconfig,"salesconfiguration").setHeader("Sales Configuration").setResizable(true);
		grid.addColumn(MapMasterBean::getStatus, "status").setHeader("Status").setResizable(true);
		grid.addColumn(new NativeButtonRenderer<>("Edit", item ->  {
			MapMasterBean mtb = (MapMasterBean)item;
			SFDCConfig sfdcdb = sfdcRepo.findByConfigId(mtb.getSalesconfigid()).get();
			ACSConfig acsdb = acsRepo.findByConfigId(mtb.getAcsconfigid()).get();
			VaadinSession.getCurrent().setAttribute("configname", mtb.getConfigname());
			VaadinSession.getCurrent().setAttribute("configstatus", mtb.getStatus());
			VaadinSession.getCurrent().setAttribute("configid", mtb.getMapconfigid());
			VaadinSession.getCurrent().setAttribute("salesconfiguration", sfdcdb.getConfigname());
			VaadinSession.getCurrent().setAttribute("acsconfiguration", acsdb.getConfigname());
			VaadinSession.getCurrent().setAttribute("acsconfigid", acsdb.getConfigId());
			VaadinSession.getCurrent().setAttribute("salesconfigid", sfdcdb.getConfigId());
			grid.getUI().ifPresent(ui -> ui.navigate("map"));
		}));
		grid.addColumn(new TAButtonRenderer<>("Delete", item ->  {
			MapMasterBean mmb = (MapMasterBean)item;
			mmb.setStatus("Deleted");
			mapMasterRepo.save(mmb);
			mappingConfigurations.remove(mmb);
			grid.setItems(mappingConfigurations);
		})).setId(VaadinConstants.STYLEDBUTTON);
		grid.setItems(mappingConfigurations);
		rightLayout.add(grid);
		contentLayout.add(navBar.sidenav, rightLayout);
	}

}
