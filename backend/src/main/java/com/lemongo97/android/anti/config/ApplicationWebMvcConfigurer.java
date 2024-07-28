package com.lemongo97.android.anti.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Configuration
public class ApplicationWebMvcConfigurer implements WebMvcConfigurer {
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.addPathPrefix("/api/", clazz -> clazz.isAnnotationPresent(RestController.class));
	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
		resolvers.add((request, response, handler, e) -> {
			if (e instanceof NoResourceFoundException){
				return new ModelAndView("forward:/");
			} else {
				return null;
			}
		});
	}
}
