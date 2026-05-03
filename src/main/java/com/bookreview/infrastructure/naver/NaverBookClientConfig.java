package com.bookreview.infrastructure.naver;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
class NaverBookClientConfig {

    private final NaverBookProperties properties;

    @Bean
    public NaverBookSearchApiClient naverBookSearchApiClient() {
        return new NaverBookSearchApiClient(RestClient.create(), properties);
    }
}
