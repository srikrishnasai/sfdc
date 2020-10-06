package com.tadigital.sfdc_campaign.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tadigital.sfdc_campaign.model.MapMasterBean;
/**
 * 
 * @author akhilreddy.b
 *
 */
@Repository
public interface MapTableRepo extends JpaRepository<MapMasterBean,Integer>{

	/**
	 * 
	 * @param userid
	 * @return
	 */
	List<MapMasterBean> findByUserid(int userid);
	
	@Query(value="SELECT * FROM sfdcacs.map_table where user_id=?1 and status=?2",nativeQuery=true)
	MapMasterBean findActiveConfiguration(int userid, String status);
	
	
}
