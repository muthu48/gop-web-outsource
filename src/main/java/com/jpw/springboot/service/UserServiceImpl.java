package com.jpw.springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpw.springboot.model.User;
import com.jpw.springboot.model.UserProfile;
import com.jpw.springboot.repositories.UserRepository;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import org.springframework.data.mongodb.core.query.Query;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	public User findById(String id) {
		User user = userRepository.findOne(id);
		
		if(user != null)
			user.setUserPassword(null);
		
		return user;
	}

	public User findByName(String name) {
		return userRepository.findByUserName(name);
	}

	public User createUser(User user) {
		return userRepository.insert(user);
	}

	public User updateUser(User user) {
		return userRepository.save(user);
	}

	public void deleteUserById(String id) {
		userRepository.delete(id);
	}

	public void deleteAllUsers() {
		userRepository.deleteAll();
	}

	public List<User> findAllUsers() {
		return userRepository.findAll();
	}

	public boolean isUserExist(User user) {
		return findByName(user.getUserName()) != null;
	}

	@Override
	public void saveUser(UserProfile user) {
		userRepository.save(user);
		
	}

	@Override
	public boolean isUserExist(UserProfile user) {
		return findByName(user.getUserId()) != null;
	}

}
