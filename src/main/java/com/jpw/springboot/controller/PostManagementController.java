package com.jpw.springboot.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpw.springboot.model.Post;
import com.jpw.springboot.model.PostVO;
import com.jpw.springboot.service.PostService;
import com.jpw.springboot.service.UserService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

@RestController
@RequestMapping("/post")
public class PostManagementController {
	public static final Logger logger = LoggerFactory.getLogger(PostManagementController.class);

	@Autowired
	PostService postService;

	@Autowired
	UserService userService;

	@Autowired
	GridFsOperations gridOperations;

	@RequestMapping(value = "/getAllPosts", method = RequestMethod.GET)
	public ResponseEntity<List<Post>> listAllPosts() {
		List<Post> posts = postService.findAllPosts();
		if (posts.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Post>>(posts, HttpStatus.OK);
	}

	@RequestMapping(value = "/getAllPosts/{entityId}/", method = RequestMethod.GET)
	public ResponseEntity<List<Post>> listAllPostsByEntity(@PathVariable("entityId") String entityId) {
		List<Post> posts = postService.findAllPosts(entityId);
		if (posts.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Post>>(posts, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getPost(@PathVariable("id") String id) {
		logger.info("Fetching Post with id {}", id);
		Post post = postService.findById(id);
		/*
		 * if (post == null) { logger.error("Post with id {} not found.", id); return
		 * new ResponseEntity(new CustomErrorType("Post with id " + id + " not found"),
		 * HttpStatus.NOT_FOUND); }
		 */
		return new ResponseEntity<Post>(post, HttpStatus.OK);
	}

	@RequestMapping(value = "downloadFile/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> downloadFile(@PathVariable("id") String id) {
		logger.info("Fetching file  with id {}", id);

		// BasicDBObject query = new BasicDBObject("metadata.postId", id);
		GridFSDBFile file = gridOperations.findOne(new Query(Criteria.where("_id").is(id)));
		
		if(file !=null) {
			return ResponseEntity.ok().contentType(MediaType.valueOf(file.getContentType()))
				.body(new InputStreamResource(file.getInputStream()));
		}
		return new ResponseEntity<Post>(HttpStatus.NO_CONTENT);
		

	}

	@RequestMapping(value = "downloadFile/user/{userId}/", method = RequestMethod.GET)
	public ResponseEntity<?> downloadFileByUser(@PathVariable("userId") String userId) {
		logger.info("Fetching file  with userId {}", userId);
		
		Sort sort = new Sort(Sort.Direction.DESC, "uploadDate");
		//Sort sort = new Sort(new Order(Sort.Direction.DESC, "metadata.uploadDate"));

		List<GridFSDBFile> files = gridOperations.find(new Query(Criteria.where("metadata.username").is(userId))
												 .with(sort));
		if(files.size() > 0){
			GridFSDBFile file = files.get(0);
			if(file !=null) {
				return ResponseEntity.ok().contentType(MediaType.valueOf(file.getContentType()))
					.body(new InputStreamResource(file.getInputStream()));
			}
		}
		return new ResponseEntity<Post>(HttpStatus.NO_CONTENT);
		

	}

	// @CrossOrigin(origins = "http://localhost:3000")
	@RequestMapping(value = "", method = RequestMethod.POST)
	//public ResponseEntity<?> createPost(@RequestParam(value ="file" , required = false) MultipartFile file, @RequestParam("post") String postData,
	//		UriComponentsBuilder ucBuilder) {
	public ResponseEntity<?> createPost(@ModelAttribute FormDataWithFile formDataWithFile,
			UriComponentsBuilder ucBuilder) {
	//	public ResponseEntity<?> createPost(PostVO postVO,
	//			UriComponentsBuilder ucBuilder) {	
		//logger.info("Creating Post : {}", postData);
		/*
		 * if (postService.isPostExist(post)) {
		 * logger.error("Unable to create. A Post with name {} already exist",
		 * post.getUserId()); return new ResponseEntity<Object>( new
		 * CustomErrorType("Unable to create. A Post with name " + post.getUserId() +
		 * " already exist."), HttpStatus.CONFLICT); }
		 */
		
		ObjectMapper mapper = new ObjectMapper();
		Post post = null;
		InputStream inputStream = null;
		//post = mapper.readValue(postData, Post.class);
		//MultipartFile file = postVO.getFile();
		//Post post = postVO.getPost();
		try {
			MultipartFile file = formDataWithFile.getFile();
			String postData = formDataWithFile.getPost();
			
			post = mapper.readValue(postData, Post.class);
			
			//Indicate PostType with combination of
			//T-TEXT, I-IMAGE, V-VIDEO
			StringBuilder strBuilder = new StringBuilder();
		    if(!StringUtils.isEmpty(post.getPostText())){
		    	strBuilder.append("T");    
		    }
			if (file != null) {
		    	strBuilder.append("I");    
		    }
			post.setPostType(strBuilder.toString());

			post = postService.createPost(post);
			if (file != null) {
				String fileName = StringUtils.cleanPath(file.getOriginalFilename());
				inputStream = file.getInputStream();
				DBObject metaData = new BasicDBObject();
				metaData.put("postId", post.getId());
				//metaData.put("userId", post.getUserId());
				metaData.put("entityId", post.getEntityId());
				GridFSFile gridFsFile = gridOperations.store(inputStream, fileName, "image/png", metaData);
				String fileId = gridFsFile.getId().toString();
				List<String> relatedFiles  = new ArrayList<String>();
				relatedFiles.add(fileId);
				post.setRelatedFiles(relatedFiles);
				postService.updatePost(post);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Done");

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/api/post/{id}").buildAndExpand(post.getUserId()).toUri());
		headers.setAccessControlAllowOrigin("*");
		headers.setAccessControlAllowCredentials(true);
		List<HttpMethod> allowedMethods = new ArrayList<HttpMethod>();
		allowedMethods.add(HttpMethod.POST);
		headers.setAccessControlAllowMethods(allowedMethods);
		String[] allowedHeadersStrArr = { "Content-Type", "Accept", "X-Requested-With" };
		/*
		 * List<String> allowedHeaders = new ArrayList<String>(); allowedHeaders.
		 */
		List<String> allowedHeaders = Arrays.asList(allowedHeadersStrArr);

		headers.setAccessControlAllowHeaders(allowedHeaders);
		return new ResponseEntity<Post>(post, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updatePost(@PathVariable("id") String id, @RequestBody Post post) {
		logger.info("Updating Post with id {}", id);

		Post currentPost = postService.findById(id);
		/*
		 * if (currentPost == null) {
		 * logger.error("Unable to update. Post with id {} not found.", id); return new
		 * ResponseEntity<Object>( new CustomErrorType("Unable to upate. Post with id "
		 * + id + " not found."), HttpStatus.NOT_FOUND); }
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
		/*
		 * if (post == null) {
		 * logger.error("Unable to delete. Post with id {} not found.", id); return new
		 * ResponseEntity<Object>( new CustomErrorType("Unable to delete. Post with id "
		 * + id + " not found."), HttpStatus.NOT_FOUND); }
		 */
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
class FormDataWithFile {
	 
    private String post;
    private MultipartFile file;
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
 
    // standard getters and setters
}
