package com.jpw.springboot.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpw.springboot.model.Post;
import com.jpw.springboot.service.NotificationPollingService;
import com.jpw.springboot.service.PostService;
import com.jpw.springboot.service.UserService;
import com.jpw.springboot.util.SystemConstants;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSFindIterable;
//import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.client.gridfs.model.GridFSFile ;

//@CrossOrigin(origins = "http://localhost:4200")
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
	
	@Autowired
	GridFsTemplate gridFsTemplate;

	@Autowired
	NotificationPollingService notificationPollingService;
	
	//GET ALL POSTS FROM THE SYSTEM
	//SHOULD NOT BE USED, ONLY FOR INTERNAL USE
	@RequestMapping(value = "/getAllPosts", method = RequestMethod.GET)
	public ResponseEntity<List<Post>> getAllPosts() {
		logger.info("in getAllPosts ");

		ResponseEntity response = null;
		try{

		List<Post> posts = postService.findAllPosts();
		if (posts.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		response = new ResponseEntity<List<Post>>(posts, HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in getAllPosts", HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in getAllPosts", e);
		}

		return response;

	}
	
	//POSTED BY AN ENTITY'S CONNECTIONS, WITHOUT PAGINATION
	@RequestMapping(value = "/getAllPosts/{entityId}/", method = RequestMethod.GET)
	public ResponseEntity<List<Post>> getAllPosts(@PathVariable("entityId") String entityId) {
		logger.info("getAllPosts  for entityId ", entityId);

		ResponseEntity response = null;
		try{
			List<Post> posts = postService.findPosts(entityId);
			if (posts.isEmpty()) {
				return new ResponseEntity(HttpStatus.NO_CONTENT);
			}
			response = new ResponseEntity<List<Post>>(posts, HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in getAllPosts  for entityId " + entityId, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in getAllPosts  for entityId " + entityId, e);
		}
		
		return response;
	}
	
	//POSTED BY AN ENTITY'S CONNECTIONS, WITH PAGINATION
	@RequestMapping(value = "/getPosts/{entityId}/", method = RequestMethod.GET)
	public ResponseEntity<List<Post>> getPostsByEntity(@PathVariable("entityId") String entityId,
			@RequestParam (value = "pageNumber", required = false) String pageNumberParam) {
		logger.info("getPostsByEntity  for entityId ", entityId);

		ResponseEntity response = null;
		int pageNumber = 0;
		try{
			if(!StringUtils.isEmpty(pageNumberParam)){
				pageNumber = Integer.parseInt(pageNumberParam);	
			}
			List<Post> posts = postService.findPosts(entityId, pageNumber, true);
			//List<Post> posts = postService.findPosts(entityId, pageNumber);
			if (posts.isEmpty()) {
				return new ResponseEntity(HttpStatus.NO_CONTENT);
			}
			response = new ResponseEntity<List<Post>>(posts, HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in getPostsByEntity  for entityId " + entityId, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in getPostsByEntity  for entityId " + entityId, e);
		}
		
		return response;
	}
	
	//POSTED BY AN ENTITY, WITH PAGINATION
	@RequestMapping(value = "/getMyPosts/{entityId}/", method = RequestMethod.GET)
	public ResponseEntity<List<Post>> getMyPostsByEntity(@PathVariable("entityId") String entityId,
			@RequestParam (value = "pageNumber", required = false) String pageNumberParam) {
		logger.info("getMyPostsByEntity  for entityId ", entityId);

		ResponseEntity response = null;
		int pageNumber = 0;

		try{
			if(!StringUtils.isEmpty(pageNumberParam)){
				pageNumber = Integer.parseInt(pageNumberParam);	
			}
			List<Post> posts = postService.findMyPosts(entityId, pageNumber);
			if (posts.isEmpty()) {
				return new ResponseEntity(HttpStatus.NO_CONTENT);
			}
			response = new ResponseEntity<List<Post>>(posts, HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in getMyPostsByEntity  for entityId " + entityId, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in getMyPostsByEntity  for entityId " + entityId, e);
		}
	
		return response;

	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getPost(@PathVariable("id") String postId) {
		logger.info("getPost with id ", postId);
		ResponseEntity response = null;
		try{
			Post post = postService.findById(postId);
						
			response = new ResponseEntity<Post>(post, HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in getPost with id " + postId, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in getPost with id " + postId, e);
		}

		return response;
	}

	//TODO
	//COMMENTS FOR A POST, WITH PAGINATION
	@RequestMapping(value = "/getPostComments/{postId}/", method = RequestMethod.GET)
	public ResponseEntity<List<Post>> getPostComments(@PathVariable("postId") String postId,
			@RequestParam (value = "pageNumber", required = false) String pageNumberParam) {
		logger.info("getPostComments  for Post ", postId);
		ResponseEntity response = null;
		int pageNumber = 0;
		int commentLevel = 0;
		List<Post> posts = null;
		try{
			if(!StringUtils.isEmpty(pageNumberParam)){
				pageNumber = Integer.parseInt(pageNumberParam);	
			}
			
			posts = postService.findComments(postId, pageNumber);
			
			//this is required to get the commentLevel
/*			Post post = postService.findById(postId);

			if(post.getCommentLevel() > 0){
				commentLevel = post.getCommentLevel();
			}
				commentLevel++;
				
				posts = postService.findPosts(postId, commentLevel, pageNumber);
*/			

			
			if (posts.isEmpty()) {
				return new ResponseEntity(HttpStatus.NO_CONTENT);
			}
			response = new ResponseEntity<List<Post>>(posts, HttpStatus.OK);
			
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in getPostComments  for Post " + postId, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in getPostComments  for Post " + postId, e);
		}

		return response;
	}
	
	//TODO, is it possible to check the user is in the liked list ?
	/*
	 * Get the Liked count for a Post/Comment
	 * */
	@RequestMapping(value = "/getLikedCount", method = RequestMethod.GET)
	public ResponseEntity<?> getLikedCount(@RequestParam (value = "postId", required = false) String postId) {
		logger.info("Fetching getLikedCount for entityId ", postId);
		ResponseEntity response = null;
		try{
			int likedCount = -1;
			Post post = postService.findById(postId);
			if(post.getLikedBy() != null)
				likedCount = post.getLikedBy().length;
			
			response = new ResponseEntity<Integer>(likedCount, HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in getLikedCount for entityId " + postId, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in getLikedCount for entityId " + postId, e);
		}
		
		return response;
	}

	/*
	 * Check if an Entity liked a Post/Comment
	 * */
	@RequestMapping(value = "/isLikedByEntity", method = RequestMethod.GET)
	public ResponseEntity<?> isLikedByEntity(@RequestParam (value = "postId", required = true) String postId,
			@RequestParam (value = "entityId", required = true) String entityId) {
		logger.info("isLikedByEntity entityId " + entityId + ", postId " + postId);
		ResponseEntity response = null;
		boolean isLiked = false;
		try{
			Post post = postService.findById(postId);
			
			String[] likedBy = post.getLikedBy();
			if(likedBy != null){
				isLiked = ArrayUtils.contains(likedBy, entityId);
			}
			
			response = new ResponseEntity<Boolean>(isLiked, HttpStatus.OK);

		}catch(Exception e){
			response = new ResponseEntity<String>("Error in isLikedByEntity for entityId " + entityId + ", postId " + postId, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in isLikedByEntity for entityId " + entityId + ", postId " + postId, e);
		}
		
		return response;
	}

	//TODO
	/*
	 * Get the Comments count for a Post/Comment
	 * */
	@RequestMapping(value = "/getCommentsCount", method = RequestMethod.GET)
	public ResponseEntity<?> getCommentsCount(@RequestParam (value = "postId", required = true) String postId) {
		logger.info("Fetching getCommentsCount for entityId ", postId);
		ResponseEntity response = null;
		int commentsCount = 0;

		try{

			commentsCount = postService.findCommentsCount(postId);

			response = new ResponseEntity<Integer>(commentsCount, HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in getCommentsCount for entityId " + postId, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in getCommentsCount for entityId " + postId, e);
		}
		
		return response;
	}
	
	//USED FOR FILE ATTACHMENT DOWNLOAD
	@RequestMapping(value = "downloadFile/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> downloadFile(@PathVariable("id") String id) {
		logger.info("Fetching file  with id {}", id);

		// BasicDBObject query = new BasicDBObject("metadata.postId", id);
		//GridFSDBFile file = gridOperations.findOne(new Query(Criteria.where("_id").is(id)));
		GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
		if(file !=null) {
			try {
				return ResponseEntity.ok().contentType(MediaType.valueOf(file.getMetadata().getString("_contentType")))
					.body(new InputStreamResource(gridOperations.getResource(file).getInputStream()));
			} catch (Exception e) {
				logger.error("Error in downloading file " + e);
				return new ResponseEntity<Post>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<Post>(HttpStatus.NO_CONTENT);
		

	}

	//OBSOLETE?
	@RequestMapping(value = "downloadFile/user/{userId}/", method = RequestMethod.GET)
	public ResponseEntity<?> downloadFileByUser(@PathVariable("userId") String userId) {
		logger.info("Fetching file  with userId {}", userId);
		
		GridFSFindIterable files = gridFsTemplate.find(new Query(Criteria.where("metadata.entityId").is(userId).and("metadata.imageType").is(SystemConstants.SMALL_PROFILE_IMAGE_METADATA))
												 .with(Sort.by(Sort.Direction.DESC, "uploadDate")));
		if( files != null && files.first() != null){
			GridFSFile file = files.first();
			if(file !=null) {
				try {
					return ResponseEntity.ok().contentType(MediaType.valueOf(file.getContentType()))
							.body(new InputStreamResource(gridOperations.getResource(file).getInputStream()));
				} catch (IllegalStateException | IOException e) {
					logger.error("Error in downloading file " + e);
				}
			}
		}
		return new ResponseEntity<Post>(HttpStatus.NO_CONTENT);
		

	}
	
	//USED FOR ENTITY'S PROFILE SMALL IMAGE DOWNLOAD
	@RequestMapping(value = "downloadFile/entity/{entityId}/", method = RequestMethod.GET)
	public ResponseEntity<?> downloadFileForEntity(@PathVariable("entityId") String entityId,
			@RequestParam (value = "metadatatype", required = false) String metadatatype) {
		logger.info("Fetching file  for entityId ", entityId);

		if(!StringUtils.isEmpty(metadatatype)){}
		
		GridFSFindIterable files = gridOperations.find(new Query(Criteria.where("metadata.entityId").is(entityId).and("metadata.imageType").is(SystemConstants.SMALL_PROFILE_IMAGE_METADATA))
				 .with(Sort.by(Sort.Direction.DESC, "uploadDate")));
		
		if( files != null && files.first() != null){
			GridFSFile file = files.first();
			if(file !=null) {
				try {
					return ResponseEntity.ok().contentType(MediaType.valueOf(file.getContentType()))
							.body(new InputStreamResource(gridOperations.getResource(file).getInputStream()));
				} catch (IllegalStateException | IOException e) {
					logger.error("Error in downloading file " + e);
				}
			}
		}
		return new ResponseEntity<Post>(HttpStatus.NO_CONTENT);
		

	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
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
		List<String> relatedFiles  = new ArrayList<String>();

		//post = mapper.readValue(postData, Post.class);
		//MultipartFile file = postVO.getFile();
		//Post post = postVO.getPost();
		try {
			MultipartFile file = formDataWithFile.getFile();
			MultipartFile videoFile = formDataWithFile.getVideofile();
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
			if (videoFile != null) {
		    	strBuilder.append("V");    
		    }
			post.setPostType(strBuilder.toString());
			
			if(post.getParentPostId() == null){
				post.setPost(true);
			}else{
				post.setPost(false);
			}
			
			post = postService.createPost(post);

			//TODO: Generate a notification and send it to the followers
			notificationPollingService.createNotification(post);


			if (file != null) {
				String fileName = StringUtils.cleanPath(file.getOriginalFilename());
				inputStream = file.getInputStream();
				DBObject metaData = new BasicDBObject();
				metaData.put("postId", post.getId());
				//metaData.put("userId", post.getUserId());
				metaData.put("entityId", post.getEntityId());
				String fileContentType = URLConnection.guessContentTypeFromName(fileName);
				ObjectId id = gridFsTemplate.store(inputStream, fileName, fileContentType, metaData);
				String fileId = id.toString();
				relatedFiles.add(fileId);

			}
			if (videoFile != null) {
				String fileName = StringUtils.cleanPath(videoFile.getOriginalFilename());
				inputStream = videoFile.getInputStream();
				DBObject metaData = new BasicDBObject();
				metaData.put("postId", post.getId());
				//metaData.put("userId", post.getUserId());
				metaData.put("entityId", post.getEntityId());
				String fileContentType = URLConnection.guessContentTypeFromName(fileName);
				ObjectId id = gridFsTemplate.store(inputStream, fileName, fileContentType, metaData);
				String fileId = id.toString();
				relatedFiles.add(fileId);

			}
			
			if (file != null || videoFile != null) {
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

		//System.out.println("Done");

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
		logger.info("Updating Post of id ", id);
		ResponseEntity response = null;
		try{
			Post currentPost = postService.findById(id);
			
			currentPost.setUserId(post.getUserId());
			currentPost.setPostType(post.getPostType());
			currentPost.setPostText(post.getPostText());
			currentPost.setImageUrl(post.getImageUrl());
			currentPost.setParentPostId(post.getParentPostId());
			currentPost.setVideoUrl(post.getVideoUrl());
	
			postService.updatePost(currentPost);
			response = new ResponseEntity<Post>(currentPost, HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in Post of id " + id, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in Post of id " + id, e);
		}

		return response;
	}

	@RequestMapping(value = "/like/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> likePost(@PathVariable("id") String id, @RequestParam String entityId) {
		logger.info("Like Post of id " + id + " , entityId " + entityId);
		ResponseEntity response = null;
		int likedByCount = 0;
		String[] updatedLikedBy = new String[1];
		try{
			Post post = postService.findById(id);
			String[] likedBy = post.getLikedBy();
			if(likedBy != null){
				likedByCount = likedBy.length;
				updatedLikedBy = new String[likedByCount + 1];
				System.arraycopy(likedBy, 0, updatedLikedBy, 0, likedByCount);

			}
			updatedLikedBy[likedByCount] = entityId;
			post.setLikedBy(updatedLikedBy);
			postService.updatePost(post);
			response = new ResponseEntity<Post>(post, HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in Like Post of id " + id + " , entityId " + entityId, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in Like Post of id " + id + " , entityId " + entityId, e);
		}

		return response;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deletePost(@PathVariable("id") String id) {
		logger.info("Deleting Post with id ", id);
		ResponseEntity response = null;
		try{
			Post post = postService.findById(id);
			postService.deletePostById(id);
			response = new ResponseEntity<Post>(HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in deleting Post with id " + id, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in deleting Post with id " + id, e);
		}

		return response;

	}

	@RequestMapping(value = "/deleteAllPosts", method = RequestMethod.DELETE)
	public ResponseEntity<Post> deleteAllPosts() {
		logger.info("Deleting All Posts");
		ResponseEntity response = null;
		try{

			postService.deleteAllPosts(); 
			response = new ResponseEntity<Post>(HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in deleteAllPosts", HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in deleteAllPosts", e);
		}
	
		return response;

	}

}
class FormDataWithFile {
	 
    private String post;
    private MultipartFile file;
    private MultipartFile videofile;
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
	public MultipartFile getVideofile() {
		return videofile;
	}
	public void setVideofile(MultipartFile videofile) {
		this.videofile = videofile;
	}
 
    // standard getters and setters
}
