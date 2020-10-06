/**
 * 
 */
package com.tadigital.sfdc_campaign.repo;

import java.util.Base64;

/**
 * @author Ravi.sangubotla
 *
 */
public class TextEncTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String encStr = Base64.getEncoder().encodeToString("welcome".getBytes());
		System.out.println("Encoded:"+encStr);
		System.out.println("Decoded:"+new String(Base64.getDecoder().decode(encStr)));

	}

}
