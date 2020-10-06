package com.tadigital.sfdc_campaign.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ravi.sangubotla
 *
 */
public class TAEncrypter {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Logger logger = LoggerFactory.getLogger(TAEncrypter.class);
		// Encode using basic encoder
		String base64encodedString;
		try {
			base64encodedString = Base64.getEncoder().encodeToString("demo".getBytes("utf-8"));
			logger.info("After :{}",base64encodedString);
			// Decode
			byte[] base64decodedBytes = Base64.getDecoder().decode(base64encodedString);
			logger.info("Before :{}",base64decodedBytes);
			
		} catch (UnsupportedEncodingException e) {
			logger.info("Exception :{}",e);
		}

	}

}
