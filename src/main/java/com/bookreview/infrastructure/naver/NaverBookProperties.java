package com.bookreview.infrastructure.naver;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("naver.book")
public class NaverBookProperties {

    private String clientId;
    private String clientSecret;
}
