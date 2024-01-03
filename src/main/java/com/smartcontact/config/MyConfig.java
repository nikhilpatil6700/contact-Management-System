package com.smartcontact.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig {

	@Bean
	public UserDetailsService getUserDetailService()
	{
		return new UserDetailServiceimpl();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider()
	{
		DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(getUserDetailService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		
		return authenticationProvider;
		
	}

	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception
	{
	   	return auth.getAuthenticationManager();
	}
	
	
	@SuppressWarnings("deprecation")
	@Bean
	public SecurityFilterChain filterchain(HttpSecurity http)throws Exception
	{
					http.authorizeRequests().requestMatchers("/admin/**")
					.hasRole("ADMIN").requestMatchers("/user/**").hasRole("USER")
					.requestMatchers("/**").permitAll().and().formLogin()
					.loginPage("/signin")
					.loginProcessingUrl("/dologin")
					.and().csrf().disable();
				http.formLogin().defaultSuccessUrl("/user/index",true);
				return http.build();
}
}