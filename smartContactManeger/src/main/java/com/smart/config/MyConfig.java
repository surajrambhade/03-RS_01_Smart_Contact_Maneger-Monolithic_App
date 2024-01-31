package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class MyConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImple();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
	 DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}
	// configure method 
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.authenticationProvider(authenticationProvider());

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN")
		.antMatchers("/user/**").hasRole("USER")
		.antMatchers("/**").permitAll().and().formLogin()
		.loginPage("/signin")
		.loginProcessingUrl("/dologin")
		.defaultSuccessUrl("/user/index")
		.and().csrf().disable();
		
		
		//.failureUrl("/login-fail") fail hone k bad page dal sakte hai 
	}

}


































/*
* ---------------------------------------------package com.smart.config;
* 
* import org.springframework.context.annotation.Bean; import
* org.springframework.context.annotation.Configuration; import
* org.springframework.security.authentication.AuthenticationManager; import
* org.springframework.security.authentication.dao.DaoAuthenticationProvider;
* import
* org.springframework.security.config.annotation.authentication.builders.
* AuthenticationManagerBuilder; import
* org.springframework.security.config.annotation.authentication.configuration.
* AuthenticationConfiguration; import
* org.springframework.security.config.annotation.web.builders.HttpSecurity;
* import org.springframework.security.config.annotation.web.configuration.
* EnableWebSecurity; import
* org.springframework.security.config.annotation.web.configuration.
* WebSecurityConfigurerAdapter; import
* org.springframework.security.core.userdetails.UserDetailsService; import
* org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import
* org.springframework.security.web.DefaultSecurityFilterChain; import
* org.springframework.security.web.SecurityFilterChain;
* 
* @Configuration
* 
* @EnableWebSecurity // Security Config public class MyConfig {
* 
* @Bean public UserDetailsService getUserDetailsService() { return new
* UserDetailsServiceImple(); }
* 
* @Bean public BCryptPasswordEncoder passwordEncoder() {
* 
* return new BCryptPasswordEncoder(); }
* 
* @Bean public DaoAuthenticationProvider authenticationProvider() {
* DaoAuthenticationProvider daoAuthenticationProvider = new
* DaoAuthenticationProvider();
* daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService())
* ; daoAuthenticationProvider.setPasswordEncoder(passwordEncoder()); return
* daoAuthenticationProvider; } // configure method
* 
* @Override protected void configure(AuthenticationManagerBuilder auth) throws
* Exception {
* 
* auth.authenticationProvider(authenticationProvider());
* 
* }
* 
* 
* @Bean public AuthenticationManager
* authenticationManager(AuthenticationConfiguration configuration) throws
* Exception { return configuration.getAuthenticationManager();
* 
* }
* 
* 
* public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
* Exception {
* 
* http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN").
* antMatchers("/user/**").hasRole("USER")
* .antMatchers("/**").permitAll().and().formLogin().loginPage("/signin").
* loginProcessingUrl("/dologin")
* .defaultSuccessUrl("/user/index").and().csrf().disable();
* 
* http.authenticationProvider(daoAuthenticationProvider());
* DefaultSecurityFilterChain defaultSecurityFilterChain = http.build(); return
* defaultSecurityFilterChain;
* 
* }
* 
* @Bean public DaoAuthenticationProvider daoAuthenticationProvider() {
* DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
* provider.setUserDetailsService(this.getUserDetailsService());
* provider.setPasswordEncoder(passwordEncoder()); return provider;
* 
* }
* 
* 
* 
* @Override protected void configure(HttpSecurity http) throws Exception { http
* .authorizeRequests() .antMatchers("/admin/**") .hasRole("ADMIN")
* .antMatchers("/user/**") .hasRole("USER") .antMatchers("/**") .permitAll()
* .and() .formLogin() .loginPage("/signin") .loginProcessingUrl("/dologin")
* .defaultSuccessUrl("/user/index") .and().csrf().disable();
* 
* 
* //.failureUrl("/login-fail") fail hone k bad page dal sakte hai }
* 
* }
*/

