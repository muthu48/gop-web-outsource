package com.jpw.springboot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;

@Document
public class Notification extends AbstractModel {
	@Id	
	private String id;	
	private String source; 
	private String target;
	private String[] otherTargets;
	private String type; // email, chat, follow, NEWS, etc
	private String message;

	@Override
	public String toString() {
		return "Notification{" +
				"id='" + id + '\'' +
				", source='" + source + '\'' +
				", target='" + target + '\'' +
				", otherTargets=" + Arrays.toString(otherTargets) +
				", type='" + type + '\'' +
				", message='" + message + '\'' +
				'}';
	}

	public Notification() {
	}

	public Notification(String source, String[] otherTargets, String type, String message) {
		this.source = source;
		this.otherTargets = otherTargets;
		this.type = type;
		this.message = message;
	}

	public Notification(String source, String target, String type, String message) {
		this.source = source;
		this.target = target;
		this.type = type;
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String[] getOtherTargets() {
		return otherTargets;
	}

	public void setOtherTargets(String[] otherTargets) {
		this.otherTargets = otherTargets;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
