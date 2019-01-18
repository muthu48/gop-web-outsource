package com.jpw.springboot.util;

public enum Entities {
	USER("users"), LEGISLATOR("legislator"), DISTRICT("district"), PARTY("party");  // Named constants
	   
   private final String value;      // Private variable
   
   Entities(String value) {     // Constructor
      this.value = value;
   }
   
   public String getValue() {              // Getter
      return value;
   }
}

