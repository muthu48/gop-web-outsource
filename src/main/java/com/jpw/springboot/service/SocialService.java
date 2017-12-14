package com.jpw.springboot.service;

import com.jpw.springboot.model.Activites;

public interface SocialService {

	
	Activites findByUserId(String id);

}