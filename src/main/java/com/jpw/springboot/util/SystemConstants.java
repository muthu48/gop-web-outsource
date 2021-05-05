package com.jpw.springboot.util;

public class SystemConstants {
	
	////USER CATEGORY
	public final static String USERTYPE_PUBLIC = "USER";
	public final static String USERTYPE_LEGIS = "LEGISLATOR";//OBSOLETE
	public final static String USERTYPE_STATELEGIS = "LEGISLATOR";//OBSOLETE
	public final static String USERTYPE_CONGRESSLEGIS = "CONGRESS LEGISLATOR";	
	public final static String USERTYPE_EXECUTIVE = "EXECUTIVE"; //roles includes governor, mayor, secretary of state, chief election officer
	public final static String USERCATEGRORY_LEGISLATURE = "LEGISLATURE";
	public final static String USERCATEGRORY_EXECUTIVE = "EXECUTIVE";
	public final static String USERCATEGRORY_MUNICIPALITIES = "MUNICIPALITIES";
	public final static String USERCATEGRORY_RETIRED = "RETIRED";
	public final static String USERTYPE_LEGISLATIVE_DISTRICT = "LEGISLATIVE DISTRICT";
	public final static String USERTYPE_POLITICAL_PARTY = "POLITICAL PARTY";
	//public final static String USERCATEGRORY_COMMITTEE_CONGRESS = "Congress Committee";//OBSOLETE
	public final static String USERCATEGRORY_SUBCOMMITTEE_LEGISLATIVE = "LEGISLATIVE SUB COMMITTEE";//OBSOLETE
	public final static String USERCATEGRORY_COMMITTEE_LEGISLATIVE = "LEGISLATIVE COMMITTEE";
	
	
	//GOVTRACK USER TYPES
	//prez, viceprez, rep, sen
	public final static String USERTYPE_GT_CONGRESS_PRESIDENT = "prez";
	public final static String USERTYPE_GT_CONGRESS_VICEPRESIDENT = "viceprez";
	public final static String USERTYPE_GT_CONGRESS_REP = "rep";
	public final static String USERTYPE_GT_CONGRESS_SENATE = "sen";

	//USER ROLES
	public final static String USERTYPE_CONGRESS_PRESIDENT = "President";
	public final static String USERTYPE_CONGRESS_VICEPRESIDENT = "Vice President";
	public final static String USERTYPE_CONGRESS_REP = "U.S. Representative";
	public final static String USERTYPE_CONGRESS_SENATOR = "U.S. Senator";
	public final static String USERTYPE_HOUSE_REP = "House Representative"; //lower
	public final static String USERTYPE_SENATOR = "Senator";//upper
	public final static String USERTYPE_COMMITTEE_CONGRESS_HOUSE = "Congress House Committee";
	public final static String USERTYPE_COMMITTEE_CONGRESS_SENATE = "Congress Senate Committee";
	public final static String USERTYPE_COMMITTEE_CONGRESS_JOINT = "Congress Joint Committee";
	
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
	public final static String PROFILE_TEMPLATE_VOTE = "upVote";
	public final static String PROFILE_TEMPLATE_EVENT = "upEvent";
	public final static String PROFILE_TEMPLATE_MEMBER = "upMember";
	public final static String PROFILE_TEMPLATE_BILL = "upBill";
	
	//READ PAGINATION
	public final static int POST_PAGINATION_SIZE = 5;
	
	//SMALL PROFILE IMAGE METADATA
	public final static String SMALL_PROFILE_IMAGE_METADATA = "SMPROFILEIMAGE";
}
