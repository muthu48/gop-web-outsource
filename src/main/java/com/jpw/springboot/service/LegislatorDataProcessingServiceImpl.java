package com.jpw.springboot.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;

import com.jpw.springboot.model.LegislatorCongressGT;
import com.jpw.springboot.model.LegislatorOpenState;
import com.jpw.springboot.model.ProfileData;
import com.jpw.springboot.model.User;
import com.jpw.springboot.repositories.LegislatorCongressGTRepository;
import com.jpw.springboot.repositories.LegislatorOpenStateRepository;
import com.jpw.springboot.repositories.ProfileDataRepository;
import com.jpw.springboot.repositories.UserRepository;
import com.jpw.springboot.util.SystemConstants;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@Service("legislatorDataProcessingService")
@Transactional
public class LegislatorDataProcessingServiceImpl implements LegislatorDataProcessingService {
	public static final Logger logger = LoggerFactory.getLogger(LegislatorDataProcessingServiceImpl.class);

	@Autowired
	private LegislatorCongressGTRepository legislatorCongressGTRepository;
	
	@Autowired
	private LegislatorOpenStateRepository legislatorOpenStateRepository;
	
	@Autowired
	private ProfileDataRepository profileDataRepository;
	
	@Autowired
	private UserRepository userRepository;
	
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
		    	
			    LegislatorCongressGT result = gson.fromJson(legislatorObj.toString(), LegislatorCongressGT.class);
		    	legislatorCongressGTRepository.insert(result);
		    	//String sourceId = result.getId().getString("govtrack");
		    	String sourceId = null;
		    	String sourceIdIntial = null;
		    	String photoUrl = null;
		    	if(!StringUtils.isEmpty(result.getId())){
			    	if(!StringUtils.isEmpty(result.getId().getString("bioguide"))){
				    	sourceId = result.getId().getString("bioguide"); //ex:A000370
				    	sourceIdIntial = sourceId.substring(0,1); //ex:A from A000370
				    	photoUrl = "http://bioguide.congress.gov/bioguide/photo/" + sourceIdIntial + "/" + sourceId + ".jpg";
			    	}else if(!StringUtils.isEmpty(result.getId().getString("govtrack"))){
				    	sourceId = String.valueOf(result.getId().getInt("govtrack")); 
			    	}else if(!StringUtils.isEmpty(result.getId().getString("opensecrets"))){
				    	sourceId = result.getId().getString("opensecrets"); 
			    	}else if(!StringUtils.isEmpty(result.getId().getString("votesmart"))){
				    	sourceId = String.valueOf(result.getId().getInt("votesmart")); 
			    	}else if(!StringUtils.isEmpty(result.getId().getString("thomas"))){
				    	sourceId = result.getId().getString("thomas"); 
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
		    	if(!StringUtils.isEmpty(result.getName())){
		    		if(!StringUtils.isEmpty(result.getName().getString("official_full"))){
		    			fullName = result.getName().getString("official_full").replaceAll(",", "");
		    		}else{
		    			if(!StringUtils.isEmpty(result.getName().getString("first"))){
		    				fullName = result.getName().getString("first").trim();
		    			}
		    			if(!StringUtils.isEmpty(result.getName().getString("middle"))){
		    				fullName = fullName.concat(" ").concat(result.getName().getString("middle")).trim();
		    			}
		    			if(!StringUtils.isEmpty(result.getName().getString("last"))){
		    				fullName = fullName.concat(" ").concat(result.getName().getString("last")).trim();

		    			}
		    			if(!StringUtils.isEmpty(result.getName().getString("suffix"))){
		    				fullName = fullName.concat(" ").concat(result.getName().getString("suffix")).trim();

		    			}
		    		}
		    	}
		    	user.setDisplayName(fullName.trim());
		    	userRepository.insert(user);
		    	
		    	//upCongressLegislatorExternal profileData
		    	JSONObject profileDataObj = new JSONObject();
		    	profileDataObj.put("leg_id", sourceId);
		    	profileDataObj.put("first_name", result.getName().getString("first"));
		    	profileDataObj.put("last_name", result.getName().getString("last"));
		    	profileDataObj.put("full_name", fullName);
		    	ProfileData profileData = new ProfileData();
		    	BasicDBObject profileDataDBObj = gson.fromJson(profileDataObj.toString(), BasicDBObject.class);
		    	profileData.setEntityId(user.getUsername());
		    	profileData.setEntityType(userType);
		    	profileData.setProfileTemplateId(SystemConstants.PROFILE_TEMPLATE_BIODATA);
		    	profileData.setData(profileDataDBObj);
		    	profileDataRepository.insert(profileData);
		    	
		    	//upRole/upOffices profileData
		    	for (int j = 0; j < result.getTerms().size(); j++) {
		    		BasicDBObject legislatorRoleObj = result.getTerms().get(j);	
	
			    	profileDataObj = new JSONObject();
			    	//finding the current role/office
			    	if(!StringUtils.isEmpty(legislatorRoleObj.getString("start"))){            
			    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			            Date startDate = sdf.parse(legislatorRoleObj.getString("start"));
			            Date endDate = sdf.parse(legislatorRoleObj.getString("end"));
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
	    		logger.error("Error Processing record " + e);
	            System.out.println("Error Processing record " + e);
	    	}
	    }
	    
        System.out.format("Successfully processed Found %d records\n", legislators.size());
	}
	
	public void loadStateLegislatorsToDb(String fileLocation) throws Exception{
		System.out.println("Processing STARTED");
		logger.info("Processing STARTED");
	    Instant start = Instant.now();

		File folder = new File(fileLocation);//new ClassPathResource(fileLocation).getFile();
		processFolder(folder);
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

}
