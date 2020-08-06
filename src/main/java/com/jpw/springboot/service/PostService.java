package com.jpw.springboot.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.jpw.springboot.model.Post;

public interface PostService {

	public Post findById(String id) throws Exception;

	//Post findByName(String name);

	public Post createPost(Post post) throws Exception;

	//void savePost(Post Post);

	public Post updatePost(Post Post) throws Exception;

	public void deletePostById(String id) throws Exception;

	public void deleteAllPosts() throws Exception;

	public List<Post> findAllPosts() throws Exception;
	public List<Post> findPosts(String entityId) throws Exception;
	public List<Post> findPosts(String entityId, int pageNumber) throws Exception;
	public List<Post> findPosts(String parentPostId, int commentlevel, int pageNumber) throws Exception;
	//boolean isPostExist(Post post);
	public List<Post> findMyPosts(String entityId) throws Exception;
	public List<Post> findMyPosts(String entityId, int pageNumber) throws Exception;
	public List<Post> findComments(String parentPostId, int pageNumber) throws Exception;
	public int findCommentsCount(String parentPostId) throws Exception;
}