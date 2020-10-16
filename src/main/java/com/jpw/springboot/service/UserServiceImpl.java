package com.jpw.springboot.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
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

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

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
	
	public User findById(String id) {
		User user = userRepository.findOne(id);
		
		if(user != null)
			user.setPassword(null);
		
		return user;
	}

	public User findByUserName(String name) throws Exception {
		User user = userRepository.findByUsername(name);

		//CAN BE DONE THRU INTERCEPTOR
		if(user != null)
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
		User user = findByUserName(username);
				
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
			userRepository.insert(user);
			
			//createProfileData(user);
			if(user.getProfileDatas() != null && user.getProfileDatas().size() > 0){
				createBioData(user.getProfileDatas().get(0));
			}
			
	       	return user;

		}else{
			throw new Exception("Entity with username " + user.getUsername() + " already exist");
		}
    	
     	
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

	public boolean isUserExist(User user) throws Exception{
		return findByUserName(user.getUsername()) != null;
	}

	@Override
	public void saveUser(UserProfile user) {
		userRepository.save(user);
		
	}

	@Override
	public boolean isUserExist(UserProfile user) throws Exception{
		return findByUserName(user.getUserId()) != null;
	}
	
	//public ProfileData createProfileData(User user, String entityType, String profileTemplateId){
	public ProfileData createProfileData(User user){
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
	
	public ProfileData createBioData(ProfileData profileData){
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
	
	public ProfileData saveProfileData(ProfileData profileData){
		ProfileData profileDataDb = null;
		if(StringUtils.isNoneEmpty(profileData.getId())){
			profileDataDb = profileDataRepository.findOne(profileData.getId());
			profileDataDb.setData(profileData.getData());
			profileDataDb = profileDataRepository.save(profileDataDb);
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
	
	public ProfileData updateUserProfileData(String entityId, String profileTemplateId, String key, String value){
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
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user= userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.emptyList());
   
	}

}
