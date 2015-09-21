package com.martmoa.surffer.config;

import com.martmoa.surffer.config.viewresolver.JsonViewResolver;
import com.martmoa.surffer.config.viewresolver.JspViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * All the web app configuration happens here
 *
 * @author LanyonM
 */
@Configuration
@EnableWebMvc
@ComponentScan("com.martmoa.surffer.controller")
public class WebConfig extends WebMvcConfigurerAdapter {

	/*
     * Configure ContentNegotiationManager
     */
	/*@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.ignoreAcceptHeader(true);
        Charset charset = Charset.forName("UTF-8");
        MediaType mediaType = new MediaType("application", "json", charset);
        configurer.defaultContentType(mediaType);

	}*/


	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.ignoreAcceptHeader(true).defaultContentType(
				MediaType.TEXT_HTML);
	}

	/*
     * Configure ContentNegotiatingViewResolver
     */
	@Bean
	public ViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager) {
		ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
		resolver.setContentNegotiationManager(manager);

		// Define all possible view resolvers
		List<ViewResolver> resolvers = new ArrayList<ViewResolver>();

		resolvers.add(jsonViewResolver());
        resolvers.add(jspViewResolver());

		resolver.setViewResolvers(resolvers);
		return resolver;
	}


	@Bean
	public ViewResolver jsonViewResolver() {
		return new JsonViewResolver();
	}

	@Bean
	public ViewResolver jspViewResolver() {
		return new JspViewResolver();
	}

}
