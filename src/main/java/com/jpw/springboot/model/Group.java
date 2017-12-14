package com.jpw.springboot.model;

import org.springframework.data.annotation.Id;

public class Group {
	@Id	
	private String id;		
	private String groupName;
	private String groupLevel;
	private String groupType;
	private String parentGroupId;
	private String sourceSystem;
	private String sourceId;
	//private ProfileTemplates profileTemplates;
	private ProfileData profileData[];

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}



	/**
	 * @return the groupLevel
	 */
	public String getGroupLevel() {
		return groupLevel;
	}

	/**
	 * @param groupLevel
	 *            the groupLevel to set
	 */
	public void setGroupLevel(String groupLevel) {
		this.groupLevel = groupLevel;
	}

	/**
	 * @return the groupType
	 */
	public String getGroupType() {
		return groupType;
	}

	/**
	 * @param groupType
	 *            the groupType to set
	 */
	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	/**
	 * @return the sourceSystem
	 */
	public String getSourceSystem() {
		return sourceSystem;
	}

	/**
	 * @param sourceSystem
	 *            the sourceSystem to set
	 */
	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	/**
	 * @return the sourceId
	 */
	public String getSourceId() {
		return sourceId;
	}

	/**
	 * @param sourceId
	 *            the sourceId to set
	 */
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentGroupId() {
		return parentGroupId;
	}

	public void setParentGroupId(String parentGroupId) {
		this.parentGroupId = parentGroupId;
	}

	public ProfileData[] getProfileData() {
		return profileData;
	}

	public void setProfileData(ProfileData[] profileData) {
		this.profileData = profileData;
	}

}