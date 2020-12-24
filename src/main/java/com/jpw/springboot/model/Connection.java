package com.jpw.springboot.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Connection extends AbstractModel {
	@Id	
	private String id;		
	private String userId;
	private String connectionUserId;//TARGET
	private String groupId;
	private String positionId;
	
	//alternative for userId, connectionUserId, groupId, positionId
	private String sourceEntityId;
	private String sourceEntityType; //USER, LEGISLATOR, PARTY, DISTRICT
	private String targetEntityId;
	private String targetEntityType;//USER, LEGISLATOR, PARTY, DISTRICT
	
	private String status; //REQUESTED, CONNECTED, REJECTED, DEACTIVATED, FOLLOWING, UNFOLLOWED
	private boolean defaultConnected = false; //
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getConnectionUserId() {
		return connectionUserId;
	}
	public void setConnectionUserId(String connectionUserId) {
		this.connectionUserId = connectionUserId;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getPositionId() {
		return positionId;
	}
	public void setPositionId(String positionId) {
		this.positionId = positionId;
	}
	public String getSourceEntityId() {
		return sourceEntityId;
	}
	public void setSourceEntityId(String sourceEntityId) {
		this.sourceEntityId = sourceEntityId;
	}
	public String getSourceEntityType() {
		return sourceEntityType;
	}
	public void setSourceEntityType(String sourceEntityType) {
		this.sourceEntityType = sourceEntityType;
	}
	public String getTargetEntityId() {
		return targetEntityId;
	}
	public void setTargetEntityId(String targetEntityId) {
		this.targetEntityId = targetEntityId;
	}
	public String getTargetEntityType() {
		return targetEntityType;
	}
	public void setTargetEntityType(String targetEntityType) {
		this.targetEntityType = targetEntityType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isDefaultConnected() {
		return defaultConnected;
	}
	public void setDefaultConnected(boolean defaultConnected) {
		this.defaultConnected = defaultConnected;
	}	
}
