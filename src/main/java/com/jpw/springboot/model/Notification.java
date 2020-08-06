package com.jpw.springboot.model;

import org.springframework.data.annotation.Id;

public class Notification extends AbstractModel {
	@Id	
	private String id;	
	private String source; 
	private String target;
	private String[] otherTargets;
	private String type; 
	private String message;
}
