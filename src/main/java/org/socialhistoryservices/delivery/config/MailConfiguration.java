package org.socialhistoryservices.delivery.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.validation.executable.ValidateOnExecution;
import java.util.Properties;

/**
 * Created by Igor on 3/7/2017.
 */
@Configuration
@EnableConfigurationProperties(DeliveryProperties.class)
public class MailConfiguration {

    @Autowired DeliveryProperties deliveryProperties;

    @Bean
    public JavaMailSenderImpl mailSender(){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(deliveryProperties.getMailHost());
        javaMailSender.setPort(deliveryProperties.getMailPort());
        javaMailSender.setUsername(deliveryProperties.getMailUsername());
        javaMailSender.setPassword(deliveryProperties.getMailPassword());
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "false");
        javaMailSender.setJavaMailProperties(properties);
        return javaMailSender;
    }
}
