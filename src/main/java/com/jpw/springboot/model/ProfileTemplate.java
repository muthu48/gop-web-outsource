package com.jpw.springboot.model;

import org.springframework.data.annotation.Id;

//PROFILE TEMPLATE DEFINITION, 
//THIS CAN BE ASSOCIATED WITH ANY ENTITY AS PROFILEDATA
public class ProfileTemplate {
	@Id	
	private String id;	
	private String profileTemplateId; // STATIC ID
	private String type;
	private String name;//DISPLAY NAME
	private String properties[];
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProfileTemplateId() {
		return profileTemplateId;
	}

	public void setProfileTemplateId(String profileTemplateId) {
		this.profileTemplateId = profileTemplateId;
	}


	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the properties
	 */
	public String[] getProperties() {
		return properties;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(String[] properties) {
		this.properties = properties;
	}

}