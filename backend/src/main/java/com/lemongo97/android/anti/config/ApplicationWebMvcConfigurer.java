package com.lemongo97.android.anti.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

import static com.lemongo97.android.anti.constants.Constants.API_BASE_PATH;

@Slf4j
@Configuration
public class ApplicationWebMvcConfigurer implements WebMvcConfigurer {

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.addPathPrefix(API_BASE_PATH, clazz -> clazz.isAnnotationPresent(RestController.class));
	}

	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
		resolvers.addFirst((request, response, handler, e) -> {
			if (request.getRequestURI().startsWith(API_BASE_PATH)){
				return null;
			}
			if (e instanceof NoResourceFoundException){
				return new ModelAndView("forward:/");
			} else {
				return new ModelAndView("forward:/500");
			}
		});
	}
}
