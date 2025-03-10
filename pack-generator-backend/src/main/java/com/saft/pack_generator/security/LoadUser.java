/*
 * LoadUser.java
 * Description: This class is responsible for loading user
 * Creation date: Thu Aug 1 17:50:58 2019
 * Author: mathieu.sescosse.external@saftbatteries.com
 * Copyright (c) 2021
 * All rights reserved by Saft
 */

package com.saft.pack_generator.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoadUser implements UserDetailsService {


    @Override
    public UserDetails loadUserByUsername(String s) {
        UserDetails details = new UserDetailsImpl();
        if (details == null){
            throw new UsernameNotFoundException("User "+s+" not found");
        }

        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        AuthenticationImpl authentication = new AuthenticationImpl();
        ctx.setAuthentication(authentication);
        SecurityContextHolder.setContext(ctx);

        return details;
    }

}
