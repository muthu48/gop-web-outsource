package com.jpw.springboot.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpw.springboot.model.User;
import com.jpw.springboot.model.UserProfile;
import com.jpw.springboot.repositories.UserRepository;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	public User findById(String id) {
		User user = userRepository.findOne(id);
		
		if(user != null)
			user.setPassword(null);
		
		return user;
	}

	public User findByName(String name) {
		return userRepository.findByUsername(name);
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
		return findByName(user.getUsername()) != null;
	}

	@Override
	public void saveUser(UserProfile user) {
		userRepository.save(user);
		
	}

	@Override
	public boolean isUserExist(UserProfile user) {
		return findByName(user.getUserId()) != null;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user= userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.emptyList());
   
	}

}
