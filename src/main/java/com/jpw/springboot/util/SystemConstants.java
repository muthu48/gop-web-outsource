package com.jpw.springboot.util;

public class SystemConstants {
	//CONNECTION STATUS
	public final static String REQUESTED_CONNECTION = "REQUESTED";
	public final static String FOLLOWING_CONNECTION = "FOLLOWING";
	public final static String REJECTED_CONNECTION = "REJECTED";
	public final static String ACCEPTED_CONNECTION = "ACCEPTED";
	public final static String AWAITING_CONNECTION = "AWAITING";
	
	//USER PROFILE STATUS
	public final static String PASSIVE_USER = "PASSIVE";

	//LEGISLATOR SOURCE
	public final static String OPENSTATE_LEGIS_SOURCE = "OPENSTATE";
	public final static String GOVTRACK_LEGIS_SOURCE = "GOVTRACK";
	
	//USER TYPE
	public final static String PUBLIC_USERTYPE = "PUBLICUSER";
	public final static String STATELEGIS_USERTYPE = "LEGISLATOR";
	public final static String CONGRESSLEGIS_USERTYPE = "LEGISLATORCONGRESS";	

	//PROFILE TEMPLATES
	public final static String PROFILE_TEMPLATE_BIODATA = "upDefault";
	public final static String PROFILE_TEMPLATE_BIODATA_EXTERNAL = "upCongressLegislatorExternal";
	public final static String PROFILE_TEMPLATE_ROLE = "upRole";
	public final static String PROFILE_TEMPLATE_OFFICE = "upOffices";
	public final static String PROFILE_TEMPLATE_COMMITTEE = "upCongressLegislatorCommitteeExternal";
}
