package com.jpw.springboot.model;

import java.util.List;

import org.springframework.data.annotation.Id;

//User can have n UserProfile
public class UserProfile {
	@Id
	private String userProfileId;
	private String profileType; //??
	private String status;
	private String userId;
	
	//Available Templates for an user
	//Depends on EntityType
	private List<ProfileTemplate> profileTemplates;		
	
	//contains Data for each associated Template
	private List<ProfileData> profileDatas;

	public String getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(String userProfileId) {
		this.userProfileId = userProfileId;
	}

	public String getProfileType() {
		return profileType;
	}

	public void setProfileType(String profileType) {
		this.profileType = profileType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<ProfileTemplate> getProfileTemplates() {
		return profileTemplates;
	}

	public void setProfileTemplates(List<ProfileTemplate> profileTemplates) {
		this.profileTemplates = profileTemplates;
	}

	public List<ProfileData> getProfileDatas() {
		return profileDatas;
	}

	public void setProfileDatas(List<ProfileData> profileDatas) {
		this.profileDatas = profileDatas;
	}	
}
