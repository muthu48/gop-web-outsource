package com.jpw.springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpw.springboot.model.Group;
import com.jpw.springboot.repositories.GroupRepository;

@Service("groupService")
@Transactional
public class GroupServiceImpl implements GroupService {

	@Autowired
	private GroupRepository groupRepository;

	public Group findById(String id) {
		return groupRepository.findOne(id);
	}

	public Group findByName(String name) {
		return groupRepository.findByGroupName(name);
	}

	public Group createGroup(Group group) {
		return groupRepository.insert(group);
	}
	
	public void saveGroup(Group group) {
		groupRepository.save(group);
	}

	public void updateGroup(Group group) {
		saveGroup(group);
	}

	public void deleteGroupById(String id) {
		groupRepository.delete(id);
	}

	public void deleteAllGroups() {
		groupRepository.deleteAll();
	}

	public List<Group> findAllGroups() {
		return groupRepository.findAll();
	}

	public boolean isGroupExist(Group group) {
		return findByName(group.getGroupName()) != null;
	}

}
