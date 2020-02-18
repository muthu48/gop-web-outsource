package com.jpw.springboot.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private User findByUserName(String name) {
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
	
	public User getUser(String username, String userType) throws Exception{
		boolean createUserProfile = false;
		List<String> profileTemplateIdsList = new ArrayList<String>();

		User user = findByUserName(username);
				
		if(user == null){
			if(!userType.equalsIgnoreCase(SystemConstants.PUBLIC_USERTYPE)){ //CREATE PROFILE FOR NON-PUBLIC USER AND KEEP IT INACTIVE
				createUserProfile = true;
				user = new User();
				user.setUsername(username);
				user.setStatus("PASSIVE");//CHANGED THE STATUS FROM INACTIVE TO PASSIVE
			}else{
				throw new Exception("User not found - " + username);
			}
		}else{
			//get profiledata
			List<ProfileData> profileDatas = getProfileDatas(username);
			user.setProfileDatas(profileDatas);
			
			for(ProfileData profileData:profileDatas){
				if(!profileTemplateIdsList.contains(profileData.getProfileTemplateId())){
					profileTemplateIdsList.add(profileData.getProfileTemplateId());
				}
			}
			//get profile
			List<ProfileTemplate> profileTemplates = profileTemplateService.findAllProfileTemplatesByIds(profileTemplateIdsList);
			user.setProfileTemplates(profileTemplates);

		}	
		
		if(userType != null && userType.equalsIgnoreCase(SystemConstants.STATELEGIS_USERTYPE)){
			if(user.getSourceSystem() != null && user.getSourceSystem().equalsIgnoreCase(SystemConstants.OPENSTATE_LEGIS_SOURCE)){
				LegislatorOpenState legislator = findLegislator(username);//find by legid which is set as username		
				if(createUserProfile && legislator != null){
					user.setSourceId(legislator.getLegId());
				}
			}			
			if(user.getSourceSystem() != null && user.getSourceSystem().equalsIgnoreCase(SystemConstants.GOVTRACK_LEGIS_SOURCE)){
				//if(userType != null && userType.equalsIgnoreCase(SystemConstants.CONGRESSLEGIS_USERTYPE)){
					LegislatorCongressGT legislatorCongressGT = findLegislatorCongressByBioguide(username);//by id.bioguide		
					if(createUserProfile && legislatorCongressGT != null){
						//user.setSourceId(legislator.getId());
					}
				//}
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
		return userRepository.findAll();
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
	
	public ProfileData createProfileData(User user, String entityType, String profileTemplateId){
		//upDefault profileData
		//how to deal if non-publicuser ?
		//currently non-publicuser comes through bathc, however have to deal if its realtime
    	JSONObject profileDataObj = new JSONObject();
    	profileDataObj.put("first_name", user.getFirstName());
    	profileDataObj.put("last_name", user.getLastName());
    	profileDataObj.put("username", user.getUsername());
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

    	ProfileData profileData = new ProfileData();
	    Gson gson = new Gson();
    	BasicDBObject profileDataDBObj = gson.fromJson(profileDataObj.toString(), BasicDBObject.class);
    	profileData.setEntityId(user.getUsername());
    	profileData.setEntityType(entityType);
    	profileData.setProfileTemplateId(profileTemplateId);
    	profileData.setData(profileDataDBObj);
    	profileDataRepository.insert(profileData);
    	
		return profileData;
	}
	
	public ProfileData createProfileData(ProfileData profileData){
		return profileDataRepository.insert(profileData);	
	}
	
	public ProfileData saveProfileData(ProfileData profileData){
		ProfileData profileDataDb = profileDataRepository.findOne(profileData.getId());
		profileDataDb.setData(profileData.getData());
		return profileDataRepository.save(profileDataDb);	
	}
	
	public List<ProfileData> getProfileDatas(String entityId){
		List<ProfileData> profileDataList = profileDataRepository.findByEntityId(entityId);
		return profileDataList;		
	}
	
	public List<ProfileData> getProfileDataByProfileTemplateId(String entityId, String profileTemplateId){
		List<ProfileData> profileDataList = profileDataRepository.findByEntityIdAndProfileTemplateId(entityId, profileTemplateId);
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
