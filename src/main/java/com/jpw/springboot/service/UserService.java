package com.jpw.springboot.service;


import java.util.List;

import com.jpw.springboot.model.LegislatorOpenState;
import com.jpw.springboot.model.ProfileData;
import com.jpw.springboot.model.User;
import com.jpw.springboot.model.UserProfile;

public interface UserService {
	
	User findById(String id);

	User getUser(String name, String userType) throws Exception;
	LegislatorOpenState findLegislator(String name);

	User createUser(User user);
	
	void saveUser(UserProfile user);
	User updateUser(User user);

	void deleteUserById(String id);

	void deleteAllUsers();

	List<User> findAllUsers();

	boolean isUserExist(User user);
	boolean isUserExist(UserProfile user);
	public ProfileData createProfileData(ProfileData profileData);
	public ProfileData saveProfileData(ProfileData profileData);
	public List<ProfileData> getProfileData(String entityId);
	
}