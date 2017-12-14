package com.jpw.springboot.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jpw.springboot.model.ProfileTemplate;

@Repository
public interface ProfileTemplateRepository extends MongoRepository<ProfileTemplate, String> {

	ProfileTemplate findByName(String name);

}
