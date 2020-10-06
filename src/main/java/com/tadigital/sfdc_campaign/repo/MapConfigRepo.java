package com.tadigital.sfdc_campaign.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tadigital.sfdc_campaign.model.MapConfig;
/**
 * 
 * @author saikrishna.sp
 *
 */
@Repository
public interface MapConfigRepo extends JpaRepository<MapConfig,Integer>{

	/**
	 * 
	 * @param userid
	 * @return
	 */
	@Query(value="SELECT mapped_pair FROM sfdcacs.map_config where user_id=?1",nativeQuery=true)
	List<String> findMappedPairsByUserid(int userid);
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	MapConfig findByMappedpairAndUserid(String map,int userid);
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	MapConfig findByMappedpairAndMapconfigidAndPairBelongsTo(String map,int mapconfigid,String belongs);
	
	/**
	 * 
	 * @param userid
	 * @param mapconfigid
	 * @param belongs
	 * @return
	 */
	@Query(value="SELECT mapped_pair FROM sfdcacs.map_config where user_id=?1 and map_config_id=?2 and pair_belongs_to=?3",nativeQuery=true)
	 List<String> findMappedPairsByUseridAndMapconfigidAndPairBelongsTo(int userid,int mapconfigid,String belongs);
	
	/**
	 * 
	 * @param mapconfigid
	 * @param belongs
	 * @return
	 */
	@Query(value="SELECT mapped_pair FROM sfdcacs.map_config where map_config_id=?1 and pair_belongs_to=?2",nativeQuery=true)
	List<String> findMappedpairsByMapconfigidAndBelongs(int mapconfigid,String belongs);
	
	/**
	 * 
	 * @param mapconfigid
	 * @return
	 */
	@Query(value="SELECT mapped_pair FROM sfdcacs.map_config where map_config_id=?1",nativeQuery=true)
	 List<String> findMappedpairsByMapconfigid(int mapconfigid);
	
	
}
