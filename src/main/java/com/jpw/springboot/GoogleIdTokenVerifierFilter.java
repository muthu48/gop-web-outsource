package com.jpw.springboot;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.jpw.springboot.util.GoogleTokenVerifier;

import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//OBSOLETE
//@Component
public class GoogleIdTokenVerifierFilter implements Filter {
	  @Autowired
	  private GoogleTokenVerifier googleTokenVerifier;


	  @Override
	  public void init(FilterConfig filterConfig) throws ServletException {
	  }

	  @Override
	  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
	      FilterChain filterChain) throws IOException, ServletException {

	    String idToken = ((HttpServletRequest) servletRequest).getHeader("X-ID-TOKEN");
	    HttpServletResponse response = (HttpServletResponse) servletResponse;

	    if (idToken != null) {
	      final Payload payload;
	      try {
	        payload = googleTokenVerifier.verify(idToken);
	        if (payload != null) {
	          String username = payload.getSubject();
	          TokenAuthenticationService.addAuthentication(response, username);
	          filterChain.doFilter(servletRequest, response);
	          return;
	        }
	      } catch (Exception e) {
	        // This is not a valid token, we will send HTTP 401 back
	      }
	    }
	    ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED);
	  }

	  @Override
	  public void destroy() {
	  }
}
