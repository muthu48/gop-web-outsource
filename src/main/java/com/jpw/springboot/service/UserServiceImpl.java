package com.jpw.springboot.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
import com.jpw.springboot.model.User;
import com.jpw.springboot.model.UserProfile;
import com.jpw.springboot.repositories.LegislatorCongressGTRepository;
import com.jpw.springboot.repositories.LegislatorOpenStateRepository;
import com.jpw.springboot.repositories.ProfileDataRepository;
import com.jpw.springboot.repositories.UserRepository;
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
		//BasicDBObject bObj = new BasicDBObject();
		//bObj.append("id.govtrack", Integer.parseInt(name));
		List<LegislatorCongressGT> legislators = legislatorCongressRepository.findByIdGovtrack(Integer.parseInt(name));
		//legislators = legislatorCongressRepository.findById(bObj);
		LegislatorCongressGT legislator = null;
		
		if(legislators != null && legislators.size() > 0)
			legislator = legislators.get(0);
		
		return legislator;
	}
	
	public User getUser(String username, String userType) throws Exception{
		boolean createUserProfile = false;
		User user = findByUserName(username);
		if(user == null){
			if(!userType.equalsIgnoreCase("publicUser")){ //CREATE PROFILE FOR NON-PUBLIC USER AND KEEP IT INACTIVE
				createUserProfile = true;
				user = new User();
				user.setUsername(username);
				user.setStatus("INACTIVE");
			}else{
				throw new Exception("User not found - " + username);
			}
		}	
		
		if(userType.equalsIgnoreCase("legislator")){
			LegislatorOpenState legislator = findLegislator(username);//find by legid which is set as username		
			if(createUserProfile && legislator != null){
				user.setSourceId(legislator.getLegId());
			}
		}	

		if(userType.equalsIgnoreCase("legislatorCongress")){
			LegislatorCongressGT legislator = findLegislatorCongress(username);//find by govtrack id, not username		
			if(createUserProfile && legislator != null){
				//user.setSourceId(legislator.getId());
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
    	JSONObject profileDataObj = new JSONObject();
    	profileDataObj.put("first_name", user.getFirstName());
    	profileDataObj.put("last_name", user.getLastName());
    	profileDataObj.put("emailId", user.getEmailId());
    	profileDataObj.put("phone", user.getPhone());
    	profileDataObj.put("address", user.getAddress());

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
		return profileDataRepository.save(profileData);	
	}
	
	public List<ProfileData> getProfileData(String entityId){
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
