package com.jpw.springboot.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
/*import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Post;*/
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpw.springboot.model.Activites;
import com.jpw.springboot.model.Connection;
import com.jpw.springboot.model.User;
import com.jpw.springboot.service.SocialService;
import com.jpw.springboot.service.UserService;
import com.jpw.springboot.util.CustomErrorType;
import com.jpw.springboot.util.SystemConstants;

@RestController
@RequestMapping("/api")
public class SocialController {

	public static final Logger logger = LoggerFactory.getLogger(SocialController.class);

	@Autowired
	SocialService socialService;

	@Autowired
	UserService userService;

/*	private Facebook facebook;
	private ConnectionRepository connectionRepository;

	public SocialController(Facebook facebook, ConnectionRepository connectionRepository) {
		this.facebook = facebook;
		this.connectionRepository = connectionRepository;
	}
*/
	//OBSOLETE
/*	@RequestMapping(value = "/social/getActivites/{userId}", method = RequestMethod.GET)
	public ResponseEntity<?> getActivites(@PathVariable("userId") String userId) {
		logger.info("Fetching Activites with userId {}", userId);
		Activites activites = socialService.findByUserId(userId);

		return new ResponseEntity<Activites>(activites, HttpStatus.OK);
	}
*/
	@RequestMapping(value = "/social/getRelation", method = RequestMethod.GET)
	public ResponseEntity<?> getRelation(@RequestParam (value = "sourceEntityId", required = false) String sourceEntityId,
			@RequestParam (value = "targetEntityId", required = false) String targetEntityId) {
		logger.info("getRelation between " + sourceEntityId + ", " + targetEntityId);
		ResponseEntity response = null;
		try{

			String relationshipStatus = socialService.getRelationshipStatus(sourceEntityId, targetEntityId, false);
			response = new ResponseEntity<String>(relationshipStatus, HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in getRelation between " + sourceEntityId + ", " + targetEntityId, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in getRelation between " + sourceEntityId + ", " + targetEntityId, e);
		}
		
		return response;
	}
	
	/*
	 * Get the count of followers for the given Entity - User, Group, Party
	 * */
	@RequestMapping(value = "/social/getFollowersCount", method = RequestMethod.GET)
	public ResponseEntity<?> getFollowersCount(@RequestParam (value = "entityId", required = true) String entityId) {
		logger.info("Fetching FollowersCount for entityId {}", entityId);
		ResponseEntity response = null;
		try{
			int followersCount = socialService.getFollowersCount(entityId);
		
			response = new ResponseEntity<Integer>(followersCount, HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in getFollowersCount for entityId " + entityId, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in getFollowersCount for entityId " + entityId, e);
		}
		
		return response;
	}
	
	/*
	 * Get the list of followers for the given Entity - User, Group, Party
	 * Can get list of 20(configurable), in case of large data set
	 * "select sourceEntityId from connection where 
		targetentityid = entityId and
		status in ('FOLLOWING')"
		Use Mongo repository syntax
		Based on sourceEntityId, get basic entity data such as name
	 * */
	@RequestMapping(value = "/social/getFollowers", method = RequestMethod.GET)
	public ResponseEntity<?> getFollowers(@RequestParam (value = "entityId", required = true) String entityId) {
		logger.info("getFollowers  for entityId ", entityId);
		ResponseEntity response = null;
		try{
			List<User> followers = socialService.getFollowers(entityId);
		
			response = new ResponseEntity<List<User>>(followers, HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in getFollowers  for entityId " + entityId, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in getFollowers  for entityId " + entityId, e);
		}
		
		return response;
	}
	
	/*
	 * Get the count of followings of an Entity
	 * */
	@RequestMapping(value = "/social/getFollowingsCount", method = RequestMethod.GET)
	public ResponseEntity<?> getFollowingsCount(@RequestParam (value = "entityId", required = true) String entityId) {
		logger.info("Fetching getFollowingsCount for entityId ", entityId);
		ResponseEntity response = null;
		try{
			int followingsCount = socialService.getFollowingsCount(entityId);
		
			response = new ResponseEntity<Integer>(followingsCount, HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in getFollowingsCount for entityId " + entityId, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in getFollowingsCount for entityId " + entityId, e);
		}
		
		return response;
	}
	
	/*
	 * Get the list of Followings of an Entity
	 * Can get list of 20(configurable), in case of large data set
	 * */
	@RequestMapping(value = "/social/getFollowings", method = RequestMethod.GET)
	public ResponseEntity<?> getFollowings(@RequestParam (value = "entityId", required = true) String entityId) {
		logger.info("getFollowings  for entityId " + entityId);
		ResponseEntity response = null;
		try{
			List<User> followings = socialService.getFollowings(entityId);
		
			response = new ResponseEntity<List<User>>(followings, HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in getFollowings  for entityId " + entityId, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in getFollowings  for entityId " + entityId, e);
		}
		
		return response;
	}
	
	@RequestMapping(value = "/social/getConnectionsByStatus/{entityId}/", method = RequestMethod.GET)
	public ResponseEntity<?> getConnectionsByStatus(@PathVariable (value = "entityId", required = true) String entityId,
			@RequestParam (value = "status", required = false) String status) {
		logger.info("Fetching getConnectionsByStatus  for entityId {}", entityId);
		if(status == null){
			status = SystemConstants.REQUESTED_CONNECTION;
		}
		ResponseEntity response = null;
		try{
			List<Connection> connections = socialService.getConnections(entityId, status);
			
			response = new ResponseEntity<List<Connection>>(connections, HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in getConnectionsByStatus for " + entityId, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in getConnectionsByStatus for " + entityId, e);
		}
		
		return response;
	}

	@RequestMapping(value = "/social/getConnectionsEntityId/{entityId}/", method = RequestMethod.GET)
	public ResponseEntity<List<String>> getConnectionsEntityId(@PathVariable (value = "entityId", required = true) String entityId,
			@RequestParam (value = "action", required = false) String action) {
		logger.info("Fetching getConnectionsEntityId  for entityId {}", entityId);

		String status = "AWAITING";
		ResponseEntity response = null;
		List<String> usersList = null;
		try{
		  if(action.equalsIgnoreCase("approvalPending")){
	        status = "AWAITING";
	      }else if(action.equalsIgnoreCase("requestSent")){
	        status = "REQUESTED";
	      }else if(action.equalsIgnoreCase("followers")){
	        status = SystemConstants.ACCEPTED_CONNECTION;
	      }else if(action.equalsIgnoreCase("followings")){
	        status = SystemConstants.FOLLOWING_CONNECTION;
	      }
		    
		  usersList = socialService.getConnectionsEntityId(entityId, status);

		  response = new ResponseEntity<List<String>>(usersList, HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in getConnectionsEntityId for " + entityId, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in getConnectionsEntityId for " + entityId, e);
		}
		
		return response;
	}

	//OBSOLETE, USE followPerson INSTEAD
	@RequestMapping(value = "/social/followDistrict", method = RequestMethod.POST)
	public ResponseEntity<?> followDistrict(@RequestBody Connection connection) {
		logger.info("followDistrict between " + connection.getSourceEntityId() + ", " + connection.getTargetEntityId());
		ResponseEntity response = null;
		try{
		connection = socialService.follow(connection);

			response = new ResponseEntity<Connection>(connection, HttpStatus.OK);
		}catch(Exception e){
			response = new ResponseEntity<String>("Error in followDistrict between " + connection.getSourceEntityId() + ", " + connection.getTargetEntityId(), HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in followDistrict between " + connection.getSourceEntityId() + ", " + connection.getTargetEntityId(), e);
		}
		
		return response;
	}	

	@RequestMapping(value = "/social/followPerson", method = RequestMethod.POST)
	public ResponseEntity<?> followPerson(@RequestBody Connection connection) {
		logger.info("followPerson between " + connection.getSourceEntityId() + ", " + connection.getTargetEntityId());

		//Get the user data, set FOLLOWING by default in case of PASSIVE profile
		User targetUser;
		ResponseEntity response = null;
		try {
			//TODO
			//BASED ON ENTITY TYPE, SHOULD DECIDE USER OR OTHER ENTITY LIKE DISTRICT, PARTY
			targetUser = userService.getUser(connection.getTargetEntityId(), false);
			if(targetUser.getStatus().equalsIgnoreCase(SystemConstants.PASSIVE_USER)){
				connection.setStatus(SystemConstants.FOLLOWING_CONNECTION);
			}
			
			connection = socialService.follow(connection);
			
			response = new ResponseEntity<Connection>(connection, HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<String>("Error in followPerson between " + connection.getSourceEntityId() + ", " + connection.getTargetEntityId(), HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in followPerson between " + connection.getSourceEntityId() + ", " + connection.getTargetEntityId(), e);
		}


		return response;
	}	
	
	@RequestMapping(value = "/social/connectionAction", method = RequestMethod.POST)
	public ResponseEntity<?> updateConnection(@RequestBody Connection connection) {
		logger.info("Updating Connection information " + connection.getSourceEntityId() 
		+ ", connected entity " + connection.getTargetEntityId()
		+ ", status " + connection.getStatus());
		try {
			connection = socialService.connectionAction(connection);
		} catch (Exception e) {
			String errorMessage = "Connection not found";
			logger.error(errorMessage, e);
			return new ResponseEntity(new CustomErrorType(errorMessage), HttpStatus.NOT_FOUND);
			
		}
 		return new ResponseEntity<Connection>(connection, HttpStatus.OK);
	}	
	
	@RequestMapping(value = "/social/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> unFollow(@PathVariable("id") String id) {
		logger.info("Fetching & Deleting User by unFollow  with id {}", id);

		List<User> users = userService.findAllUsers();

/*		if (users == null) {
			logger.error("Unable to delete. User by unFollow  with id {} not found.", id);
			return new ResponseEntity<Object>(
					new CustomErrorType("Unable to delete. User by unFollow  with id  " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}*/
		userService.deleteUserById(id);
		return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
	}

	//OBSOLETE
/*	@GetMapping
	public String requestConnection(Model model) {
		if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
			return "redirect:/connect/facebook";
		}

		model.addAttribute("facebookProfile", facebook.userOperations().getUserProfile());
		PagedList<Post> feed = facebook.feedOperations().getFeed();
		model.addAttribute("feed", feed);
		return "hello";
	}
*/
}