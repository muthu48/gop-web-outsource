package com.jpw.springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpw.springboot.model.ProfileTemplate;
import com.jpw.springboot.repositories.ProfileTemplateRepository;

@Service("profileTemplateService")
@Transactional
public class ProfileTemplateServiceImpl implements ProfileTemplateService {

	@Autowired
	private ProfileTemplateRepository profileTemplateRepository;

	public ProfileTemplate findById(String id) {
		return null;//profileTemplateRepository.findOne(id);
	}

	public ProfileTemplate findByName(String name) {
		return profileTemplateRepository.findByName(name);
	}

	public void saveProfileTemplate(ProfileTemplate user) {
		profileTemplateRepository.save(user);
	}
	
	public ProfileTemplate createProfileTemplate(ProfileTemplate pTemplate) {
		return profileTemplateRepository.insert(pTemplate);
	}
	
	public ProfileTemplate updateProfileTemplate(ProfileTemplate pTemplate) {
		return profileTemplateRepository.save(pTemplate);
	}

	public void deleteProfileTemplateById(String id) {
		//profileTemplateRepository.delete(id);
	}

	public void deleteAllProfileTemplates() {
		profileTemplateRepository.deleteAll();
	}

	public List<ProfileTemplate> findAllProfileTemplates() {
		return profileTemplateRepository.findAll();
	}

	public List<ProfileTemplate> findByProfileTemplateId(String profileTemplateId){
		return profileTemplateRepository.findByProfileTemplateId(profileTemplateId);
	}
	
	public List<ProfileTemplate> findByProfileTemplateId(String profileTemplateId, String type){
		return profileTemplateRepository.findByProfileTemplateId(profileTemplateId, type);
	}
	
	public List<ProfileTemplate> findAllProfileTemplatesByIds(List<String> profileTemplateIds) {
		return profileTemplateRepository.findByProfileTemplateIdIn(profileTemplateIds);
	}
	
	public List<ProfileTemplate> findAllProfileTemplatesByIds(List<String> profileTemplateIds, String type) {
		return profileTemplateRepository.findByProfileTemplateIdInAndType(profileTemplateIds, type);
	}
	
	public List<ProfileTemplate> findAllByType(String type) {
		return profileTemplateRepository.findByType(type);
	}	
	
	public boolean isProfileTemplateExist(ProfileTemplate pTemplate) {
		return findByName(pTemplate.getName()) != null;
	}

}
