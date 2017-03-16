package org.socialhistoryservices.delivery.config;

import org.socialhistoryservices.delivery.RequestContextToViewInterceptor;
import org.socialhistoryservices.delivery.api.IISHRecordLookupService;
import org.socialhistoryservices.delivery.api.PayWayService;
import org.socialhistoryservices.delivery.api.SharedObjectRepositoryService;
import org.socialhistoryservices.delivery.user.controller.SecurityToViewInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Igor on 3/6/2017.
 */
@Configuration
@EnableConfigurationProperties(DeliveryProperties.class)
public class RootContextConfiguration extends WebMvcConfigurerAdapter{

    @Autowired
    DeliveryProperties deliveryProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(secIntercept());
        registry.addInterceptor(reqIntercept());
    }

    @Bean
    public IISHRecordLookupService myLookupService(){
        IISHRecordLookupService iishRecordLookupService = new IISHRecordLookupService();
        iishRecordLookupService.setDeliveryProperties(deliveryProperties);
        return iishRecordLookupService;
    }

    @Bean
    public PayWayService payWayService(){
        PayWayService payWayService = new PayWayService(deliveryProperties.getPayWayAddress(),
             deliveryProperties.getPayWayPassPhraseIn(),
            deliveryProperties.getPayWayPassPhraseOut(),
            deliveryProperties.getPayWayProjectName());
        return payWayService;
    }

    @Bean
    public SharedObjectRepositoryService sharedObjectRepositoryService(){
        SharedObjectRepositoryService sharedObjectRepositoryService = new SharedObjectRepositoryService(deliveryProperties.getSorAddress());
        return sharedObjectRepositoryService;
    }

    @Bean
    public SecurityToViewInterceptor secIntercept(){
        return new SecurityToViewInterceptor();
    }

    @Bean
    public RequestContextToViewInterceptor reqIntercept(){
        return new RequestContextToViewInterceptor();
    }
}
