package com.jpw.springboot.repositories;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jpw.springboot.model.Connection;

@Repository
public interface ConnectionEntityRepository  extends MongoRepository<Connection, String> {
	List<Connection> findByUserId(String userId);
	List<Connection> findByGroupId(String groupId);	
	List<Connection> findBySourceEntityIdAndTargetEntityId(String sourceEntityId, String targetEntityId, Sort sort);
	//List<Connection> findBySourceEntityIdAndTargetEntityIdAndStatus(String sourceEntityId, String targetEntityId, String status, Sort sort);
	List<Connection> findByTargetEntityIdAndStatus(String targetEntityId, String status);
}
