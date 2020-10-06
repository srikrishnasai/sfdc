/**
 *  The class MainView is used to show the schedules of the logged in user. 
 */
package com.tadigital.sfdc_campaign.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tadigital.sfdc_campaign.constants.VaadinConstants;
import com.tadigital.sfdc_campaign.model.DashboardData;
import com.tadigital.sfdc_campaign.model.SchedulerConfig;
import com.tadigital.sfdc_campaign.model.SchedulerRunsLog;
import com.tadigital.sfdc_campaign.repo.ACSConfigRepo;
import com.tadigital.sfdc_campaign.repo.DashboardDataRepo;
import com.tadigital.sfdc_campaign.repo.SchedulerRepo;
import com.tadigital.sfdc_campaign.repo.SchedulerRunsLogRepo;
import com.tadigital.sfdc_campaign.service.ACSConfigServiceImpl;
import com.tadigital.sfdc_campaign.service.SFDCConfigServiceImpl;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Crosshair;
import com.vaadin.flow.component.charts.model.Cursor;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.PlotOptionsColumn;
import com.vaadin.flow.component.charts.model.PlotOptionsPie;
import com.vaadin.flow.component.charts.model.Tooltip;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;

/**
 * @author akhilreddy.b
 *
 */
@Route(value = "home", layout = MainLayout.class)
@StyleSheet("css/mainViewStyles.css")
public class HomeView extends VerticalLayout implements RouterLayout {

	private static final long serialVersionUID = 1L;

	VerticalLayout rightLayout = new VerticalLayout();
	VerticalLayout headerLayout = new VerticalLayout();
	transient Logger log = LoggerFactory.getLogger(MainView.class);

	/* The acs service */
	@Autowired
	transient ACSConfigServiceImpl acsService;

	/* The sales service */
	@Autowired
	transient SFDCConfigServiceImpl salesService;

	@Autowired
	transient ACSConfigRepo acsConfigRepo;

	transient DashboardDataRepo dashboardDataRepo;

	transient SchedulerRepo schedulerRepo;

	transient SchedulerRunsLogRepo schedulerRunsLogRepo;

	int todaySchedulesCount = 0;
	int tomorrowSchedulesCount = 0;
	int monthSchedulesCount = 0;
	int todayCompletedSchedulesCount = 0;
	int monthCompletedSchedulesCount = 0;

	public HomeView() {
		this.dashboardDataRepo = BeanUtil.getBean(DashboardDataRepo.class);
		this.schedulerRunsLogRepo = BeanUtil.getBean(SchedulerRunsLogRepo.class);
		setPadding(false);
		setHeight("100%");
		VaadinSession.getCurrent();
		HorizontalLayout contentLayout = new HorizontalLayout();
		contentLayout.addClassName("contentLayout");
		rightLayout.addClassName("rightLayout");
		rightLayout.setSizeFull();
		rightLayout.getStyle().set("padding", "0px");
		rightLayout.getStyle().set("margin-left", "0px");
		rightLayout.getStyle().set("display", "block");

		try {
			calculateSchedules();
		} catch (ParseException pe) {
			log.info("MainView init method::{}", pe.getMessage());
		}
		NavigationBarView navBar = new NavigationBarView();
		navBar.home.addClassName("active");

		Button showSchedules = new Button("View Schedules");
		showSchedules.addClassNames("configButton", VaadinConstants.STYLEDBUTTON);
		showSchedules.addClickListener(e -> showSchedules.getUI().ifPresent(ui -> ui.navigate("schedules")));
		HorizontalLayout firstLayout = new HorizontalLayout();
		VerticalLayout todaySchedules = new VerticalLayout();
		VerticalLayout tomorrowSchedules = new VerticalLayout();
		VerticalLayout monthSchedules = new VerticalLayout();
		firstLayout.addClassName("firstLayout");
		todaySchedules.addClassName("todaySchedules");
		monthSchedules.addClassName("monthSchedules");
		tomorrowSchedules.addClassName("tomorrowSchedules");

		Label todaySchedulesLabel = new Label(
				Integer.toString(todayCompletedSchedulesCount) + "/" + Integer.toString(todaySchedulesCount));
		todaySchedulesLabel.addClassName(VaadinConstants.SCHEDULESLABEL);
		todaySchedules.add(todaySchedulesLabel);
		Label todayLabel = new Label("TODAY");
		todayLabel.addClassName(VaadinConstants.DESCLABEL);
		todaySchedules.add(todayLabel);

		Label tomorrowSchedulesLabel = new Label(Integer.toString(tomorrowSchedulesCount));
		tomorrowSchedulesLabel.addClassName("schedulesLabel");
		tomorrowSchedules.add(tomorrowSchedulesLabel);
		Label tomorrowLabel = new Label("TOMORROW");
		tomorrowLabel.addClassName("descLabel");
		tomorrowSchedules.add(tomorrowLabel);

		Label monthSchedulesLabel = new Label("" + monthCompletedSchedulesCount + "/" + monthSchedulesCount);
		monthSchedulesLabel.addClassName("schedulesLabel");
		monthSchedules.add(monthSchedulesLabel);
		Label monthLabel = new Label("THIS MONTH");
		monthLabel.addClassName("descLabel");
		monthSchedules.add(monthLabel);

		firstLayout.add(todaySchedules, tomorrowSchedules, monthSchedules);
		rightLayout.add(showSchedules, firstLayout);

		HorizontalLayout secondLayout = new HorizontalLayout();
		secondLayout.addClassName("secondLayout");
		secondLayout.add(columnChart());
		secondLayout.add(pieChart());
		rightLayout.add(secondLayout);

		contentLayout.add(navBar.sidenav, rightLayout);
		add(contentLayout);
	}

	public Chart pieChart() {
		int leadDataCount = 0;
		int contactDataCount = 0;
		Chart chart = new Chart(ChartType.PIE);
		chart.addClassName("pieChart");

		ListIterator<DashboardData> leadData = dashboardDataRepo
				.findByUseridAndDatatype((int) VaadinSession.getCurrent().getAttribute(VaadinConstants.USERID), "Lead")
				.listIterator();
		ListIterator<DashboardData> contactData = dashboardDataRepo.findByUseridAndDatatype(
				(int) VaadinSession.getCurrent().getAttribute(VaadinConstants.USERID), "Contact").listIterator();
		while (leadData.hasNext()) {
			leadDataCount = leadDataCount + leadData.next().getDatacount();
		}
		while (contactData.hasNext()) {
			contactDataCount = contactDataCount + contactData.next().getDatacount();
		}

		Configuration conf = chart.getConfiguration();

		conf.setTitle("Leads & Contacts updated.");

		Tooltip tooltip = new Tooltip();
		tooltip.setValueDecimals(1);
		tooltip.setPointFormat("{series.name}: <b>{point.percentage}%</b>");
		conf.setTooltip(tooltip);

		PlotOptionsPie plotOptions = new PlotOptionsPie();
		plotOptions.setAllowPointSelect(true);
		plotOptions.setCursor(Cursor.POINTER);
		plotOptions.setShowInLegend(true);
		conf.setPlotOptions(plotOptions);

		DataSeries series = new DataSeries();
		DataSeriesItem lead = new DataSeriesItem("Lead", leadDataCount);
		lead.setColorIndex(2);
		lead.setHigh(2);
		series.add(lead);
		DataSeriesItem contact = new DataSeriesItem("Contact", contactDataCount);
		contact.setColorIndex(9);
		series.add(contact);
		conf.setSeries(series);
		chart.setVisibilityTogglingDisabled(true);

		return chart;

	}

	public Chart columnChart() {
		Chart chart = new Chart();
		chart.addClassName("columnChart");
		Configuration configuration = chart.getConfiguration();
		configuration.setTitle("Number of profiles updated");
		chart.getConfiguration().getChart().setType(ChartType.COLUMN);

		PlotOptionsColumn plotOptions = new PlotOptionsColumn();
		plotOptions.setPointWidth(25);
		configuration.setPlotOptions(plotOptions);
		ArrayList<Number> data = (ArrayList<Number>) getColumnChartData();
		configuration.addSeries(new ListSeries("Profiles Updated", data));
		XAxis x = new XAxis();
		x.setCrosshair(new Crosshair());
		x.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
		configuration.addxAxis(x);

		YAxis y = new YAxis();
		y.setMin(0);
		y.setTitle("profiles count");
		configuration.addyAxis(y);

		Tooltip tooltip = new Tooltip();
		tooltip.setShared(true);
		configuration.setTooltip(tooltip);

		return chart;

	}

	public List<Number> getColumnChartData() {
		ArrayList<Number> list = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			list.add(i, 0);
		}
		ListIterator<DashboardData> dashboardDataListIterator = dashboardDataRepo
				.findByUserid((int) VaadinSession.getCurrent().getAttribute("userid")).listIterator();
		while (dashboardDataListIterator.hasNext()) {
			DashboardData dashboardData = dashboardDataListIterator.next();
			String date = dashboardData.getDate();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			try {
				Calendar cal = Calendar.getInstance();
				cal.setTime(sdf.parse(date));
				int count = list.get(cal.get(Calendar.MONTH)).intValue();
				list.set((cal.get(Calendar.MONTH)), count + dashboardData.getDatacount());

			} catch (ParseException pe) {
				log.error("ParseException in HomeView getColumnChartData::{}", pe.getMessage());
			}

		}

		return list;
	}

	/**
	 * calculateSchedules method calculates the schedules of a particular user.
	 * 
	 * 
	 */
	private void calculateSchedules() throws ParseException {
		schedulerRepo = BeanUtil.getBean(SchedulerRepo.class);
		ListIterator<SchedulerConfig> iterator = schedulerRepo.findAll().listIterator();

		while (iterator.hasNext()) {
			SchedulerConfig sc = iterator.next();
			if (sc.getUserid() == VaadinSession.getCurrent().getAttribute("userid")) {
				String[] cron = sc.getCronexp().split(" ");
				String startDate = cron[3].split("-")[0] + "-" + cron[4].split("-")[0] + "-" + cron[6].split("-")[0]
						+ " " + cron[2] + ":" + cron[1];
				String endDate = cron[3].split("-")[1] + "-" + cron[4].split("-")[1] + "-" + cron[6].split("-")[1] + " "
						+ cron[2] + ":" + cron[1];
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

				getSchedulesCount(sc, simpleDateFormat, startDate, endDate);
			}
		}

	}

	/**
	 * getSchedulesCount method calculates the schedules of today, tomorrow and this
	 * month.
	 * 
	 * 
	 */
	private void getSchedulesCount(SchedulerConfig sc, SimpleDateFormat simpleDateFormat, String startDate,
			String endDate) throws ParseException {
		String current = simpleDateFormat.format(new Date());
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(simpleDateFormat.parse(current));

		Calendar end = Calendar.getInstance();
		end.setTime(simpleDateFormat.parse(endDate));

		Calendar start = Calendar.getInstance();
		start.setTime(simpleDateFormat.parse(startDate));

		SchedulerRunsLog srl = schedulerRunsLogRepo.findEndstatusByRunId((int) sc.getSchid());

		if (currentDate.compareTo(end) <= 0 && currentDate.compareTo(start) >= 0) {
			todaySchedulesCount++;
			if (srl != null && srl.getEndstatus() != null && (srl.getEndstatus().equalsIgnoreCase("Failure")
					|| srl.getEndstatus().equalsIgnoreCase("Success"))) {
				todayCompletedSchedulesCount++;
			}
		}
		Calendar tomorrow = currentDate;
		tomorrow.add(Calendar.DATE, 1);
		if (tomorrow.compareTo(end) <= 0 && tomorrow.compareTo(start) >= 0) {
			tomorrowSchedulesCount++;
		}

		if (currentDate.get(Calendar.MONTH) == end.get(Calendar.MONTH) || currentDate.get(Calendar.MONTH) == start.get(Calendar.MONTH)) {
			monthSchedulesCount++;
			if (srl != null && srl.getEndstatus() != null && (srl.getEndstatus().equalsIgnoreCase("Failure")
					|| srl.getEndstatus().equalsIgnoreCase("Success"))) {
				monthCompletedSchedulesCount++;
			}
		}

	}

}
