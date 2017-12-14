package com.jpw.springboot.model;

import org.springframework.data.annotation.Id;

public class Connection {
	@Id	
	private String id;		
	private String userId;
	private String connectionUserId;
	private String groupId;
	private String positionId;
	private String status; //REQUESTED, CONNECTED, REJECTED
	private boolean defaultConnected = false; //
}
