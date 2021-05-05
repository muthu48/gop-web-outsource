package com.jpw.springboot.model;

import java.util.ArrayList;

import javax.persistence.Column;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.mongodb.BasicDBObject;

@Document(collection="congresscommittee")
public class CommitteeCongressGT extends AbstractModel {
	@Id	
	private String id;
	@Field("thomas_id")
	//private String thomasId;
	private String thomas_id;
	private String thomas_id_comment;
	private String parent_committee_id;
	private String parent_committee_type;
	private String senate_committee_id;
	private String house_committee_id;
	private String name;
	private String type;
	private String wikipedia;
	private String jurisdiction;
	private String jurisdiction_source;
	private String url;
	private String rss_url;
	private String minority_url;
	private String minority_rss_url;
	private String address;
	private String phone;
	private ArrayList<BasicDBObject> subcommittees;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getThomas_id() {
		return thomas_id;
	}
	public void setThomas_id(String thomas_id) {
		this.thomas_id = thomas_id;
	}
	public String getThomas_id_comment() {
		return thomas_id_comment;
	}
	public void setThomas_id_comment(String thomas_id_comment) {
		this.thomas_id_comment = thomas_id_comment;
	}
	public String getParent_committee_id() {
		return parent_committee_id;
	}
	public void setParent_committee_id(String parent_committee_id) {
		this.parent_committee_id = parent_committee_id;
	}
	public String getParent_committee_type() {
		return parent_committee_type;
	}
	public void setParent_committee_type(String parent_committee_type) {
		this.parent_committee_type = parent_committee_type;
	}
	public String getSenate_committee_id() {
		return senate_committee_id;
	}
	public void setSenate_committee_id(String senate_committee_id) {
		this.senate_committee_id = senate_committee_id;
	}
	public String getHouse_committee_id() {
		return house_committee_id;
	}
	public void setHouse_committee_id(String house_committee_id) {
		this.house_committee_id = house_committee_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getWikipedia() {
		return wikipedia;
	}
	public void setWikipedia(String wikipedia) {
		this.wikipedia = wikipedia;
	}
	public String getJurisdiction() {
		return jurisdiction;
	}
	public void setJurisdiction(String jurisdiction) {
		this.jurisdiction = jurisdiction;
	}
	public String getJurisdiction_source() {
		return jurisdiction_source;
	}
	public void setJurisdiction_source(String jurisdiction_source) {
		this.jurisdiction_source = jurisdiction_source;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRss_url() {
		return rss_url;
	}
	public void setRss_url(String rss_url) {
		this.rss_url = rss_url;
	}
	public String getMinority_rss_url() {
		return minority_rss_url;
	}
	public void setMinority_rss_url(String minority_rss_url) {
		this.minority_rss_url = minority_rss_url;
	}
	public String getMinority_url() {
		return minority_url;
	}
	public void setMinority_url(String minority_url) {
		this.minority_url = minority_url;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public ArrayList<BasicDBObject> getSubcommittees() {
		return subcommittees;
	}
	public void setSubcommittees(ArrayList<BasicDBObject> subcommittees) {
		this.subcommittees = subcommittees;
	}
}
