package com.jpw.springboot.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.gson.Gson;
import com.jpw.springboot.util.GoogleTokenVerifier;
import com.jpw.springboot.TokenAuthenticationService;
import com.jpw.springboot.model.LegislatorCongressGT;
import com.jpw.springboot.model.LegislatorOpenState;
import com.jpw.springboot.model.ProfileData;
import com.jpw.springboot.model.ProfileTemplate;
import com.jpw.springboot.model.User;
import com.jpw.springboot.model.UserProfile;
import com.jpw.springboot.repositories.LegislatorCongressGTRepository;
import com.jpw.springboot.repositories.LegislatorOpenStateRepository;
import com.jpw.springboot.repositories.ProfileDataRepository;
import com.jpw.springboot.repositories.ProfileTemplateRepository;
import com.jpw.springboot.repositories.UserRepository;
import com.jpw.springboot.util.SystemConstants;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
	public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;
		
	@Autowired
	private ProfileDataRepository profileDataRepository;
	
	@Autowired
	private LegislatorOpenStateRepository legislatorOpenStateRepository;
	
	@Autowired
	private LegislatorCongressGTRepository legislatorCongressRepository;
	
	@Autowired
	private ProfileTemplateService profileTemplateService;
	
	@Autowired
	private GoogleTokenVerifier googleTokenVerifier;

	public User findById(String id) {
		User user = null;
		Optional<User> oUser = userRepository.findById(id);
		
		if(oUser.isPresent() && oUser.get() != null){
			user = oUser.get();
			user.setPassword(null);
		}
		
		return user;
	}

	public User findByUserName(String name, boolean pwdRequired) throws Exception {
		User user = userRepository.findByUsername(name);

		//CAN BE DONE THRU INTERCEPTOR
		if(user != null && !pwdRequired)
			user.setPassword(null);
		
		return user;

	}
	
	public LegislatorOpenState findLegislator(String name) {
		LegislatorOpenState legislator = legislatorOpenStateRepository.findByLegId(name);
		return legislator;
	}

	public LegislatorCongressGT findLegislatorCongress(String name) {
		List<LegislatorCongressGT> legislators = legislatorCongressRepository.findByIdGovtrack(Integer.parseInt(name));
		
		LegislatorCongressGT legislator = null;
		
		if(legislators != null && legislators.size() > 0)
			legislator = legislators.get(0);
		
		return legislator;
	}

	public LegislatorCongressGT findLegislatorCongressByBioguide(String name) {
		List<LegislatorCongressGT> legislators = legislatorCongressRepository.findByIdBioguide(name);
		
		LegislatorCongressGT legislator = null;
		
		if(legislators != null && legislators.size() > 0)
			legislator = legislators.get(0);
		
		return legislator;
	}
	
	public User getUser(String username) throws Exception{
		List<String> profileTemplateIdsList = new ArrayList<String>();
		String userType = null;
		User user = findByUserName(username, false);
				
		if(user == null){
			throw new Exception("User not found - " + username);

		}else{
			userType = user.getUserType();
			//get profiledata
			List<ProfileData> profileDatas = getProfileDatas(username);
			//List<ProfileData> profileDatasNoBio = new ArrayList<ProfileData>(); 
			for(ProfileData profileData:profileDatas){
				
				
				//IGNORING BIODATA TEMPLATE DATA for LEGISLATOR AS UI SHOWS BIODATA SEPARATELY 
				//ProfileManagementController::listEntityProfileTemplates - Reference
				/*if(!(userType.equalsIgnoreCase(SystemConstants.USERTYPE_LEGIS) && profileData.getProfileTemplateId().equalsIgnoreCase(SystemConstants.PROFILE_TEMPLATE_BIODATA))){
					profileDatasNoBio.add(profileData);
					
					if(!profileTemplateIdsList.contains(profileData.getProfileTemplateId())){
						profileTemplateIdsList.add(profileData.getProfileTemplateId());
					}
				}*/
				if(!profileTemplateIdsList.contains(profileData.getProfileTemplateId())){
					profileTemplateIdsList.add(profileData.getProfileTemplateId());
				}

			}
			//user.setProfileDatas(profileDatasNoBio);
			user.setProfileDatas(profileDatas);
			
			//get profile
			if(userType != null && (userType.equalsIgnoreCase(SystemConstants.USERTYPE_LEGIS) ||
					userType.equalsIgnoreCase(SystemConstants.USERTYPE_CONGRESSLEGIS) ||
					userType.equalsIgnoreCase(SystemConstants.USERTYPE_EXECUTIVE))){
				userType = SystemConstants.USERTYPE_LEGIS;
			}	
			List<ProfileTemplate> profileTemplates = profileTemplateService.findAllProfileTemplatesByIds(profileTemplateIdsList, userType);
			user.setProfileTemplates(profileTemplates);

		}	
		
	
		
		return user;

	}

	public User createUser(User user) throws Exception{
		User userLookup = userRepository.findByUsername(user.getUsername());
		if(userLookup == null){
	/*		if(user.getProfileDatas() != null){
				List<ProfileData> profileDatas = new ArrayList<ProfileData>();
				for(ProfileData profileData : user.getProfileDatas()){
					profileDatas.add(profileData);
				}
				user.setProfileDatas(profileDatas);
			}*/
			/*if(user.getProfileData() != null){
				ProfileData pd = profileDataRepository.insert(user.getProfileData());
				user.setProfileData(pd);
			}*/
			if(user.getProfileDatas() != null){
				//List<ProfileData> profileDatas = new ArrayList<ProfileData>();
				for(ProfileData profileData : user.getProfileDatas()){
					//profileDatas.add(profileDataRepository.insert(profileData));
					profileDataRepository.insert(profileData);
				}
				//user.setProfileDatas(profileDatas);
			}
			userRepository.insert(user);
/*			
			if(user.getProfileDatas() != null && user.getProfileDatas().size() > 0){
				createBioData(user.getProfileDatas().get(0));
			}
*/			
	       	return user;

		}else{
			throw new Exception("Entity with username " + user.getUsername() + " already exist");
		}
    	
     	
	}

	public User updateUser(User user) {
		return userRepository.save(user);
	}

	public void deleteUserById(String id) {
		userRepository.deleteById(id);
	}

	public void deleteAllUsers() {
		userRepository.deleteAll();
	}

	public List<User> findAllUsers() {
		return userRepository.findAll();
	}

	public boolean isUserExist(User user) throws Exception{
		return findByUserName(user.getUsername(), false) != null;
	}

	@Override
	public void saveUser(UserProfile user) {
		userRepository.save(user);
		
	}

	@Override
	public boolean isUserExist(UserProfile user) throws Exception{
		return findByUserName(user.getUserId(), false) != null;
	}
	
	//public ProfileData createProfileData(User user, String entityType, String profileTemplateId){
	public ProfileData createProfileData(User user) throws Exception{
		//TODO 
		String profileTemplateId=null;
		String entityType = user.getUserType();
		//upDefault profileData
		//how to deal if non-publicuser ?
		//currently non-publicuser comes through bathc, however have to deal if its realtime
    	JSONObject profileDataObj = new JSONObject();
    	profileDataObj.put("username", user.getUsername());
    	
    	if(entityType.equalsIgnoreCase(SystemConstants.USERTYPE_PUBLIC) ||
    			entityType.equalsIgnoreCase(SystemConstants.USERTYPE_LEGIS)){
    		profileTemplateId = SystemConstants.PROFILE_TEMPLATE_BIODATA;
    		
	    	profileDataObj.put("first_name", user.getFirstName());
	    	profileDataObj.put("last_name", user.getLastName());
	
	    	profileDataObj.put("emailId", user.getEmailId());
	    	profileDataObj.put("phone", user.getPhone());
	    	profileDataObj.put("address", user.getAddress());
	
	    	//check for email/phone pattern with user.getUsername() and set corresponding value
	    	String emailRegex = "^(.+)@(.+)$";
	    	String phoneRegex = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$"; //North America
	    	Pattern pattern = Pattern.compile(emailRegex);
	        Matcher matcher = pattern.matcher(user.getUsername());
	        if(matcher.matches()){
	        	profileDataObj.put("emailId", user.getUsername());        	
	        }else{
	        	pattern = Pattern.compile(phoneRegex);
	            matcher = pattern.matcher(user.getUsername());
	            if(matcher.matches()){
	            	profileDataObj.put("phone", user.getUsername());        	
	            }
	        }
    	}else if(entityType.equalsIgnoreCase(SystemConstants.USERTYPE_LEGISLATIVE_DISTRICT)){
    		
    	}else if(entityType.equalsIgnoreCase(SystemConstants.USERTYPE_POLITICAL_PARTY)){
    		
    	}
    	
    	ProfileData profileData = new ProfileData();
	    Gson gson = new Gson();
    	BasicDBObject profileDataDBObj = gson.fromJson(profileDataObj.toString(), BasicDBObject.class);
    	profileData.setEntityId(user.getUsername());
    	profileData.setEntityType(entityType);
    	profileData.setProfileTemplateId(profileTemplateId);
    	profileData.setData(profileDataDBObj);
    	
    	saveProfileData(profileData);
/*    	for(ProfileData profileData : user.getProfileDatas()){
        	saveProfileData(profileData);

    	}*/

		return profileData;
	}
	
	public ProfileData createBioData(ProfileData profileData) throws Exception{
		//TODO 
	    Gson gson = new Gson();
		String entityType = profileData.getEntityType();

		//DBObject profileDataObj = gson.fromJson(profileData.getData().toString(), DBObject.class);
		BasicDBObject profileDataObj = profileData.getData();

    	if(entityType.equalsIgnoreCase(SystemConstants.USERTYPE_PUBLIC) ||
    			entityType.equalsIgnoreCase(SystemConstants.USERTYPE_LEGIS)){

	    	//check for email/phone pattern with user.getUsername() and set corresponding value
	    	String emailRegex = "^(.+)@(.+)$";
	    	String phoneRegex = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$"; //North America
	    	Pattern pattern = Pattern.compile(emailRegex);
	        Matcher matcher = pattern.matcher(profileData.getEntityId());
	        if(matcher.matches()){
	        	profileDataObj.put("emailId", profileData.getEntityId());        	
	        }else{
	        	pattern = Pattern.compile(phoneRegex);
	            matcher = pattern.matcher(profileData.getEntityId());
	            if(matcher.matches()){
	            	profileDataObj.put("phone", profileData.getEntityId());        	
	            }
	        }
    	}
    	/*else if(entityType.equalsIgnoreCase(SystemConstants.USERTYPE_LEGISLATIVE_DISTRICT)){
    		
    	}else if(entityType.equalsIgnoreCase(SystemConstants.USERTYPE_POLITICAL_PARTY)){
    		
    	}*/
    	
    	//BasicDBObject profileDataDBObj = gson.fromJson(profileDataObj.toString(), BasicDBObject.class);
    	//profileData.setData(profileDataDBObj);
    	
    	saveProfileData(profileData);

		return profileData;
	}
	
	public ProfileData createProfileData(ProfileData profileData){
		return profileDataRepository.insert(profileData);	
	}
	
	public ProfileData saveProfileData(ProfileData profileData) throws Exception{
		ProfileData profileDataDb = null;
		if(StringUtils.isNoneEmpty(profileData.getId())){
			Optional oPprofileData = profileDataRepository.findById(profileData.getId());
			if(oPprofileData.isPresent()){
				profileDataDb = (ProfileData)oPprofileData.get();
				profileDataDb.setData(profileData.getData());
				profileDataDb = profileDataRepository.save(profileDataDb);
				
			}else{
				throw new Exception("Profile Data not found for id " + profileData.getId());
			}
		}else{
			profileDataDb = profileDataRepository.insert(profileData);
		}
		return profileDataDb;	
	}
	
	public List<ProfileData> getProfileDatas(String entityId){
		List<ProfileData> profileDataList = profileDataRepository.findByEntityId(entityId);
		return profileDataList;		
	}
	
	public List<ProfileData> getProfileDataByProfileTemplateId(String entityId, String profileTemplateId){
		List<ProfileData> profileDataList = new ArrayList<ProfileData>();
		profileDataList = profileDataRepository.findByEntityIdAndProfileTemplateId(entityId, profileTemplateId);
		return profileDataList;		
	}
	
	public ProfileData updateUserProfileData(String entityId, String profileTemplateId, String key, String value) throws Exception{
		//TODO
		//profileTemplateId = "upCongressLegislatorDefault"
		//get the profile data, set the biodata template with profile image id and then set the profile data	
		ProfileData profileData = null;
		List<ProfileData> profileDatas = getProfileDataByProfileTemplateId(entityId, profileTemplateId);
		if(profileDatas != null && profileDatas.size() > 0){
			profileData = profileDatas.get(0); // one profiledata exist for a give profileTemplateId
			JSONObject obj = profileData.getJSONData();
			//obj.put("profileSMImageId", value);
			obj.put(key, value);
			BasicDBObject dbObject = (BasicDBObject)JSON.parse(obj.toString());
			profileData.setData(dbObject);
			this.saveProfileData(profileData);
		}
		
		return profileData;
	}
	
	public ResponseEntity tokenVerify(String token, String provider) throws Exception{
		ResponseEntity response = null;

		if(StringUtils.isNotBlank(provider)){
			if("GOOGLE".equalsIgnoreCase(provider)){
				response = tokenVerifyGoogle(token);
			}else if("FACEBOOK".equalsIgnoreCase(provider)){
				response = tokenVerifyFB(token);
				
			}
		}
		
		return response;
	}
	
	private ResponseEntity tokenVerifyGoogle(String idToken) throws Exception{
		ResponseEntity response = null;
		logger.info("Authenticating token " + idToken);

	    if (idToken != null) {
	    	final Payload payload;
	      
	        payload = googleTokenVerifier.verify(idToken);
	        if (payload != null) {
	          String subject = payload.getSubject();
	          response = TokenAuthenticationService.addAuthentication(subject);
	          //async - check and register the user
	          User user = new User();

	          List<ProfileData> profileDatas = new ArrayList<ProfileData>();
	          ProfileData profileData = new ProfileData();
	          profileData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_BIODATA);
	          
	          BasicDBObject data = new BasicDBObject();
			  
	          user.setUsername(payload.getEmail());
	          user.setUserType(SystemConstants.USERTYPE_PUBLIC);
			  profileData.setEntityId(user.getUsername());
			  user.setSourceSystem("GOOGLE");
			  user.setSourceId(payload.getEmail());
			  data.append("emailId", user.getSourceId());
			  if(payload.containsKey("name")){
				  user.setDisplayName(payload.get("name").toString());
				  data.append("full_name", user.getDisplayName());
			  }
			  if(payload.containsKey("given_name")){
				  user.setFirstName(payload.get("given_name").toString());
				  data.append("first_name", user.getFirstName());
			  }
			  if(payload.containsKey("family_name")){
				  user.setLastName(payload.get("family_name").toString());
				  data.append("last_name", user.getLastName());
			  }
			  if(payload.containsKey("picture")){
				  user.setPhotoUrl(payload.get("picture").toString());
			  }

			  profileData.setData(data);
			  profileData.setCurrent(true);
			  profileDatas.add(profileData);
			  user.setProfileDatas(profileDatas);
			  user.setStatus(SystemConstants.ACTIVE);
			  ArrayList<String> members = new ArrayList<String>();
			  members.add(user.getUsername());
			  user.setMembers(members);
	          registerUserExternal(user);
	        }
	      
	    }else{
	    	throw new Exception("Unauthorized access");
	    }
	    
	    return response;

	}
	
	private ResponseEntity tokenVerifyFB(String token) throws Exception{
		ResponseEntity response = null;
		logger.info("Authenticating FB auth token " + token);


		
	    if (token != null) {
	    	final FacebookClient facebookClient = new DefaultFacebookClient(token, Version.VERSION_7_0);	    	
	    	com.restfb.types.User fbUser = facebookClient.fetchObject("me", com.restfb.types.User.class, Parameter.with("fields", "id,name,email,first_name,last_name,picture"));
	    	
	    	 if (fbUser != null) {
	    		 response = TokenAuthenticationService.addAuthentication(fbUser.getEmail());

				 //async - check and register the user
				 User user = new User();
		         List<ProfileData> profileDatas = new ArrayList<ProfileData>();
		         ProfileData profileData = new ProfileData();
		         profileData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_BIODATA);
		          
		         BasicDBObject data = new BasicDBObject();

				 user.setUsername(fbUser.getEmail());
		         user.setUserType(SystemConstants.USERTYPE_PUBLIC);
				 profileData.setEntityId(user.getUsername());
				  
				 user.setSourceSystem("FACEBOOK");
				 if(fbUser.getEmail() != null) {
					 user.setSourceId(fbUser.getEmail());
					 data.append("emailId", user.getSourceId());
				 }

				 if(fbUser.getName() != null) {
					 user.setDisplayName(fbUser.getName());
					 data.append("full_name", user.getDisplayName());
				 }
					 
				 if(fbUser.getFirstName() != null) {
					 user.setFirstName(fbUser.getFirstName());
					 data.append("first_name", user.getFirstName());
				 }
				 
				 if(fbUser.getLastName() != null) {
					 user.setLastName(fbUser.getLastName());
					 data.append("last_name", user.getLastName());
				 }
				 
				 if(fbUser.getPicture() != null && fbUser.getPicture().getUrl() != null)
				  user.setPhotoUrl(fbUser.getPicture().getUrl());
				

				 profileData.setData(data);
				 profileData.setCurrent(true);
				 profileDatas.add(profileData);
				 user.setProfileDatas(profileDatas);
				 
				 user.setStatus(SystemConstants.ACTIVE);
				 ArrayList<String> members = new ArrayList<String>();
				 members.add(user.getUsername());
				 user.setMembers(members); 
				 
				 registerUserExternal(user);
	        }
	    	 
	      
	    }else{
	    	throw new Exception("Unauthorized access");
	    }
	    
	    return response;

	}
	
	@Async
	//Async method should be PUBLIC and called from separate class
	public void registerUserExternal(User user){
	//private void registerUserExternal(String username){
		logger.info("in registerUserExternal for username " + user.getUsername());

		try {
			User userSys = findByUserName(user.getUsername(), false); 
			if(userSys == null){
				createUser(user);
			}else{//user already exist
				//update user if other attributes such as name, picture got changed ?

				boolean update = false;
				if(!userSys.getPhotoUrl().equalsIgnoreCase(user.getPhotoUrl())){
					update = true;
					userSys.setPhotoUrl(user.getPhotoUrl());
				}
				
				if(!userSys.getDisplayName().equalsIgnoreCase(user.getDisplayName())){
					update = true;
					userSys.setDisplayName(user.getDisplayName());
				}
				
				if(update){
					updateUser(userSys);
				}
			}
		} catch (Exception e) {
			logger.error("Error in registerUserExternal for username " + user.getUsername() + e.getMessage(), e);
		}
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
