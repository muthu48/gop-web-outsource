package com.jpw.springboot.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jpw.springboot.model.Group;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {

	Group findByGroupName(String groupName);
	Group findBySourceId(String sourceId);

}
