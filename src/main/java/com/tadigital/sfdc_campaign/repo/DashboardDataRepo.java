package com.tadigital.sfdc_campaign.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tadigital.sfdc_campaign.model.DashboardData;

/**
 * @author akhilreddy.b
 *
 */
@Repository
public interface DashboardDataRepo extends JpaRepository<DashboardData, Integer>{

	/**
	 * @param userid
	 * @return
	 */
	List<DashboardData> findByUseridAndDatatype(int userid,String datatype);

	List<DashboardData> findByUserid(int userid); 
	
}
