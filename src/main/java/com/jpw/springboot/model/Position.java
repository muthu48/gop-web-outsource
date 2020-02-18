package com.jpw.springboot.model;

import org.springframework.data.annotation.Id;

public class Position extends AbstractModel{
	@Id	
	private String id;		
	private String positionName;
	private String status;
	//private ProfileData profileData[];		
	
	/*
	       "district":"33",
            "level":"state",
            "country":"us",
            "chamber":"upper",
            "state":"pa",
            "party":"Republican",
            "type":"member",

	 */
	//type - commmitte member
}
