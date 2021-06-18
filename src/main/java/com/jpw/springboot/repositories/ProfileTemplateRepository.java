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
    
	@Query("{'profileTemplateId':?0, 'category':?1}")
	List<ProfileTemplate> findByProfileTemplateId(String profileTemplateId, String category);
	//same as findByProfileTemplateId
    List<ProfileTemplate> findByProfileTemplateIdAndCategory(String profileTemplateId, String category);
   
    List<ProfileTemplate> findByProfileTemplateIdInAndCategory(List<String> profileTemplateIds, String category);
    List<ProfileTemplate> findByProfileTemplateIdIn(List<String> profileTemplateIds);
    List<ProfileTemplate> findByCategory(String category);	
    List<ProfileTemplate> findByType(String type);	
    List<ProfileTemplate> findByCategoryAndType(String category, String type);
}
