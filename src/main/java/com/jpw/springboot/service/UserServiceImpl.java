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

import com.jpw.springboot.model.LegislatorOpenState;
import com.jpw.springboot.model.User;
import com.jpw.springboot.model.UserProfile;
import com.jpw.springboot.repositories.LegislatorOpenStateRepository;
import com.jpw.springboot.repositories.UserRepository;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LegislatorOpenStateRepository legislatorOpenStateRepository;

	public User findById(String id) {
		User user = userRepository.findOne(id);
		
		if(user != null)
			user.setPassword(null);
		
		return user;
	}

	private User findByUserName(String name) {
		User user = userRepository.findByUsername(name);
		
		if(user != null)
			user.setPassword(null);
		
		return user;

	}
	
	public LegislatorOpenState findLegislator(String name) {
		LegislatorOpenState legislator = legislatorOpenStateRepository.findByLegId(name);
		return legislator;
	}
	
	public User getUser(String name, String userType) throws Exception{
		boolean createUserProfile = false;
		User user = findByUserName(name);
		if(user == null){
			if(!userType.equalsIgnoreCase("publicUser")){ //CREATE PROFILE FOR NON-PUBLIC USER AND KEEP IT INACTIVE
				createUserProfile = true;
				user = new User();
				user.setUsername(name);
				user.setStatus("INACTIVE");
			}else{
				throw new Exception("User not found - " + name);
			}
		}	
		
		if(userType.equalsIgnoreCase("legislator")){
			LegislatorOpenState legislator = findLegislator(name);		
			if(createUserProfile && legislator != null){
				//user.setLegislatorOpenState(legislator);
				user.setSourceId(legislator.getLegId());
			}
		}	
		
		/*
		 if user does not exist, then mark the profile as inactive.
		 Also shall create an user with inactive profile.
		 Transform LegislatorOpenState data into User data, during data import ?? 
		 */				
		if(createUserProfile){
			//@Async user creation
			user = createUser(user);
		}
		
		return user;

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
		return null;//userRepository.findAll();
	}

	public boolean isUserExist(User user) {
		return findByUserName(user.getUsername()) != null;
	}

	@Override
	public void saveUser(UserProfile user) {
		userRepository.save(user);
		
	}

	@Override
	public boolean isUserExist(UserProfile user) {
		return findByUserName(user.getUserId()) != null;
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
