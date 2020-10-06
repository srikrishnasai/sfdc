/**
 * The Class LoginView generates a view where user logins with credentials.
 */
package com.tadigital.sfdc_campaign.view;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tadigital.sfdc_campaign.constants.VaadinConstants;
import com.tadigital.sfdc_campaign.model.TAUser;
import com.tadigital.sfdc_campaign.repo.UserRepository;
import com.tadigital.sfdc_campaign.utils.ReadPropertiesFile;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;

/**
 * @author Ravi.sangubotla
 * @author akhilreddy.b
 *
 */
@Route(value = "")
@StyleSheet("css/styles.css")
public class LoginView extends VerticalLayout implements RouterLayout {

	private static final long serialVersionUID = 1L;

	transient Logger logger = LoggerFactory.getLogger(LoginView.class);

	private TextField userName = new TextField("User Name : ");
	private PasswordField password = new PasswordField("Password : ");
	private transient TAUser taUser = new TAUser();
	private PasswordField newPassword = new PasswordField("Enter your new password");
	private TextField otpField = new TextField("Enter OTP sent to mail");
	TextField useremailname = new TextField("User Name");
	Button submitPswd = new Button("Submit");
	Button submitOtp = new Button("Submit");
	public static final String BRANDNAME = "brandName";

	@Autowired
	transient UserRepository userRepo;

	public LoginView() {
		getStyle().set("padding", "0px");
		getStyle().set("margin", "0px");
		getStyle().set("height", "100%");
		submitOtp.addClassName(VaadinConstants.STYLEDBUTTON);
		submitPswd.addClassName(VaadinConstants.STYLEDBUTTON);

		userName.setAutofocus(true);
		userName.setRequired(true);
		password.setRequired(true);
		setPadding(false);
		Binder<TAUser> binder = new BeanValidationBinder<>(TAUser.class);
		binder.bindInstanceFields(this);
		binder.setBean(taUser);

		VerticalLayout contentLayout = new VerticalLayout();
		contentLayout.addClassName("loginContent");
		contentLayout.getStyle().set("align-items", VaadinConstants.CENTER);

		VerticalLayout backgroundLayout = new VerticalLayout();
		backgroundLayout.addClassName("bgLayout");

		VerticalLayout loginMainLayout = new VerticalLayout();
		HorizontalLayout fieldsContainer = new HorizontalLayout();
		fieldsContainer.getStyle().set(VaadinConstants.WIDTH, "100%");
		fieldsContainer.getStyle().set(VaadinConstants.JUSTIFYCONTENT, "center");
		HorizontalLayout loginContainer = new HorizontalLayout();
		loginContainer.getStyle().set(VaadinConstants.WIDTH, "100%");
		loginContainer.getStyle().set(VaadinConstants.JUSTIFYCONTENT, "center");
		HorizontalLayout forgotPasswordContainer = new HorizontalLayout();
		forgotPasswordContainer.getStyle().set(VaadinConstants.WIDTH, "100%");
		forgotPasswordContainer.getStyle().set(VaadinConstants.MARGINTOP, "0px");
		forgotPasswordContainer.getStyle().set(VaadinConstants.JUSTIFYCONTENT, "flex-end");

		Image loginBanner = new Image();
		loginBanner.setSrc("frontend/images/login-banner.png");
		loginBanner.addClassName("loginBanner");
		backgroundLayout.add(loginBanner);

		backgroundLayout.getStyle().remove("width");
		loginMainLayout.addClassName("loginWrapperLayout");
		Button login = new Button("Login");
		login.getStyle().set("margin-top", "36px");
		login.getElement().getThemeList().add("primary");
		login.addClassName(VaadinConstants.STYLEDBUTTON);

		Anchor forgotPassword = new Anchor();
		forgotPassword.setText("Forgot Password?");
		forgotPassword.addClassName("forgotPassword");
		forgotPassword.setTarget("");

		Button fp = new Button("Forgot password?");

		Dialog dialog = createDialog();
		fp.addClassName("forgotPasswordButton");
		fp.addClickListener(e -> dialog.open());
		ProgressBar progress = new ProgressBar();
		progress.setVisible(false);
		/*
		 * password.addKeyPressListener(Key.ENTER, e-> { String pwdStr = null;
		 * 
		 * try { pwdStr =
		 * Base64.getEncoder().encodeToString(password.getValue().getBytes(
		 * VaadinConstants.ENCODINGFORMAT)); } catch (UnsupportedEncodingException e1) {
		 * logger.info("Unsupported Exception is ::{}",e1); }
		 * 
		 * if (isValid(userName.getValue(), pwdStr)) { login.getUI().ifPresent(ui ->
		 * ui.navigate("home")); } else { Notification.show("Check your Credentials:");
		 * }
		 * 
		 * });
		 */
		login.addClickListener(e -> {
			String pwdStr = null;

			try {
				pwdStr = Base64.getEncoder().encodeToString(password.getValue().getBytes("utf-8"));
			} catch (UnsupportedEncodingException e1) {
				logger.info("Unsupported Exception is ::{}", e1);
			}

			if (isValid(userName.getValue(), pwdStr)) {
				login.getUI().ifPresent(ui -> ui.navigate("home"));
			} else {
				Notification.show("Check your Credentials:");
			}
		});
		setSpacing(true);
		setMargin(true);
		setAlignItems(Alignment.CENTER);
		userName.setWidth("40%");
		userName.addClassName("userName");
		password.addClassName("password");
		userName.getStyle().set("position", "relative");
		password.getStyle().set("margin-top", "0px");
		password.setWidth("40%");
		password.getStyle().set("position", "relative");
		login.getStyle().set("width", "20%");
		login.getStyle().set("cursor", "pointer");

		fieldsContainer.add(userName, password, login);
		forgotPasswordContainer.add(fp);
		loginMainLayout.add(fieldsContainer, forgotPasswordContainer);
		contentLayout.add(backgroundLayout);
		contentLayout.add(loginMainLayout);
		add(contentLayout);

	}

	/**
	 * createDialog method creates a dialog and adds a text field and submit button
	 * to it.
	 * 
	 * 
	 */

	private Dialog createDialog() {
		Dialog dialog = new Dialog();
		dialog.setWidth("500px");
		dialog.setHeight("250px");
		dialog.setId("fpmodal");
		VerticalLayout vl = new VerticalLayout();
		vl.setClassName("modalWrapper");
		HorizontalLayout hl = new HorizontalLayout();
		useremailname.setPlaceholder("enter your username");
		useremailname.setRequired(true);
		useremailname.setAutofocus(true);
		useremailname.setTabIndex(0);
		Button submit = new Button();
		Button closeModal = new Button("Close");
		submit.addClassName(VaadinConstants.STYLEDBUTTON);
		closeModal.addClassNames("closeModalButton", VaadinConstants.STYLEDBUTTON);
		Label msg = new Label();
		submit.getElement().getThemeList().add("primary");
		submit.setText("Submit");
		vl.getStyle().set("align-items", "center");
		vl.add(msg);
		hl.add(submit, closeModal);
		vl.add(useremailname, hl);
		dialog.add(vl);
		submit.addClickListener(e -> {
			if (!useremailname.getValue().isEmpty()) {
				TAUser user = userRepo.findByUserName(useremailname.getValue());
				if (user != null) {
					if (sendMail(user)) {
						vl.remove(msg);
						dialog.removeAll();
						hl.removeAll();
						vl.removeAll();
						hl.add(submitOtp, closeModal);
						vl.add(otpField, hl);
						dialog.add(vl);
					} else {
						vl.remove(msg);
						msg.setText("An error occured, try later");
						msg.getStyle().set(VaadinConstants.COLOR, "red");
						vl.add(msg);
					}
				} else {
					vl.remove(msg);
					msg.setText("Username doesn't exist");
					msg.getStyle().set(VaadinConstants.COLOR, "red");
					vl.add(msg);
				}
			} else {
				vl.remove(msg);
				msg.setText("Please enter username");
				msg.getStyle().set(VaadinConstants.COLOR, "red");
				vl.add(msg);
			}
		});
		submitOtp.addClickListener(e -> {
			if (otpField.getValue().isEmpty()) {
				vl.remove(msg);
				msg.setText("Please enter OTP");
				msg.getStyle().set(VaadinConstants.COLOR, "red");
				vl.add(msg);
				dialog.add(vl);
			} else {
				if (otpField.getValue().equals(userRepo.findByUserName(useremailname.getValue()).getOtp())) {
					dialog.removeAll();
					hl.removeAll();
					vl.removeAll();
					hl.add(submitPswd, closeModal);
					vl.add(newPassword, hl);
					dialog.add(vl);
				} else {
					vl.remove(msg);
					msg.setText("Entered OTP is wrong");
					msg.getStyle().set(VaadinConstants.COLOR, "red");
					vl.add(msg);
					dialog.add(vl);
				}
			}
		});

		submitPswd.addClickListener(e -> {
			if (newPassword.getValue().isEmpty()) {
				vl.remove(msg);
				msg.setText("Please enter your new password");
				msg.getStyle().set(VaadinConstants.COLOR, "red");
				vl.add(msg);
			} else {
				dialog.removeAll();
				TAUser tauser = userRepo.findByUserName(useremailname.getValue());
				try {
					tauser.setPassword(Base64.getEncoder().encodeToString((newPassword.getValue()).getBytes("utf-8")));
				} catch (UnsupportedEncodingException uee) {
					logger.error("UnsupportedEncodingException in submitPswd click listener ::{}", uee);
				}
				userRepo.save(tauser);
				vl.remove(msg);
				dialog.close();
				Notification.show("Your password is successfully updated", 3000, Position.TOP_CENTER);
			}
		});
		closeModal.addClickListener(e -> dialog.close());
		
		return dialog;
	}

	/**
	 * isValid method verifies the user's credentials entered and returns true if
	 * the user is present else false.
	 * 
	 */
	private boolean isValid(String userName, String password) {
		Optional<TAUser> user = userRepo.findByUserNameAndPassword(userName, password);
		if (user.isPresent()) {
			VaadinSession.getCurrent().setAttribute("userid", user.get().getUserid());
			VaadinService.getCurrentRequest().setAttribute("userid", user.get().getUserid());
			return true;
		}
		return false;
	}

	public boolean sendMail(TAUser user) {
		ReadPropertiesFile properties = new ReadPropertiesFile();

		HtmlEmail email = new HtmlEmail();

		email.setHostName(properties.getProperty("hostName"));
		email.setSmtpPort(Integer.parseInt(properties.getProperty("smtpPort")));
		email.setAuthentication(properties.getProperty("emailid"), properties.getProperty("password"));
		email.setSSLOnConnect(true);
		email.setSSLCheckServerIdentity(true);
		String msg = "";
		try {
			msg = "Please find OTP below" + "<br>" + "OTP is " + "<b>" + generateOtp() + "</b>" + "<br>" + " Thanks";
			email.setFrom("sfdcacsconnector@gmail.com");
			email.addTo(user.getUserName());
			email.setSubject("Forgot password");
			email.setHtmlMsg(msg);
			email.send();
			return true;
		} catch (EmailException e) {
			logger.info("Exception is ::{}", e);
		}
		return false;
	}

	public String generateOtp() {
		String numbers = "0123456789";

		Random rndmMethod = new Random();

		char[] otpgenerated = new char[4];

		for (int i = 0; i < 4; i++) {
			otpgenerated[i] = numbers.charAt(rndmMethod.nextInt(numbers.length()));
		}
		TAUser tauser = userRepo.findByUserName(useremailname.getValue());
		tauser.setOtp(new String(otpgenerated));
		userRepo.save(tauser);
		return new String(otpgenerated);
	}

}
