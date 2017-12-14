package com.jpw.springboot.model;
//OBSOLETE?
public class ProfileTemplates {
	private String profileTemplateId;
	private String data[] = { "establishedDate", "ideology", "address" };

	/**
	 * @return the profileTemplateId
	 */
	public String getProfileTemplateId() {
		return profileTemplateId;
	}

	/**
	 * @param profileTemplateId
	 *            the profileTemplateId to set
	 */
	public void setProfileTemplateId(String profileTemplateId) {
		this.profileTemplateId = profileTemplateId;
	}

	/**
	 * @return the data
	 */
	public String[] getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(String[] data) {
		this.data = data;
	}

}
