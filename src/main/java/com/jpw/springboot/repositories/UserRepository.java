package com.jpw.springboot.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jpw.springboot.model.User;
import com.jpw.springboot.model.UserProfile;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

	User findByUsername(String Username);

	void save(UserProfile user);

}
