package com.martmoa.surffer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource({"classpath:db.local.properties"})
public class PropertiesConfig {

	@Bean
    static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(){
		return new PropertySourcesPlaceholderConfigurer();
	}
}
