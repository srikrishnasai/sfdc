package com.tadigital.sfdc_campaign.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tadigital.sfdc_campaign.model.SFDCConfig;

/**
 * @author nivedha.g
 *
 */
@Repository
public interface SFDCConfigRepo extends JpaRepository<SFDCConfig, Integer>{
	/**
	 * 
	 * @param userid
	 * @return
	 */
	Optional<SFDCConfig> findByUseridAndCheckstatus(Integer userid,String status);
	
	/**
	 * 
	 * @param userid
	 * @return
	 */
	List<SFDCConfig> findAllByUserid(int userid);

	/**
	 * 
	 * @param configid
	 * @return
	 */
	Optional<SFDCConfig> findByConfigId(long configid);
	
	/**
	 * 
	 * @param userid
	 * @param status
	 * @return
	 */
	List<SFDCConfig> findAllByCheckstatusNotAndUserid(String status,int userid);
	
	
	/**
	 * 
	 * @param userid
	 * @param status
	 * @return
	 */
	@Query(value="SELECT config_name FROM sfdcacs.sfdc_config where userid=?1 and checkstatus=?2",nativeQuery=true)
	List<String> findAllByUseridAndCheckstatus(int userid,String status);
	
	SFDCConfig findByConfigname(String configname);

	SFDCConfig findByConfignameAndUserid(String configname, int userid);
	
}
