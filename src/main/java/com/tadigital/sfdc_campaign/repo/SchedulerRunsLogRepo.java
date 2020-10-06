/**
 * 
 */
package com.tadigital.sfdc_campaign.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tadigital.sfdc_campaign.model.SchedulerRunsLog;


/**
 * @author Srividya.Bobbiti
 *
 */


@Repository
public interface SchedulerRunsLogRepo extends JpaRepository<SchedulerRunsLog, Integer> {
	
	
	Optional<SchedulerRunsLog> findByRunId(int runId);
	
	SchedulerRunsLog findEndstatusByRunId(int runId);
}