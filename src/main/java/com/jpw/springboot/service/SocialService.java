package com.jpw.springboot.service;

import com.jpw.springboot.model.Activites;
import com.jpw.springboot.model.Connection;

public interface SocialService {
	
	Activites findByUserId(String id);
	
	//not used
	Connection follow(String userName, String entityId);
	
	Connection follow(Connection connection);
	
	boolean isFollowingUserAndGroup(String userId, String groupId);
}