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

	boolean isProfileTemplateExist(ProfileTemplate profileTemplate);
}