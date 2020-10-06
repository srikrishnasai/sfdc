package com.tadigital.sfdc_campaign.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tadigital.sfdc_campaign.model.FieldsBean;

/**
 * 
 * @author saikrishna.sp
 *
 */
@Repository
public interface FieldsRepo extends JpaRepository<FieldsBean,Integer> {
	
	@Query(value="SELECT field_name from sfdcacs.field_repo where belongs_to=?1 and user_id=?2",nativeQuery=true)
	List<String> findByBelongsAndUserId(String repotype,int userid);

	List<FieldsBean> findAllByRepoType(String repotype);
	
	@Query(value="Select field_name from sfdcacs.field_repo where salesconfigid=?1 and user_id=?2 and belongs_to=?3",nativeQuery=true)
	List<String> findBySalesidAndUserIdAndBelongs(long salesid,int userid,String repotype);
	
	@Query(value="Select field_name from sfdcacs.field_repo where acsconfigid=?1 and user_id=?2 and belongs_to=?3",nativeQuery=true)
	List<String> findByAcsidAndUserIdAndBelongs(long acsid,int userid,String repotype);
	
}
