package com.jpw.springboot.model;

import java.util.List;

import org.springframework.data.annotation.Id;

public class User extends AbstractModel {
	@Id	
	private String userId;
	private String username; //email/phone
	private String password;
	private String userType;//USER, LEGISLATOR, PUBLICUSER
	private String status = "active"; //active
	private String sourceSystem; //GOVTRACK/OPENSTATE
	private String sourceId;	
	
	//shall be part of UserProfile - upCongressLegislatorDefault
	private String firstName;
	private String lastName;
	private String emailId;
	private String phone;
	private String address;
	private String photoUrl;
	
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

}