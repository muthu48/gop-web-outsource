package com.jpw.springboot.model;

import org.springframework.data.annotation.Id;

public class Position {
	@Id	
	private String id;		
	private String positionName;
	private ProfileData profileData[];		
}
