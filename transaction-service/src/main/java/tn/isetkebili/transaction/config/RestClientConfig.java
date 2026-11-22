package tn.isetkebili.transaction.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient accountRestClient(@Value("${services.account.url}") String accountServiceUrl) {
        return RestClient.builder()
                .baseUrl(accountServiceUrl)
                .build();
    }

    @Bean
    RestClient notificationRestClient(@Value("${services.notification.url}") String notificationServiceUrl) {
        return RestClient.builder()
                .baseUrl(notificationServiceUrl)
                .build();
    }
}