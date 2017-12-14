package com.jpw.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpw.springboot.model.Activites;
import com.jpw.springboot.repositories.SocialServiceRepository;

@Service("socialService")
@Transactional
public class SocialServiceImpl implements SocialService {

	@Autowired
	private SocialServiceRepository socialServiceRepository;

	@Override
	public Activites findByUserId(String id) {
		return socialServiceRepository.findByUserId(id);
	}

	

}
