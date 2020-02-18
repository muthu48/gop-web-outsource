package com.jpw.springboot.model;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Document(collection="legislatorcongress")
public class LegislatorCongressGT extends AbstractModel {
	@Id	
	private String entityId;
	private BasicDBObject id;
	private BasicDBObject name;	
	private BasicDBObject bio;
	private ArrayList<BasicDBObject> terms;

	public LegislatorCongressGT(){}

	public BasicDBObject getId() {
		return id;
	}

	public void setId(BasicDBObject id) {
		this.id = id;
	}

	public BasicDBObject getName() {
		return name;
	}

	public void setName(BasicDBObject name) {
		this.name = name;
	}

	public BasicDBObject getBio() {
		return bio;
	}

	public void setBio(BasicDBObject bio) {
		this.bio = bio;
	}

	public ArrayList<BasicDBObject> getTerms() {
		return terms;
	}

	public void setTerms(ArrayList<BasicDBObject> terms) {
		this.terms = terms;
	}
}
