package com.tadigital.sfdc_campaign.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tadigital.sfdc_campaign.model.SchedulerConfig;

/**
 * @author Srividya.Bobbiti
 *
 */

public interface SchedulerRepo extends JpaRepository<SchedulerConfig, Integer>{
	
	@Query(value="select * from sfdcacs.sch_config where userid=?1 order by schid desc",nativeQuery=true)
	List<SchedulerConfig> findByUserId(int userid);

}