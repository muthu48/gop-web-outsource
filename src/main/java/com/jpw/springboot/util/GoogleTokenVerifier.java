package com.jpw.springboot.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.jpw.springboot.service.UserServiceImpl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GoogleTokenVerifier  {
	  public static final Logger logger = LoggerFactory.getLogger(GoogleTokenVerifier.class);

	  private static final HttpTransport transport = new NetHttpTransport();
	  private static final JsonFactory jsonFactory = new JacksonFactory();
	  private static final String CLIENT_ID = "300979965528-5oo12abv1dtekvh6ugtgmfvofm8h903p.apps.googleusercontent.com";
	  
	  public Payload verify(String idTokenString)
	      throws GeneralSecurityException, IOException, Exception {
	    return GoogleTokenVerifier.verifyToken(idTokenString);
	  }

	  private static Payload verifyToken(String idTokenString)
	      throws Exception {
	    final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.
	        Builder(transport, jsonFactory)
	        .setIssuers(Arrays.asList("https://accounts.google.com", "accounts.google.com"))
	        .setAudience(Collections.singletonList(CLIENT_ID))
	        .build();

	    GoogleIdToken idToken = null;
        idToken = verifier.verify(idTokenString);

	    if (idToken == null) {
	      logger.error("idToken is invalid");
	      throw new Exception("idToken is invalid");
	    }

	    return idToken.getPayload();
	  }
}
