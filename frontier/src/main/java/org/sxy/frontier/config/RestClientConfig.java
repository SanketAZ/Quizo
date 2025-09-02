package org.sxy.frontier.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private String OPTIMUS_SERVICE_BASE_URL="http://localhost:8000/api/internal/v1";

    @Bean
    public RestClient optimusRestServiceClient(ClientHttpRequestFactory requestFactory){
        return RestClient.builder()
                .baseUrl(OPTIMUS_SERVICE_BASE_URL)
                .requestFactory(requestFactory)
                .build();
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(
            @Value("${http.client.connect-timeout-ms:2000}") int connectTimeoutMs,
            @Value("${http.client.read-timeout-ms:1500}") int readTimeoutMs) {

        var requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMs);
        requestFactory.setReadTimeout(readTimeoutMs);
        return requestFactory;
    }
}
