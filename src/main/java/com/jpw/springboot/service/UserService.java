package com.jpw.springboot.service;


import java.util.List;

import org.springframework.http.ResponseEntity;

import com.jpw.springboot.model.LegislatorCongressGT;
import com.jpw.springboot.model.LegislatorOpenState;
import com.jpw.springboot.model.ProfileData;
import com.jpw.springboot.model.User;
import com.jpw.springboot.model.UserProfile;

public interface UserService {
	
	User findById(String id);
	public User findByUserName(String name, boolean pwdRequired) throws Exception;
	User getUser(String name, boolean pwdRequired) throws Exception;
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
	public ProfileData createProfileData(ProfileData profileData);
	public ProfileData saveProfileData(ProfileData profileData) throws Exception;
	public List<ProfileData> getProfileDatas(String entityId);
	public List<ProfileData> getProfileDataByProfileTemplateId(String entityId, String profileTemplateId);
	public ProfileData updateUserProfileData(String entityId, String profileTemplateId, String key, String value) throws Exception;
	public ResponseEntity tokenVerify(String token, String provider) throws Exception;
	public void registerUserExternal(User user);
	
}