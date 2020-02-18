package com.jpw.springboot.repositories;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.jpw.springboot.model.Connection;

@Repository
public interface ConnectionEntityRepository  extends MongoRepository<Connection, String> {
	List<Connection> findByUserId(String userId);
	List<Connection> findByGroupId(String groupId);	
	List<Connection> findBySourceEntityIdAndTargetEntityId(String sourceEntityId, String targetEntityId, Sort sort);
	//List<Connection> findBySourceEntityIdAndTargetEntityIdAndStatus(String sourceEntityId, String targetEntityId, String status, Sort sort);
	List<Connection> findByTargetEntityIdAndStatus(String targetEntityId, String status);
	List<Connection> findByTargetEntityIdAndStatus(String targetEntityId, String status, Sort sort);
	List<Connection> findBySourceEntityIdAndStatus(String sourceEntityId, String status, Sort sort);
	
	//THIS IS SAME findBySourceEntityIdAndStatus, HOWEVER GETTING ONLY LIMITED FIELDS
	@Query(value="{ 'sourceEntityId' : ?0, 'status' : ?1 }", fields="{ 'targetEntityId' : 1}")
	List<Connection> findBySourceEntityId(String entityId, String status);
	
	//THIS IS SAME findByTargetEntityIdAndStatus, HOWEVER GETTING ONLY LIMITED FIELDS
	@Query(value="{ 'targetEntityId' : ?0, 'status' : ?1 }", fields="{ 'sourceEntityId' : 1}")
	List<Connection> findByTargetEntityId(String entityId, String status);
}
