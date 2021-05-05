package com.jpw.springboot.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jpw.springboot.model.Connection;
import com.jpw.springboot.model.ProfileData;
import com.jpw.springboot.model.ProfileTemplate;

@Repository
public interface ProfileDataRepository extends MongoRepository<ProfileData, String> {

	//ProfileData findByEntityId(String entityId);
	List<ProfileData> findByEntityId(String entityId);
	List<ProfileData> findByEntityIdAndProfileTemplateId(String entityId, String profileTemplateId);
	List<ProfileData> findByEntityIdAndProfileTemplateIdAndCurrent(String entityId, String profileTemplateId, boolean current);
}
