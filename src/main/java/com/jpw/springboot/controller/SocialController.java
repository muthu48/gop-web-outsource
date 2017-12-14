package com.jpw.springboot.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Post;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jpw.springboot.model.Activites;
import com.jpw.springboot.model.User;
import com.jpw.springboot.service.SocialService;
import com.jpw.springboot.service.UserService;
//import com.jpw.springboot.util.CustomErrorType;

@RestController
@RequestMapping("/api")
public class SocialController {

	public static final Logger logger = LoggerFactory.getLogger(SocialController.class);

	@Autowired
	SocialService socialService;

	@Autowired
	UserService userService;

	private Facebook facebook;
	private ConnectionRepository connectionRepository;

	public SocialController(Facebook facebook, ConnectionRepository connectionRepository) {
		this.facebook = facebook;
		this.connectionRepository = connectionRepository;
	}

	@RequestMapping(value = "/social/getActivites/{userId}", method = RequestMethod.GET)
	public ResponseEntity<?> getActivites(@PathVariable("userId") String userId) {
		logger.info("Fetching Activites with userId {}", userId);
		Activites activites = socialService.findByUserId(userId);
/*		if (activites == null) {
			logger.error("Activites with userId {} not found.", userId);
			return new ResponseEntity(new CustomErrorType("Activites with id " + userId + " not found"),
					HttpStatus.NOT_FOUND);
		}
*/		return new ResponseEntity<Activites>(activites, HttpStatus.OK);
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

	@GetMapping
	public String requestConnection(Model model) {
		if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
			return "redirect:/connect/facebook";
		}

		model.addAttribute("facebookProfile", facebook.userOperations().getUserProfile());
		PagedList<Post> feed = facebook.feedOperations().getFeed();
		model.addAttribute("feed", feed);
		return "hello";
	}

}