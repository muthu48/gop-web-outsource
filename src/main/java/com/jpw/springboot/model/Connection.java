package com.jpw.springboot.model;

import org.springframework.data.annotation.Id;

public class Connection {
	@Id	
	private String id;		
	private String userId;
	private String connectionUserId;
	private String groupId;
	private String positionId;
	private String status; //REQUESTED, CONNECTED, REJECTED, DEACTIVATED, FOLLOWING
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
	public String getId() {
		return id;
	}
	
}
