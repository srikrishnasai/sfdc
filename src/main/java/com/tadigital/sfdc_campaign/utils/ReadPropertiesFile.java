package com.tadigital.sfdc_campaign.utils;

import java.io.InputStream;
import java.util.Properties;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;

/**
 * @author nivedha.g
 *
 */
public class ReadPropertiesFile {

	private static final Logger LOG = LoggerFactory.getLogger(ReadPropertiesFile.class);
	
	public String getProperty(String property) {
		
		  Properties prop;
		  InputStream is = null;
		  String url = "";
		  try {
			   prop = new Properties();
			   is = getClass().getResourceAsStream("/application.properties");
			   prop.load(is);
		       url = prop.getProperty(property);
	}catch (Exception e) {
		LOG.error(e.getMessage());
	}
		  return url;
	}
	
	
}
