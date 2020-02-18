package com.jpw.springboot.service;

import java.util.List;

import com.jpw.springboot.model.Activites;
import com.jpw.springboot.model.Connection;
import com.jpw.springboot.model.User;

public interface SocialService {
	
	Activites findByUserId(String id);
	
	//not used
	Connection follow(String userName, String entityId);
	
	Connection follow(Connection connection);
	
	boolean isSourceEntityFollowingTargetEntity(String userId, String groupId);
	
	public String getRelationshipStatus(String userId, String groupId, boolean checkAndExit);
	
	int getFollowersCount(String entityId);
	
	List<User> getFollowers(String entityId);
	
	List<Connection> getConnections(String userId, String status);
	
	List<String> getConnectionsEntityId(String userId, String status);
	
	public Connection connectionAction(Connection connectionIP) throws Exception;
	

}