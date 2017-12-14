package com.jpw.springboot.model;

import org.springframework.data.annotation.Id;

import com.mongodb.util.JSON;

public class ProfileData {
	//@Id	
	//private String id; // not required	
	private String profileTemplateId; //references profileTemplateId of ProfileTemplate
	private Object data[];
	
	public String getProfileTemplateId() {
		return profileTemplateId;
	}
	public void setProfileTemplateId(String profileTemplateId) {
		this.profileTemplateId = profileTemplateId;
	}
	public Object[] getData() {
		return data;
	}
	public void setData(Object[] data) {
		this.data = data;
	}
}
