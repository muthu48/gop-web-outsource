package com.jpw.springboot;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.util.UrlPathHelper;

import com.jpw.springboot.service.UserServiceImpl;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private UserServiceImpl userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    //private final GoogleIdTokenVerifierFilter googleIdTokenVerifierFilter;

    @Autowired
    public WebSecurityConfig(UserServiceImpl userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        //this.googleIdTokenVerifierFilter = googleIdTokenVerifierFilter;
    }
	    
  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  
//    http.csrf().disable().authorizeRequests()
//        .antMatchers("/").permitAll();
    /*
        .antMatchers(HttpMethod.POST, "/login").permitAll()
        .anyRequest().authenticated()
        .and()
        // We filter the api/login requests
        .addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
                UsernamePasswordAuthenticationFilter.class)
        // And filter other requests to check the presence of JWT in header
        .addFilterBefore(new JWTAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class);
      */          
	  
	  http.cors().and().csrf().disable()
	  .authorizeRequests()
      //Login url is handled through Angular routing
	  .antMatchers(HttpMethod.POST, TokenAuthenticationService.SIGN_UP_URL).permitAll()
	  //.antMatchers("/user/legis/**", "/post/downloadFile/user/**", "/user/legisv1/**", "/api/social/getFollowersCount/**").permitAll()
	  //.antMatchers(HttpMethod.POST, TokenAuthenticationService.SIGN_UP_URL, "/user/legis/**", "/user/legis/biodata/**", "/user/legisv1/**").permitAll()
	  .antMatchers("/user/**").permitAll()
	  .antMatchers("/user/legis/**").permitAll()
	  .antMatchers("/user/legisv1/**").permitAll()
	  .antMatchers("/user/legis/biodata/**").permitAll()
	  .antMatchers("/post/downloadFile/**").permitAll()
	  .antMatchers("/profile/template/**").permitAll()
      .antMatchers(HttpMethod.POST, "/tokenVerify/**").permitAll()
      .antMatchers(HttpMethod.GET, "/api/social/**").permitAll()
	  .anyRequest().authenticated()
      .and()
      .addFilter(new JWTLoginFilter(authenticationManager()))
      .addFilter(new JWTAuthenticationFilter(authenticationManager()))
      // this disables session creation on Spring Security
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    // Create a default account
/*    auth.inMemoryAuthentication()
        .withUser("admin")
        .password("password")
        .roles("ADMIN");
*/    
	  auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);

  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
    return source;
  }	

  @Bean
	    public WebMvcConfigurer corsConfigurer() {
	        return new WebMvcConfigurerAdapter() {
	            @Override
	            public void addCorsMappings(CorsRegistry registry) {
	                registry.addMapping("/**")
	                //.allowedOrigins("http://localhost:4200", "https://gopolitix.com", "https://www.gopolitix.com")
	                .allowedOrigins("https://gopolitix.com", "https://www.gopolitix.com")
	                .allowedMethods("GET", "POST", "PUT", "DELETE","OPTIONS")
	                .allowedHeaders("*");
	            }
	            
	            public void configurePathMatch(PathMatchConfigurer configurer) {
	                UrlPathHelper urlPathHelper = new UrlPathHelper();
	                urlPathHelper.setRemoveSemicolonContent(false);
	                configurer.setUrlPathHelper(urlPathHelper);
	            }

	        };
  }
  /*
  @Bean
  public FilterRegistrationBean googleIdTokenVerifierBean() {
    FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
    filterRegistrationBean.setFilter(googleIdTokenVerifierFilter);
    filterRegistrationBean.setUrlPatterns(Collections.singletonList("/gTokenVerify/*"));
    return filterRegistrationBean;
  }
  */
}
