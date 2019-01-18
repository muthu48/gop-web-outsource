package com.jpw.springboot;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {
	public JWTAuthenticationFilter(AuthenticationManager authManager) {
	    super(authManager);
	}

	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response,
             FilterChain filterChain)
      throws IOException, ServletException {
	
		String header = request.getHeader(TokenAuthenticationService.HEADER_STRING);

        if (header == null || !header.startsWith(TokenAuthenticationService.TOKEN_PREFIX)) {
        	filterChain.doFilter(request, response);
            return;
        }
        
		Authentication authentication = TokenAuthenticationService
	    .getAuthentication((HttpServletRequest)request);
	
		SecurityContextHolder.getContext()
		    .setAuthentication(authentication);
		filterChain.doFilter(request,response);
	}
}