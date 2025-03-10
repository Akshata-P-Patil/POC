/*
 * WebSecurityLocalConfiguration.java
 * Description: Class to handle security aspects for local profile
 * Creation date: Mon Feb 10 11:06:35 2020
 * Author: mathieu.sescosse.external@saftbatteries.com
 * Copyright (c) 2021
 * All rights reserved by Saft
 */

package com.saft.pack_generator.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.util.Arrays;


@Configuration
@EnableWebSecurity
public class WebSecurityLocalConfiguration extends WebSecurityConfigurerAdapter {

    public static final String AUTHORIZED_ROLE = "authorizedRoleName";
    private final StatelessAuthenticationFilter statelessAuthenticationFilter;

    private LoadUser loadUser;

    public WebSecurityLocalConfiguration(StatelessAuthenticationFilter statelessAuthenticationFilter, LoadUser loadUser) {
        super(true);
        this.statelessAuthenticationFilter = statelessAuthenticationFilter;
        this.loadUser = loadUser;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // we use jwt so that we can disable csrf protection
//        http.csrf().disable().cors().and().authorizeRequests().anyRequest().permitAll()
//                .and()
//        http.addFilterBefore(statelessAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeRequests().anyRequest().permitAll().and().addFilterBefore(statelessAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

//        http.headers().frameOptions().disable()
//                .addHeaderWriter(new StaticHeadersWriter("X-FRAME-OPTIONS", "ALLOW-FROM *"))
//                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin", "*"))
//                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Credentials", "true"))
//                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Methods", "ALLOW-FROM *"))
//                .addHeaderWriter(new StaticHeadersWriter("X-FRAME-OPTIONS", "GET, POST, PUT, OPTIONS, DELETE"))
//                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Headers", "DNT,X-Mx-ReqToken,Keep-Alive,User-Agent,X-Requested-With,Authorization,If-Modified-Since,Cache-Control,Content-Type"))
//                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Max-Age", "3600"));
////        http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());
    }

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("*");
//    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Prevent StatelessAuthenticationFilter will be added to Spring Boot filter chain.
     * Only Spring Security must use it.
     */
    @Bean
    public FilterRegistrationBean<StatelessAuthenticationFilter> registration(StatelessAuthenticationFilter filter) {
        FilterRegistrationBean<StatelessAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider
                = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(this.loadUser);
        return authProvider;
    }

//    @Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOriginPattern("*");
//        config.addExposedHeader(HttpHeaders.CONTENT_DISPOSITION);
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("OPTIONS");
//        config.addAllowedMethod("GET");
//        config.addAllowedMethod("POST");
//        config.addAllowedMethod("PUT");
//        config.addAllowedMethod("DELETE");
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }

}
