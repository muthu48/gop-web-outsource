package com.jpw.springboot.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jpw.springboot.model.Activites;
import com.jpw.springboot.model.User;

@Repository
public interface SocialServiceRepository extends MongoRepository<Activites, String> {

	Activites findByUserId(String userId);

}
