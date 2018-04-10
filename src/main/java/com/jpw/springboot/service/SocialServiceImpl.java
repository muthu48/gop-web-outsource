package com.jpw.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpw.springboot.model.Activites;
import com.jpw.springboot.model.Connection;
import com.jpw.springboot.repositories.ConnectionEntityRepository;
import com.jpw.springboot.repositories.SocialServiceRepository;

@Service("socialService")
@Transactional
public class SocialServiceImpl implements SocialService {

	@Autowired
	private SocialServiceRepository socialServiceRepository;

	@Autowired
	private ConnectionEntityRepository connectionEntityRepository;
	
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
		connection = connectionEntityRepository.save(connection);
		return connection;
		
	}
	
	public boolean isFollowingUserAndGroup(String userId, String groupId){
		boolean following = false;
		Connection connection = connectionEntityRepository.findByUserIdAndGroupId(userId, groupId);
		if(connection != null && connection.getStatus() != null && "FOLLOWING".equalsIgnoreCase(connection.getStatus())){
			following = true;
		}
		
		return following;
	}

}
