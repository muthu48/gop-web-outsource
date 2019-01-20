package com.jpw.springboot.model;

import java.util.List;

import org.springframework.data.annotation.Id;

public class Post {
	@Id	
	private String id;		
	private String userId;
	private String postType;
	private String postText;
	private String imageUrl;
	private String videoUrl;
	private String parentPostId;
	private String districtId;
	private List<String> relatedFiles;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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
	 * @return the postType
	 */
	public String getPostType() {
		return postType;
	}

	/**
	 * @param postType
	 *            the postType to set
	 */
	public void setPostType(String postType) {
		this.postType = postType;
	}

	/**
	 * @return the postText
	 */
	public String getPostText() {
		return postText;
	}

	/**
	 * @param postText
	 *            the postText to set
	 */
	public void setPostText(String postText) {
		this.postText = postText;
	}

	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * @param imageUrl
	 *            the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/**
	 * @return the videoUrl
	 */
	public String getVideoUrl() {
		return videoUrl;
	}

	/**
	 * @param videoUrl
	 *            the videoUrl to set
	 */
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	/**
	 * @return the parentPostId
	 */
	public String getParentPostId() {
		return parentPostId;
	}

	/**
	 * @param parentPostId
	 *            the parentPostId to set
	 */
	public void setParentPostId(String parentPostId) {
		this.parentPostId = parentPostId;
	}

	public String getDistrictId() {
		return districtId;
	}

	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}

	public List<String> getRelatedFiles() {
		return relatedFiles;
	}

	public void setRelatedFiles(List<String> relatedFiles) {
		this.relatedFiles = relatedFiles;
	}
}