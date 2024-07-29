package com.lemongo97.android.anti.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.vidstige.jadb.JadbConnection;

@Configuration
public class ApplicationConfiguration {

	@Bean
	public JadbConnection jadbConnection(){
		return new JadbConnection();
	}

}
