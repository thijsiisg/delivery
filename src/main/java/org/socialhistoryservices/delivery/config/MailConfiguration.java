package org.socialhistoryservices.delivery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.validation.executable.ValidateOnExecution;
import java.util.Properties;

/**
 * Created by Igor on 3/7/2017.
 */
@Configuration
public class MailConfiguration {

    @Value("${prop_mailHost}") private String mailHost;
    @Value("${prop_mailPort}") private int mailPort;
    @Value("${prop_mailUsername}") private String mailUsername;
    @Value("${prop_mailPassword}") private String mailPassword;

    @Bean
    public JavaMailSenderImpl mailSender(){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(mailHost);
        javaMailSender.setPort(mailPort);
        javaMailSender.setUsername(mailUsername);
        javaMailSender.setPassword(mailPassword);
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "false");
        javaMailSender.setJavaMailProperties(properties);
        return javaMailSender;
    }
}
