package com.tadigital.sfdc_campaign.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tadigital.sfdc_campaign.model.TAUser;

/**
 * @author Ravi.sangubotla
 *
 */
@Repository
public interface UserRepository extends JpaRepository<TAUser, Integer> {

	Optional<TAUser> findByUserNameAndPassword(String userName,String password);
	
	@Query(value="SELECT * FROM sfdcacs.logins where userid=?1", nativeQuery=true)
	TAUser findUserNameAndPasswordByUserid(int userid);
	
	@Query(value="SELECT * FROM sfdcacs.logins where username=?1", nativeQuery=true)
	TAUser findByUserName(String username);
	
	@Query(value="SELECT * FROM sfdcacs.logins where userid=?1", nativeQuery=true)
	TAUser findByUserid(int userid);
	
}
