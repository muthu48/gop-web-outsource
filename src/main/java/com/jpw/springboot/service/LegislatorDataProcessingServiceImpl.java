package com.jpw.springboot.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import java.time.Duration;
import java.time.Instant;

import com.jpw.springboot.model.CommitteeCongressGT;
import com.jpw.springboot.model.CommitteeCongressMembershipGT;
import com.jpw.springboot.model.LegislatorCongressGT;
import com.jpw.springboot.model.LegislatorOpenState;
import com.jpw.springboot.model.LegislatorOpenStateV2;
import com.jpw.springboot.model.ProfileData;
import com.jpw.springboot.model.User;
import com.jpw.springboot.repositories.CongressCommitteeMembershipRepository;
import com.jpw.springboot.repositories.CongressCommitteeRepository;
import com.jpw.springboot.repositories.CongressDistrictOfficesRepository;
import com.jpw.springboot.repositories.LegislatorCongressGTRepository;
import com.jpw.springboot.repositories.LegislatorOpenStateRepository;
import com.jpw.springboot.repositories.LegislatorOpenStateRepositoryV2;
import com.jpw.springboot.repositories.ProfileDataRepository;
import com.jpw.springboot.repositories.UserRepository;
import com.jpw.springboot.util.DataConvertor;
import com.jpw.springboot.util.SystemConstants;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Service("legislatorDataProcessingService")
@Transactional
public class LegislatorDataProcessingServiceImpl implements LegislatorDataProcessingService {
	public static final Logger logger = LoggerFactory.getLogger(LegislatorDataProcessingServiceImpl.class);

	@Autowired
	private LegislatorCongressGTRepository legislatorCongressGTRepository;
	
	@Autowired
	private LegislatorOpenStateRepository legislatorOpenStateRepository;
	
	@Autowired
	private LegislatorOpenStateRepositoryV2 legislatorOpenStateRepositoryV2;
	
	@Autowired
	private CongressCommitteeRepository congressCommitteeRepository;
	
	@Autowired
	private CongressCommitteeMembershipRepository congressCommitteeMembershipRepository;
	
	@Autowired
	private CongressDistrictOfficesRepository congressDistrictOfficesRepository;
	
	@Autowired
	private ProfileDataRepository profileDataRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	String state = null;
	String userCategory = null;
	String userType = null; //role

	@Override
	public void loadCongressLegislatorsToDb(File file, String userType) throws Exception{
		InputStream fis = null;
		fis = new FileInputStream(file);
		

		JsonReader reader = Json.createReader(fis);
/*		JsonObject legislatorsObj = reader.readObject();
		JsonArray legislators = (JsonArray)legislatorsObj.getJsonArray("data");*/
		JsonArray legislators = reader.readArray();
		reader.close();
		
	    Gson gson = new Gson();
        System.out.format("Found %d records\n", legislators.size());

	    for (int i = 0; i < legislators.size(); i++) {
		    try{
		    	JsonObject legislatorObj = legislators.getJsonObject(i);	
		        System.out.format("Processing %dth records\n %s", i, legislatorObj.toString());
		    	
		        //legislatorcongress
			    LegislatorCongressGT legislatorCongressGT = gson.fromJson(legislatorObj.toString(), LegislatorCongressGT.class);
			    try {
				    legislatorCongressGTRepository.insert(legislatorCongressGT);
			    }catch(Exception e){
		    		System.out.println("Error in creating entry in legislatorcongress " + e);
		    		logger.error("Error in creating entry in legislatorcongress " + e);
				}
			    

		    	String sourceId = null;
		    	String sourceIdIntial = null;
		    	String photoUrl = null;
		    	if(!StringUtils.isEmpty(legislatorCongressGT.getId())){
			    	if(!StringUtils.isEmpty(legislatorCongressGT.getId().getString("bioguide"))){
				    	sourceId = legislatorCongressGT.getId().getString("bioguide"); //ex:A000370
				    	sourceIdIntial = sourceId.substring(0,1); //ex:A from A000370
				    	photoUrl = "http://bioguide.congress.gov/bioguide/photo/" + sourceIdIntial + "/" + sourceId + ".jpg";
			    	}else if(!StringUtils.isEmpty(legislatorCongressGT.getId().getString("govtrack"))){
				    	sourceId = String.valueOf(legislatorCongressGT.getId().getInt("govtrack")); 
			    	}else if(!StringUtils.isEmpty(legislatorCongressGT.getId().getString("opensecrets"))){
				    	sourceId = legislatorCongressGT.getId().getString("opensecrets"); 
			    	}else if(!StringUtils.isEmpty(legislatorCongressGT.getId().getString("votesmart"))){
				    	sourceId = String.valueOf(legislatorCongressGT.getId().getInt("votesmart")); 
			    	}else if(!StringUtils.isEmpty(legislatorCongressGT.getId().getString("thomas"))){
				    	sourceId = legislatorCongressGT.getId().getString("thomas"); 
			    	}
		    	}
		    	
		    	String fullName = null;		
		    	User user = new User();
		    	BasicDBObject settings = new BasicDBObject();
		    	//user.setUserId(sourceId);
		    	user.setUsername(sourceId);
		    	//user.setUsername(result.getName().getString("first") + "." + result.getName().getString("last"));
		    	user.setUserType(userType);
		    	user.setSourceSystem(SystemConstants.GOVTRACK_LEGIS_SOURCE);
		    	user.setStatus(SystemConstants.PASSIVE_USER);
		    	settings.put("accessRestriction", false);
		    	user.setSettings(settings);
		    	user.setSourceId(sourceId);
		    	user.setPhotoUrl(photoUrl);
		    	//user.setFirstName(result.getName().getString("first"));
		    	//user.setLastName(result.getName().getString("last"));
		    	if(!StringUtils.isEmpty(legislatorCongressGT.getName())){
		    		if(!StringUtils.isEmpty(legislatorCongressGT.getName().getString("official_full"))){
		    			fullName = legislatorCongressGT.getName().getString("official_full").replaceAll(",", "");
		    		}else{
		    			if(!StringUtils.isEmpty(legislatorCongressGT.getName().getString("first"))){
		    				fullName = legislatorCongressGT.getName().getString("first").trim();
		    			}
		    			if(!StringUtils.isEmpty(legislatorCongressGT.getName().getString("middle"))){
		    				fullName = fullName.concat(" ").concat(legislatorCongressGT.getName().getString("middle")).trim();
		    			}
		    			if(!StringUtils.isEmpty(legislatorCongressGT.getName().getString("last"))){
		    				fullName = fullName.concat(" ").concat(legislatorCongressGT.getName().getString("last")).trim();

		    			}
		    			if(!StringUtils.isEmpty(legislatorCongressGT.getName().getString("suffix"))){
		    				fullName = fullName.concat(" ").concat(legislatorCongressGT.getName().getString("suffix")).trim();

		    			}
		    		}
		    	}
		    	user.setDisplayName(fullName.trim());
		    	userRepository.insert(user);
		    	
		    	//upCongressLegislatorExternal profileData
		    	JSONObject profileDataObj = new JSONObject();
		    	profileDataObj.put("leg_id", sourceId);
		    	profileDataObj.put("first_name", legislatorCongressGT.getName().getString("first"));
		    	profileDataObj.put("last_name", legislatorCongressGT.getName().getString("last"));
		    	profileDataObj.put("full_name", fullName);
		    	ProfileData profileData = new ProfileData();
		    	BasicDBObject profileDataDBObj = gson.fromJson(profileDataObj.toString(), BasicDBObject.class);
		    	profileData.setEntityId(user.getUsername());
		    	profileData.setEntityType(userType);
		    	profileData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_BIODATA);
		    	profileData.setData(profileDataDBObj);
		    	profileDataRepository.insert(profileData);
		    	
		    	//upRole/upOffices profileData
		    	for (int j = 0; j < legislatorCongressGT.getTerms().size(); j++) {
		    		BasicDBObject legislatorRoleObj = legislatorCongressGT.getTerms().get(j);	
	
			    	profileDataObj = new JSONObject();
			    	//finding the current role/office
			    	if(!StringUtils.isEmpty(legislatorRoleObj.getString("start"))){            
			    		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			            Date startDate = DataConvertor.convertString2Date(legislatorRoleObj.getString("start"));
			            Date endDate = DataConvertor.convertString2Date(legislatorRoleObj.getString("end"));
			            Date currentDate = new Date();
			            if (currentDate.compareTo(startDate) > 0 &&
			            		(StringUtils.isEmpty(legislatorRoleObj.getString("end")) ||endDate.compareTo(currentDate) > 0)) {
					    	profileDataObj.put("isCurrent", true);
			            }
			    		profileDataObj.put("start", legislatorRoleObj.getString("start"));
			    		profileDataObj.put("end", legislatorRoleObj.getString("end"));
		            }
			    	
			    	if(legislatorRoleObj.getString("office") != null && legislatorRoleObj.getString("office").length() > 0){
			            
			    		profileDataObj.put("term", legislatorRoleObj.getString("start")+"-"+legislatorRoleObj.getString("end"));
			    		profileDataObj.put("phone", legislatorRoleObj.getString("phone"));
				    	profileDataObj.put("address", legislatorRoleObj.getString("address"));
				    	profileDataObj.put("fax", legislatorRoleObj.getString("fax"));
				    	profileDataDBObj = gson.fromJson(profileDataObj.toString(), BasicDBObject.class);
				    	
				    	profileData = new ProfileData();
				    	profileData.setEntityId(user.getUsername());
				    	profileData.setEntityType(userType);
				    	profileData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_OFFICE);
				    	if(profileDataObj.has("isCurrent"))
				    		profileData.setCurrent(profileDataObj.getBoolean("isCurrent"));
				    	
				    	profileData.setData(profileDataDBObj);
			    		
			    	}else{
			    		profileDataObj.put("start", legislatorRoleObj.getString("start"));
			    		profileDataObj.put("end", legislatorRoleObj.getString("end"));
			    		profileDataObj.put("term", legislatorRoleObj.getString("start")+"-"+legislatorRoleObj.getString("end"));
				    	profileDataObj.put("district", legislatorRoleObj.getString("district"));
				    	profileDataObj.put("state", legislatorRoleObj.getString("state"));
				    	profileDataObj.put("party", legislatorRoleObj.getString("party"));
				    	profileDataObj.put("type", legislatorRoleObj.getString("type"));
				    	profileDataDBObj = gson.fromJson(profileDataObj.toString(), BasicDBObject.class);
				    	
				    	profileData = new ProfileData();
				    	profileData.setEntityId(user.getUsername());
				    	profileData.setEntityType(userType);
				    	profileData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_ROLE);
				    	if(profileDataObj.has("isCurrent"))
				    		profileData.setCurrent(profileDataObj.getBoolean("isCurrent"));			    	
				    	
				    	profileData.setData(profileDataDBObj);
			    		
			    	}
			    	profileDataRepository.insert(profileData);	    	
		    	}
	    	}catch(Exception e){
	    		logger.error("Error Processing record " + legislators.getInt(i) + e);
	            System.out.println("Error Processing record " + legislators.getInt(i) + e);
	    	}
	    }
	    
        System.out.format("Successfully processed Found %d records\n", legislators.size());
	}
	
	public void loadCongressMembersToDb(File file, String userCategory) throws Exception{
		Gson gson = new Gson();
		Date startDate = null;
		Date endDate = null;
		Date latestStartDate = null;
		Date latestEndDate = null;
		Boolean isLatestRoleData = false;
		String role = null;
		String district = null;
		String party = null;
		JSONObject profileDataObj = null;
		BasicDBObject profileDataDBObj = null;
		BasicDBObject losRole = null;
		ProfileData latestProfileData = null;
		BasicDBObject latestRoleData = null;
		BasicDBObject roleData = null;
		ProfileData profileData = null;
    	User user = null;
    	
		InputStream fis = null;
		fis = new FileInputStream(file);

		JsonReader reader = Json.createReader(fis);

		JsonArray legislators = reader.readArray();
		reader.close();
		
        System.out.format("Found %d records\n", legislators.size());

	    for (int i = 0; i < legislators.size(); i++) {
		    try{
		    	JsonObject legislatorObj = legislators.getJsonObject(i);	
		        System.out.format("Processing %dth records\n %s", i, legislatorObj.toString());
		    	
		        //legislatorcongress
			    LegislatorCongressGT legislatorCongressGT = gson.fromJson(legislatorObj.toString(), LegislatorCongressGT.class);
			    try {
				    legislatorCongressGTRepository.insert(legislatorCongressGT);
			    }catch(Exception e){
		    		System.out.println("Error in creating entry in legislatorcongress " + e);
		    		logger.error("Error in creating entry in legislatorcongress " + e);
				}
			    
		    	String sourceId = null;
		    	String sourceIdIntial = null;
		    	String photoUrl = null;
		    	if(!StringUtils.isEmpty(legislatorCongressGT.getId())){
			    	if(!StringUtils.isEmpty(legislatorCongressGT.getId().getString("bioguide"))){
				    	sourceId = legislatorCongressGT.getId().getString("bioguide"); //ex:A000370
				    	sourceIdIntial = sourceId.substring(0,1); //ex:A from A000370
				    	photoUrl = "http://bioguide.congress.gov/bioguide/photo/" + sourceIdIntial + "/" + sourceId + ".jpg";
			    	}else if(!StringUtils.isEmpty(legislatorCongressGT.getId().getString("govtrack"))){
				    	sourceId = String.valueOf(legislatorCongressGT.getId().getInt("govtrack")); 
			    	}else if(!StringUtils.isEmpty(legislatorCongressGT.getId().getString("opensecrets"))){
				    	sourceId = legislatorCongressGT.getId().getString("opensecrets"); 
			    	}else if(!StringUtils.isEmpty(legislatorCongressGT.getId().getString("votesmart"))){
				    	sourceId = String.valueOf(legislatorCongressGT.getId().getInt("votesmart")); 
			    	}else if(!StringUtils.isEmpty(legislatorCongressGT.getId().getString("thomas"))){
				    	sourceId = legislatorCongressGT.getId().getString("thomas"); 
			    	}
		    	}
		    	
			  //upRole/upOffices profileData
		    	for (int j = 0; j < legislatorCongressGT.getTerms().size(); j++) {
		    		BasicDBObject legislatorRoleObj = legislatorCongressGT.getTerms().get(j);	
	
		    		profileDataObj = new JSONObject();
		    		profileData = new ProfileData();
			    	//finding the current role/office
			    	//if(!StringUtils.isEmpty(legislatorRoleObj.getString("start"))){            
			    		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			            //Date startDate = DataConvertor.convertString2Date(legislatorRoleObj.getString("start"));
			            //Date endDate = DataConvertor.convertString2Date(legislatorRoleObj.getString("end"));
			            //Date currentDate = new Date();
						/*
						 * if (currentDate.compareTo(startDate) > 0 &&
						 * (StringUtils.isEmpty(legislatorRoleObj.getString("end"))
						 * ||endDate.compareTo(currentDate) > 0)) { profileDataObj.put("isCurrent",
						 * true); }
						 */
			            
		            if(legislatorRoleObj.containsField("start"))
			    		startDate = DataConvertor.convertString2Date(legislatorRoleObj.getString("start"));
			    	
			    	if(legislatorRoleObj.containsField("end"))
			    		endDate = DataConvertor.convertString2Date(legislatorRoleObj.getString("end"));
			    	
			    	//current role
			    	if((startDate != null && startDate.compareTo(new Date()) <= 0 && (endDate == null || endDate.compareTo(new Date()) >= 0)) || //start date <= current date and and end date >= current date if exist
			    	   (startDate == null && (endDate != null && endDate.compareTo(new Date()) >= 0 ))){//contains only end date and >= current date
				    	profileData.setCurrent(true);
			    	}
			    	

		            //}
			    	
		    		//setting role
			    	if(legislatorRoleObj.containsField("type")) {
			    		String userRole = null; 
			    		if(legislatorRoleObj.getString("type").equalsIgnoreCase(SystemConstants.USERTYPE_GT_CONGRESS_REP))
			    			userRole = SystemConstants.USERTYPE_CONGRESS_REP;
			    		else if(legislatorRoleObj.getString("type").equalsIgnoreCase(SystemConstants.USERTYPE_GT_CONGRESS_SENATE))
			    			userRole = SystemConstants.USERTYPE_CONGRESS_SENATOR;
			    		else if(legislatorRoleObj.getString("type").equalsIgnoreCase(SystemConstants.USERTYPE_GT_CONGRESS_PRESIDENT))
			    			userRole = SystemConstants.USERTYPE_CONGRESS_PRESIDENT;
			    		else if(legislatorRoleObj.getString("type").equalsIgnoreCase(SystemConstants.USERTYPE_GT_CONGRESS_VICEPRESIDENT))
			    			userRole = SystemConstants.USERTYPE_CONGRESS_VICEPRESIDENT;
			    		else
			    			userRole = legislatorRoleObj.getString("type");
			    		
			    		profileData.setEntityType(userRole);
				    	profileDataObj.put("type", userRole);

			    	}else {//can this be LEGISLATOR ?
			    		profileData.setEntityType(SystemConstants.USERTYPE_LEGIS);
				    	profileDataObj.put("type", SystemConstants.USERTYPE_LEGIS);

			    	}
			    	
		    		profileDataObj.put("start_date", legislatorRoleObj.getString("start"));
		    		profileDataObj.put("end_date", legislatorRoleObj.getString("end"));
			    	profileDataObj.put("district", legislatorRoleObj.getString("district"));
			    	profileDataObj.put("state", legislatorRoleObj.getString("state"));
			    	profileDataObj.put("party", legislatorRoleObj.getString("party"));
			    	profileDataObj.put("url", legislatorRoleObj.getString("url"));
		    		profileDataObj.put("address", legislatorRoleObj.getString("address"));
			    	profileDataObj.put("office", legislatorRoleObj.getString("office"));
			    	profileDataObj.put("phone", legislatorRoleObj.getString("phone"));
			    	profileDataObj.put("fax", legislatorRoleObj.getString("fax"));
			    	profileDataObj.put("rss_url", legislatorRoleObj.getString("rss_url"));
			    	profileDataObj.put("contact_form", legislatorRoleObj.getString("contact_form"));
			    	profileDataDBObj = gson.fromJson(profileDataObj.toString(), BasicDBObject.class);
			    	
			    	//profileData = new ProfileData();
			    	profileData.setEntityId(sourceId);
			    	//profileData.setEntityType(userRole);
			    	profileData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_ROLE);
			    	//if(profileDataObj.has("isCurrent"))
			    	//	profileData.setCurrent(profileDataObj.getBoolean("isCurrent"));			    	
			    	
			    	profileData.setData(profileDataDBObj);
			    		
			    	//finding the latest role
		    		if(latestProfileData == null)
			    		latestProfileData = profileData;
			    	else {//finding latest role
						latestStartDate = null;
						latestEndDate = null;
						latestRoleData = latestProfileData.getData();
			    		if(latestRoleData.containsField("start_date"))
			    			latestStartDate = DataConvertor.convertString2Date(latestRoleData.getString("start_date"));
				    	
				    	if(latestRoleData.containsField("end_date"))
				    		latestEndDate = DataConvertor.convertString2Date(latestRoleData.getString("end_date"));
				    	
				    	//either start date should be greater than the previous role start date OR
				    	//end date should be greater than the previous role end date
				    	if((startDate != null && latestStartDate != null && startDate.compareTo(latestStartDate) > 0 ) ||
				    	   (endDate != null && latestEndDate != null && endDate.compareTo(latestEndDate) > 0 )) {
				    		latestProfileData = profileData;
				    	}
				    	
			    	}
		    		
			    	profileDataRepository.insert(profileData);	    	
			    	
			    	//get the office information only for the current role
			    	if(profileData.isCurrent() && legislatorRoleObj.getString("office") != null){
			    		profileDataObj = new JSONObject();
				    	profileDataObj.put("address", legislatorRoleObj.getString("address"));
			    		profileDataObj.put("phone", legislatorRoleObj.getString("phone"));
				    	profileDataObj.put("fax", legislatorRoleObj.getString("fax"));
				    	profileDataDBObj = gson.fromJson(profileDataObj.toString(), BasicDBObject.class);
				    	
				    	profileData = new ProfileData();
				    	profileData.setEntityId(sourceId);
				    	//profileData.setEntityType(userType);
				    	profileData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_OFFICE);
				    	
				    	profileData.setData(profileDataDBObj);
				    	profileDataRepository.insert(profileData);	    	

			    		
			    	}


		    	}
		    	
		    	
		    	user = userRepository.findByUsername(sourceId);		    	
			    //USER
		    	if(user == null) {
			    	String fullName = null;		
			    	user = new User();
			    	BasicDBObject settings = new BasicDBObject();
			    	//user.setUserId(sourceId);
			    	user.setUsername(sourceId);
			    	//user.setUsername(result.getName().getString("first") + "." + result.getName().getString("last"));
			    	//user.setUserType(userType);
					if(latestProfileData != null && latestProfileData.getEntityType() != null)
				    	user.setUserType(latestProfileData.getEntityType());
					else
						user.setUserType(SystemConstants.USERTYPE_LEGIS);
	
					user.setCategory(userCategory);
					user.setSourceId(sourceId);
			    	user.setSourceSystem(SystemConstants.GOVTRACK_LEGIS_SOURCE);
			    	user.setStatus(SystemConstants.PASSIVE_USER);
			    	settings.put("accessRestriction", false);
			    	user.setSettings(settings);
			    	user.setPhotoUrl(photoUrl);
			    	//user.setFirstName(result.getName().getString("first"));
			    	//user.setLastName(result.getName().getString("last"));
			    	if(!StringUtils.isEmpty(legislatorCongressGT.getName())){
			    		if(!StringUtils.isEmpty(legislatorCongressGT.getName().getString("official_full"))){
			    			fullName = legislatorCongressGT.getName().getString("official_full").replaceAll(",", "");
			    		}else{
			    			if(!StringUtils.isEmpty(legislatorCongressGT.getName().getString("first"))){
			    				fullName = legislatorCongressGT.getName().getString("first").trim();
			    			}
			    			if(!StringUtils.isEmpty(legislatorCongressGT.getName().getString("middle"))){
			    				fullName = fullName.concat(" ").concat(legislatorCongressGT.getName().getString("middle")).trim();
			    			}
			    			if(!StringUtils.isEmpty(legislatorCongressGT.getName().getString("last"))){
			    				fullName = fullName.concat(" ").concat(legislatorCongressGT.getName().getString("last")).trim();
	
			    			}
			    			if(!StringUtils.isEmpty(legislatorCongressGT.getName().getString("suffix"))){
			    				fullName = fullName.concat(" ").concat(legislatorCongressGT.getName().getString("suffix")).trim();
	
			    			}
			    		}
			    	}
			    	
		
			    	
			    	user.setDisplayName(fullName.trim());
			    	userRepository.insert(user);
			    	
			    	//upDefault / upCongressLegislatorExternal profileData
			    	profileDataObj = new JSONObject();
			    	if(!StringUtils.isEmpty(legislatorCongressGT.getName()) && !StringUtils.isEmpty(legislatorCongressGT.getName().getString("first")))
			    		profileDataObj.put("first_name", legislatorCongressGT.getName().getString("first"));
			    	
			    	if(!StringUtils.isEmpty(legislatorCongressGT.getName()) && !StringUtils.isEmpty(legislatorCongressGT.getName().getString("last")))
			    		profileDataObj.put("last_name", legislatorCongressGT.getName().getString("last"));
			    	
			    	
			    	if(!StringUtils.isEmpty(legislatorCongressGT.getBio())){
			    		if(!StringUtils.isEmpty(legislatorCongressGT.getBio().getString("birthday"))){
			    			profileDataObj.put("birthDate", DataConvertor.convertString2Date(legislatorCongressGT.getBio().getString("birthday")));
			    		}
			    		
			    		if(!StringUtils.isEmpty(legislatorCongressGT.getBio().getString("gender"))){
			    			if(legislatorCongressGT.getBio().getString("gender").equalsIgnoreCase("M"))
			    				profileDataObj.put("gender", "Male");
			    			else if(legislatorCongressGT.getBio().getString("gender").equalsIgnoreCase("F"))
			    				profileDataObj.put("gender", "Female");
			    			else
			    				profileDataObj.put("gender", legislatorCongressGT.getBio().getString("gender"));
			    		}
			    	}
			    	
			    	profileData = new ProfileData();
			    	profileDataDBObj = gson.fromJson(profileDataObj.toString(), BasicDBObject.class);
			    	profileData.setEntityId(sourceId);
			    	//profileData.setEntityType(latestProfileData.getEntityType());
			    	profileData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_BIODATA);
			    	profileData.setData(profileDataDBObj);
			    	profileDataRepository.insert(profileData);
			    	
			    	
		    	}else {//IF USER ALREADY EXIST, CHECK FOR LATEST ROLE AND ACCORDINGLY UPDATE THE USER WITH USERTYPE AND LATEST ROLE FOR DISPLAY
		    		latestProfileData = null;
		    		List<ProfileData> profileDatas = profileDataRepository.findByEntityIdAndProfileTemplateId(sourceId, SystemConstants.PROFILE_TEMPLATE_ROLE);
		    		for(ProfileData pd : profileDatas) {
		    			roleData = pd.getData();

		    			//finding the latest role
			    		if(latestProfileData == null)
				    		latestProfileData = pd;
				    	else {//finding latest role
				    		latestStartDate = null;
							latestEndDate = null;
							startDate = null;
							endDate = null;
							latestRoleData = latestProfileData.getData();
							
				    		if(roleData != null && roleData.containsField("start_date"))
				    			startDate = DataConvertor.convertString2Date(roleData.getString("start_date"));
					    	
					    	if(roleData != null && roleData.containsField("end_date"))
					    		endDate = DataConvertor.convertString2Date(roleData.getString("end_date"));
					    	
				    		if(latestRoleData != null && latestRoleData.containsField("start_date"))
				    			latestStartDate = DataConvertor.convertString2Date(latestRoleData.getString("start_date"));
					    	
					    	if(latestRoleData != null && latestRoleData.containsField("end_date"))
					    		latestEndDate = DataConvertor.convertString2Date(latestRoleData.getString("end_date"));
					    	
					    	//either start date should be greater than the previous role start date OR
					    	//end date should be greater than the previous role end date
					    	if((startDate != null && latestStartDate != null && startDate.compareTo(latestStartDate) > 0 ) ||
					    	   (endDate != null && latestEndDate != null && endDate.compareTo(latestEndDate) > 0 )) {
					    		latestProfileData = pd;
					    	}
				    	}
		    		}
		    		
		    		if(latestProfileData != null) {
		    			user.setUserType(latestProfileData.getEntityType());
		    			//user.setRole(latestProfileData.getData());
		    			userRepository.save(user);
		    		}
			    	
		    	}
		    	

		    	
		    	
	    	}catch(Exception e){
	    		logger.error("Error Processing record " + legislators.getInt(i) + e);
	            System.out.println("Error Processing record " + legislators.getInt(i) + e);
	    	}
	    }
	    
        System.out.format("Successfully processed Found %d records\n", legislators.size());
	}
	
	public void loadStateLegislatorsToDb(String fileLocation) throws Exception{
		System.out.println("Processing STARTED");
		logger.info("Processing STARTED");
	    Instant start = Instant.now();

		File folder = new File(fileLocation);//new ClassPathResource(fileLocation).getFile();
		//processFolder(folder);
		processOSFolderV2(folder);
	    Instant finish = Instant.now();
	    long timeElapsed = Duration.between(start, finish).toMillis();  //in millis
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeElapsed);
        System.out.format("%d Milliseconds = %d minutes\n", timeElapsed, minutes );

		System.out.println("Processing COMPLETED");
		logger.info("Processing COMPLETED");

/*		File[] listOfFiles = folder.listFiles();
		InputStream fis = null;
		for (File file : listOfFiles) {
		    if (file.isFile()) {
				fis = new FileInputStream(file);
				JsonReader reader = Json.createReader(fis);
		    	JsonObject legislatorObj = reader.readObject();	 
				reader.close();
		    	
		    	loadStateLegislatorToDb(legislatorObj);
		    }
		}
*/	
	}
	
	private void processFolder(File folder) throws Exception{
		//System.out.println("Processing file/folder " + folder.getName());
		//logger.info("Processing file/folder " + folder.getName());
		//File[] listOfFiles = folder.listFiles();
		InputStream fis = null;

		    	if(!folder.isFile()){//FOLDER
		    		System.out.println("Processing Folder " + folder.getName());
		    		logger.info("Processing Folder " + folder.getName());
		    		
		    		if(folder.getName().equalsIgnoreCase("legislators")){
			    		File[] listOfStatesFiles = folder.listFiles();
			    		System.out.println("Legislators count " + listOfStatesFiles.length);
			    		logger.info("Legislators count " + listOfStatesFiles.length);
			    		for (File file : listOfStatesFiles) {
			    			//FILE
			    			try{
					    		System.out.println("Processing File " + file.getName());
					    		logger.info("Processing File " + file.getName());
					    		
								fis = new FileInputStream(file);
								JsonReader reader = Json.createReader(fis);
						    	JsonObject legislatorObj = reader.readObject();	 
								reader.close();
						    	
						    	loadStateLegislatorToDb(legislatorObj);
			    			}catch(Exception e){
					    		System.out.println("Error in Processing File " + file.getName());
					    		logger.info("Error in Processing File " + file.getName());
			    			}
					    }
		    		}else{
		    			File[] listOfFiles = folder.listFiles();
		    			for(File file:listOfFiles){
		    				processFolder(file);
		    			}
		    		}
		    	}
		    
		
	
	}
	
	/*
	 * private void processOSFolderV2(File folder) throws Exception{
	 * 
	 * if(!folder.isFile()){//FOLDER System.out.println("Processing Folder " +
	 * folder.getName()); logger.info("Processing Folder " + folder.getName());
	 * 
	 * File[] listOfStatesFiles = folder.listFiles();
	 * System.out.println("States count " + listOfStatesFiles.length);
	 * logger.info("States count " + listOfStatesFiles.length); for (File file :
	 * listOfStatesFiles) { processOSNestedFolder(file);
	 * 
	 * }
	 * 
	 * 
	 * }
	 * 
	 * 
	 * 
	 * }
	 */
	
	private void processOSFolderV2(File fileP) throws Exception{
		//System.out.println("Processing file/folder " + folder.getName());
		//logger.info("Processing file/folder " + folder.getName());
		//File[] listOfFiles = folder.listFiles();
		//InputStream fis = null;
		    	if(!fileP.isFile()){//FOLDER
		    		System.out.println("Processing Folder " + fileP.getName());
		    		logger.info("Processing Folder " + fileP.getName());
		    		
		    		/*
		    		if(fileP.getName().equalsIgnoreCase("executive") ||
		    		   fileP.getName().equalsIgnoreCase("legislature") ||
		    		   fileP.getName().equalsIgnoreCase("municipalities") ||
		    		   fileP.getName().equalsIgnoreCase("retired")){
			    		File[] listOfPersonFiles = fileP.listFiles();
			    		System.out.println("Legislators count " + listOfPersonFiles.length);
			    		logger.info("Legislators count " + listOfPersonFiles.length);
			    		for (File file : listOfPersonFiles) {
			    			//processOSPersonFile(file);
			    			processOSFolderV2(file);
					    }
		    		}else { 
		    			File[] listOfFolders = fileP.listFiles(); // DATA -> EACH STATE
		    			//if(fileP.getName().length() == 2) {}
			    		for (File file : listOfFolders) {
			    			processOSFolderV2(file);
			    		}

		    		}
		    		*/
		    		
		    		File[] listOfFolders = fileP.listFiles(); // DATA -> EACH STATE -> executive | legislature | municipalities | retired
	    			//if(fileP.getName().length() == 2) {}
		    		for (File file : listOfFolders) {
		    			if(file.getName().length() == 2) {
		    				state = file.getName(); 
		    				
		    			}
		    			
		    			if(file.getName().equalsIgnoreCase(SystemConstants.USERCATEGRORY_EXECUTIVE) ||
		 		    	   file.getName().equalsIgnoreCase(SystemConstants.USERCATEGRORY_LEGISLATURE) ||
		 		    	   file.getName().equalsIgnoreCase(SystemConstants.USERCATEGRORY_MUNICIPALITIES) ||
		 		    	   file.getName().equalsIgnoreCase(SystemConstants.USERCATEGRORY_RETIRED)){
		    				userCategory = file.getName();
		    			}
		    			
		    			processOSFolderV2(file);
		    		}
		    	}else {//FILE
		    		if(fileP.getName().equalsIgnoreCase("municipalities.yml")) {
		    			//processOSMunicipalitiesFile(fileP, state);
		    		}else {
		    			processOSPersonFile(fileP, state);
		    		}
		    	}
		    
		
	
	}
	
	private void processOSPersonFile(File file, String state) throws Exception{
		InputStream fis = null;

		try{
    		System.out.println("Processing File " + file.getName());
    		logger.info("Processing File " + file.getName());
    		
    		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    		mapper.findAndRegisterModules();
    		LegislatorOpenStateV2 person = mapper.readValue(file, LegislatorOpenStateV2.class);
    		//person.setState(state);
    		
	    	loadOpenStateDataToDb(person);
		}catch(Exception e){
    		System.out.println("Error in Processing File " + file.getName());
    		logger.info("Error in Processing File " + file.getName());
		}	
	}

	//OBSOLETE
	public void loadStateLegislatorToDb(JsonObject legislatorObj) throws Exception{
		
	    Gson gson = new Gson();
	    //for (int i = 0; i < legislators.size(); i++) {
	    //	JsonObject legislatorObj = legislators.getJsonObject(i);	 
		    LegislatorOpenState result = gson.fromJson(legislatorObj.toString(), LegislatorOpenState.class);
		    legislatorOpenStateRepository.insert(result);
	    	//String sourceId = result.getId().getString("govtrack");
	    	String sourceId = result.getLegId();
	    	String fullName = null;		

	    	sourceId = legislatorObj.getString("leg_id");
	    	//sourceId = sourceId.substring(0,sourceId.indexOf('.'));

	    	User user = new User();
	    	BasicDBObject settings = new BasicDBObject();
    	
	    	//user.setUserId(sourceId);
	    	user.setUsername(sourceId);
	    	//user.setUsername(result.getName().getString("first") + "." + result.getName().getString("last"));
	    	user.setSourceId(sourceId);
	    	user.setUserType(SystemConstants.USERTYPE_LEGIS);
	    	user.setSourceSystem(SystemConstants.OPENSTATE_LEGIS_SOURCE);
	    	user.setStatus(SystemConstants.PASSIVE_USER);
	    	settings.put("accessRestriction", false);
	    	user.setSettings(settings);
	    	user.setPhotoUrl(result.getPhoto_url());
	    	//user.setFirstName(result.getFull_name());
	    	//user.setLastName(result.getLast_name());
	    	//user.setDisplayName(result.getFull_name() + " " + result.getLast_name());
	    	if(!StringUtils.isEmpty(result.getFull_name())){
	    		fullName = result.getFull_name().replaceAll(",", "");
		    	user.setDisplayName(fullName.trim());
	    	}
	    	userRepository.insert(user);
	    	
	    	//upCongressLegislatorExternal profileData
	    	JSONObject profileDataObj = new JSONObject();
	    	profileDataObj.put("leg_id", sourceId);
	    	profileDataObj.put("first_name", result.getFirst_name());
	    	profileDataObj.put("last_name", result.getLast_name());
	    	profileDataObj.put("full_name", result.getFull_name());
	    	profileDataObj.put("district", result.getDistrict());
	    	profileDataObj.put("party", result.getParty());
	    	profileDataObj.put("chamber", result.getChamber());
	    	profileDataObj.put("state", result.getState());

	    	ProfileData profileData = new ProfileData();
	    	BasicDBObject profileDataDBObj = gson.fromJson(profileDataObj.toString(), BasicDBObject.class);
	    	profileData.setEntityId(user.getUsername());
	    	profileData.setEntityType(SystemConstants.USERTYPE_LEGIS);
	    	profileData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_BIODATA);
	    	profileData.setData(profileDataDBObj);
	    	profileDataRepository.insert(profileData);
	    	
	    	//upRole profileData
	    	for (int j = 0; j < result.getRoles().size(); j++) {
	    		profileDataDBObj = result.getRoles().get(j);	

	    		//profileDataObj = gson.fromJson(legislatorRoleObj.toString(), BasicDBObject.class);
		    	
		    	profileData = new ProfileData();
		    	profileData.setEntityId(user.getUsername());
		    	profileData.setEntityType(SystemConstants.USERTYPE_LEGIS);
		    	profileData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_ROLE);
		    	profileData.setData(profileDataDBObj);
		    		
		    	profileDataRepository.insert(profileData);	    	
	    	}
	    	
	    	//upOffices profileData
	    	for (int j = 0; j < result.getOffices().size(); j++) {
	    		profileDataDBObj = result.getOffices().get(j);
		    	//profileDataObj = result.getOffices().get(j);		    	

		    	profileData = new ProfileData();
		    	profileData.setEntityId(user.getUsername());
		    	profileData.setEntityType(SystemConstants.USERTYPE_LEGIS);
		    	profileData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_OFFICE);
		    	profileData.setData(profileDataDBObj);
		    		
		    	
		    	profileDataRepository.insert(profileData);	    	
	    	}
	    //}
	}
	
	public void loadOpenStateDataToDb(LegislatorOpenStateV2 los2) throws Exception{
		
			Gson gson = new Gson();
			Date startDate;
			Date endDate;
			Boolean isLatestRoleData = false;
			String role = null;
			String district = null;
			String party = null;
			JSONObject profileDataObj;
			BasicDBObject losBio = null;
			BasicDBObject losRole = null;
			BasicDBObject losOffice = null;
			ProfileData latestProfileData = null;
			ProfileData profileData = new ProfileData();
	    	User user = new User();

			try {
				legislatorOpenStateRepositoryV2.insert(los2);
			}catch(Exception e){
	    		System.out.println("Error in creating entry in legislatorOpenState " + e);
	    		logger.error("Error in creating entry in legislatorOpenState " + e);
			}
	    	
			//upRole profileData
			//to get role
			//1) current - if iscurrent is true
			//2) if no record from #1, pick first by desc order end_date
			if(los2.getRoles() != null) {
				for (int j = 0; j < los2.getRoles().size(); j++) {
		    		losRole = los2.getRoles().get(j);	
		    		losRole.append("state", state);
			    	if(los2.getParty() != null && los2.getParty().size() > 0) {
			    		BasicDBObject partyObj = los2.getParty().get(0);
			    		if(partyObj.containsField("name")) {
			    			party = partyObj.getString("name");
			    			losRole.append("party", party);
			    		}
			    	}
		    		//profileDataObj = gson.fromJson(legislatorRoleObj.toString(), BasicDBObject.class);
			    	
			    	profileData = new ProfileData();
					startDate = null;
					endDate = null;
					role = null;
					district = null;
					
			    	profileData.setEntityId(los2.getName());
			    	
			    	if(losRole.containsField("type")) {
			    		String userRole = null; 
			    		if(losRole.getString("type").equalsIgnoreCase("lower"))
			    			userRole = SystemConstants.USERTYPE_HOUSE_REP;
			    		else if(losRole.getString("type").equalsIgnoreCase("upper"))
			    			userRole = SystemConstants.USERTYPE_SENATOR;
			    		else
			    			userRole = losRole.getString("type");
			    		profileData.setEntityType(userRole);
			    	}else {//can this be LEGISLATOR ?
			    		profileData.setEntityType(SystemConstants.USERTYPE_LEGIS);
			    	}
			    	
			    	profileData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_ROLE);
			    	profileData.setData(losRole);
			    	if(losRole.containsField("start_date"))
			    		startDate = DataConvertor.convertString2Date(losRole.getString("start_date"));
			    	
			    	if(losRole.containsField("end_date"))
			    		endDate = DataConvertor.convertString2Date(losRole.getString("end_date"));

			    	
			    	//current role
			    	if((startDate != null && startDate.compareTo(new Date()) <= 0 && (endDate == null || endDate.compareTo(new Date()) >= 0)) || //start date <= current date and and end date >= current date if exist
			    	   (startDate == null && (endDate != null && endDate.compareTo(new Date()) >= 0 ))){//contains only end date and >= current date
				    	profileData.setCurrent(true);
			    	}

			    	if(latestProfileData == null)
			    		latestProfileData = profileData;
			    	else {//finding latest role
						Date latestStartDate = null;
						Date latestEndDate = null;
						BasicDBObject latestRoleData = latestProfileData.getData();
			    		if(latestRoleData.containsField("start_date"))
			    			latestStartDate = DataConvertor.convertString2Date(latestRoleData.getString("start_date"));
				    	
				    	if(latestRoleData.containsField("end_date"))
				    		latestEndDate = DataConvertor.convertString2Date(latestRoleData.getString("end_date"));
				    	
				    	//either start date should be greater than the previous role start date OR
				    	//end date should be greater than the previous role end date
				    	if((startDate != null && latestStartDate != null && startDate.compareTo(latestStartDate) > 0 ) ||
				    	   (endDate != null && latestEndDate != null && endDate.compareTo(latestEndDate) > 0 )) {
				    		latestProfileData = profileData;
				    	}
				    	
			    	}

			    	profileDataRepository.insert(profileData);	    	
				}
				
				if(latestProfileData != null && latestProfileData.getData() != null && latestProfileData.getData().containsField("type"))
			    	user.setUserType(latestProfileData.getEntityType());
				else
					user.setUserType(SystemConstants.USERTYPE_LEGIS);
				
			}
			


			//USER object
	    	String sourceId = los2.getId();
	    	BasicDBObject settings = new BasicDBObject();
    	
	    	user.setUsername(los2.getName());
	    	user.setSourceId(sourceId);
	    	user.setCategory(userCategory);
	    	user.setSourceSystem(SystemConstants.OPENSTATE_LEGIS_SOURCE);
	    	user.setStatus(SystemConstants.PASSIVE_USER);
	    	settings.put("accessRestriction", false);
	    	user.setSettings(settings);
	    	user.setPhotoUrl(los2.getImage());
	    	user.setDisplayName(los2.getName());
	    	
			try {
		    	userRepository.insert(user);
			}catch(Exception e){
	    		System.out.println("Error in creating entry in User " + e);
	    		logger.error("Error in creating entry in User " + e);
			}
	    	
	    	
	    	//upOffices profileData
			if(los2.getContact_details() != null) {
		    	for (int j = 0; j < los2.getContact_details().size(); j++) {
		    		losOffice = los2.getContact_details().get(j);
			    	
			    	//rename voice to phone
			    	if(losOffice.containsField("voice")) {
			    		losOffice.append("phone", losOffice.getString("voice"));
			    		losOffice.removeField("voice");
			    	}
			    	
			    	//rename note to name
			    	if(losOffice.containsField("note")) {
			    		losOffice.append("name", losOffice.getString("note"));
			    		losOffice.removeField("note");
			    	}
	
			    	profileData = new ProfileData();
			    	profileData.setEntityId(user.getUsername());
			    	profileData.setEntityType(user.getUserType());
			    	profileData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_OFFICE);
			    	profileData.setData(losOffice);
			    		
			    	
			    	profileDataRepository.insert(profileData);	    	
		    	}
	    	}
	    	
	    	//upCongressLegislatorExternal profileData - BIODATA
	    	profileDataObj = new JSONObject();
	    	profileDataObj.put("firstName", los2.getGiven_name());
	    	profileDataObj.put("lastName", los2.getFamily_name());
	    	profileDataObj.put("emailId", los2.getEmail());
	    	profileDataObj.put("gender", los2.getGender());
	    	if(los2.getBirth_date() != null)
		    	profileDataObj.put("birthDate", DataConvertor.convertString2Date(los2.getBirth_date()));
	    	
	    	profileData = new ProfileData();
	    	losBio = gson.fromJson(profileDataObj.toString(), BasicDBObject.class);
	    	profileData.setEntityId(user.getUsername());
	    	profileData.setEntityType(user.getUserType());
	    	profileData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_BIODATA);
	    	profileData.setData(losBio);
	    	profileDataRepository.insert(profileData);
	    	
	    	
	    //}
	}
	
	public void processCongressCommitteeFile(File file) throws Exception{

		try{
    		System.out.println("Processing File " + file.getName());
    		logger.info("Processing File " + file.getName());
    		
    		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    		mapper.findAndRegisterModules();
    		CommitteeCongressGT[] committees = mapper.readValue(file, CommitteeCongressGT[].class);
    		for(CommitteeCongressGT committee : committees) {
    			try {
    				loadCongressCommitteeDataToDb(committee, false);
	    		}catch(Exception e){
	        		System.out.println("Error in Processing committee " + committee);
	        		logger.info("Error in Processing committee " + committee);
	    		}	
    		}
		}catch(Exception e){
    		System.out.println("Error in Processing File " + file.getName());
    		logger.info("Error in Processing File " + file.getName());
		}	
	}
	
	private void loadCongressCommitteeDataToDb(CommitteeCongressGT committee, boolean isSubCommittee) throws Exception{
		if(!isSubCommittee) {
			try {
				congressCommitteeRepository.insert(committee);
			}catch(Exception e){
	    		System.out.println("Error in creating entry in CongressCommittee " + e);
	    		logger.error("Error in creating entry in CongressCommittee " + e);
			}	
		}
	
		//USER object
		User user = new User();
		String sourceId = committee.getThomas_id();
		if(isSubCommittee) {
			sourceId = committee.getParent_committee_id() + sourceId; 
			user.setParentId(committee.getParent_committee_id());
		}
		BasicDBObject settings = new BasicDBObject();
		settings.put("accessRestriction", false);
		user.setSettings(settings);
	
		user.setUsername(sourceId);
		user.setSourceId(sourceId);
		user.setFull_name(committee.getName());
		user.setSourceSystem(SystemConstants.GOVTRACK_LEGIS_SOURCE);
		
		user.setCategory(SystemConstants.USERCATEGRORY_COMMITTEE_LEGISLATIVE);
		if(isSubCommittee) {
			//user.setCategory(SystemConstants.USERCATEGRORY_SUB_COMMITTEE_CONGRESS);
			user.setUserType(SystemConstants.USERCATEGRORY_SUBCOMMITTEE_LEGISLATIVE);
		}else{
			//user.setCategory(SystemConstants.USERCATEGRORY_COMMITTEE_CONGRESS);

			if(committee.getType() != null && committee.getType().equalsIgnoreCase("HOUSE")) {
				user.setUserType(SystemConstants.USERTYPE_COMMITTEE_CONGRESS_HOUSE);	
			}else if(committee.getType() != null && committee.getType().equalsIgnoreCase("SENATE")) {
				user.setUserType(SystemConstants.USERTYPE_COMMITTEE_CONGRESS_SENATE);
			}else if(committee.getType() != null && committee.getType().equalsIgnoreCase("JOINT")) {
				user.setUserType(SystemConstants.USERTYPE_COMMITTEE_CONGRESS_JOINT);
			}else {
				user.setUserType(SystemConstants.USERCATEGRORY_COMMITTEE_LEGISLATIVE);
			}
		}
		
	
		user.setStatus(SystemConstants.PASSIVE_USER);
		
		try {
	    	userRepository.insert(user);
		}catch(Exception e){
			System.out.println("Error in creating entry in User " + e);
			logger.error("Error in creating entry in User " + e);
		}
	
		//upDefault profileData - BIODATA
		JSONObject profileDataObj = new JSONObject();
		if(!isSubCommittee) {
	    	profileDataObj.put("url", committee.getUrl());
	    	if(committee.getType() != null && committee.getType().equalsIgnoreCase("HOUSE")) {
	        	profileDataObj.put("committee_id", committee.getHouse_committee_id());
			}else if(committee.getType() != null && committee.getType().equalsIgnoreCase("SENATE")) {
		    	profileDataObj.put("committee_id", committee.getSenate_committee_id());
			}
	    	profileDataObj.put("wikipedia", committee.getWikipedia());
	    	profileDataObj.put("jurisdiction", committee.getJurisdiction());
	    	profileDataObj.put("jurisdiction_source", committee.getJurisdiction_source());
	    	profileDataObj.put("rss_url", committee.getRss_url());
	    	profileDataObj.put("minority_rss_url", committee.getMinority_rss_url());
		}
    	profileDataObj.put("address", committee.getAddress());
    	profileDataObj.put("phone", committee.getPhone());
    	
    	ProfileData profileData = new ProfileData();
	    Gson gson = new Gson();
    	BasicDBObject profileDataDBObj = gson.fromJson(profileDataObj.toString(), BasicDBObject.class);
    	profileData.setEntityId(user.getUsername());
    	profileData.setEntityType(user.getUserType());
    	profileData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_BIODATA);
    	profileData.setData(profileDataDBObj);
    	profileDataRepository.insert(profileData);
    	
    	//process Sub Committees
		if(!isSubCommittee && committee.getSubcommittees() != null && committee.getSubcommittees().size() > 0) {
	    	for (BasicDBObject subCommittee : committee.getSubcommittees()) {
	    		CommitteeCongressGT subCommitteeGT = gson.fromJson(subCommittee.toString(), CommitteeCongressGT.class);
	    		subCommitteeGT.setParent_committee_id(sourceId);
	    		subCommitteeGT.setParent_committee_type(user.getUserType());
	    		loadCongressCommitteeDataToDb(subCommitteeGT, true);
	    	}
		}
	}
	
	public void processCongressCommitteeMembershipFile(File file) throws Exception{

		try{
    		System.out.println("Processing File " + file.getName());
    		logger.info("Processing File " + file.getName());
    		
    		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    		mapper.findAndRegisterModules();
    		Map<String, List<Object>> congressCommitteeMembership = mapper.readValue(file, Map.class);
    		
    		loadCongressCommitteeMembershipDataToDb(congressCommitteeMembership);
		}catch(Exception e){
    		System.out.println("Error in Processing File " + file.getName());
    		logger.info("Error in Processing File " + file.getName());
		}	
	}
	
	private void loadCongressCommitteeMembershipDataToDb(Map congressCommitteeMembership) throws Exception{
			ProfileData memberData = null;
			String committeeId = null;
			int recordsCount = 0;
			try {
				//congressCommitteeMembershipRepository.insert(congressCommitteeMembership);
				Set<String> keyset = congressCommitteeMembership.keySet();
			    Iterator<String> keys = keyset.iterator();

			    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	    		mapper.findAndRegisterModules();
	    		
	    		
				while(keys.hasNext()){ 
					committeeId = keys.next();
					//CommitteeCongressGT committeeCongressGT = congressCommitteeRepository.findByThomasId(committeeId);
					User user = userRepository.findByUsername(committeeId);
					if(user != null) {
						
						  ArrayList<LinkedHashMap> members = (ArrayList<LinkedHashMap>) congressCommitteeMembership.get(committeeId);
						  if(members != null) {
							  recordsCount = recordsCount + members.size();
							  for(Object memberJson : members) { 
								  memberData = new ProfileData(); 
								  //Gson gson = new Gson(); 
								  //BasicDBObject profileDataDBObj = (BasicDBObject)memberJson;
								  //gson. .fromJson(memberJson.toString(), BasicDBObject.class);
								  BasicDBObject profileDataDBObj  = mapper.convertValue(memberJson, BasicDBObject.class);

						    		
								  memberData.setEntityId(committeeId);
								  memberData.setEntityType(user.getUserType());
								  memberData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_MEMBER);
								  memberData.setData(profileDataDBObj);
								  profileDataRepository.insert(memberData); 
							  }
						  }
						 
					}else {
						//COMMITTEE NOT FOUND
					}
					
				}
				
		        System.out.format("Processed %d records\n", recordsCount);
				
			}catch(Exception e){
	    		System.out.println("Error in creating entry in CommitteeMembership " + e);
	    		logger.error("Error in creating entry in CommitteeMembership " + e);
			}	

		
	}
	
	public void processCongressDistrictOfficeFile(File file) throws Exception{

		try{
    		System.out.println("Processing File " + file.getName());
    		logger.info("Processing File " + file.getName());
    		
    		JsonReader reader = Json.createReader(new FileInputStream(file));
    		JsonArray congressDistrictOffices = reader.readArray();	 
			reader.close();
			
			/*
			 * ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			 * mapper.findAndRegisterModules(); JSONArray congressDistrictOffices =
			 * mapper.readValue(file, JSONArray.class);
			 */
    		
    		loadCongressDistrictOfficeDataToDb(congressDistrictOffices);
		}catch(Exception e){
    		System.out.println("Error in Processing File " + file.getName());
    		logger.info("Error in Processing File " + file.getName());
		}	
	}
	
	private void loadCongressDistrictOfficeDataToDb(JsonArray congressDistrictOffices) throws Exception{
		ProfileData officeData = null;
		User user = null;
		BasicDBObject data = null;
		String entityId = null;
		try {
			//congressDistrictOfficesRepository.insert(congressDistrictOffices);
		    //Gson gson = new Gson();
		    for (int i = 0; i < congressDistrictOffices.size(); i++) {

			    //JSONObject districtOfficeJson = gson.fromJson(districtOfficeObj.toString(), JSONObject.class);
				JsonObject districtOfficeJson = congressDistrictOffices.getJsonObject(i);
				DBObject dbObject = (DBObject) com.mongodb.util.JSON.parse(districtOfficeJson.toString());
				congressDistrictOfficesRepository.insert(dbObject);
				JsonObject idJson = districtOfficeJson.getJsonObject("id");
			    if(idJson != null && idJson.containsKey("bioguide")) {
			    	entityId = idJson.getString("bioguide");
					user = userRepository.findByUsername(entityId);
		    		System.out.println("Processing User bioguide " + entityId);
			    	
			    }
				JsonArray offices = districtOfficeJson.getJsonArray("offices");

				for (int j = 0; j < offices.size(); j++) {

				    //JSONObject officeJson = gson.fromJson(officeObj.toString(), JSONObject.class);
					//JSONObject officeJson = (JSONObject)officeObj;
					JsonObject officeJson = offices.getJsonObject(j);
		    		System.out.println("Processing office index " + j);

					if(officeJson != null) {
						officeData = new ProfileData();
				    	officeData.setEntityId(entityId);
				    	officeData.setEntityType(user.getUserType());
				    	officeData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_OFFICE);
						
				    	data = new BasicDBObject();
				    	if(officeJson.containsKey("building"))
				    		data.append("name", officeJson.get("building"));
				    	
				    	StringBuffer addressSB = new StringBuffer();
				    	if(officeJson.containsKey("address"))
				    		addressSB.append(officeJson.getString("address"));
				    	if(officeJson.containsKey("suite"))
				    		addressSB.append(",").append(officeJson.get("suite"));
				    	if(officeJson.containsKey("city"))
				    		addressSB.append(",").append(officeJson.getString("city"));
				    	if(officeJson.containsKey("state"))
				    		addressSB.append(",").append(officeJson.getString("state"));
				    	if(officeJson.containsKey("zip"))
				    		addressSB.append(",").append(officeJson.getString("zip"));
				    	data.append("address", addressSB.toString());
				    	
				    	if(officeJson.containsKey("email"))
				    		data.append("email", officeJson.getString("email"));
				    	if(officeJson.containsKey("phone"))
				    		data.append("phone", officeJson.getString("phone"));
				    	if(officeJson.containsKey("fax"))
				    		data.append("fax", officeJson.getString("fax"));
				    	if(officeJson.containsKey("hours"))
				    		data.append("hours", officeJson.getString("hours"));
				    	if(officeJson.containsKey("latitude"))
				    		data.append("latitude", officeJson.get("latitude").toString());
				    	if(officeJson.containsKey("longitude"))
				    		data.append("longitude", officeJson.get("longitude").toString());
				    	
				    	//BasicDBObject profileDataDBObj = gson.fromJson(officeData.toString(), BasicDBObject.class);
				    	officeData.setData(data);
				    	profileDataRepository.insert(officeData);
					}
				}
				
			}
			
	        System.out.format("Processed %d records\n", congressDistrictOffices.size());

			
		}catch(Exception e){
    		System.out.println("Error in creating entry for congressDistrictOffices " + e);
    		logger.error("Error in creating entry for congressDistrictOffices " + e);
		}	

	
	}
}
