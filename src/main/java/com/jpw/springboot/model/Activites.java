package com.jpw.springboot.model;

public class Activites {
	private String userId;
	private String activitesOnGroupId;
	private String activitesOnUserId;

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
	 * @return the activitesOnGroupId
	 */
	public String getActivitesOnGroupId() {
		return activitesOnGroupId;
	}

	/**
	 * @param activitesOnGroupId
	 *            the activitesOnGroupId to set
	 */
	public void setActivitesOnGroupId(String activitesOnGroupId) {
		this.activitesOnGroupId = activitesOnGroupId;
	}

	/**
	 * @return the activitesOnUserId
	 */
	public String getActivitesOnUserId() {
		return activitesOnUserId;
	}

	/**
	 * @param activitesOnUserId
	 *            the activitesOnUserId to set
	 */
	public void setActivitesOnUserId(String activitesOnUserId) {
		this.activitesOnUserId = activitesOnUserId;
	}

}