package com.jpw.springboot.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bson.BSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpw.springboot.model.LegislatorCongressGT;
import com.jpw.springboot.model.LegislatorOpenState;
import com.jpw.springboot.model.Post;
import com.jpw.springboot.model.ProfileData;
import com.jpw.springboot.model.ProfileTemplate;
import com.jpw.springboot.model.User;
import com.jpw.springboot.service.LegislatorDataProcessingService;
//import com.jpw.springboot.model.UserProfile;
import com.jpw.springboot.service.UserService;
import com.jpw.springboot.util.SystemConstants;
import com.mongodb.BasicDBList;
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
	
	@Autowired
	private LegislatorDataProcessingService legislatorDataProcessingService;
	
	public UserManagementController(BCryptPasswordEncoder bCryptPasswordEncoder){
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	@RequestMapping(value = "/getAllUsers", method = RequestMethod.GET)
	public ResponseEntity<List<User>> listAllUsers() {
		List<User> users = userService.findAllUsers();
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
	@RequestMapping(value = "/{userName}/", method = RequestMethod.GET)
	public ResponseEntity<?> getUserByUserName(@PathVariable("userName") String userName) {
		logger.info("Fetching User by userName " + userName);
		ResponseEntity response = null;
		User user = null;
		try{
			user = userService.getUser(userName);
			response = new ResponseEntity<User>(user, HttpStatus.OK);

		}catch(Exception e){
			response = new ResponseEntity("User with username " + userName + " not found", HttpStatus.NOT_FOUND);
		}
		
		return response;
	}

	@RequestMapping(value = "/legis/{userName}", method = RequestMethod.GET)
	public ResponseEntity<?> getLegislatorUser(@PathVariable("userName") String userName) {
		logger.info("Fetching Legislator by userName " + userName);

		ResponseEntity response = null;
		User user = null;
		try{
			user = userService.getUser(userName);
			response = new ResponseEntity<User>(user, HttpStatus.OK);

		}catch(Exception e){
			response = new ResponseEntity("Legislator with username " + userName + " not found", HttpStatus.NOT_FOUND);
		}
		
		return response;

	}

	@RequestMapping(value = "/legis/congress/{userName}", method = RequestMethod.GET)
	public ResponseEntity<?> getLegislatorCongressUser(@PathVariable("userName") String userName) {
		logger.info("Fetching Congress Legislator by userName " + userName);

		ResponseEntity response = null;
		User user = null;
		try{
			user = userService.getUser(userName);
			response = new ResponseEntity<User>(user, HttpStatus.OK);

		}catch(Exception e){
			response = new ResponseEntity("Congress Legislator with username " + userName + " not found", HttpStatus.NOT_FOUND);
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

	@RequestMapping(value = "/legisv1/congress/{userName}", method = RequestMethod.GET)
	public ResponseEntity<?> getLegislatorCongress(@PathVariable("userName") String userName) {
		logger.info("Fetching Legislator by userName " + userName);

		ResponseEntity response = null;
		LegislatorCongressGT user = null;
		try{
			user = userService.findLegislatorCongress(userName);
			response = new ResponseEntity<LegislatorCongressGT>(user, HttpStatus.OK);

		}catch(Exception e){
			response = new ResponseEntity("Retrieving Legislator with username " + userName + ", " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return response;

	}

	//USED
	//Get Biodata for Legislator
	@RequestMapping(value = "/legis/biodata/{userName}/", method = RequestMethod.GET)
	public ResponseEntity<ProfileData> getLegislatorBiodata(@RequestHeader("userType") String userType, @PathVariable("userName") String userName) {
		logger.info("Fetching Legislator Biodata by userName " + userName);

		ResponseEntity response = null;
		try{
			
			ProfileData profileData = null;
			String profileName = (userType.equalsIgnoreCase("internal") ? "upDefault" : "upCongressLegislatorExternal");
			List<ProfileData> profileDatas = userService.getProfileDataByProfileTemplateId(userName, profileName);
			if(profileDatas != null && profileDatas.size() > 0){
				profileData = profileDatas.get(0);
				response = new ResponseEntity<ProfileData>(profileData, HttpStatus.OK);

			}else{
				response = new ResponseEntity("No Data found", HttpStatus.NOT_FOUND);

			}

		}catch(Exception e){
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return response;

	}

	//Get Roles for Openstate
	@RequestMapping(value = "/legisv1/roles/{userName}", method = RequestMethod.GET)
	public ResponseEntity<?> getLegislatorRolesOS(@PathVariable("userName") String userName) {
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
	
	//NOT USED
	//Get Roles for Congress - in terms of ProfileData
	@RequestMapping(value = "/legisv1/congress/rolesv1/{userName}", method = RequestMethod.GET)
	public ResponseEntity<ProfileData> getLegislatorRolesCongress(@PathVariable("userName") String userName) {
		logger.info("Fetching Congress Legislator by userName " + userName);

		ResponseEntity response = null;
		LegislatorCongressGT user = null;
		ArrayList arrRoles = null;
		try{
			
			ProfileData profileData = null;
			List<ProfileData> profileDatas = userService.getProfileDataByProfileTemplateId(userName, "upRole");
			if(profileDatas != null && profileDatas.size() > 0){
				profileData = profileDatas.get(0);//PICKING ONE TO USE IT IN THE RESPONSE
				BasicDBList profileDataList = new BasicDBList();
				for(ProfileData profile : profileDatas){
					profileDataList.add(profile.getData());
				}
				profileData.setDataList(profileDataList);
				response = new ResponseEntity<ProfileData>(profileData, HttpStatus.OK);

			}else{
				response = new ResponseEntity("No Data found", HttpStatus.NOT_FOUND);

			}

		}catch(Exception e){
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return response;

	}
	
	//USED
	//Get Roles
	@RequestMapping(value = "/legis/roles/{userName}", method = RequestMethod.GET)
	public ResponseEntity<ArrayList> getLegislatorRoles(@PathVariable("userName") String userName) {
		logger.info("Fetching Roles for userName " + userName);

		ResponseEntity response = null;
		//ArrayList arrRoles = null;
		try{
			
			//ProfileData profileData = null;
			ArrayList<ProfileData> profileDatas = (ArrayList)userService.getProfileDataByProfileTemplateId(userName, "upRole");
			if(profileDatas != null && profileDatas.size() > 0){
				//Collections.sort(profileDatas); //sort by desc order based on start date
				/*arrRoles = new ArrayList<>();
				for(ProfileData profile : profileDatas){
					arrRoles.add(profile.getData());
				}*/
				response = new ResponseEntity<ArrayList>(profileDatas, HttpStatus.OK);

			}else{
				response = new ResponseEntity("No Data found", HttpStatus.NOT_FOUND);

			}

		}catch(Exception e){
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return response;

	}

	//Get Roles for Openstate legislators
	@RequestMapping(value = "/legisv1/offices/{userName}", method = RequestMethod.GET)
	public ResponseEntity<?> getLegislatorOfficesOS(@PathVariable("userName") String userName) {
		logger.info("Fetching Legislator Offices by userName " + userName);

		ResponseEntity response = null;
		LegislatorOpenState user = null;
		ArrayList arrOffices = null;
		try{
			user = userService.findLegislator(userName);
			if(user.getOffices() != null && user.getOffices().size() > 0){
				arrOffices = user.getOffices();
				response = new ResponseEntity<ArrayList>(arrOffices, HttpStatus.OK);

			}else{
			
				response = new ResponseEntity<ArrayList>(arrOffices, HttpStatus.NOT_FOUND);
			}

		}catch(Exception e){
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return response;

	}
	
	//Get Offices for legislator
	@RequestMapping(value = "/legis/offices/{userName}", method = RequestMethod.GET)
	public ResponseEntity<?> getLegislatorOffices(@PathVariable("userName") String userName) {
		logger.info("Fetching Offices for userName " + userName);

		ResponseEntity response = null;
		//ArrayList arrOffices = null;
		try{
			
			ProfileData profileData = null;
			ArrayList<ProfileData> profileDatas = (ArrayList)userService.getProfileDataByProfileTemplateId(userName, "upOffices");
			if(profileDatas != null && profileDatas.size() > 0){
				//Collections.sort(profileDatas); //sort by desc order based on start date
				/*arrOffices = new ArrayList<>();
				for(ProfileData profile : profileDatas){
					arrOffices.add(profile.getData());
				}*/
				response = new ResponseEntity<ArrayList>(profileDatas, HttpStatus.OK);

			}else{
				response = new ResponseEntity("No Data found", HttpStatus.NOT_FOUND);

			}

		}catch(Exception e){
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return response;

	}

	/* load data from data/congress/congress-legislators-govtrack/legislators-current.json
	 * insert record into legislatorcongress entity
	 * insert 3 records into ProfileData entity
	 * with ProfileTemplateId-upCongressLegislatorExternal, upRole, upOffices / EntityType-LEGISLATORCONGRESS, id.bioguide as user/username
	 * */
	@RequestMapping(value = "/legis/loadCongressLegislatorsToDb", method = RequestMethod.POST)	
	public ResponseEntity loadCongressLegislatorsToDb() {
		String filePath = "C:\\Users\\OPSKY\\Documents\\Project\\Data\\congress\\congress-legislators-govtrack\\legislators-current.json";
		logger.info("loadCongressLegislatorsToDb " + filePath);

		ResponseEntity response = null;
		File file;
		try {
			//file = new ClassPathResource("data/congress/congress-legislators-govtrack/legislators-current.json").getFile();
			file = new File(filePath);
		    Instant start = Instant.now();
		    
			legislatorDataProcessingService.loadCongressLegislatorsToDb(file);	
		    
			Instant finish = Instant.now();
		    long timeElapsed = Duration.between(start, finish).toMillis();  //in millis
	        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeElapsed);
	        System.out.format("%d Milliseconds = %d minutes\n", timeElapsed, minutes );
			
			response = new ResponseEntity("data loaded successfully", HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;

	}
	
	/* load data from data/Openstates/pa/legislators
	 * insert record into legislatoropenstate entity
	 * insert 3 records into ProfileData entity
	 * with ProfileTemplateId-upCongressLegislatorExternal, upRole, upOffices / EntityType-LEGISLATOROPENSTATE, leg_id as user/username
	 * */
	@RequestMapping(value = "/legis/loadStateLegislatorsToDb", method = RequestMethod.POST)	
	public ResponseEntity loadStateLegislatorsToDb() {
		String filePath = "C:\\Users\\OPSKY\\Documents\\Project\\Data\\Openstates";
		//logger.info("loadStateLegislatorsToDb data/Openstates/pa/legislators");
		logger.info("loadStateLegislatorsToDb " + filePath);
		ResponseEntity response = null;
		try {
			//legislatorDataProcessingService.loadStateLegislatorsToDb("data/Openstates/pa/legislators");
			legislatorDataProcessingService.loadStateLegislatorsToDb(filePath);
			response = new ResponseEntity("data loaded successfully", HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;

	}
	
	//for Registering User
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
		logger.info("Creating User : {}", user);
		if(user.getUserType() == null){
			user.setUserType(SystemConstants.PUBLIC_USERTYPE);
		}

/*		if (userService.isUserExist(user)) {
			logger.error("Unable to create. A User with name {} already exist", user.getUserName());
			return new ResponseEntity<Object>(
					new CustomErrorType("Unable to create. A User with name " + user.getUserName() + " already exist."),
					HttpStatus.CONFLICT);
		}*/
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user = userService.createUser(user);
		
		//TODO
		//should differentiate and the profile based on the usertype
		if(user.getUserType().equalsIgnoreCase(SystemConstants.PUBLIC_USERTYPE)){
			userService.createProfileData(user, SystemConstants.PUBLIC_USERTYPE, SystemConstants.PROFILE_TEMPLATE_BIODATA);
		}else{
			userService.createProfileData(user, SystemConstants.STATELEGIS_USERTYPE, SystemConstants.PROFILE_TEMPLATE_BIODATA_EXTERNAL);
		}

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
	
	@RequestMapping(value = "/profileData/{userName}/{profileTemplateId}", method = RequestMethod.GET)
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
	
	@RequestMapping(value = "/profileData/{userName}/", method = RequestMethod.GET)
	public ResponseEntity<ProfileData> getUserProfileDataByUsername(
			@PathVariable("userName") String userName) {
		List<ProfileData> profileDatas = null;

		ResponseEntity response = null;
		try {
			profileDatas = userService.getProfileDatas(userName);
			response = new ResponseEntity(profileDatas, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		
		return response;
	}
	
	@RequestMapping(value = "/profileData/update", method = RequestMethod.POST)
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