package com.jpw.springboot.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import com.jpw.springboot.model.LegislatorCongressGT;
import com.jpw.springboot.model.LegislatorOpenState;
import com.jpw.springboot.model.ProfileData;
import com.jpw.springboot.model.User;
import com.jpw.springboot.repositories.LegislatorCongressGTRepository;
import com.jpw.springboot.repositories.LegislatorOpenStateRepository;
import com.jpw.springboot.repositories.ProfileDataRepository;
import com.jpw.springboot.repositories.UserRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@Service("legislatorDataProcessingService")
@Transactional
public class LegislatorDataProcessingServiceImpl implements LegislatorDataProcessingService {
	@Autowired
	private LegislatorCongressGTRepository legislatorCongressGTRepository;
	
	@Autowired
	private LegislatorOpenStateRepository legislatorOpenStateRepository;
	
	@Autowired
	private ProfileDataRepository profileDataRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public void loadCongressLegislatorsToDb(File file) throws Exception{
		InputStream fis = null;
		fis = new FileInputStream(file);
		

		JsonReader reader = Json.createReader(fis);
		JsonObject legislatorsObj = reader.readObject();
		JsonArray legislators = (JsonArray)legislatorsObj.getJsonArray("data");
		reader.close();
		
	    Gson gson = new Gson();
	    for (int i = 0; i < legislators.size(); i++) {
	    	JsonObject legislatorObj = legislators.getJsonObject(i);	 
		    LegislatorCongressGT result = gson.fromJson(legislatorObj.toString(), LegislatorCongressGT.class);
	    	legislatorCongressGTRepository.insert(result);
	    	//String sourceId = result.getId().getString("govtrack");
	    	String sourceId = result.getId().getString("bioguide");
	    	//sourceId = sourceId.substring(0,sourceId.indexOf('.'));

	    	User user = new User();
	    	user.setUserId(sourceId);
	    	user.setUsername(sourceId);
	    	//user.setUsername(result.getName().getString("first") + "." + result.getName().getString("last"));
	    	user.setUserType("LEGISLATOR");
	    	user.setSourceId(sourceId);
	    	user.setSourceSystem("GOVTRACK");
	    	user.setStatus("PASSIVE");
	    	userRepository.insert(user);
	    	
	    	//upCongressLegislatorExternal profileData
	    	JSONObject profileDataObj = new JSONObject();
	    	profileDataObj.put("leg_id", sourceId);
	    	profileDataObj.put("first_name", result.getName().getString("first"));
	    	profileDataObj.put("last_name", result.getName().getString("last"));
	    	profileDataObj.put("full_name", result.getName().getString("official_full"));
	    	ProfileData profileData = new ProfileData();
	    	BasicDBObject profileDataDBObj = gson.fromJson(profileDataObj.toString(), BasicDBObject.class);
	    	profileData.setEntityId(user.getUsername());
	    	profileData.setEntityType("LEGISLATORCONGRESS");
	    	profileData.setProfileTemplateId("upCongressLegislatorExternal");
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
			    	profileData.setEntityType("LEGISLATORCONGRESS");
			    	profileData.setProfileTemplateId("upOffices");
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
			    	profileData.setEntityType("LEGISLATORCONGRESS");
			    	profileData.setProfileTemplateId("upRole");
			    	if(profileDataObj.has("isCurrent"))
			    		profileData.setCurrent(profileDataObj.getBoolean("isCurrent"));			    	
			    	
			    	profileData.setData(profileDataDBObj);
		    		
		    	}
		    	profileDataRepository.insert(profileData);	    	
	    	}
	    }
	}
	
	public void loadStateLegislatorsToDb(String fileLocation) throws Exception{
		File folder = new ClassPathResource(fileLocation).getFile();
		File[] listOfFiles = folder.listFiles();
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
	}

	public void loadStateLegislatorToDb(JsonObject legislatorObj) throws Exception{
		
	    Gson gson = new Gson();
	    //for (int i = 0; i < legislators.size(); i++) {
	    //	JsonObject legislatorObj = legislators.getJsonObject(i);	 
		    LegislatorOpenState result = gson.fromJson(legislatorObj.toString(), LegislatorOpenState.class);
		    legislatorOpenStateRepository.insert(result);
	    	//String sourceId = result.getId().getString("govtrack");
	    	String sourceId = result.getLegId();
	    	sourceId = legislatorObj.getString("leg_id");
	    	//sourceId = sourceId.substring(0,sourceId.indexOf('.'));

	    	User user = new User();
	    	user.setUserId(sourceId);
	    	user.setUsername(sourceId);
	    	//user.setUsername(result.getName().getString("first") + "." + result.getName().getString("last"));
	    	user.setUserType("LEGISLATOR");
	    	user.setSourceId(sourceId);
	    	user.setSourceSystem("OPENSTATE");
	    	user.setStatus("PASSIVE");
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
	    	profileData.setEntityType("LEGISLATOROPENSTATE");
	    	profileData.setProfileTemplateId("upCongressLegislatorExternal");
	    	profileData.setData(profileDataDBObj);
	    	profileDataRepository.insert(profileData);
	    	
	    	//upRole profileData
	    	for (int j = 0; j < result.getRoles().size(); j++) {
	    		profileDataDBObj = result.getRoles().get(j);	

	    		//profileDataObj = gson.fromJson(legislatorRoleObj.toString(), BasicDBObject.class);
		    	
		    	profileData = new ProfileData();
		    	profileData.setEntityId(user.getUsername());
		    	profileData.setEntityType("LEGISLATOROPENSTATE");
		    	profileData.setProfileTemplateId("upRole");
		    	profileData.setData(profileDataDBObj);
		    		
		    	profileDataRepository.insert(profileData);	    	
	    	}
	    	
	    	//upOffices profileData
	    	for (int j = 0; j < result.getOffices().size(); j++) {
	    		profileDataDBObj = result.getOffices().get(j);
		    	//profileDataObj = result.getOffices().get(j);		    	

		    	profileData = new ProfileData();
		    	profileData.setEntityId(user.getUsername());
		    	profileData.setEntityType("LEGISLATOROPENSTATE");
		    	profileData.setProfileTemplateId("upOffices");
		    	profileData.setData(profileDataDBObj);
		    		
		    	
		    	profileDataRepository.insert(profileData);	    	
	    	}
	    //}
	}

}
