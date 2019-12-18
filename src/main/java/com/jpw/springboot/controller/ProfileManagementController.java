package com.jpw.springboot.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.jpw.springboot.model.Post;
import com.jpw.springboot.model.ProfileData;
import com.jpw.springboot.model.ProfileTemplate;
import com.jpw.springboot.service.ProfileTemplateService;
import com.jpw.springboot.service.UserService;
//import com.jpw.springboot.util.CustomErrorType;

@RestController
@RequestMapping("/profile")
public class ProfileManagementController {

	public static final Logger logger = LoggerFactory.getLogger(ProfileManagementController.class);

	@Autowired
	ProfileTemplateService profileTemplateService;
	
	@Autowired
	UserService userService;
	
	@RequestMapping(value = "/template/getAllProfileTemplates", method = RequestMethod.GET)
	public ResponseEntity<List<ProfileTemplate>> listAllProfileTemplates() {
		List<ProfileTemplate> profiletemplate = profileTemplateService.findAllProfileTemplates();
		if (profiletemplate.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<ProfileTemplate>>(profiletemplate, HttpStatus.OK);
	}

	@RequestMapping(value = "/template/getProfileTemplate/{profileTemplateId}", method = RequestMethod.GET)
	public ResponseEntity<List<ProfileTemplate>> listProfileTemplate(@PathVariable("profileTemplateId") String profileTemplateId) {
		List<ProfileTemplate> profiletemplates = profileTemplateService.findByProfileTemplateId(profileTemplateId);
		if (profiletemplates.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<ProfileTemplate>>(profiletemplates, HttpStatus.OK);
	}	
	
	@RequestMapping(value = "/template/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getProfileTemplate(@PathVariable("id") String id) {
		logger.info("Fetching ProfileTemplate with id {}", id);
		ProfileTemplate profiletemplate = profileTemplateService.findById(id);
		/*if (profiletemplate == null) {
			logger.error("ProfileTemplate with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("ProfileTemplate with id " + id + " not found"),
					HttpStatus.NOT_FOUND);
		}*/
		return new ResponseEntity<ProfileTemplate>(profiletemplate, HttpStatus.OK);
	}
	
/*	@RequestMapping(value = "/data/{entityid}", method = RequestMethod.GET)
	public ResponseEntity<?> getProfileData(@PathVariable("id") String id) {
		logger.info("Fetching ProfileTemplate with id {}", id);
		ProfileTemplate profiletemplate = profileTemplateService.findById(id);
		if (profiletemplate == null) {
			logger.error("ProfileTemplate with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("ProfileTemplate with id " + id + " not found"),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<ProfileTemplate>(profiletemplate, HttpStatus.OK);
	}
*/	
	@RequestMapping(value = "/template", method = RequestMethod.POST)
	public ResponseEntity<?> createProfileTemplate(@RequestBody ProfileTemplate profiletemplate,
			UriComponentsBuilder ucBuilder) {
		logger.info("Creating ProfileTemplate : {}", profiletemplate);

/*		if (profileTemplateService.isProfileTemplateExist(profiletemplate)) {
			logger.error("Unable to create. A ProfileTemplate with name {} already exist", profiletemplate.getName());
			return new ResponseEntity<Object>(new CustomErrorType(
					"Unable to create. A ProfileTemplate with name " + profiletemplate.getName() + " already exist."),
					HttpStatus.CONFLICT);
		}*/
		profiletemplate = profileTemplateService.createProfileTemplate(profiletemplate);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/profile/template/{id}").buildAndExpand(profiletemplate.getId()).toUri());
		return new ResponseEntity<ProfileTemplate>(profiletemplate, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/template/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateProfileTemplate(@PathVariable("id") String id,
			@RequestBody ProfileTemplate profiletemplate) {
		logger.info("Updating ProfileTemplate with id {}", id);

		ProfileTemplate currentProfileTemplate = profileTemplateService.findById(id);

/*		if (currentProfileTemplate == null) {
			logger.error("Unable to update. ProfileTemplate with id {} not found.", id);
			return new ResponseEntity<Object>(
					new CustomErrorType("Unable to upate. ProfileTemplate with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}*/
		
		currentProfileTemplate.setProfileTemplateId(profiletemplate.getProfileTemplateId());
		currentProfileTemplate.setName(profiletemplate.getName());
		currentProfileTemplate.setProperties(profiletemplate.getProperties());
		currentProfileTemplate.setType(profiletemplate.getType());

		profileTemplateService.updateProfileTemplate(currentProfileTemplate);
		return new ResponseEntity<ProfileTemplate>(currentProfileTemplate, HttpStatus.OK);
	}

	@RequestMapping(value = "/template/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProfileTemplate(@PathVariable("id") String id) {
		logger.info("Fetching & Deleting ProfileTemplate with id {}", id);

		ProfileTemplate profiletemplate = profileTemplateService.findById(id);
/*		if (profiletemplate == null) {
			logger.error("Unable to delete. ProfileTemplate with id {} not found.", id);
			return new ResponseEntity<Object>(
					new CustomErrorType("Unable to delete. ProfileTemplate with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}*/
		profileTemplateService.deleteProfileTemplateById(id);
		return new ResponseEntity<ProfileTemplate>(HttpStatus.OK);
	}

	@RequestMapping(value = "/template/deleteAllProfileTemplates", method = RequestMethod.DELETE)
	public ResponseEntity<ProfileTemplate> deleteAllProfileTemplates() {
		logger.info("Deleting All ProfileTemplates");

		profileTemplateService.deleteAllProfileTemplates();
		return new ResponseEntity<ProfileTemplate>(HttpStatus.OK);
	}

/*	@RequestMapping(value = "/profile/getProfile/{id}", method = RequestMethod.GET)
	public ResponseEntity<ProfileTemplate> getProfile(@PathVariable("id") String id) {
		logger.info("Fetching Post with id {}", id);
		ProfileTemplate profileTemplate = profileTemplateService.findById(id);
		if (profileTemplate == null) {
			logger.error("profileTemplate with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("ProfileTemplate with id " + id + " not found"),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<ProfileTemplate>(profileTemplate, HttpStatus.OK);
	}*/
	

}