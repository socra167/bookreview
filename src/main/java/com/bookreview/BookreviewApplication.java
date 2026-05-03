package com.bookreview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BookreviewApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookreviewApplication.class, args);
	}

}
