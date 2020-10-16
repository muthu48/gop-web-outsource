package com.jpw.springboot.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import com.mongodb.BasicDBObject;

public class User extends AbstractModel {
	@Id	
	private String userId;
	private String username; //email/phone/other alphanumeric
	private String full_name;//Display name
	private String password;//[applicable only for PUBLICUSER/LEGISLATOR]
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
	
	//shall be part of UserProfile - upCongressLegislatorDefault / upDefault
	private String firstName;
	private String lastName;
	private String emailId;
	private String phone;
	private String address;
	
	private String photoUrl;
	private String profileAvatarImgFileId;//for profile image
	private String profileBannerImgFileId;//for banner image
	
	//shall be part of UserProfile - upDefault
	//shall be part of UserProfile - upCongressLegislatorDefault
	private String sourceSystem; //GOVTRACK/OPENSTATE
	private String sourceId;	
	
	private Enum tagName;
	private List<Connection> connections;
	private UserProfile userProfile; // contains profileTemplates, profileDatas
	
	//Available Templates for an user
	//Depends on EntityType
	private List<ProfileTemplate> profileTemplates;		
	//contains Data for each associated Template

	private List<ProfileData> profileDatas;		
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

/*	*//**
	 * @return the userName
	 *//*
	public String getUserName() {
		return userName;
	}

	*//**
	 * @param userName
	 *            the userName to set
	 *//*
	public void setUserName(String userName) {
		this.userName = userName;
	}
*/
/*	*//**
	 * @return the userPassword
	 *//*
	public String getUserPassword() {
		return userPassword;
	}

	*//**
	 * @param userPassword
	 *            the userPassword to set
	 *//*
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
*/
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

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public List<Connection> getConnections() {
		return connections;
	}

	public void setConnections(List<Connection> connections) {
		this.connections = connections;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	public List<ProfileTemplate> getProfileTemplates() {
		return profileTemplates;
	}

	public void setProfileTemplates(List<ProfileTemplate> profileTemplates) {
		this.profileTemplates = profileTemplates;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getDisplayName() {
		return full_name;
	}

	public void setDisplayName(String displayName) {
		this.full_name = displayName;
	}

	public ArrayList<String> getProfileManagedBy() {
		return profileManagedBy;
	}

	public void setProfileManagedBy(ArrayList<String> profileManagedBy) {
		this.profileManagedBy = profileManagedBy;
	}

}