package com.tadigital.sfdc_campaign.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tadigital.sfdc_campaign.model.SchedulerRuns;


/**
 * @author Srividya.Bobbiti
 *
 */


@Repository
public interface SchedulerRunsRepo extends CrudRepository<SchedulerRuns, Integer> {

	@Query(value = "select * from sfdcacs.scheduler_runs where userid=?1 order by runid desc", nativeQuery = true)
	List<SchedulerRuns> fetchLastRun(int userid);

}
