package com.jpw.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security
            .authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.jpw.springboot.service.UserService;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static java.util.Collections.emptyList;

public class TokenAuthenticationService {
  static final long EXPIRATIONTIME = 864_000_000; // 10 days
  static final String SECRET = "ThisIsASecret";
  static final String TOKEN_PREFIX = "Bearer ";
  static final String HEADER_STRING = "Authorization";
  static final String SIGN_UP_URL = "/user";
	
  //Generate Token after successful login
  static void addAuthentication(HttpServletResponse res, String username) {
//    String token = Jwts.builder()
//        .setSubject(username)
//        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
//        .signWith(SignatureAlgorithm.HS512, SECRET.getBytes())
//        .compact();
    String token = JWT.create()
            .withSubject(username)
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
            .sign(HMAC512(SECRET.getBytes()));
    res.addHeader("Access-Control-Expose-Headers", "Authorization");
    res.addHeader("Access-Control-Allow-Headers", "Authorization, X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept, X-Custom-header"); 
    res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
  }

  public static ResponseEntity addAuthentication(String subject) {
    String token = JWT.create()
            .withSubject(subject)
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
            .sign(HMAC512(SECRET.getBytes()));
    
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Access-Control-Expose-Headers", "Authorization");
    responseHeaders.set("Access-Control-Allow-Headers", "Authorization, X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept, X-Custom-header"); 
    responseHeaders.set(HEADER_STRING, TOKEN_PREFIX + token);

  
    return ResponseEntity.ok()
      .headers(responseHeaders)
      .body("App token"); 
  }

  //Parse the token to authenticate an User
  static Authentication getAuthentication(HttpServletRequest request) {
    String token = request.getHeader(HEADER_STRING);
    if (token != null) {
      // parse the token.
/*      String user = Jwts.parser()
          .setSigningKey(SECRET)
          .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
          .getBody()
          .getSubject();
*/      
    	String user = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""))
                .getSubject();
    	
    	return user != null ?
          new UsernamePasswordAuthenticationToken(user, null, emptyList()) :
          null;
    }
    return null;
  }
}