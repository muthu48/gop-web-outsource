package com.jpw.springboot.service;

import java.util.List;

import com.jpw.springboot.model.Activites;
import com.jpw.springboot.model.Connection;
import com.jpw.springboot.model.User;

public interface SocialService {
	
	Activites findByUserId(String id) throws Exception;
	
	//not used
	Connection follow(String userName, String entityId) throws Exception;
	
	Connection follow(Connection connection) throws Exception;
	
	boolean isSourceEntityFollowingTargetEntity(String userId, String groupId) throws Exception;
	
	public String getRelationshipStatus(String userId, String groupId, boolean checkAndExit) throws Exception;
	
	
	List<String> getConnectionsEntityId(String userId, String status) throws Exception;
	
	public Connection connectionAction(Connection connectionIP) throws Exception;

	List<Connection> getConnections(String userId, String status) throws Exception;

	//FOLLOWERS
	public int getFollowersCount(String entityId) throws Exception;
	public List<Connection> getFollowersConnection(String entityId) throws Exception;
	public List<User> getFollowers(String entityId) throws Exception;	

	//FOLLOWINGS
	public int getFollowingsCount(String entityId) throws Exception;
	public List<Connection> getFollowingsConnection(String entityId) throws Exception;
	public List<User> getFollowings(String entityId) throws Exception;
	

}