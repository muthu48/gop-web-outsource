package com.jpw.springboot;

//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security
            .authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static java.util.Collections.emptyList;

class TokenAuthenticationService {
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
    
    res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
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