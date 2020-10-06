package com.tadigital.sfdc_campaign.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tadigital.sfdc_campaign.model.AnalyticsConfig;

/**
 * @author akhilreddy.b
 *
 */
@Repository
public interface AnalyticsConfigRepo extends JpaRepository<AnalyticsConfig, Integer>{

	List<AnalyticsConfig> findByUserId(int userId);
	
	AnalyticsConfig findByAnalyticsConfigId(int analyticsConfigId);
	
	List<AnalyticsConfig> findAllByStatusNotAndUserId(String status,int userid);
	
	List<AnalyticsConfig> findAllByStatusNotAndAnalyticsConfigTypeAndUserId(String status, String configtype, int userid);
	
	AnalyticsConfig findAllByAnalyticsConfigNameAndUserId(String configname, int userid);
	
}
