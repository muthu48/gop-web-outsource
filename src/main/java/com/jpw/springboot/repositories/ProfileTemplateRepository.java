package com.jpw.springboot.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.jpw.springboot.model.ProfileTemplate;

@Repository
public interface ProfileTemplateRepository extends MongoRepository<ProfileTemplate, String> {

	ProfileTemplate findByName(String name);
	List<ProfileTemplate> findByProfileTemplateId(String profileTemplateId);
    
	@Query("{'profileTemplateId':?0, 'type':?1}")
	List<ProfileTemplate> findByProfileTemplateId(String profileTemplateId, String type);
    
    List<ProfileTemplate> findByProfileTemplateIdIn(List<String> profileTemplateIds);
    
    List<ProfileTemplate> findByProfileTemplateIdInAndType(List<String> profileTemplateIds, String type);
	
    List<ProfileTemplate> findByType(String type);	

}
