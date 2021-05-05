package com.jpw.springboot.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.mongodb.BasicDBObject;

@Document
public class User extends AbstractModel {
	@Id	
	private String userId;
	
	@Indexed(unique = true)
	private String username; //email/phone/other alphanumeric
	//field will be indexed with a constraint of unique
	
	private String full_name;//Display name
	private String description;
	private String password;//[applicable only for PUBLICUSER/LEGISLATOR]
	
	//BROADER GROUP OF USER SUCH AS EXECUTIVE, LEGISLATOR
	private String category;//SystemConstants - LEGISLATURE, EXECUTIVE, MUNICIPALITIES, RETIRED, PUBLICUSER, LEGISLATIVE DISTRICT, POLITICAL PARTY, CONGRESS COMMITTEE,CONGRESS SUB COMMITTEE 
	
	//USER TYPE => ROLE
	private String userType;//SystemConstants - LEGISLATOR, PUBLICUSER, LEGISLATIVE_DISTRICT, POLITICAL_PARTY
	//private String[] roles;//createprofile, deleteprofile, deleteprofiletemplate
	private String status = "active"; //active
	private ArrayList<String> profileManagedBy;//NOT USED
	private ArrayList<String> members;//Members of this group
	private ArrayList<String> circleUsers;
	private ArrayList<BasicDBObject> circleUsersInfo;
	private BasicDBObject settings;//{"accessRestriction":true, "blockedUsers":[]}
	//accessRestriction true-PRIVATE, false-PUBLIC[DEFAULT]
	private String modifiedBy;
	@Transient
	private boolean isShowSettings = false;
	@Transient
	private boolean isSelfProfile = false;
	@Transient
	private boolean isProfileManaged = false;

	
	private String photoUrl;
	private String profileAvatarImgFileId;//for profile image
	private String profileBannerImgFileId;//for banner image
	
	//shall be part of UserProfile - upDefault
	//shall be part of UserProfile - upCongressLegislatorDefault
	private String sourceSystem; //GOVTRACK/OPENSTATE
	private String sourceId;	
	private String parentId;	
	
	private Enum tagName;
	@Transient
	private List<Connection> connections;
	//private UserProfile userProfile; // contains profileTemplates, profileDatas
	
	//Available Templates for an user
	//Depends on EntityType
	@Transient	
	private List<ProfileTemplate> profileTemplates;		
	//contains Data for each associated Template
	//private List<ProfileData> profileDatas;
/*	@DBRef
	private ProfileData profileData;*/
	
	//@DBRef
	@Transient	
	private List<ProfileData> profileDatas = new ArrayList<ProfileData>();
	@Transient	//USED AS A DTO FOR UI
	private BasicDBObject biodata = null;
	@Transient	//USED AS A DTO FOR UI
	private BasicDBObject role = null;
	
	//private LegislatorOpenState legislatorOpenState;	
	
	/**
	 * @return the tagName
	 */
	public Enum getTagName() {
		return tagName;
	}

	/**
	 * @param tagName the tagName to set
	 */
	public void setTagName(Enum tagName) {
		this.tagName = tagName;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the userType
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * @param userType
	 *            the userType to set
	 */
	public void setUserType(String userType) {
		this.userType = userType;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}
	
	public String getDisplayName() {
		return full_name;
	}

	public void setDisplayName(String displayName) {
		this.full_name = displayName;
	}
	
	public ArrayList<String> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<String> members) {
		this.members = members;
	}

	public ArrayList<String> getCircleUsers() {
		return circleUsers;
	}

	public void setCircleUsers(ArrayList<String> circleUsers) {
		this.circleUsers = circleUsers;
	}

	public ArrayList<BasicDBObject> getCircleUsersInfo() {
		return circleUsersInfo;
	}

	public void setCircleUsersInfo(ArrayList<BasicDBObject> circleUsersInfo) {
		this.circleUsersInfo = circleUsersInfo;
	}

	public BasicDBObject getSettings() {
		return settings;
	}

	public void setSettings(BasicDBObject settings) {
		this.settings = settings;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public boolean isShowSettings() {
		return isShowSettings;
	}

	public void setShowSettings(boolean isShowSettings) {
		this.isShowSettings = isShowSettings;
	}

	public boolean isSelfProfile() {
		return isSelfProfile;
	}

	public void setSelfProfile(boolean isSelfProfile) {
		this.isSelfProfile = isSelfProfile;
	}

	public boolean isProfileManaged() {
		return isProfileManaged;
	}

	public void setProfileManaged(boolean isProfileManaged) {
		this.isProfileManaged = isProfileManaged;
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

	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ProfileData> getProfileDatas() {
		return profileDatas;
	}

	public void setProfileDatas(List<ProfileData> profileDatas) {
		this.profileDatas = profileDatas;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public void setConnections(List<Connection> connections) {
		this.connections = connections;
	}

	public List<ProfileTemplate> getProfileTemplates() {
		return profileTemplates;
	}

	public void setProfileTemplates(List<ProfileTemplate> profileTemplates) {
		this.profileTemplates = profileTemplates;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getProfileAvatarImgFileId() {
		return profileAvatarImgFileId;
	}

	public void setProfileAvatarImgFileId(String profileAvatarImgFileId) {
		this.profileAvatarImgFileId = profileAvatarImgFileId;
	}

	public String getProfileBannerImgFileId() {
		return profileBannerImgFileId;
	}

	public void setProfileBannerImgFileId(String profileBannerImgFileId) {
		this.profileBannerImgFileId = profileBannerImgFileId;
	}

	public ArrayList<String> getProfileManagedBy() {
		return profileManagedBy;
	}

	public void setProfileManagedBy(ArrayList<String> profileManagedBy) {
		this.profileManagedBy = profileManagedBy;
	}

	public BasicDBObject getBiodata() {
		return biodata;
	}

	public void setBiodata(BasicDBObject biodata) {
		this.biodata = biodata;
	}

	public BasicDBObject getRole() {
		return role;
	}

	public void setRole(BasicDBObject role) {
		this.role = role;
	}

	

}