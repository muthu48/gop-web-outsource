package com.jpw.springboot.service;


import java.util.List;

import com.jpw.springboot.model.LegislatorCongressGT;
import com.jpw.springboot.model.LegislatorOpenState;
import com.jpw.springboot.model.ProfileData;
import com.jpw.springboot.model.User;
import com.jpw.springboot.model.UserProfile;

public interface UserService {
	
	User findById(String id);
	public User findByUserName(String name) throws Exception;
	User getUser(String name) throws Exception;
	LegislatorOpenState findLegislator(String name);
	public LegislatorCongressGT findLegislatorCongress(String name);
	public LegislatorCongressGT findLegislatorCongressByBioguide(String name);
	User createUser(User user) throws Exception;
	
	void saveUser(UserProfile user);
	User updateUser(User user);

	void deleteUserById(String id);

	void deleteAllUsers();

	List<User> findAllUsers();

	boolean isUserExist(User user) throws Exception;
	boolean isUserExist(UserProfile user) throws Exception;
	public ProfileData createProfileData(User user);
	public ProfileData createProfileData(ProfileData profileData);
	public ProfileData createBioData(ProfileData profileData);
	public ProfileData saveProfileData(ProfileData profileData);
	public List<ProfileData> getProfileDatas(String entityId);
	public List<ProfileData> getProfileDataByProfileTemplateId(String entityId, String profileTemplateId);
	public ProfileData updateUserProfileData(String entityId, String profileTemplateId, String key, String value);

	
}