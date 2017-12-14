package com.jpw.springboot.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.jpw.springboot.model.Post;
import com.jpw.springboot.model.User;
import com.jpw.springboot.service.PostService;
import com.jpw.springboot.service.UserService;
import com.mongodb.util.JSON;
//import com.jpw.springboot.util.CustomErrorType;
//import com.jpw.springboot.util.EnumTags;

@RestController
@RequestMapping("/position")
public class PositionManagementController {

	public static final Logger logger = LoggerFactory.getLogger(PositionManagementController.class);

	@Autowired
	PostService postService;

	@Autowired
	UserService userService;


	@RequestMapping(value = "/tag", method = RequestMethod.POST)
	public ResponseEntity<Post> tagPosition(@RequestBody JSON request) {
		logger.info("tag All Position");

		/*User user = userService.findByName(id);
		if (user != null) {
		
		}*/
		return new ResponseEntity<Post>(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/position/untagPosition", method = RequestMethod.GET)
	public ResponseEntity<Post> untagPosition(@PathVariable("id") String id) {
		logger.info("untag All Position");

		User user = userService.findByName(id);
		if (user != null && user.getTagName() != null) {
			//user.setTagName(EnumTags.tagNames.EMPTY);

		}
		return new ResponseEntity<Post>(HttpStatus.NO_CONTENT);
	}

}