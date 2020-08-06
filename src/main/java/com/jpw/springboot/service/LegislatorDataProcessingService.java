package com.jpw.springboot.service;

import java.io.File;

import javax.json.JsonObject;

public interface LegislatorDataProcessingService {
	public void loadCongressLegislatorsToDb(File file, String userType) throws Exception;
	
	public void loadStateLegislatorsToDb(String fileLocation) throws Exception;

	public void loadStateLegislatorToDb(JsonObject legislatorObj) throws Exception;	
}
