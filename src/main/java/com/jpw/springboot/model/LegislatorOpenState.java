package com.jpw.springboot.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Document(collection="legislatoropenstate")
public class LegislatorOpenState extends AbstractModel {
	@Id	
	@Field("_id")
	private String id;

	private String last_name;
	//private JSONArray sources;//[["url":""]]
	private ArrayList<BasicDBObject> sources;
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
	private String full_name;
	private BasicDBObject old_roles;//{"year range":[{}], "year range":[{}]}
	/*
	 {
      "2009-2010":[
         {
            "term":"2009-2010",
            "end_date":null,
            "district":"33",
            "level":"state",
            "country":"us",
            "chamber":"upper",
            "state":"pa",
            "party":"Republican",
            "type":"member",
            "start_date":null
         }
      ]
     }
	 */
	private String first_name;
	private String middle_name;
	private String district;//33
	private String state;//pa
	private String party;//Republican
	private String email;
	private String _type;//"person"
	private String _scraped_name;
	
	@Field("leg_id")
	private String legId;//"PAL000001"
	private boolean active;//true
	private String photo_url;//"http://www.legis.state.pa.us/images/members/200/1187.jpg",
	private ArrayList<BasicDBObject> roles;//[{}]
	/*
	 {
         "term":"2017-2018",
         "end_date":null,
         "district":"33",
         "chamber":"upper",
         "state":"pa",
         "party":"Republican",
         "type":"member",
         "start_date":null
      }
      */
	private String url; 
	private String chamber;
	private ArrayList<BasicDBObject> offices;//Position[]
	/*
	 {
        "fax":"717-772-2753",
        "name":"Capitol Office",
        "phone":"717-787-4651",
        "address":"Senate Box 203033;Harrisburg, PA 17120-3033;Room:;172 Main Capitol",
        "type":"capitol",
        "email":"alloway@pasen.gov"
     }
	*/
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public ArrayList<BasicDBObject> getSources() {
		return sources;
	}
	public void setSources(ArrayList<BasicDBObject> sources) {
		this.sources = sources;
	}
	public String getFull_name() {
		return full_name;
	}
	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}
	public BasicDBObject getOld_roles() {
		return old_roles;
	}
	public void setOld_roles(BasicDBObject old_roles) {
		this.old_roles = old_roles;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getMiddle_name() {
		return middle_name;
	}
	public void setMiddle_name(String middle_name) {
		this.middle_name = middle_name;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getParty() {
		return party;
	}
	public void setParty(String party) {
		this.party = party;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String get_type() {
		return _type;
	}
	public void set_type(String _type) {
		this._type = _type;
	}
	public String get_scraped_name() {
		return _scraped_name;
	}
	public void set_scraped_name(String _scraped_name) {
		this._scraped_name = _scraped_name;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public String getLegId() {
		return legId;
	}
	public void setLegId(String legId) {
		this.legId = legId;
	}
	public String getPhoto_url() {
		return photo_url;
	}
	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}
	public ArrayList<BasicDBObject> getRoles() {
		return roles;
	}
	public void setRoles(ArrayList<BasicDBObject> roles) {
		this.roles = roles;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getChamber() {
		return chamber;
	}
	public void setChamber(String chamber) {
		this.chamber = chamber;
	}
	public ArrayList<BasicDBObject> getOffices() {
		return offices;
	}
	public void setOffices(ArrayList<BasicDBObject> offices) {
		this.offices = offices;
	}
	public String getId() {
		return id;
	}
}
