package com.jpw.springboot.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.jpw.springboot.model.User;
//import com.jpw.springboot.model.UserProfile;
import com.jpw.springboot.service.UserService;
//import com.jpw.springboot.util.CustomErrorType;

@RestController
@RequestMapping("/user")
public class UserManagementController {

	public static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

	@Autowired
	UserService userService;

	@RequestMapping(value = "/getAllUsers", method = RequestMethod.GET)
	public ResponseEntity<List<User>> listAllUsers() {
		List<User> users = userService.findAllUsers();
		if (users.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getUser(@PathVariable("id") String id) {
		logger.info("Fetching User with id {}", id);
		User user = userService.findById(id);
/*		if (user == null) {
			logger.error("User with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("User with id " + id + " not found"), HttpStatus.NOT_FOUND);
		}
*/		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
		logger.info("Creating User : {}", user);

/*		if (userService.isUserExist(user)) {
			logger.error("Unable to create. A User with name {} already exist", user.getUserName());
			return new ResponseEntity<Object>(
					new CustomErrorType("Unable to create. A User with name " + user.getUserName() + " already exist."),
					HttpStatus.CONFLICT);
		}*/
		user = userService.createUser(user);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/api/user/{id}").buildAndExpand(user.getUserId()).toUri());
		return new ResponseEntity<User>(user, HttpStatus.CREATED);
	}

/*	@RequestMapping(value = "/addUserProfile", method = RequestMethod.POST)
	public ResponseEntity<?> addUserProfile(@RequestBody UserProfile userProfile, UriComponentsBuilder ucBuilder) {
		logger.info("Creating User : {}", userProfile);

		if (userService.isUserExist(userProfile)) {
			logger.error("Unable to create. A User with name {} already exist", userProfile.getUserId());
			return new ResponseEntity<Object>(
					new CustomErrorType(
							"Unable to create. A User with name " + userProfile.getUserId() + " already exist."),
					HttpStatus.CONFLICT);
		}
		
		userService.saveUser(userProfile);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/api/user/{id}").buildAndExpand(userProfile.getUserId()).toUri());
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}
*/
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@PathVariable("id") String id, @RequestBody User user) {
		logger.info("Updating User with id {}", id);

		User currentUser = userService.findById(id);

/*		if (currentUser == null) {
			logger.error("Unable to update. User with id {} not found.", id);
			return new ResponseEntity<Object>(
					new CustomErrorType("Unable to upate. User with id " + id + " not found."), HttpStatus.NOT_FOUND);
		}
*/
		currentUser.setUserName(user.getUserName());
		currentUser.setUserPassword(user.getUserPassword());
		currentUser.setUserType(user.getUserType());
		currentUser.setSourceSystem(user.getSourceSystem());
		currentUser.setStatus(user.getStatus());
		currentUser.setEmailId(user.getEmailId());
		
		userService.updateUser(currentUser);
		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {
		logger.info("Fetching & Deleting User with id {}", id);

		User user = userService.findById(id);
/*		if (user == null) {
			logger.error("Unable to delete. User with id {} not found.", id);
			return new ResponseEntity<Object>(
					new CustomErrorType("Unable to delete. User with id " + id + " not found."), HttpStatus.NOT_FOUND);
		}*/
		userService.deleteUserById(id);
		return new ResponseEntity<User>(HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteAllUsers", method = RequestMethod.DELETE)
	public ResponseEntity<User> deleteAllUsers() {
		logger.info("Deleting All Users");

		userService.deleteAllUsers();
		return new ResponseEntity<User>(HttpStatus.OK);
	}

}