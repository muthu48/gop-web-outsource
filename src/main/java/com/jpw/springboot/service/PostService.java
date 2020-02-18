package com.jpw.springboot.service;

import java.util.List;

import com.jpw.springboot.model.Post;

public interface PostService {

	Post findById(String id);

	//Post findByName(String name);

	public Post createPost(Post post);

	//void savePost(Post Post);

	Post updatePost(Post Post);

	void deletePostById(String id);

	void deleteAllPosts();

	List<Post> findAllPosts();
	List<Post> findAllPosts(String entityId);
	//boolean isPostExist(Post post);
}