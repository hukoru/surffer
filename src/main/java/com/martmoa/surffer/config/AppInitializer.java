package com.martmoa.surffer.config;

import com.martmoa.surffer.config.filter.CORSFilter;
import com.martmoa.surffer.service.impl.*;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;


/**
 * Java Config for this application.  Life begins here.
 * 
 * @author hukoru
 */
public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[]{
				PropertiesConfig.class,
				DataConfig.class,
                CategoryServiceImpl.class
		};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[]{WebConfig.class};
	}

	@Override
	protected Filter[] getServletFilters() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		return new Filter[]{ characterEncodingFilter, new CORSFilter()};
	}



	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};
	}

}
