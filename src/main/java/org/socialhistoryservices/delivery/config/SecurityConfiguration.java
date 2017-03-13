package org.socialhistoryservices.delivery.config;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import org.socialhistoryservices.delivery.user.dao.GroupDAOImpl;
import org.socialhistoryservices.delivery.user.dao.UserDAOImpl;
import org.socialhistoryservices.delivery.user.service.AuthoritiesPopulator;
import org.socialhistoryservices.delivery.user.service.UserServiceImpl;
import org.socialhistoryservices.utils.CaptchaEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

/**
 * Created by Igor on 3/3/2017.
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(DeliveryProperties.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{

    private GroupDAOImpl groupDAOBean;
    private UserDAOImpl userDAOBean;
    private CaptchaEngine captchaEngine;

    //@Value("${prop_ldapUserSearchFilter}") private String[] userPatterns;
    @Value("${prop_ldapUrl}") private String ldapUrl;
    @Value("${prop_ldapManagerDn}") private String ldapManagerDn;
    @Value("${prop_ldapManagerPassword}") private String ldapManagerPassword;
    @Value("${prop_ldapUserSearchBase}") private String ldapUserSearchBase;
    @Value("${prop_ldapUserSearchFilter}") private String ldapUserSearchFilter;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
            .authorizeRequests()
                .antMatchers("/login").permitAll()
//                .antMatchers("/css/**", "/fonts/**", "/js/**").permitAll()
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
    }

   /* @Bean
    public DefaultSpringSecurityContextSource contextSource(){
        DefaultSpringSecurityContextSource defaultSpringSecurityContextSource = new DefaultSpringSecurityContextSource(ldapUrl);
        defaultSpringSecurityContextSource.setUserDn(ldapManagerDn);
        defaultSpringSecurityContextSource.setPassword(ldapManagerPassword);
        defaultSpringSecurityContextSource.afterPropertiesSet();

        LdapAuthoritiesPopulator ldapAuthoritiesPopulator =
            new LdapAuthoritiesPopulator(defaultSpringSecurityContextSource, this.ldapUserSearchBase, this.userRepository);

        authenticationManagerBuilder
            .ldapAuthentication()
            .contextSource(contextSource)
            .userSearchBase(this.ldapSearchBase)
            .userSearchFilter(this.ldapSearchFilter)
            .ldapAuthoritiesPopulator(ldapAuthoritiesPopulator);

       *//* return defaultSpringSecurityContextSource;


        authenticationManagerBuilder
            .ldapAuthentication()
            .contextSource(contextSource)
            .userSearchBase(this.ldapSearchBase)
            .userSearchFilter(this.ldapSearchFilter)
            .ldapAuthoritiesPopulator(ldapAuthoritiesPopulator);*//*
    }
*/
    /*@Bean
    public FilterBasedLdapUserSearch userSearch(){
        FilterBasedLdapUserSearch filterBasedLdapUserSearch = new FilterBasedLdapUserSearch(ldapUserSearchBase, ldapUserSearchFilter, contextSource());
        filterBasedLdapUserSearch.setSearchSubtree(true);
        return filterBasedLdapUserSearch;
    }

    @Bean
    public LdapAuthenticationProvider ldapAuthProvider(){
        BindAuthenticator bindAuthenticator = new BindAuthenticator(contextSource());
        bindAuthenticator.setUserDnPatterns(userPatterns);
        bindAuthenticator.setUserSearch(userSearch());

        AuthoritiesPopulator authoritiesPopulator = new AuthoritiesPopulator(contextSource(), "", userDetailsService());
        authoritiesPopulator.setGroupRoleAttribute("cn");

        return new LdapAuthenticationProvider(bindAuthenticator, authoritiesPopulator);
    }*/

    @Bean
    public UserServiceImpl userDetailsService(){
        UserServiceImpl userService = new UserServiceImpl();
        userService.setUserDAO(userDAOBean);
        userService.setGroupDAO(groupDAOBean);
        return userService;
    }

    @Bean
    public DefaultManageableImageCaptchaService captchaService(){
        DefaultManageableImageCaptchaService defaultManageableImageCaptchaService = new DefaultManageableImageCaptchaService();
        defaultManageableImageCaptchaService.setCaptchaEngine(captchaEngine);
        return defaultManageableImageCaptchaService;
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(this.ldapUrl);
        contextSource.setUserDn(this.ldapManagerDn);
        contextSource.setPassword(this.ldapManagerPassword);
        contextSource.afterPropertiesSet();

        DefaultLdapAuthoritiesPopulator ldapAuthoritiesPopulator =
            new DefaultLdapAuthoritiesPopulator(contextSource, ldapUserSearchBase);

        authenticationManagerBuilder
            .ldapAuthentication()
            .contextSource(contextSource)
            .userSearchBase(ldapUserSearchBase)
            .userSearchFilter(ldapUserSearchFilter)
            .ldapAuthoritiesPopulator(ldapAuthoritiesPopulator);
    }
}
