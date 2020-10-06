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
public interface MapMasterRepo extends JpaRepository<MapMasterBean,Integer>{

	/**
	 * 
	 * @param userid
	 * @return
	 */
	List<MapMasterBean> findByUserid(int userid);
	
	
	/**
	 * 
	 * @param userid
	 * @param status
	 * @return
	 */
	@Query(value="SELECT * FROM sfdcacs.map_master where user_id=?1 and status=?2",nativeQuery=true)
	MapMasterBean findActiveConfiguration(int userid, String status);
	
	
	/**
	 * 
	 * @param userid
	 * @param status
	 * @return
	 */
	MapMasterBean findByUseridAndStatus(int userid,String status);
	
	
	/**
	 * 
	 * @return
	 */
	@Query(value="SELECT MAX(map_config_id) FROM sfdcacs.map_master",nativeQuery=true)
	Integer findMaxMapconfigid();
	
	/**
	 * 
	 * @param userid
	 * @param mapconfigid
	 * @return
	 */
	List<MapMasterBean> findMappedPairsByUseridAndMapconfigid(int userid,int mapconfigid);
	
	/**
	 * 
	 * @param userid
	 * @param status
	 * @return
	 */
	@Query(value="SELECT config_name from sfdcacs.map_master where user_id=?1 and status=?2",nativeQuery=true)
	List<String> findConfignameByUseridAndStatus(int userid,String status);
	
	/**
	 * 
	 * @param configname
	 * @return
	 */
	MapMasterBean findByConfigname(String configname);
	
	/**
	 * 
	 * @param userid
	 * @return
	 */
	List<MapMasterBean> findAllByUserid(int userid);
	
	/**
	 * 
	 * @param status
	 * @param userid
	 * @return
	 */
	List<MapMasterBean> findAllByStatusNotAndUserid(String status,int userid);
	
	/**
	 * 
	 * @param acsconfig
	 * @return
	 */
	List<MapMasterBean> findAllByAcsconfigid(long acsconfigid);
	
	
	List<MapMasterBean> findAllBySalesconfigid(long salesconfigid);
	
}
