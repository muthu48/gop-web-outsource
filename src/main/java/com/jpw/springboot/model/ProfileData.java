package com.jpw.springboot.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.springframework.data.annotation.Id;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
//CONTAINS THE DATA FOR ANY ENTITY PROFILE TEMPLATE, 
//PROFILE DATA CHANGE IS HANDLED BY THIS ENTITY 
public class ProfileData extends AbstractModel implements Comparable<ProfileData>{
	@Id	
	private String id; // not required	
	private String profileTemplateId; //references profileTemplateId of ProfileTemplate
	private BasicDBObject data;
	private BasicDBList dataList; // not required
	private String entityId;
	private String entityType;
	private boolean current;
	
	public String getId() {
		return id;
	}
	public String getProfileTemplateId() {
		return profileTemplateId;
	}
	public void setProfileTemplateId(String profileTemplateId) {
		this.profileTemplateId = profileTemplateId;
	}
	public BasicDBObject getData() {
		return data;
	}
	public JSONObject getJSONData(){
		return new JSONObject(JSON.serialize(this.getData())); 
	}
	public void setData(BasicDBObject data) {
		this.data = data;
	}
	public BasicDBList getDataList() {
		return dataList;
	}
	public void setDataList(BasicDBList dataList) {
		this.dataList = dataList;
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
	public boolean isCurrent() {
		return current;
	}
	public void setCurrent(boolean current) {
		this.current = current;
	}
	@Override
	public int compareTo(ProfileData o) {
		/*
		if(this.getData().getDate("start").compareTo(o.getData().getDate("start")) > 0){
			return 1;
		}else if(this.getData().getDate("start").compareTo(o.getData().getDate("start")) < 0){
			return -1;
		}else{
			return 0;
		}
		*/
		int result = 0;
        try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date paramStartDate = sdf.parse(o.getData().getString("start"));
			Date thisStartDate = sdf.parse(this.getData().getString("start"));
			result = (paramStartDate.compareTo(thisStartDate)); // Descending order
			//result = (thisStartDate.compareTo(paramStartDate)); // Ascending order
        } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return result;
		
	}
	
}
