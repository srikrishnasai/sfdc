package com.tadigital.sfdc_campaign.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tadigital.sfdc_campaign.model.TAUser;
import com.tadigital.sfdc_campaign.repo.UserRepository;

/**
 * 
 * @author saikrishna.sp
 *
 */
@Service
public class UserServiceImpl {

	@Autowired
	private UserRepository userRepo;
	
	public Optional<TAUser> findByUserNameAndPassword(String userName,String password) {
		return userRepo.findByUserNameAndPassword(userName,password);
	}
	
}
