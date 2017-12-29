package com.jpw.springboot.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.jpw.springboot.model.Post;
import com.jpw.springboot.service.PostService;
import com.jpw.springboot.service.UserService;
//import com.jpw.springboot.util.CustomErrorType;

@RestController
@RequestMapping("/post")
public class PostManagementController {
	public static final Logger logger = LoggerFactory.getLogger(PostManagementController.class);

	@Autowired
	PostService postService;

	@Autowired
	UserService userService;
	@RequestMapping(value = "/getAllPosts", method = RequestMethod.GET)
	public ResponseEntity<List<Post>> listAllPosts() {
		List<Post> post = postService.findAllPosts();
		if (post.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Post>>(post, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getPost(@PathVariable("id") String id) {
		logger.info("Fetching Post with id {}", id);
		Post post = postService.findById(id);
		/*if (post == null) {
			logger.error("Post with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Post with id " + id + " not found"), HttpStatus.NOT_FOUND);
		}*/
		return new ResponseEntity<Post>(post, HttpStatus.OK);
	}

    //@CrossOrigin(origins = "http://localhost:3000")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> createPost(@RequestBody Post post, UriComponentsBuilder ucBuilder) {
		logger.info("Creating Post : {}", post);

/*		if (postService.isPostExist(post)) {
			logger.error("Unable to create. A Post with name {} already exist", post.getUserId());
			return new ResponseEntity<Object>(
					new CustomErrorType("Unable to create. A Post with name " + post.getUserId() + " already exist."),
					HttpStatus.CONFLICT);
		}
*/		
		post = postService.createPost(post);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/api/post/{id}")
				.buildAndExpand(post.getUserId()).toUri());
		headers.setAccessControlAllowOrigin("*");
		headers.setAccessControlAllowCredentials(true);
		List<HttpMethod> allowedMethods = new ArrayList<HttpMethod>();
		allowedMethods.add(HttpMethod.POST);
		headers.setAccessControlAllowMethods(allowedMethods);
String[] allowedHeadersStrArr = {"Content-Type", "Accept", "X-Requested-With"};
/*List<String> allowedHeaders = new ArrayList<String>();
allowedHeaders.*/
List<String> allowedHeaders = Arrays.asList(allowedHeadersStrArr) ;

		headers.setAccessControlAllowHeaders(allowedHeaders);
		return new ResponseEntity<Post>(post, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updatePost(@PathVariable("id") String id, @RequestBody Post post) {
		logger.info("Updating Post with id {}", id);

		Post currentPost = postService.findById(id);
/*
		if (currentPost == null) {
			logger.error("Unable to update. Post with id {} not found.", id);
			return new ResponseEntity<Object>(
					new CustomErrorType("Unable to upate. Post with id " + id + " not found."), HttpStatus.NOT_FOUND);
		}
*/
		currentPost.setUserId(post.getUserId());
		currentPost.setPostType(post.getPostType());
		currentPost.setPostText(post.getPostText());
		currentPost.setImageUrl(post.getImageUrl());
		currentPost.setParentPostId(post.getParentPostId());
		currentPost.setVideoUrl(post.getVideoUrl());

		postService.updatePost(currentPost);
		return new ResponseEntity<Post>(currentPost, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deletePost(@PathVariable("id") String id) {
		logger.info("Fetching & Deleting Post with id {}", id);

		Post post = postService.findById(id);
/*		if (post == null) {
			logger.error("Unable to delete. Post with id {} not found.", id);
			return new ResponseEntity<Object>(
					new CustomErrorType("Unable to delete. Post with id " + id + " not found."), HttpStatus.NOT_FOUND);
		}*/
		postService.deletePostById(id);
		return new ResponseEntity<Post>(HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteAllPosts", method = RequestMethod.DELETE)
	public ResponseEntity<Post> deleteAllPosts() {
		logger.info("Deleting All Posts");

		postService.deleteAllPosts();
		return new ResponseEntity<Post>(HttpStatus.OK);
	}

}
