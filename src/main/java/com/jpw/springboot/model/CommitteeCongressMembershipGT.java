package com.jpw.springboot.model;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.BasicDBObject;

@Document(collection="congresscommitteemembership")
public class CommitteeCongressMembershipGT extends AbstractModel {
	@Id	
	private String id;
	private String committeeId;
	private ArrayList<BasicDBObject> members;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCommitteeId() {
		return committeeId;
	}
	public void setCommitteeId(String committeeId) {
		this.committeeId = committeeId;
	}
	public ArrayList<BasicDBObject> getMembers() {
		return members;
	}
	public void setMembers(ArrayList<BasicDBObject> members) {
		this.members = members;
	}
	

}
