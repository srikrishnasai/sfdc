package com.tadigital.sfdc_campaign.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tadigital.sfdc_campaign.model.ACSProfile;
import com.tadigital.sfdc_campaign.model.SFContact;
import com.tadigital.sfdc_campaign.model.SFLead;

/**
 * @author saikrishna.sp
 *
 */
public class SFTOACSConverter {

	Logger logger = LoggerFactory.getLogger(SFTOACSConverter.class);

	public ACSProfile contactToProfile(JSONObject jobj, List<String> mappedPairs){
		logger.info("Converting contact object to profile object");
		ACSProfile acsProfile = new ACSProfile();
		Object data;
		for (int i = 0; i < mappedPairs.size(); i++) {
			String[] fields = mappedPairs.get(i).split("\\+");
			if (fields[0].equals("type")) {
				JSONObject attributeObj = jobj.getJSONObject("attributes");
				data = attributeObj.get(fields[0]);
			} else if (fields[0].equals("Company")) {
				continue;
			} else {
				data = jobj.get(fields[0]);
			}
			fields[1] = "set" + fields[1].substring(0, 1).toUpperCase() + fields[1].substring(1, fields[1].length());
			Method[] methods = acsProfile.getClass().getMethods();
			Method targetMethod = Arrays.asList(methods).stream().filter(m -> fields[1].equals(m.getName())).findAny()
					.orElse(null);
			try {
				if (null != data && null != targetMethod) {
					targetMethod.invoke(acsProfile, data);
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.info("Exception is ::{}",e);
			} 
		}
		logger.info("Returns profile object");
		return acsProfile;
	}

	public ACSProfile leadToProfile(JSONObject jobj, List<String> mappedPairs) {
		logger.info("Converting Lead object to profile object");
		ACSProfile acsProfile = new ACSProfile();
		Object data;
		for (int i = 0; i < mappedPairs.size(); i++) {
			String[] fields = mappedPairs.get(i).split("\\+");
			if (fields[0].equals("type")) {
				JSONObject attributeObj = jobj.getJSONObject("attributes");
				data = attributeObj.get(fields[0]);
			} else {
				data = jobj.get(fields[0]);
			}
			fields[1] = "set" + fields[1].substring(0, 1).toUpperCase() + fields[1].substring(1, fields[1].length());
			Method[] methods = acsProfile.getClass().getMethods();
			Method targetMethod = Arrays.asList(methods).stream().filter(m -> fields[1].equals(m.getName())).findAny()
					.orElse(null);
			try {
				if (null != data && null != targetMethod) {
					targetMethod.invoke(acsProfile, data);
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.info("Exception is ::{}",e);
			} 
		}
		logger.info("Returns profile object");
		return acsProfile;
	}

	public SFContact profileToContact(JSONObject jobj, List<String> mappedPairs) {
		logger.info("Converting profile object to Contact object");
		SFContact sfContact = new SFContact();
		Object data;
		for (int i = 0; i < mappedPairs.size(); i++) {
			String[] fields = mappedPairs.get(i).split("\\+");
			if (fields[0].equals("Company")) {
				continue;
			} else {
				data = jobj.get(fields[1]);
			}

			fields[0] = "set" + fields[0].substring(0, 1).toUpperCase() + fields[0].substring(1, fields[0].length());
			Method[] methods = sfContact.getClass().getMethods();
			Method targetMethod = Arrays.asList(methods).stream().filter(m -> fields[0].equals(m.getName())).findAny()
					.orElse(null);
			try {
				if (null != data && null != targetMethod) {
					targetMethod.invoke(sfContact, data);
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.info("Exception is ::{}",e);
			} 
		}
		logger.info("Returns SfContact object");
		return sfContact;

	}

	public SFLead profileToLead(JSONObject jobj, List<String> mappedPairs) {
		logger.info("Converting profile object to Contact object");
		SFLead sfLead = new SFLead();
		Object data;
		for (int i = 0; i < mappedPairs.size(); i++) {
			String[] fields = mappedPairs.get(i).split("\\+");
			data = jobj.get(fields[1]);
			fields[0] = "set" + fields[0].substring(0, 1).toUpperCase() + fields[0].substring(1, fields[0].length());
			Method[] methods = sfLead.getClass().getMethods();
			Method targetMethod = Arrays.asList(methods).stream().filter(m -> fields[0].equals(m.getName())).findAny()
					.orElse(null);
			try {
				if (null != data && null != targetMethod) {
					targetMethod.invoke(sfLead, data);
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.info("Exception is ::{}",e);
			} 
		}
		logger.info("Returns SFlead object");
		return sfLead;

	}

}
