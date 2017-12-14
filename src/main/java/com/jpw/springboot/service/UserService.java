package com.jpw.springboot.service;


import java.util.List;

import com.jpw.springboot.model.User;
import com.jpw.springboot.model.UserProfile;

public interface UserService {
	
	User findById(String id);

	User findByName(String name);

	User createUser(User user);
	
	void saveUser(UserProfile user);

	User updateUser(User user);

	void deleteUserById(String id);

	void deleteAllUsers();

	List<User> findAllUsers();

	boolean isUserExist(User user);
	boolean isUserExist(UserProfile user);
	
}