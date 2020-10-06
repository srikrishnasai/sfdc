/**
 * 
 */
package com.tadigital.sfdc_campaign.repo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.tadigital.sfdc_campaign.service.SFDCSync;

/**
 * @author Ravi.sangubotla
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SFDCSyncTest {

	@Autowired
	private SFDCSync sfdcSync;

	
	StringBuilder contacts = new StringBuilder();
	@Test
	public void sfdcSyncTest() {
		String resultStr = sfdcSync.fetchData(1, contacts, 1);
		System.out.println("Result String ::" + resultStr);
		Assert.assertNotNull(resultStr);

	}
}
