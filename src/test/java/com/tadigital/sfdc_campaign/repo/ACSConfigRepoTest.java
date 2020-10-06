/**
 * 
 */
package com.tadigital.sfdc_campaign.repo;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.tadigital.sfdc_campaign.model.ACSConfig;

/**
 * @author Ravi.sangubotla
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ACSConfigRepoTest {
	
	@Autowired
	private ACSConfigRepo acsRepo;
	
	@Test
	public void findById() {
		Optional<ACSConfig> configs= acsRepo.findById(1);
		System.out.println("configs.isPresent()::"+configs.isPresent());
		if(configs.isPresent())
			System.out.println("configs.get().getClientid()::"+configs.get().getTechAccountId());
		System.out.println("configs.get().getClientid()::"+configs.get().getTechAccountId());
		
	}
	
}
