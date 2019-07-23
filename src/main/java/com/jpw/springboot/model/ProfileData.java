package com.jpw.springboot.model;

import org.json.JSONObject;
import org.springframework.data.annotation.Id;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
//CONTAINS THE DATA FOR ANY ENTITY PROFILE TEMPLATE, 
//PROFILE DATA CHANGE IS HANDLED BY THIS ENTITY 
public class ProfileData {
	@Id	
	private String id; // not required	
	private String profileTemplateId; //references profileTemplateId of ProfileTemplate
	private DBObject data;
	private String entityId;
	private String entityType;
	
	public String getProfileTemplateId() {
		return profileTemplateId;
	}
	public void setProfileTemplateId(String profileTemplateId) {
		this.profileTemplateId = profileTemplateId;
	}
	public DBObject getData() {
		return data;
	}
	public JSONObject getJSONData(){
		return new JSONObject(JSON.serialize(this.getData())); 
	}
	public void setData(DBObject data) {
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
