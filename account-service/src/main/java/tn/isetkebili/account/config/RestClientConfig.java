package tn.isetkebili.account.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient customerRestClient(@Value("${services.customer.url}") String customerServiceUrl) {
        return RestClient.builder()
                .baseUrl(customerServiceUrl)
                .build();
    }
}