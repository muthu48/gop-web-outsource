package com.jpw.springboot.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jpw.springboot.model.UserProfile;

@Repository
public interface UserProfileRepository extends MongoRepository<UserProfile, Long> {

	UserProfile findByUserId(String userId);

}
