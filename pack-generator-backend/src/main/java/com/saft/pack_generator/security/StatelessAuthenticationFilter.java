/*
 * StatelessAuthenticationFilter.java
 * Description: Class to handle authentication
 * Creation date: Mon Feb 10 11:06:35 2020
 * Author: mathieu.sescosse.external@saftbatteries.com
 * Copyright (c) 2021
 * All rights reserved by Saft
 */

package com.saft.pack_generator.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Component // one per request
public class StatelessAuthenticationFilter extends GenericFilterBean {
	
	private static final Logger log = LoggerFactory.getLogger(StatelessAuthenticationFilter.class);

	private static final String JWT_PREFIX = "Bearer";
	private static final String CORS_HEADER = "Access-Control-Allow-Origin";


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    	final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        
        
    	try {
        	if ("OPTIONS".equalsIgnoreCase(((HttpServletRequest )req).getMethod())
					|| request.getRequestURI().equals("/api/auth")
					|| request.getRequestURI().equals("/api/installation/info/software")
					|| ! request.getRequestURI().startsWith("/api/")) {
        		// return ok in case of :
        		// 1. CORS preflight OPTIONS (request that aims to checks server allows requests with the Authorization header)
        		// 2. trying to authenticate
				// 3. loading web static resources
				SecurityContext ctx = SecurityContextHolder.createEmptyContext();
				AuthenticationImpl authentication = new AuthenticationImpl();
				ctx.setAuthentication(authentication);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				setNewToken(authentication, response);
        		response.setStatus(HttpServletResponse.SC_OK);
				log.error("Option 1");
        	}
			else {
				log.error("Option 2");
				SecurityContext ctx = SecurityContextHolder.createEmptyContext();
				AuthenticationImpl authentication = new AuthenticationImpl();
				ctx.setAuthentication(authentication);
	            SecurityContextHolder.getContext().setAuthentication(authentication);
				setNewToken(authentication, response);
				response.setStatus(HttpServletResponse.SC_OK);
				log.error("Option 3");
        	}
			log.error("Option 4");
            chain.doFilter(req, res);
            SecurityContextHolder.getContext().setAuthentication(null);
			log.error("Option 5");
        } catch (AuthenticationException e) {
        	SecurityContextHolder.clearContext();
        	handleResponseError(response, "Authentication error", null);
        	log.error("Authentication error : {}", e.getMessage());
        }
    }
    


	private Authentication getRemoteAuthentication(HttpServletRequest request) throws UnsupportedEncodingException {
		// Assume we have only one Authorization header value
		final Optional<String> token = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));
		Authentication authentication = null;

		return authentication;
	}


    private void setNewToken(Authentication authentication, HttpServletResponse response) {
    	if (authentication != null) {
			String token = Jwts.builder()
					//.setSubject(user.getId().toString()) // expiration + session
					.signWith(Keys.secretKeyFor(SignatureAlgorithm.HS512))
					.setIssuedAt(new Date())
					.compact();
			response.setHeader(HttpHeaders.AUTHORIZATION, JWT_PREFIX + " " + token);
		}
	}

    private void handleResponseError(HttpServletResponse httpServletResponse, String message, String errorKey) throws IOException {
    	httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    	httpServletResponse.setHeader(CORS_HEADER, "*");
		if (errorKey != null) {
    		String json = "error";
            httpServletResponse.getWriter().write(json);
            httpServletResponse.flushBuffer();
    	}

    }
}
