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

import javax.json.JsonValue;
import javax.servlet.http.HttpServletResponse;

import org.bson.BSONObject;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.util.Version;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.gson.Gson;
import com.jpw.springboot.TokenAuthenticationService;
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
import com.mongodb.util.JSON;
import com.restfb.json.Json;

@RestController
@RequestMapping("/user")
public class UserManagementController {

	public static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);
	@Autowired
	GridFsOperations gridOperations;
	
	@Autowired
	GridFsTemplate gridFsTemplate;
		  
	@Autowired
	UserService userService;
	
	@Autowired
	BuildProperties buildProperties;
	
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private LegislatorDataProcessingService legislatorDataProcessingService;
	
	public UserManagementController(BCryptPasswordEncoder bCryptPasswordEncoder){
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	@RequestMapping(value = "/buildInfo", method = RequestMethod.GET)
	public String ping() {
		String version = buildProperties.getVersion();

		return "Service is available, build version " + version;
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
	public ResponseEntity<?> getUserByUserName(@PathVariable("userName") String userName,
			@RequestParam (value = "requestorId", required = false) String requestorId) {
		logger.info("Fetching User by userName " + userName);
		ResponseEntity response = null;
		User user = null;
		try{
			user = userService.getUser(userName, false);
			
			if(requestorId != null){
				if(requestorId.equalsIgnoreCase(userName)){
					user.setSelfProfile(true);
					user.setShowSettings(true);

				}
				
				if(user.getMembers() != null && user.getMembers().contains(requestorId)){
					user.setShowSettings(true);
					user.setProfileManaged(true);
				}
				
			}
			
			response = new ResponseEntity<User>(user, HttpStatus.OK);

		}catch(Exception e){
			response = new ResponseEntity("User with username " + userName + " have issue " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return response;
	}

	@RequestMapping(value = "/legis/{userName}", method = RequestMethod.GET)
	public ResponseEntity<?> getLegislatorUser(@PathVariable("userName") String userName) {
		logger.info("Fetching Legislator by userName " + userName);

		ResponseEntity response = null;
		User user = null;
		try{
			user = userService.getUser(userName, false);
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
			user = userService.getUser(userName, false);
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
	//Get Biodata
	@RequestMapping(value = "/biodata/{userName}/", method = RequestMethod.GET)
	public ResponseEntity<ProfileData> getLegislatorBiodata(@PathVariable("userName") String userName) {
		logger.info("Fetching Legislator Biodata by userName " + userName);

		ResponseEntity response = null;
		ProfileData profileData = null;
		String profileName = "upDefault";

		try{
			//User user = userService.getUser(userName);
			/*
			 * if(user != null &&
			 * user.getUserType().equalsIgnoreCase(SystemConstants.USERTYPE_LEGIS)) {
			 * profileName = SystemConstants.PROFILE_TEMPLATE_BIODATA_EXTERNAL; }
			 */
			//String profileName = (user.getUserType().equalsIgnoreCase(SystemConstants.USERTYPE_LEGIS) ? "upCongressLegislatorExternal" : "upDefault");
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
				/*BasicDBList profileDataList = new BasicDBList();
				for(ProfileData profile : profileDatas){
					profileDataList.add(profile.getData());
				}
				profileData.setDataList(profileDataList);*/
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

	/* load data from data/Openstates/pa/legislators
	 * insert record into legislatoropenstate entity
	 * insert 3 records into ProfileData entity
	 * with ProfileTemplateId-upCongressLegislatorExternal, upRole, upOffices / EntityType-LEGISLATOROPENSTATE, leg_id as user/username
	 * */
	@RequestMapping(value = "/legis/loadStateLegislatorsToDb", method = RequestMethod.POST)	
	public ResponseEntity loadStateLegislatorsToDb() {
		//String filePath = "C:\\Users\\SKYDOTS\\Dropbox\\Project\\Data\\Openstates";
		//String filePath = "C:\\Users\\SKYDOTS\\Project-Workspace\\people\\testdata";
		String filePath = "C:\\Users\\SKYDOTS\\Project-Workspace\\people\\data";
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

	/* load data from data/congress/congress-legislators-govtrack/legislators-current.json
	 * insert record into legislatorcongress entity
	 * insert 3 records into ProfileData entity
	 * with ProfileTemplateId-upCongressLegislatorExternal, upRole, upOffices / EntityType-LEGISLATORCONGRESS, id.bioguide as user/username
	 * */
	@RequestMapping(value = "/legis/loadCongressMembersToDb", method = RequestMethod.POST)	
	public ResponseEntity loadCongressMembersToDb(@RequestParam (value = "loadHistoricalLegislators", required = false) String loadHistoricalLegislators,
			@RequestParam (value = "loadExecutives", required = false) String loadExecutives) {
		String filePath = null;//"C:\\Users\\SKYDOTS\\Dropbox\\Project\\Data\\Congress\\congress-legislators-govtrack\\asof-02-09-2021\\legislators-current.json";
		logger.info("loadCongressMembersToDb " + filePath);

		ResponseEntity response = null;
		File file;
		String userCategory = SystemConstants.USERCATEGRORY_LEGISLATURE;//SystemConstants.USERTYPE_CONGRESSLEGIS;
		try {
			
			if(loadExecutives != null && loadExecutives.equalsIgnoreCase("Y")) {
				filePath = "C:\\Users\\SKYDOTS\\Project-Workspace\\congress-legislators\\executive.json";
				userCategory = SystemConstants.USERTYPE_EXECUTIVE;
			}else if(loadHistoricalLegislators != null && loadHistoricalLegislators.equalsIgnoreCase("Y")) {
				filePath = "C:\\Users\\SKYDOTS\\Project-Workspace\\congress-legislators\\legislators-historical.json";				
			}else {
				filePath = "C:\\Users\\SKYDOTS\\Project-Workspace\\congress-legislators\\legislators-current.json";
			}

			file = new File(filePath);
		    Instant start = Instant.now();
		    
			legislatorDataProcessingService.loadCongressMembersToDb(file, userCategory);	
		    
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
	
	//OBSOLETE
	@RequestMapping(value = "/legis/loadCongressExecutivesToDb", method = RequestMethod.POST)	
	public ResponseEntity loadCongressExecutivesToDb() {
		String filePath = "C:\\Users\\SKYDOTS\\Dropbox\\Project\\Data\\Congress\\congress-legislators-govtrack\\asof-02-09-2021\\executive.json";
		logger.info("loadCongressExecutivesToDb " + filePath);

		ResponseEntity response = null;
		File file;
		try {
			//file = new ClassPathResource("data/congress/congress-legislators-govtrack/legislators-current.json").getFile();
			file = new File(filePath);
		    Instant start = Instant.now();
		    
			legislatorDataProcessingService.loadCongressLegislatorsToDb(file, SystemConstants.USERTYPE_EXECUTIVE);	
		    
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
	
	@RequestMapping(value = "/legis/loadCongressCommitteeFileToDb", method = RequestMethod.POST)	
	public ResponseEntity loadCongressCommitteeFileToDb() {
		String filePath = "C:\\Users\\SKYDOTS\\Project-Workspace\\congress-legislators\\committees-current.yaml";
		logger.info("loadCongressCommitteeFileToDb " + filePath);

		ResponseEntity response = null;
		File file;
		try {
			//file = new ClassPathResource("data/congress/congress-legislators-govtrack/legislators-current.json").getFile();
			file = new File(filePath);
		    Instant start = Instant.now();
		    
			legislatorDataProcessingService.processCongressCommitteeFile(file);	
		    
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
	
	@RequestMapping(value = "/legis/loadCongressCommitteeMembershipFileToDb", method = RequestMethod.POST)	
	public ResponseEntity loadCongressCommitteeMembershipFileToDb() {
		String filePath = "C:\\Users\\SKYDOTS\\Project-Workspace\\congress-legislators\\committee-membership-current.yaml";
		logger.info("loadCongressCommitteeMembershipFileToDb " + filePath);

		ResponseEntity response = null;
		File file;
		try {
			file = new File(filePath);
		    Instant start = Instant.now();
		    
			legislatorDataProcessingService.processCongressCommitteeMembershipFile(file);	
		    
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
	
	@RequestMapping(value = "/legis/loadCongressDistrictOfficeFileToDb", method = RequestMethod.POST)	
	public ResponseEntity loadCongressDistrictOfficeFileToDb() {
		String filePath = "C:\\Users\\SKYDOTS\\Project-Workspace\\congress-legislators\\legislators-district-offices.json";
		logger.info("loadCongressDistrictOfficeFileToDb " + filePath);

		ResponseEntity response = null;
		File file;
		try {
			file = new File(filePath);
		    Instant start = Instant.now();
		    
			legislatorDataProcessingService.processCongressDistrictOfficeFile(file);	
		    
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
	
	//for Registering User
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody User user) {
		logger.info("Creating User : {}", user);
		ResponseEntity response = null;
		try{
			if(user.getCategory() == null){
				user.setCategory(SystemConstants.USERTYPE_PUBLIC);
			}
	
			if(user.getStatus().equalsIgnoreCase(SystemConstants.ACTIVE)){// && (user.getUserType().equalsIgnoreCase(SystemConstants.USERTYPE_PUBLIC) || user.getUserType().equalsIgnoreCase(SystemConstants.USERTYPE_LEGIS))){
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			}
			
			user = userService.createUser(user);

			response = new ResponseEntity<User>(user, HttpStatus.CREATED);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in createUser  " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error - " + e.getMessage(), e);
		}
		
		return response;
	}

	//TokenVerify - Google/Facebook
	@RequestMapping(value = "/tokenVerify", method = RequestMethod.POST)
	public ResponseEntity<?> tokenVerify(@RequestHeader("X-ID-TOKEN") String token, @RequestHeader("PROVIDER") String provider) {
		logger.info("Verifying token: " + token + " ,provider: " + provider);
		ResponseEntity response = null;

		try{
			response = userService.tokenVerify(token, provider);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in verifying token", HttpStatus.UNAUTHORIZED);
			logger.error("Error in verifying token " + e.getMessage(), e);
		}
		
		return response;
	}
	
	//for Creating other Profile
	@RequestMapping(value = "/createprofile", method = RequestMethod.POST)
	public ResponseEntity<User> createUserProfile(@RequestBody User user) {
		logger.info("Creating User Profile: {}", user);
		ResponseEntity response = null;
		try{
		if(user.getUserType() == null){
			user.setUserType(SystemConstants.USERTYPE_PUBLIC);
		}

		if(user.getStatus().equalsIgnoreCase(SystemConstants.ACTIVE) && (user.getUserType().equalsIgnoreCase(SystemConstants.USERTYPE_PUBLIC) || user.getUserType().equalsIgnoreCase(SystemConstants.USERTYPE_LEGIS))){
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		}
		
		user = userService.createUser(user);

		response = new ResponseEntity<User>(user, HttpStatus.CREATED);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in createUserProfile  for entityId " + user.getUsername(), HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in createUserProfile  for entityId " + user.getUsername(), e);
		}
		
		return response;
	}
	
	//OBSOLETE
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
		//currentUser.setEmailId(user.getEmailId());
		
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

		try {
			profileData = userService.saveProfileData(profileData);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);		
		}

		return new ResponseEntity<ProfileData>(profileData, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/updateSettings", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> updateSettings(@RequestBody User user) {
		logger.info("Updating user settings : ", user);
		
		User sysUser;
		ResponseEntity response = null;

		try {
			sysUser = userService.getUser(user.getUsername(), true);
			
			BasicDBObject jObj = user.getSettings();
			
			if(sysUser.getSettings() == null){
				sysUser.setSettings(new BasicDBObject());
			}
			sysUser.getSettings().put("accessRestriction", jObj.getBoolean("accessRestriction"));
		
			if(!StringUtils.isEmpty(user.getModifiedBy())){
				sysUser.setModifiedBy(user.getModifiedBy());
			}
			
			sysUser = userService.updateUser(sysUser);
			response = new ResponseEntity(sysUser.getSettings(), HttpStatus.OK);

			
		} catch (Exception e) {
			logger.error(e.getMessage());
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}


		return response;
	}
	
	@RequestMapping(value = "/getSettings/{entityId}/", method = RequestMethod.GET)
	public ResponseEntity<String> getSettings(@PathVariable("entityId") String entityId) {
		User sysUser;
		ResponseEntity response = null;
		String settings = null;
		
		try {
			logger.info("getSettings : " + entityId);

			sysUser = userService.getUser(entityId, false);		
			if(sysUser.getSettings() != null){
				settings = sysUser.getSettings().toJson();
			}

			response = new ResponseEntity(settings, HttpStatus.OK);

			
		} catch (Exception e) {
			logger.error(e.getMessage());
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}
	
	@RequestMapping(value = "/addCircleUser", method = RequestMethod.POST)
	public ResponseEntity<ArrayList<BasicDBObject>> addCircleUser(@RequestBody String jsonStr) {
		
		User sysUser, circleUser;
		ResponseEntity response = null;
		JSONObject jObj = null;
		ArrayList<String> usernameList = new ArrayList<String>();

		try {
			jObj = new JSONObject(jsonStr);
			logger.info("addCircleUser : " + jObj.getString("circlememberUsername") + " to " + jObj.getString("username"));

			sysUser = userService.getUser(jObj.getString("username"), true);
			circleUser = userService.getUser(jObj.getString("circlememberUsername"), false);
			/*
			if(sysUser.getCircleUsers() == null){
				ArrayList<String> circleUsers = new ArrayList<String>(); 
				sysUser.setCircleUsers(circleUsers);
			}
			sysUser.getCircleUsers().add(jObj.getString("circlememberUsername"));
			*/
			/*
			[
			    {"LEGISLATOR":["PAL000515", "2013104362"]}
			]
			*/
			if(sysUser.getCircleUsersInfo() == null){
				ArrayList<BasicDBObject> circleUsersInfo = new ArrayList<BasicDBObject>(); 

				BasicDBObject circle = new BasicDBObject();
				usernameList.add(jObj.getString("circlememberUsername"));
				circle.append(circleUser.getUserType(), usernameList);
				
				circleUsersInfo.add(circle);
				sysUser.setCircleUsersInfo(circleUsersInfo);

			}else{
				boolean categoryExist = false;
				//for(BasicDBObject circle:sysUser.getCircleUsersInfo()){
				for(int i=0; i<sysUser.getCircleUsersInfo().size(); i++) {
						Gson gson = new Gson();
						String json = gson.toJson(sysUser.getCircleUsersInfo().get(i));
						
						BasicDBObject circle = (BasicDBObject)JSON.parse(json);	
					if(circle.containsKey(circleUser.getUserType())){
						categoryExist = true;
						usernameList = (ArrayList<String>)circle.get(circleUser.getUserType());
						if(!usernameList.contains(jObj.getString("circlememberUsername"))){
							usernameList.add(jObj.getString("circlememberUsername"));
							sysUser.getCircleUsersInfo().remove(i);
							sysUser.getCircleUsersInfo().add(circle);
						}else{
							throw new Exception("User already in Circle");
						}
						
						break;
					}
				}
				
				if(!categoryExist){//adding new category
					BasicDBObject circle = new BasicDBObject();
					usernameList.add(jObj.getString("circlememberUsername"));
					circle.append(circleUser.getUserType(), usernameList);
					
					sysUser.getCircleUsersInfo().add(circle);
				}
			}
			///
			sysUser.setModifiedBy(jObj.getString("modifiedBy"));

			sysUser = userService.updateUser(sysUser);
			response = new ResponseEntity(sysUser.getCircleUsersInfo(), HttpStatus.OK);

			
		} catch (Exception e) {
			logger.error(e.getMessage());
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}


		return response;
	}
	public Document getDocument(DBObject doc)
	{
	   if(doc == null) return null;
	   return new Document(doc.toMap());
	}
	@RequestMapping(value = "/removeCircleUser", method = RequestMethod.POST)
	public ResponseEntity<ArrayList<BasicDBObject>> removecircleuser(@RequestBody String jsonStr) {
		
		User sysUser, circleUser;
		ResponseEntity response = null;
		JSONObject jObj = null;
		ArrayList<String> usernameList = new ArrayList<String>();

		try {
			jObj = new JSONObject(jsonStr);
			logger.info("removeCircleUser : " + jObj.getString("circlememberUsername") + " from " + jObj.getString("username"));

			sysUser = userService.getUser(jObj.getString("username"), true);
			circleUser = userService.getUser(jObj.getString("circlememberUsername"), false);

/*			if(sysUser.getCircleUsers() != null && sysUser.getCircleUsers().size() >0){
				sysUser.getCircleUsers().remove(jObj.getString("circlememberUsername"));
			}else{
				throw new Exception("Nothing to remove");
			}*/
			if(sysUser.getCircleUsersInfo() != null){
				boolean removed = false;

				//for(BasicDBObject circle:sysUser.getCircleUsersInfo()){
				for(int i=0; i<sysUser.getCircleUsersInfo().size(); i++) {
					Gson gson = new Gson();
					String json = gson.toJson(sysUser.getCircleUsersInfo().get(i));
					
					BasicDBObject circle = (BasicDBObject)JSON.parse(json);	
					if(circle.containsKey(circleUser.getUserType())){
						usernameList = (ArrayList<String>)circle.get(circleUser.getUserType());
						usernameList.remove(jObj.getString("circlememberUsername"));
						
						sysUser.getCircleUsersInfo().remove(i);
						if(usernameList.size() > 0) {//add back the circle category only if any user exist in it
							sysUser.getCircleUsersInfo().add(circle);
						}
						removed = true;
						break;
					}
				}

				if(!removed){
					throw new Exception("User not removed from Circle as not exist.");
				}
			}else{
				throw new Exception("Nothing to remove");
			}			
			
			
			sysUser.setModifiedBy(jObj.getString("modifiedBy"));

			sysUser = userService.updateUser(sysUser);
			response = new ResponseEntity(sysUser.getCircleUsersInfo(), HttpStatus.OK);

			
		} catch (Exception e) {
			logger.error(e.getMessage());
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}


		return response;
	}
	
	@RequestMapping(value = "/getCircleUsers/{entityId}/", method = RequestMethod.GET)
	//public ResponseEntity<ArrayList<String>> getCircleUsers(@PathVariable("entityId") String entityId) {
	public ResponseEntity<ArrayList<BasicDBObject>> getCircleUsers(@PathVariable("entityId") String entityId) {

		
		User sysUser;
		ResponseEntity response = null;
		ArrayList<BasicDBObject> circleUsers = null;
		
		try {
			logger.info("getCircleUsers : " + entityId);

			sysUser = userService.getUser(entityId, false);		
			circleUsers = sysUser.getCircleUsersInfo();

			response = new ResponseEntity(circleUsers, HttpStatus.OK);

			
		} catch (Exception e) {
			logger.error(e.getMessage());
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}
	
	@RequestMapping(value = "/isInCircle/{profileId}/{entityId}/", method = RequestMethod.GET)
	public ResponseEntity<Boolean> isInCircle(@PathVariable("profileId") String profileId, @PathVariable("entityId") String entityId) {
		User sysUser;
		ResponseEntity response = null;
		boolean inCircle = false;

		try {
			logger.info("isInCircle : " + profileId + " , " + entityId);

			sysUser = userService.getUser(entityId, false);		
			
			ArrayList<BasicDBObject> circleUsers = sysUser.getCircleUsersInfo();
			
			if(circleUsers != null){
				for(int i=0; i<circleUsers.size(); i++) {
					Gson gson = new Gson();
					String json = gson.toJson(circleUsers.get(i));
					
					BasicDBObject circle = (BasicDBObject)JSON.parse(json);
					//JsonValue circle = (JsonValue) Json.parse(circleUsers.get(i).toString());
				//for(BasicDBObject circle:circleUsers){
					//Document circle = new Document(circleUsers.get(i).toMap());
					Set<String> keys = circle.keySet();
					for(String key:keys){
						ArrayList<String> usernameList = (ArrayList<String>)circle.get(key);
						inCircle = usernameList.contains(profileId);
						if(inCircle){
							break;
						}
					}
					if(inCircle){
						break;
					}
	
				}
			}
			response = new ResponseEntity(inCircle, HttpStatus.OK);

			
		} catch (Exception e) {
			logger.error(e.getMessage());
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}


		return response;
	}
	
	@RequestMapping(value = "/addMember", method = RequestMethod.POST)
	public ResponseEntity<ArrayList<String>> addMember(@RequestBody String jsonStr) {
		
		User sysUser;
		ResponseEntity response = null;
		JSONObject jObj = null;

		try {
			jObj = new JSONObject(jsonStr);
			logger.info("addMember : " + jObj.getString("memberUsername") + " to " + jObj.getString("username"));

			sysUser = userService.getUser(jObj.getString("username"), true);
			if(sysUser.getMembers() == null){
				ArrayList<String> members = new ArrayList<String>(); 
				members.add(jObj.getString("memberUsername"));
				sysUser.setMembers(members);
			}else{
				if(sysUser.getMembers().contains(jObj.getString("memberUsername"))){
					throw new Exception(jObj.getString("memberUsername") + " already a Member");
				}
				
				sysUser.getMembers().add(jObj.getString("memberUsername"));
			}
			sysUser.setModifiedBy(jObj.getString("modifiedBy"));

			sysUser = userService.updateUser(sysUser);
			response = new ResponseEntity(sysUser.getMembers(), HttpStatus.OK);

			
		} catch (Exception e) {
			logger.error(e.getMessage());
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}


		return response;
	}
	
	@RequestMapping(value = "/removeMember", method = RequestMethod.POST)
	public ResponseEntity<ArrayList<String>> removeMember(@RequestBody String jsonStr) {
		
		User sysUser;
		ResponseEntity response = null;
		JSONObject jObj = null;

		try {
			jObj = new JSONObject(jsonStr);
			logger.info("removeMember : " + jObj.getString("memberUsername") + " from " + jObj.getString("username"));

			sysUser = userService.getUser(jObj.getString("username"), true);
			if(sysUser.getMembers() != null && sysUser.getMembers().size() > 0){
				sysUser.getMembers().remove(jObj.getString("memberUsername"));
			}else{
				throw new Exception("Nothing to remove");
			}
			sysUser.setModifiedBy(jObj.getString("modifiedBy"));

			sysUser = userService.updateUser(sysUser);
			response = new ResponseEntity(sysUser.getMembers(), HttpStatus.OK);

			
		} catch (Exception e) {
			logger.error(e.getMessage());
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}


		return response;
	}
	
	@RequestMapping(value = "/getManagedByUsers/{entityId}/", method = RequestMethod.GET)
	public ResponseEntity<ArrayList<String>> getManagedByUsers(@PathVariable("entityId") String entityId) {
		User sysUser;
		ResponseEntity response = null;
		ArrayList<String> managedByUsers = null;
		
		try {
			logger.info("getCircleUsers : " + entityId);

			sysUser = userService.getUser(entityId, false);		
			managedByUsers = sysUser.getMembers();

			response = new ResponseEntity(managedByUsers, HttpStatus.OK);

			
		} catch (Exception e) {
			logger.error(e.getMessage());
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
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
				metaData.put("entityId", user.getUsername());
				metaData.put("imageType", SystemConstants.SMALL_PROFILE_IMAGE_METADATA);
				//TODO
				//MAKE FILE UPLOAD AS REUSABLE, CHANGE THE FILE TYPE
				//GridFSFile gridFsFile = gridOperations.store(inputStream, fileName, "image/png", metaData);
				ObjectId id = gridFsTemplate.store(inputStream, fileName, file.getContentType(), metaData);
				//String fileId = gridFsFile.getId().toString();
				String fileId = id.toString();
				ProfileData profileData = userService.updateUserProfileData(user.getUsername(), profileTemplateId, "profileSMImageId", fileId);
				if(user.getProfileDatas() != null)
					user.getProfileDatas().add(profileData);
				else{
					List<ProfileData> profileDatas = new ArrayList<ProfileData>();
					profileDatas.add(profileData);
					user.setProfileDatas(profileDatas);
				}

				//not required, as gridfs has the entityId metadata
				//update profile image reference
				User userTemp = userService.findByUserName(user.getUsername(), true);
				userTemp.setProfileAvatarImgFileId(fileId);
				userService.updateUser(userTemp);

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

	@RequestMapping(value = "/isProfileEditable/{profileId}/{entityId}/", method = RequestMethod.GET)
	public ResponseEntity<Boolean> isProfileEditable(@PathVariable("profileId") String profileId, @PathVariable("entityId") String entityId) {
		User sysUser;
		ResponseEntity response = null;
		boolean profileEditable = false;

		try {
			logger.info("isProfileEditable: profile-> " + profileId + " , " + entityId);
			if(entityId != null){
				sysUser = userService.getUser(profileId, false);		
				if(SystemConstants.PASSIVE_USER.equalsIgnoreCase(sysUser.getStatus()) || (sysUser.getMembers() != null  && sysUser.getMembers().contains(entityId))){
					profileEditable = true;
				}
			}
			response = new ResponseEntity(profileEditable, HttpStatus.OK);

			
		} catch (Exception e) {
			logger.error(e.getMessage());
			response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}


		return response;
	}
	
}