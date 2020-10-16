package com.jpw.springboot.util;

public class SystemConstants {
	//USER/ENTITY TYPE
	public final static String USERTYPE_PUBLIC = "PUBLICUSER";
	public final static String USERTYPE_LEGIS = "LEGISLATOR";
	public final static String USERTYPE_STATELEGIS = "LEGISLATOR";
	public final static String USERTYPE_CONGRESSLEGIS = "LEGISLATORCONGRESS";	
	public final static String USERTYPE_EXECUTIVE = "LEGISLATOREXECUTIVE";
	public final static String USERTYPE_LEGISLATIVE_DISTRICT = "LEGISLATIVE_DISTRICT";
	public final static String USERTYPE_POLITICAL_PARTY = "POLITICAL_PARTY";
	
	//LEGISLATOR SOURCE
	public final static String OPENSTATE_LEGIS_SOURCE = "OPENSTATE";
	public final static String GOVTRACK_LEGIS_SOURCE = "GOVTRACK";
	
	//CONNECTION STATUS
	public final static String AWAITING_CONNECTION = "AWAITING";
	public final static String REQUESTED_CONNECTION = "REQUESTED";
	public final static String ACCEPTED_CONNECTION = "ACCEPTED";
	public final static String FOLLOWING_CONNECTION = "FOLLOWING";
	public final static String REJECTED_CONNECTION = "REJECTED";
	public final static String CANCELLED_CONNECTION = "CANCELLED";
	
	//USER PROFILE STATUS
	public final static String PASSIVE_USER = "PASSIVE";
	public final static String ACTIVE = "ACTIVE";
	
	//USER PROFILE ACCESS RESTRICTION
	public final static String ACCESS_RESTRICTION_PUBLIC = "PUBLIC";
	public final static String ACCESS_RESTRICTION_PRIVATE = "PRIVATE";
	
	//PROFILE TEMPLATES
	public final static String PROFILE_TEMPLATE_BIODATA = "upDefault";
	public final static String PROFILE_TEMPLATE_BIODATA_EXTERNAL = "upCongressLegislatorExternal";
	public final static String PROFILE_TEMPLATE_ROLE = "upRole";
	public final static String PROFILE_TEMPLATE_OFFICE = "upOffices";
	public final static String PROFILE_TEMPLATE_COMMITTEE = "upCongressLegislatorCommitteeExternal";
	
	//READ PAGINATION
	public final static int POST_PAGINATION_SIZE = 5;
	
	//SMALL PROFILE IMAGE METADATA
	public final static String SMALL_PROFILE_IMAGE_METADATA = "SMPROFILEIMAGE";
}
