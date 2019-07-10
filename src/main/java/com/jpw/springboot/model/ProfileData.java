package com.jpw.springboot.model;

import org.springframework.data.annotation.Id;

import com.mongodb.util.JSON;
//CONTAINS THE DATA FOR ANY ENTITY PROFILE TEMPLATE, 
//PROFILE DATA CHANGE IS HANDLED BY THIS ENTITY 
public class ProfileData {
	@Id	
	private String id; // not required	
	private String profileTemplateId; //references profileTemplateId of ProfileTemplate
	private Object data;
	private String entityId;
	private String entityType;
	
	public String getProfileTemplateId() {
		return profileTemplateId;
	}
	public void setProfileTemplateId(String profileTemplateId) {
		this.profileTemplateId = profileTemplateId;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	
}
