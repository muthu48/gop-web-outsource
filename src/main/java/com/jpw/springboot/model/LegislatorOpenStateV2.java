package com.jpw.springboot.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.mongodb.BasicDBObject;

@Document(collection="legislatoropenstate")
public class LegislatorOpenStateV2 extends AbstractModel {
	@Id	
	@Field("id")
	private String id;
	private BasicDBObject ids;
	private String name;//full_name;
	private String first_name;
	private String given_name;
	private String family_name;
	private String last_name;
	private String middle_name;
	private String email;
	private String gender;
	private String birth_date;
	private String image;//photo_url;//"http://www.legis.state.pa.us/images/members/200/1187.jpg",
	private List<BasicDBObject> other_names;
	private List<BasicDBObject> other_identifiers;
	private List<BasicDBObject> sources;
	/*
	 [
     {
        "url":"http://www.legis.state.pa.us/cfdocs/legis/home/member_information/senators_alpha.cfm"
     },
     {
        "url":"http://www.legis.state.pa.us/cfdocs/legis/home/member_information/Senate_bio.cfm?id=1187"
     }
  ]
	 */	
	private ArrayList<BasicDBObject> links;
	private ArrayList<BasicDBObject> party;
	private ArrayList<BasicDBObject> roles;//
	/*
	 [{
         "term":"2017-2018",
         "end_date":null,
         "district":"33",
         "chamber":"upper",
         "state":"pa",
         "party":"Republican",
         "type":"member",
         "start_date":null
      }]
      */	



	private ArrayList<BasicDBObject> contact_details;//
	/*
	 [{
        "fax":"717-772-2753",
        "name":"Capitol Office",
        "phone":"717-787-4651",
        "address":"Senate Box 203033;Harrisburg, PA 17120-3033;Room:;172 Main Capitol",
        "type":"capitol",
        "email":"alloway@pasen.gov"
     }]
	*/



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public BasicDBObject getIds() {
		return ids;
	}



	public void setIds(BasicDBObject ids) {
		this.ids = ids;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getFirst_name() {
		return first_name;
	}



	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}



	public String getGiven_name() {
		return given_name;
	}



	public void setGiven_name(String given_name) {
		this.given_name = given_name;
	}



	public String getFamily_name() {
		return family_name;
	}



	public void setFamily_name(String family_name) {
		this.family_name = family_name;
	}



	public String getLast_name() {
		return last_name;
	}



	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}



	public String getMiddle_name() {
		return middle_name;
	}



	public void setMiddle_name(String middle_name) {
		this.middle_name = middle_name;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getBirth_date() {
		return birth_date;
	}



	public void setBirth_date(String birth_date) {
		this.birth_date = birth_date;
	}



	public String getImage() {
		return image;
	}



	public void setImage(String image) {
		this.image = image;
	}

	public List<BasicDBObject> getSources() {
		return sources;
	}



	public void setSources(ArrayList<BasicDBObject> sources) {
		this.sources = sources;
	}



	public ArrayList<BasicDBObject> getLinks() {
		return links;
	}



	public void setLinks(ArrayList<BasicDBObject> links) {
		this.links = links;
	}



	public ArrayList<BasicDBObject> getParty() {
		return party;
	}



	public void setParty(ArrayList<BasicDBObject> party) {
		this.party = party;
	}



	public ArrayList<BasicDBObject> getRoles() {
		return roles;
	}



	public void setRoles(ArrayList<BasicDBObject> roles) {
		this.roles = roles;
	}



	public ArrayList<BasicDBObject> getContact_details() {
		return contact_details;
	}



	public void setContact_details(ArrayList<BasicDBObject> contact_details) {
		this.contact_details = contact_details;
	}



	public String getGender() {
		return gender;
	}



	public void setGender(String gender) {
		this.gender = gender;
	}



	public List<BasicDBObject> getOther_names() {
		return other_names;
	}



	public void setOther_names(List<BasicDBObject> other_names) {
		this.other_names = other_names;
	}

	public List<BasicDBObject> getOther_identifiers() {
		return other_identifiers;
	}



	public void setOther_identifiers(List<BasicDBObject> other_identifiers) {
		this.other_identifiers = other_identifiers;
	}

	
}
