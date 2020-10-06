/**
 * 
 */
package com.tadigital.sfdc_campaign.repo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.tadigital.sfdc_campaign.service.ACSSync;

/**
 * @author nivedha.g
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ACSSyncTest {
	
	@Autowired
	private ACSSync acsSync;
	
	StringBuilder contacts = new StringBuilder();
	
	@Test
	public void acsSyncTest() {
		String resultStr= acsSync.acsData("Contact+mapConfig1",1,contacts,1);
		System.out.println("Test class result"+resultStr);
	}

}
