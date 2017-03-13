package org.socialhistoryservices.delivery.config;

import org.socialhistoryservices.utils.PropertiesLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import java.text.SimpleDateFormat;

/**
 * Created by Igor on 3/6/2017.
 */
@Configuration
public class ConfigConfiguration {

    @Value("${prop_dateFormat}") private String prop_dateFormat;

    @Bean
    public SimpleDateFormat dateFormat(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(prop_dateFormat);
        simpleDateFormat.setLenient(false);
        return simpleDateFormat;
    }

}
