package com.tadigital.sfdc_campaign.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tadigital.sfdc_campaign.model.SFDCConfig;
import com.tadigital.sfdc_campaign.repo.SFDCConfigRepo;

/**
 * @author nivedha.g
 *
 */
@Service
public class SFDCConfigServiceImpl {

	@Autowired
	private SFDCConfigRepo sfdcRepo;

	public Optional<SFDCConfig> findById(int configId) {
		return sfdcRepo.findById(configId);
	}

	public SFDCConfig save(SFDCConfig salesConfig) {
		return sfdcRepo.save(salesConfig);
	}

	public Optional<SFDCConfig> findByUserId(int userid,String status) {
		
		return sfdcRepo.findByUseridAndCheckstatus(userid,status);
	}

}
