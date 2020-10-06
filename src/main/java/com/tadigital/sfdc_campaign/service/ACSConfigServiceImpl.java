package com.tadigital.sfdc_campaign.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tadigital.sfdc_campaign.model.ACSConfig;
import com.tadigital.sfdc_campaign.repo.ACSConfigRepo;

/**
 * @author Ravi.sangubotla
 *
 */
@Service
public class ACSConfigServiceImpl {

	@Autowired
	private ACSConfigRepo acsRepo;

	public Optional<ACSConfig> findById(int configId) {
		return acsRepo.findById(configId);
	}

	public ACSConfig save(ACSConfig acsConfig) {
		return acsRepo.save(acsConfig);
		
	}

	public Optional<ACSConfig> findByUserId(int userid,String status) {

		return acsRepo.findByUseridAndCheckstatus(userid,status);
	}

}
