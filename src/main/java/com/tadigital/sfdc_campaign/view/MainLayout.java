/**
 * The Class MainLayout generates header and footer components, which can be used in other views.
 *
 */

package com.tadigital.sfdc_campaign.view;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;		
import org.slf4j.LoggerFactory;

import com.tadigital.sfdc_campaign.constants.VaadinConstants;
import com.tadigital.sfdc_campaign.model.TAUser;
import com.tadigital.sfdc_campaign.repo.UserRepository;
import com.tadigital.sfdc_campaign.utils.BeanUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.ui.Transport;

/**
 * @author akhilreddy.b
 *
 */
@Push(transport = Transport.LONG_POLLING)
@StyleSheet("css/styles.css")
public class MainLayout extends Div implements RouterLayout {
	private static final long serialVersionUID = 1L;

	Div contentContainer;
	
	Label username = new Label();
	Dialog dialog = new Dialog();
	Dialog myProfileDialog = new Dialog();
	
	transient UserRepository userRepo;
	transient Logger logger = LoggerFactory.getLogger(MainLayout.class);
	
	@PostConstruct
	private void init() {
		this.userRepo = BeanUtil.getBean(UserRepository.class);
		
		Div logoWrapper = new Div();
		logoWrapper.addClassName("logoWrapper");
		Image logo = new Image();
		logo.setSrc("frontend/images/ta-logo.png");
		logo.addClassName("logo");
		
		Anchor logoNavigator = new Anchor();
		logoNavigator.addClassName("logoNavigator");
		logoNavigator.add(logo);
		logoNavigator.setHref("https://tadigital.com");
		logoNavigator.setTarget("_blank");

		Paragraph navBrand = new Paragraph("Salesforce Connector");
		navBrand.addClassName("navBrand");
		logoWrapper.add(logoNavigator, navBrand);
	
		Div userWrapper = new Div();
		userWrapper.addClassName("userWrapper");
		
		Div userIconWrapper = new Div();
		userIconWrapper.addClassName("userIconWrapper");
		Icon userIcon = new Icon(VaadinIcon.USER);
		userIcon.addClassName("userIcon");
		
		Button changePassword = new Button("Change Password");
		changePassword.addClassName("dropdownWrapper");
		Button myProfile = new Button("My Profile");
		myProfile.addClassName("myProfile");
		myProfileDialogContent(myProfileDialog);
		changePassword(dialog);
		changePassword.addClickListener(e->
			dialog.open()
		);
		
		myProfile.addClickListener(e->
			myProfileDialog.open()
		);
		
		userIconWrapper.add(userIcon, changePassword, myProfile);
		
		Button logout = new Button("Logout");
		logout.addClassNames("logout", VaadinConstants.STYLEDBUTTON);
		logout.addClickListener(e -> logout.getUI().ifPresent(ui -> ui.navigate("")));
		
		TAUser user = userRepo.findByUserid((int) VaadinSession.getCurrent().getAttribute(VaadinConstants.USERID)); 
		username.addClassName("username");
		if(user.getFirstname() != null) {
			username.setText("Welcome " + user.getFirstname());
		} else {
			username.setText("Welcome User");
		}
		userWrapper.add(username, userIconWrapper, logout);
		
		Div header = new Div(logoWrapper, userWrapper);
		header.setClassName("header");
		add(header);

		contentContainer = new Div();
		contentContainer.addClassName("content");
		contentContainer.getStyle().remove("height");

		Paragraph para = new Paragraph("content");
		contentContainer.add(para);
		add(contentContainer);

		Div footer = new Div();
		footer.setClassName("footer");

		
		Paragraph copyRight = new Paragraph("© " + Calendar.getInstance().get(Calendar.YEAR) + " TA Digital");
		copyRight.setClassName("copyRight");

		footer.add(copyRight);
		add(footer);
		
	}

	public void myProfileDialogContent(Dialog myProfileDialog) {
		myProfileDialog.setWidth("600px");
		myProfileDialog.setHeight("420px");
		VerticalLayout modalWrapper = new VerticalLayout();
		modalWrapper.setClassName("modalWrapper");
		HorizontalLayout buttonsWrapper = new HorizontalLayout();
		
		Label msg = new Label();
		msg.getStyle().set(VaadinConstants.COLOR, "green");
		TextField firstname = new TextField();
		TextField lastname = new TextField();
		TextField email = new TextField();
		Checkbox emailNotify = new Checkbox();
		
		emailNotify.setLabel("Notify schedule results");
		firstname.setLabel("First Name");
		firstname.setPlaceholder("Enter your first name");
		lastname.setLabel("Last Name");
		lastname.setPlaceholder("Enter your lastname");
		email.setLabel("emailid");
		firstname.setWidth("60%");
		lastname.setWidth("60%");
		email.setWidth("60%");
		
		Button submit = new Button("Submit");
		submit.addClassName(VaadinConstants.CURSORPOINTER);
		submit.getElement().getThemeList().add("primary");
		
		Button closeModal = new Button("Close");
		closeModal.addClassNames("closeModalButton", VaadinConstants.CURSORPOINTER);
		buttonsWrapper.add(submit, closeModal);
		modalWrapper.add(firstname, lastname, email, emailNotify, buttonsWrapper);
		
		myProfileDialog.add(modalWrapper);
		
		TAUser tauser = userRepo.findByUserid((int) VaadinSession.getCurrent().getAttribute(VaadinConstants.USERID));
		if(tauser.getFirstname() != null) {
			firstname.setValue(tauser.getFirstname());
		}
		if(tauser.getLastname() != null) {
			lastname.setValue(tauser.getLastname());
		}
		email.setValue(tauser.getUserName());
		if(tauser.getSubscribed() != null) {
			if(tauser.getSubscribed().equals("Yes")) {
				emailNotify.setValue(true);
			} else {
				emailNotify.setValue(false);
			}
		}
		
		submit.addClickListener(e-> {
			if(!firstname.getValue().isEmpty()) {
				tauser.setFirstname(firstname.getValue());
			}
			if(!lastname.getValue().isEmpty()) {
				tauser.setLastname(lastname.getValue());
			}
			if(!email.getValue().isEmpty()) {
				tauser.setUserName(email.getValue());
			}
			
			if(emailNotify.getValue()) {
				tauser.setSubscribed("Yes");
			} else {
				tauser.setSubscribed("No");
			}
			userRepo.save(tauser);
			username.setText("Welcome " + firstname.getValue());
			myProfileDialog.close();
			Notification.show("Your profile is updated successfully", 3000, Position.TOP_CENTER);
			
		});
		
		closeModal.addClickListener(e-> 
			myProfileDialog.close()
		);
		
	}

	/**
	 * This method removes the contentContainer div's content from the layout.
	 * 
	 */
	@Override
	public void showRouterLayoutContent(HasElement content) {
		if (content != null) {
			this.contentContainer.removeAll();
			this.contentContainer.add(Objects.requireNonNull((Component) content));
		}
	}
	
	public void changePassword(Dialog dialog) {
		dialog.setWidth("600px");
		dialog.setHeight("300px");

		VerticalLayout modalWrapper = new VerticalLayout();
		modalWrapper.setClassName("modalWrapper");
		HorizontalLayout buttonsWrapper = new HorizontalLayout();
		
		Label msg = new Label();
		PasswordField oldPassword = new PasswordField();
		PasswordField newPassword = new PasswordField();
		
		oldPassword.setLabel("old password");
		oldPassword.setPlaceholder("Enter your old password");
		newPassword.setLabel("new password");
		newPassword.setPlaceholder("Enter your new password");
		oldPassword.setWidth("60%");
		newPassword.setWidth("60%");
		oldPassword.setRequired(true);
		newPassword.setRequired(true);
		
		Button submit = new Button("Submit");
		submit.addClassName("cursorPointer");
		submit.getElement().getThemeList().add("primary");
		
		Button closeModal = new Button("Close");
		closeModal.addClassNames("closeModalButton", "cursorPointer");
		buttonsWrapper.add(submit, closeModal);
		modalWrapper.add(oldPassword, newPassword, buttonsWrapper);
		
		dialog.add(modalWrapper);
		
		submit.addClickListener(e-> {
			if(oldPassword.getValue().isEmpty() || newPassword.getValue().isEmpty()) {
				modalWrapper.remove(msg);
				msg.setText("Enter the required fields");
				msg.getStyle().set("color", "red");
				modalWrapper.add(msg);		
			} else {
				
				if(!validateUser(oldPassword)) {
					modalWrapper.remove(msg);
					msg.setText("Entered old password is wrong");
					msg.getStyle().set("color", "red");
					modalWrapper.add(msg);	 
				} else {
					TAUser taUser = userRepo.findUserNameAndPasswordByUserid((Integer) VaadinSession.getCurrent().getAttribute("userid"));
					try {
						taUser.setPassword(Base64.getEncoder().encodeToString((newPassword.getValue()).getBytes("utf-8")));
					} catch (UnsupportedEncodingException ue) {
						logger.info("Exception is ::{}",ue);
					}
					userRepo.save(taUser);
					modalWrapper.remove(msg);
					oldPassword.clear();
					newPassword.clear();
					dialog.close();
					Notification.show("Your password is updated successfully", 3000, Position.TOP_CENTER);
				}
			}
		});
		
		closeModal.addClickListener(e-> 
			dialog.close()
		);
	}
	
	public boolean validateUser(PasswordField oldPassword) {
		String currentPassword = userRepo.findUserNameAndPasswordByUserid((Integer) VaadinSession.getCurrent().getAttribute("userid")).getPassword();
		String password = new String(Base64.getDecoder().decode(currentPassword));
		return oldPassword.getValue().equals(password); 
	}
}
