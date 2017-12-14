package com.jpw.springboot.model;

public class UserProfile {
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the profile_template_id
	 */
	public String[] getProfile_template_id() {
		return profile_template_id;
	}

	/**
	 * @param profile_template_id
	 *            the profile_template_id to set
	 */
	public void setProfile_template_id(String[] profile_template_id) {
		this.profile_template_id = profile_template_id;
	}

	private String userId;
	private String[] profile_template_id;

}