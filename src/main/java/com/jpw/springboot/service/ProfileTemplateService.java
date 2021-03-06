package com.jpw.springboot.service;

import java.util.List;

import com.jpw.springboot.model.ProfileTemplate;

public interface ProfileTemplateService {

	ProfileTemplate findById(String id);

	ProfileTemplate findByName(String name);

	void saveProfileTemplate(ProfileTemplate profileTemplate);
	
	ProfileTemplate createProfileTemplate(ProfileTemplate profileTemplate);
	
	ProfileTemplate updateProfileTemplate(ProfileTemplate profileTemplate);

	void deleteProfileTemplateById(String id);

	void deleteAllProfileTemplates();

	List<ProfileTemplate> findAllProfileTemplates();
	List<ProfileTemplate> findByProfileTemplateId(String profileTemplateId);
	List<ProfileTemplate> findByProfileTemplateIdAndCategory(String profileTemplateId, String type);
	List<ProfileTemplate> findAllProfileTemplatesByIds(List<String> profileTemplateIds);
	List<ProfileTemplate> findAllProfileTemplatesByIds(List<String> profileTemplateIds, String type);
	List<ProfileTemplate> findAllByCategory(String category);
	boolean isProfileTemplateExist(ProfileTemplate profileTemplate);
}