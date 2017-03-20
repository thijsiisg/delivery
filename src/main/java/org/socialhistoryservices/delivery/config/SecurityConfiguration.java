package org.socialhistoryservices.delivery.config;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import org.socialhistoryservices.delivery.user.dao.GroupDAO;
import org.socialhistoryservices.delivery.user.dao.UserDAO;
import org.socialhistoryservices.delivery.user.service.AuthoritiesPopulator;
import org.socialhistoryservices.delivery.user.service.UserServiceImpl;
import org.socialhistoryservices.utils.CaptchaEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.web.firewall.DefaultHttpFirewall;

/**
 * Created by Igor on 3/3/2017.
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(DeliveryProperties.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{

    @Autowired private GroupDAO groupDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private CaptchaEngine captchaEngine;
    @Autowired private Environment env;
    @Autowired private DeliveryProperties deliveryProperties;

    @Override
    public void configure(WebSecurity web) throws Exception {
        DefaultHttpFirewall firewall = new DefaultHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        web.httpFirewall(firewall);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
            .authorizeRequests()
//                .antMatchers("/login").permitAll()
                .antMatchers("/css/**", "/js/**", "/logo.ico").permitAll()
                .anyRequest().authenticated()
                .and()
            // Disable Cross-Site Request Forgery token
            .csrf().disable()
            // Disable HTTP Basic authentication
            .httpBasic().disable()
            // What is our login/logout page?
            .formLogin()
                .defaultSuccessUrl("/")
                .loginPage("/user/login")
                .failureForwardUrl("/user/login?error=true")
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/user/logout")
                .logoutSuccessUrl("/user/logout-success")
                .invalidateHttpSession(true)
                .permitAll();

        // If we are running H2 in development mode, then make sure we can always reach the H2 console
        if (this.env.acceptsProfiles("development") && this.env.acceptsProfiles("h2")) {
            httpSecurity
                .authorizeRequests()
                .antMatchers("/console/**").permitAll()
                .and()
                .headers()
                .frameOptions().disable();
        }

        // If an auth profile has been chosen, close all other requests: the user needs to be authenticated first
        if (this.env.acceptsProfiles("ldapAuth", "dbAuth")) {
            httpSecurity.authorizeRequests()
                .antMatchers("/").authenticated()
                .anyRequest().hasRole("USER");
        }
    }

    @Bean
    public UserServiceImpl userDetailsService(){
        UserServiceImpl userService = new UserServiceImpl();
        userService.setUserDAO(userDAO);
        userService.setGroupDAO(groupDAO);
        return userService;
    }

    @Bean
    public CaptchaEngine captchaEngine(){
        return new CaptchaEngine();
    }

    @Bean
    public DefaultManageableImageCaptchaService captchaService(){
        DefaultManageableImageCaptchaService defaultManageableImageCaptchaService = new DefaultManageableImageCaptchaService();
        defaultManageableImageCaptchaService.setCaptchaEngine(captchaEngine);
        return defaultManageableImageCaptchaService;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(deliveryProperties.getLdapUrl());
        contextSource.setUserDn(deliveryProperties.getLdapManagerDn());
        contextSource.setPassword(deliveryProperties.getLdapManagerPassword());
        contextSource.afterPropertiesSet();

        AuthoritiesPopulator ldapAuthoritiesPopulator =
            new AuthoritiesPopulator(contextSource, deliveryProperties.getLdapUserSearchBase(), userDetailsService());

        authenticationManagerBuilder
            .ldapAuthentication()
            .contextSource(contextSource)
            .userSearchBase(deliveryProperties.getLdapUserSearchBase())
            .userSearchFilter(deliveryProperties.getLdapUserSearchFilter())
            .ldapAuthoritiesPopulator(ldapAuthoritiesPopulator);
    }
}
