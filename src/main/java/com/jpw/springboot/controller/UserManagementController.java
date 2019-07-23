package com.jpw.springboot.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.BSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpw.springboot.model.LegislatorOpenState;
import com.jpw.springboot.model.Post;
import com.jpw.springboot.model.ProfileData;
import com.jpw.springboot.model.ProfileTemplate;
import com.jpw.springboot.model.User;
//import com.jpw.springboot.model.UserProfile;
import com.jpw.springboot.service.UserService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
//import com.jpw.springboot.util.CustomErrorType;
import com.mongodb.gridfs.GridFSFile;

@RestController
@RequestMapping("/user")
public class UserManagementController {

	public static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);
	@Autowired
	GridFsOperations gridOperations;
	
	@Autowired
	UserService userService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public UserManagementController(BCryptPasswordEncoder bCryptPasswordEncoder){
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	@RequestMapping(value = "/getAllUsers", method = RequestMethod.GET)
	public ResponseEntity<List<User>> listAllUsers() {
		List<User> users = null;//userService.findAllUsers();
		if (users.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}
/*
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getUser(@PathVariable("id") String id) {
		logger.info("Fetching User with id {}", id);
		User user = userService.findById(id);
		if (user == null) {
			logger.error("User with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("User with id " + id + " not found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	*/
	@RequestMapping(value = "/{userName}", method = RequestMethod.GET)
	public ResponseEntity<?> getUserByUserName(@PathVariable("userName") String userName) {
		logger.info("Fetching User by userName " + userName);
		ResponseEntity response = null;
		User user = null;
		try{
			user = userService.getUser(userName, "publicUser");
			response = new ResponseEntity<User>(user, HttpStatus.OK);

		}catch(Exception e){
			response = new ResponseEntity("User with username " + userName + " not found", HttpStatus.NOT_FOUND);
		}
		
		return response;
	}

	@RequestMapping(value = "/legis/{userName}", method = RequestMethod.GET)
	public ResponseEntity<?> getLegisUser(@PathVariable("userName") String userName) {
		logger.info("Fetching Legislator by userName " + userName);

		ResponseEntity response = null;
		User user = null;
		try{
			user = userService.getUser(userName, "legislator");
			response = new ResponseEntity<User>(user, HttpStatus.OK);

		}catch(Exception e){
			response = new ResponseEntity("Legislator with username " + userName + " not found", HttpStatus.NOT_FOUND);
		}
		
		return response;

	}

	@RequestMapping(value = "/legisv1/{userName}", method = RequestMethod.GET)
	public ResponseEntity<?> getLegislator(@PathVariable("userName") String userName) {
		logger.info("Fetching Legislator by userName " + userName);

		ResponseEntity response = null;
		LegislatorOpenState user = null;
		try{
			user = userService.findLegislator(userName);
			response = new ResponseEntity<LegislatorOpenState>(user, HttpStatus.OK);

		}catch(Exception e){
			response = new ResponseEntity("Legislator with username " + userName + " not found", HttpStatus.NOT_FOUND);
		}
		
		return response;

	}

	@RequestMapping(value = "/legisv1/roles/{userName}", method = RequestMethod.GET)
	public ResponseEntity<?> getLegislatorRoles(@PathVariable("userName") String userName) {
		logger.info("Fetching Legislator by userName " + userName);

		ResponseEntity response = null;
		LegislatorOpenState user = null;
		ArrayList arrRoles = null;
		try{
			user = userService.findLegislator(userName);
			if(user.getOld_roles() != null){
				arrRoles = new ArrayList();
				//DBObject oldRolesObj = (DBObject)
				DBObject	oldRolesObj =	(DBObject)user.getOld_roles();
				Set<String> keySet = oldRolesObj.keySet();
				Iterator<String> iter = keySet.iterator();
			    while(iter.hasNext()){
			    	ArrayList arrobjRoles = (ArrayList)oldRolesObj.get(iter.next());
			    	arrRoles.addAll(arrobjRoles);
			    }				
			}
			
			response = new ResponseEntity<ArrayList>(arrRoles, HttpStatus.OK);

		}catch(Exception e){
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return response;

	}

	//for Registering User
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
		logger.info("Creating User : {}", user);

/*		if (userService.isUserExist(user)) {
			logger.error("Unable to create. A User with name {} already exist", user.getUserName());
			return new ResponseEntity<Object>(
					new CustomErrorType("Unable to create. A User with name " + user.getUserName() + " already exist."),
					HttpStatus.CONFLICT);
		}*/
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
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
		currentUser.setUsername(user.getUsername());
		currentUser.setPassword(user.getPassword());
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
	
	@RequestMapping(value = "/profileData", method = RequestMethod.POST)
	public ResponseEntity<?> createProfileData(@RequestBody ProfileData profileData,
			UriComponentsBuilder ucBuilder) {
		logger.info("Creating user ProfileData : {}", profileData);

		profileData = userService.createProfileData(profileData);

//		HttpHeaders headers = new HttpHeaders();
//		headers.setLocation(ucBuilder.path("/profile/template/{id}").buildAndExpand(profiletemplate.getId()).toUri());
		return new ResponseEntity<ProfileData>(profileData, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/profileData", method = RequestMethod.GET)
	public ResponseEntity<ProfileData> getUserProfileDataByTemplateId(
			@PathVariable("userName") String userName,
			@PathVariable("profileTemplateId") String profileTemplateId) {
		ProfileData profileData = null;
		List<ProfileData> profileDatas = userService.getProfileDataByProfileTemplateId(userName, profileTemplateId);
		if(profileDatas != null && profileDatas.size() > 0){
			profileData = profileDatas.get(0);
		}

		return new ResponseEntity<ProfileData>(profileData, HttpStatus.OK);
	}

	@RequestMapping(value = "/profileData", method = RequestMethod.PUT)
	public ResponseEntity<?> updateProfileData(@RequestBody ProfileData profileData,
			UriComponentsBuilder ucBuilder) {
		logger.info("Creating user ProfileData : {}", profileData);

		profileData = userService.saveProfileData(profileData);

		return new ResponseEntity<ProfileData>(profileData, HttpStatus.OK);
	}

	@RequestMapping(value = "/uploadUserSmProfileImage", method = RequestMethod.POST)
	public ResponseEntity<?> uploadUserSmProfileImage(@ModelAttribute FormDataWithFile formDataWithFile,
			UriComponentsBuilder ucBuilder) {
		
		ObjectMapper mapper = new ObjectMapper();
		User user = null;
		InputStream inputStream = null;

		try {
			MultipartFile file = formDataWithFile.getFile();
			String userData = formDataWithFile.getPost();
			
			user = mapper.readValue(userData, User.class);
			List<ProfileTemplate> profileTemplates = user.getProfileTemplates();
			String profileTemplateId = null;
			if(profileTemplates != null && profileTemplates.size() > 0){
				profileTemplateId = profileTemplates.get(0).getProfileTemplateId();
			}
			
			//post = postService.createPost(post);
			if (file != null) {
				String fileName = StringUtils.cleanPath(file.getOriginalFilename());
				inputStream = file.getInputStream();
				DBObject metaData = new BasicDBObject();
				//metaData.put("postId", post.getId());
				metaData.put("username", user.getUsername());
				metaData.put("imageType", "USERSMPROFILEIMAGE");
				//TODO
				//MAKE FILE UPLOAD AS REUSABLE, CHANGE THE FILE TYPE
				GridFSFile gridFsFile = gridOperations.store(inputStream, fileName, "image/png", metaData);
				String fileId = gridFsFile.getId().toString();
				ProfileData profileData = userService.updateUserProfileData(user.getUsername(), profileTemplateId, "profileSMImageId", fileId);
				if(user.getProfileDatas() != null)
					user.getProfileDatas().add(profileData);
				else{
					List<ProfileData> profileDatas = new ArrayList<ProfileData>();
					profileDatas.add(profileData);
					user.setProfileDatas(profileDatas);
				}
				//TODO
				//update file id with user template data, also if possible capture the current session user
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	//request to get the uploaded image file
	//refer postcontroller
}