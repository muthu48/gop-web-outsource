package com.jpw.springboot.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.jpw.springboot.model.Group;
import com.jpw.springboot.service.GroupService;
//import com.jpw.springboot.util.CustomErrorType;

@RestController
@RequestMapping("/group")
//@CrossOrigin(origins = "*", allowedHeaders = "*")

public class GroupManagementController {

	public static final Logger logger = LoggerFactory.getLogger(GroupManagementController.class);

	@Autowired
	GroupService groupService;

	@RequestMapping(value = "/getAllGroups", method = RequestMethod.GET)
	public ResponseEntity<List<Group>> listAllGroups() {
		List<Group> groups = groupService.findAllGroups();
		if (groups.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Group>>(groups, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getGroup(@PathVariable("id") String id) {
		logger.info("Fetching Group with id {}", id);
		Group Group = groupService.findById(id);
		/*if (Group == null) {
			logger.error("Group with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Group with id " + id + " not found"), HttpStatus.NOT_FOUND);
		}*/
		return new ResponseEntity<Group>(Group, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> createGroup(@RequestBody Group group, UriComponentsBuilder ucBuilder) {
		logger.info("Creating Group : {}", group);

/*		if (groupService.isGroupExist(group)) {
			logger.error("Unable to create. A Group with name {} already exist", group.getGroupName());
			return new ResponseEntity<Object>(
					new CustomErrorType(
							"Unable to create. A Group with name " + group.getGroupName() + " already exist."),
					HttpStatus.CONFLICT);
		}
*/		
		group = groupService.createGroup(group);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/{id}").buildAndExpand(group.getId()).toUri());
		return new ResponseEntity<Group>(group, HttpStatus.CREATED);

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateGroup(@PathVariable("id") String id, @RequestBody Group group) {
		logger.info("Updating Group with id {}", id);

		Group currentGroup = groupService.findById(id);

/*		if (currentGroup == null) {
			logger.error("Unable to update. Group with id {} not found.", id);
			return new ResponseEntity<Object>(
					new CustomErrorType("Unable to upate. Group with id " + id + " not found."), HttpStatus.NOT_FOUND);
		}
*/
		currentGroup.setGroupName(group.getGroupName());		
		currentGroup.setGroupLevel(group.getGroupLevel());
		currentGroup.setGroupType(group.getGroupType());
		currentGroup.setParentGroupId(group.getParentGroupId());
		currentGroup.setSourceId(group.getSourceId());
		currentGroup.setSourceSystem(group.getSourceSystem());
		currentGroup.setProfileData(group.getProfileData());

		groupService.updateGroup(currentGroup);
		return new ResponseEntity<Group>(currentGroup, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteGroup(@PathVariable("id") String id) {
		logger.info("Fetching & Deleting Group with id {}", id);

		Group Group = groupService.findById(id);
/*		if (Group == null) {
			logger.error("Unable to delete. Group with id {} not found.", id);
			return new ResponseEntity<Object>(
					new CustomErrorType("Unable to delete. Group with id " + id + " not found."), HttpStatus.NOT_FOUND);
		}
*/		groupService.deleteGroupById(id);
		return new ResponseEntity<Group>(HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteAllGroups", method = RequestMethod.DELETE)
	public ResponseEntity<Group> deleteAllGroups() {
		logger.info("Deleting All Groups");

		groupService.deleteAllGroups();
		return new ResponseEntity<Group>(HttpStatus.OK);
	}

}