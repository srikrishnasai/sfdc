/**
 *  The class NavigationBarView generates navBar menu which is used in other views.
 */
package com.tadigital.sfdc_campaign.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;

/**
 * @author akhilreddy.b
 *
 */
public class NavigationBarView extends VerticalLayout implements RouterLayout{

	private static final long serialVersionUID = 1L;
	Button home = new Button("Home");
	Button acsConfig = new Button("ACS Configurations");
	Button salesForceConfig = new Button("Salesforce Configurations");
	Button campaignSync = new Button("Analytics Configurations");
	Button scheduler = new Button("Scheduler Configurations");
	Button mapConfig = new Button("Mapping Configurations");
	public static final String NAVBUTTON = "navButton";
	VerticalLayout leftLayout = new VerticalLayout();
	Div sidenav = new Div();
	
	public NavigationBarView() {	
		
		sidenav.addClassName("sidenav");
		leftLayout.addClassName("leftLayout");
		leftLayout.add(home);
		leftLayout.add(acsConfig);
		leftLayout.add(salesForceConfig);
		leftLayout.add(mapConfig);
		leftLayout.add(campaignSync);
		leftLayout.add(scheduler);

		acsConfig.getStyle().set("background-image", "../images/login-banner.png");
		home.addClassName(NAVBUTTON);
		acsConfig.addClassName(NAVBUTTON);
		salesForceConfig.addClassName(NAVBUTTON);
		scheduler.addClassName(NAVBUTTON);
		mapConfig.addClassName(NAVBUTTON);
		campaignSync.addClassName(NAVBUTTON);

		home.setIcon(new Icon(VaadinIcon.HOME));
		acsConfig.setIcon(new Icon(VaadinIcon.COGS));
		salesForceConfig.setIcon(new Icon(VaadinIcon.COGS));
		mapConfig.setIcon(new Icon(VaadinIcon.COGS));
		scheduler.setIcon(new Icon(VaadinIcon.CALENDAR));
		campaignSync.setIcon(new Icon(VaadinIcon.CHECK_SQUARE_O));
		
		home.addClickListener(e -> home.getUI().ifPresent(ui -> ui.navigate("home"))
		);
		acsConfig.addClickListener(e -> acsConfig.getUI().ifPresent(ui -> ui.navigate("acstable")));
		salesForceConfig.addClickListener(e -> salesForceConfig.getUI().ifPresent(ui -> ui.navigate("salestable")));
		scheduler.addClickListener(e -> scheduler.getUI().ifPresent(ui -> ui.navigate("scheduler")));
		mapConfig.addClickListener(e -> mapConfig.getUI().ifPresent(ui -> ui.navigate("maptable")));
		campaignSync.addClickListener(e -> campaignSync.getUI().ifPresent(ui -> ui.navigate("analyticsconfig")));
		sidenav.add(leftLayout);
	}

}
