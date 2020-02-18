package com.jpw.springboot.model;

import java.util.List;

import org.springframework.data.annotation.Id;

public class Post extends AbstractModel{
	@Id	
	private String id;		
	private String entityId; // userid, districtid, partyid mapped here
	private String userId;// may not be required
	private String districtId;// may not be required
	private String parentPostId;	
	private String postCategory; //Health, Science, Auto, etc // may not be required
	private String postType; //V-video / I-image / T -text, 
	private String postText;
	private String imageUrl;// may not be required
	private String videoUrl;// may not be required
	private String[] taggedEntityId;
	private String[] likedByEntityIds;
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

	public String[] getTaggedEntityId() {
		return taggedEntityId;
	}

	public void setTaggedEntityId(String[] taggedEntityId) {
		this.taggedEntityId = taggedEntityId;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getPostCategory() {
		return postCategory;
	}

	public void setPostCategory(String postCategory) {
		this.postCategory = postCategory;
	}

}