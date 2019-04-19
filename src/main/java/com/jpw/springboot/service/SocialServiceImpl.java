package com.jpw.springboot.service;

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
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		connection.setCreatedDate(now);
		connection = connectionEntityRepository.save(connection);
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

	public String getRelationshipStatus(String userId, String groupId){
		String relationshipStatus = null;
		Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		List<Connection> connections = connectionEntityRepository.findBySourceEntityIdAndTargetEntityId(userId, groupId, sort);
		if(!CollectionUtils.isEmpty(connections)){
			Connection connection = connections.get(0);
			if(connection != null && connection.getStatus() != null){
				relationshipStatus = connection.getStatus();
			}
		}
		
		return relationshipStatus;
	}

}
