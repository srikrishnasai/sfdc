package com.tadigital.sfdc_campaign.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tadigital.sfdc_campaign.model.ACSConfig;

/**
 * @author Ravi.sangubotla
 *
 */
@Repository
public interface ACSConfigRepo extends JpaRepository<ACSConfig, Integer>{

	/**
	 * @param userid
	 * @return
	 */
	Optional<ACSConfig> findByUseridAndCheckstatus(int userid,String status);
	
	/**
	 * 
	 * @param userid
	 * @return
	 */
	List<ACSConfig> findAllByUserid(int userid);

	/**
	 * 
	 * @param configid
	 * @return
	 */
	Optional<ACSConfig> findByConfigId(long configid);
	
	/**
	 * 
	 * @param userid
	 * @param status
	 * @return
	 */
	List<ACSConfig> findAllByCheckstatusNotAndUserid(String status,int userid);
	
	/**
	 * 
	 * @param userid
	 * @param status
	 * @return
	 */
	@Query(value="SELECT config_name FROM sfdcacs.acs_config where userid=?1 and checkstatus=?2",nativeQuery=true)
	List<String> findAllByUseridAndCheckstatus(int userid,String status);
	
	
	ACSConfig findByConfigname(String configname);
	
	ACSConfig findByConfignameAndUserid(String configname, int userid);

	
}
