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

	public Connection follow(Connection connection) throws Exception{
/*		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		connection.setCreatedDate(now);*/
		connection = connectionEntityRepository.save(connection);
		return connection;
		
	}

	public Connection connectionAction(Connection connectionIP) throws Exception{
		Connection connection = null;
		//Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		List<Connection> connections = connectionEntityRepository.findBySourceEntityIdAndTargetEntityId(connectionIP.getSourceEntityId(), connectionIP.getTargetEntityId(), Sort.by(Sort.Direction.DESC, "createdDate"));
		
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
	
	public boolean isSourceEntityFollowingTargetEntity(String userId, String groupId) throws Exception{
		boolean following = false;
		//Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		List<Connection> connections = connectionEntityRepository.findBySourceEntityIdAndTargetEntityId(userId, groupId, Sort.by(Sort.Direction.DESC, "createdDate"));
		if(!CollectionUtils.isEmpty(connections)){
			Connection connection = connections.get(0);
			if(connection != null && connection.getStatus() != null && SystemConstants.FOLLOWING_CONNECTION.equalsIgnoreCase(connection.getStatus())){
				following = true;
			}
		}
		
		return following;
	}

	public String getRelationshipStatus(String sourceEntityId, String targetEntityId, boolean checkAndExit) throws Exception{
		String relationshipStatus = null;
		//Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		List<Connection> connections = connectionEntityRepository.findBySourceEntityIdAndTargetEntityId(sourceEntityId, targetEntityId, Sort.by(Sort.Direction.DESC, "createdDate"));
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
				if(relationshipStatus != null && relationshipStatus.equalsIgnoreCase(SystemConstants.REQUESTED_CONNECTION)){
					relationshipStatus = SystemConstants.AWAITING_CONNECTION;
				}
			}
		}
		
		return relationshipStatus;
	}

	//ENTITIES FROM BOTH DIRECTIONS HAVING THE RELATION 'FOLLOWING'
	public List<Connection> getConnections(String userId, String status) throws Exception{
		//Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		List<Connection> connections = new ArrayList<Connection>();
		connections = connectionEntityRepository.findByTargetEntityIdAndStatus(userId, status, Sort.by(Sort.Direction.DESC, "createdDate"));
		if(SystemConstants.FOLLOWING_CONNECTION.equalsIgnoreCase(status)){
			List<Connection> sourceConnections = connectionEntityRepository.findBySourceEntityIdAndStatus(userId, status, Sort.by(Sort.Direction.DESC, "createdDate"));
			connections.addAll(sourceConnections);
		}
		
		return connections;
	}
	
	//Get Connections that follows this User
	//Get Connections that this User follows
	public List<String> getConnectionsEntityId(String entityId, String status) throws Exception{
		List<String> connectionsIdList = new ArrayList<String>();
		List<Connection> connections = null;
		if(SystemConstants.AWAITING_CONNECTION.equalsIgnoreCase(status)){//APPROVAL PENDING
			connections = connectionEntityRepository.findByTargetEntityId(entityId, SystemConstants.REQUESTED_CONNECTION);
			for(Connection connection : connections){
				connectionsIdList.add(connection.getSourceEntityId());
			}
		}else if(SystemConstants.REQUESTED_CONNECTION.equalsIgnoreCase(status)){//REQUEST SENT
			connections = connectionEntityRepository.findBySourceEntityId(entityId, SystemConstants.REQUESTED_CONNECTION);
			for(Connection connection : connections){
				connectionsIdList.add(connection.getTargetEntityId());
			}
		}else if(SystemConstants.ACCEPTED_CONNECTION.equalsIgnoreCase(status)){//FOLLOWERS 
			connections = connectionEntityRepository.findByTargetEntityId(entityId, SystemConstants.FOLLOWING_CONNECTION);
			for(Connection connection : connections){
				connectionsIdList.add(connection.getSourceEntityId());
			}
		}else if(SystemConstants.FOLLOWING_CONNECTION.equalsIgnoreCase(status)){//FOLLOWINGS 
			connections = connectionEntityRepository.findBySourceEntityId(entityId, SystemConstants.FOLLOWING_CONNECTION);
			for(Connection connection : connections){
				connectionsIdList.add(connection.getTargetEntityId());
			}
		}
		
/*		connectionsIdList.add(entityId);
		List<Connection> sourceConnections = connectionEntityRepository.findByTargetEntityId(entityId, status);
		for(Connection connection : sourceConnections){
			connectionsIdList.add(connection.getSourceEntityId());
		}
		if(SystemConstants.FOLLOWING_CONNECTION.equalsIgnoreCase(status)){
			List<Connection> targetConnections = connectionEntityRepository.findBySourceEntityId(entityId, status);
			for(Connection connection : targetConnections){
				connectionsIdList.add(connection.getTargetEntityId());
			}
		}*/
		return connectionsIdList;
	}

	@Override
	public int getFollowersCount(String entityId) throws Exception {		
		int count = 0;
		List<Connection> connections = getFollowersConnection(entityId);
		if(connections != null && connections.size() > 0){
			count = connections.size();
		}
		
		return count;

	}
	
	//ENTITIES THAT FOLLOWS ME
	public List<Connection> getFollowersConnection(String entityId) throws Exception{
		//Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		List<Connection> connections = connectionEntityRepository.findByTargetEntityIdAndStatus(entityId, SystemConstants.FOLLOWING_CONNECTION, Sort.by(Sort.Direction.DESC, "createdDate"));
		
		return connections;
	}

	
	@Override
	public List<User> getFollowers(String entityId) throws Exception {
		List<Connection> connections = getFollowersConnection(entityId);
		List<User> users = getConnectionEntities(connections);		
		return users;
	}

	//get users of the connections, eventually should get other type of entities information
	private List<User> getConnectionEntities(List<Connection> connections) throws Exception{
		List<User> users = new ArrayList<User>();
		
		for(Connection connection: connections) {
			User user = userRepository.findByUsername(connection.getSourceEntityId());
			
			//CAN BE DONE THRU INTERCEPTOR
			if(user != null)
				user.setPassword(null);

			users.add(user);
		}
		
		return users;
		
	}
	
	public int getFollowingsCount(String entityId) throws Exception {
		int count = 0;
		List<Connection> connections = getFollowingsConnection(entityId);
		if(connections != null && connections.size() > 0){
			count = connections.size();
		}
		
		return count;
	}	
	
	//ENTITIES I FOLLOW
	public List<Connection> getFollowingsConnection(String entityId) throws Exception{
		//Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		List<Connection> connections = connectionEntityRepository.findBySourceEntityIdAndStatus(entityId, SystemConstants.FOLLOWING_CONNECTION, Sort.by(Sort.Direction.DESC, "createdDate"));

		
		return connections;
	}
	
	public List<User> getFollowings(String entityId) throws Exception{
		List<Connection> connections = getFollowingsConnection(entityId);
		List<User> users = getConnectionEntities(connections);		
		return users;
	}

}
