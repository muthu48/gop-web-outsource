package com.jpw.springboot.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.jpw.springboot.model.Activites;
import com.jpw.springboot.model.Connection;
import com.jpw.springboot.model.User;
import com.jpw.springboot.repositories.ConnectionEntityRepository;
import com.jpw.springboot.repositories.SocialServiceRepository;
import com.jpw.springboot.repositories.UserRepository;
import com.jpw.springboot.util.SystemConstants;

@Service("socialService")
@Transactional
public class SocialServiceImpl implements SocialService {

	@Autowired
	private SocialServiceRepository socialServiceRepository;

	@Autowired
	private ConnectionEntityRepository connectionEntityRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public Activites findByUserId(String id) {
		return socialServiceRepository.findByUserId(id);
	}

	//not used
	public Connection follow(String userName, String entityId){
		Connection connection = new Connection();
	/*	connection.setUserId(userName);
		connection.setGroupId(entityId);
		connection.setStatus("FOLLOWING");
		connection = connectionEntityRepository.save(connection);*/
		return connection;
		
	}

	public Connection follow(Connection connection){
/*		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		connection.setCreatedDate(now);*/
		connection = connectionEntityRepository.save(connection);
		return connection;
		
	}

	public Connection connectionAction(Connection connectionIP) throws Exception{
		Connection connection = null;
		Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		List<Connection> connections = connectionEntityRepository.findBySourceEntityIdAndTargetEntityId(connectionIP.getSourceEntityId(), connectionIP.getTargetEntityId(), sort);
		
		if(!CollectionUtils.isEmpty(connections) && connections.get(0) != null){
			connection = connections.get(0);
			if(connection != null && connection.getStatus() != null){
				connection.setStatus(connectionIP.getStatus());
				
/*				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();
				connection.setLastModifiedDate(now);*/
				connection = connectionEntityRepository.save(connection);

			}
		}else{
			throw new Exception("Connection Data not found");
		}

		
		return connection;
		
	}
	
	public boolean isSourceEntityFollowingTargetEntity(String userId, String groupId){
		boolean following = false;
		Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		List<Connection> connections = connectionEntityRepository.findBySourceEntityIdAndTargetEntityId(userId, groupId, sort);
		if(!CollectionUtils.isEmpty(connections)){
			Connection connection = connections.get(0);
			if(connection != null && connection.getStatus() != null && "FOLLOWING".equalsIgnoreCase(connection.getStatus())){
				following = true;
			}
		}
		
		return following;
	}

	public String getRelationshipStatus(String sourceEntityId, String targetEntityId, boolean checkAndExit){
		String relationshipStatus = null;
		Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		List<Connection> connections = connectionEntityRepository.findBySourceEntityIdAndTargetEntityId(sourceEntityId, targetEntityId, sort);
		if(!CollectionUtils.isEmpty(connections)){
			Connection connection = connections.get(0);
			if(connection != null && connection.getStatus() != null){
				relationshipStatus = connection.getStatus();
			}
		}else{
			//RELATION IS BIDIRECTIONAL, SO CHECKING THE RELATION FROM REVERSE DIRECTION
			// else part should be executed only once, so controlling it with checkAndExit flag
			//if other entity's connection request is in REQUESTED status, 
			//then it can be considered AWAITING TO ACCEPT/REJECT 
			if(!checkAndExit){ 
				relationshipStatus = getRelationshipStatus(targetEntityId, sourceEntityId, true);
				if(relationshipStatus.equalsIgnoreCase(SystemConstants.REQUESTED_CONNECTION)){
					relationshipStatus = SystemConstants.AWAITING_CONNECTION;
				}
			}
		}
		
		return relationshipStatus;
	}

	//ENTITIES FROM BOTH DIRECTIONS HAVING THE RELATION 'FOLLOWING'
	public List<Connection> getConnections(String userId, String status){
		Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		List<Connection> connections = connectionEntityRepository.findByTargetEntityIdAndStatus(userId, status, sort);
		if(SystemConstants.FOLLOWING_CONNECTION.equalsIgnoreCase(status)){
			List<Connection> sourceConnections = connectionEntityRepository.findBySourceEntityIdAndStatus(userId, status, sort);
			connections.addAll(sourceConnections);
		}
		
		return connections;
	}
	
	public List<String> getConnectionsEntityId(String userId, String status){
		List<String> connectionsIdList = new ArrayList<String>();

		List<Connection> sourceConnections = connectionEntityRepository.findByTargetEntityId(userId, status);
		for(Connection connection : sourceConnections){
			connectionsIdList.add(connection.getSourceEntityId());
		}
		if(SystemConstants.FOLLOWING_CONNECTION.equalsIgnoreCase(status)){
			List<Connection> targetConnections = connectionEntityRepository.findBySourceEntityId(userId, status);
			for(Connection connection : targetConnections){
				connectionsIdList.add(connection.getTargetEntityId());
			}
		}
		return connectionsIdList;
	}

	@Override
	public int getFollowersCount(String entityId) {
		
		return connectionEntityRepository.findByTargetEntityIdAndStatus(entityId, "FOLLOWING").size();
	}

	//ENTITIES THAT FOLLOWS ME
	@Override
	public List<User> getFollowers(String entityId) {
		
		List<User> users = new ArrayList<User>();
		List<Connection> connections = connectionEntityRepository.findByTargetEntityIdAndStatus(entityId, "FOLLOWING");
		
		for(Connection connection: connections) {
			User user = userRepository.findByUsername(connection.getSourceEntityId());
			
			//CAN BE DONE THRU INTERCEPTOR
			if(user != null)
				user.setPassword(null);

			users.add(user);
		}
		
		return users;
	}
	
	public int getFollowingsCount(String entityId) {
		return -1;
	}	
	
	//ENTITIES I FOLLOW
	public List<User> getFollowings(String entityId) {
		return null;
	}

}
