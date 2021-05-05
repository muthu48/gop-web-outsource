package com.jpw.springboot.service;

import java.io.File;

import javax.json.JsonObject;

import com.jpw.springboot.model.LegislatorOpenStateV2;

public interface LegislatorDataProcessingService {
	public void loadCongressLegislatorsToDb(File file, String userType) throws Exception;
	public void loadCongressMembersToDb(File file, String userType) throws Exception;

	public void processCongressCommitteeFile(File file) throws Exception;
	
	public void processCongressCommitteeMembershipFile(File file) throws Exception;
	
	public void processCongressDistrictOfficeFile(File file) throws Exception;
	
	public void loadStateLegislatorsToDb(String fileLocation) throws Exception;

	public void loadStateLegislatorToDb(JsonObject legislatorObj) throws Exception;	
	
	public void loadOpenStateDataToDb(LegislatorOpenStateV2 result) throws Exception;
}
