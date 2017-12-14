package com.jpw.springboot.service;

import java.util.List;

import com.jpw.springboot.model.Group;

public interface GroupService {

	Group findById(String id);

	Group findByName(String name);

	Group createGroup(Group group);
	
	void saveGroup(Group group);

	void updateGroup(Group group);

	void deleteGroupById(String id);

	void deleteAllGroups();

	List<Group> findAllGroups();

	boolean isGroupExist(Group group);
}