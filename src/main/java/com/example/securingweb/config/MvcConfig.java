package com.example.securingweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/index.html").setViewName("index");
		registry.addViewController("/index2.html").setViewName("index2");
		registry.addViewController("/index").setViewName("index");
		registry.addViewController("/index2").setViewName("index2");
		registry.addViewController("/page-login").setViewName("page-login");
		registry.addViewController("/support").setViewName("support");
		registry.addViewController("/support.html").setViewName("support");
	}

}
