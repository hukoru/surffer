package com.martmoa.surffer.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@MapperScan("com.martmoa.surffer.dao")
@EnableTransactionManagement
public class DataConfig {


    @Autowired Environment env;

	@Bean
    @Autowired
	public DataSource dataSource() {

        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(env.getProperty("db.mysql.driverClassName"));
        dataSource.setUrl(env.getProperty("db.mysql.url"));
        dataSource.setUsername(env.getProperty("db.mysql.username"));
        dataSource.setPassword(env.getProperty("db.mysql.password"));
        dataSource.setDefaultAutoCommit(Boolean.parseBoolean(env.getProperty("db.mysql.defaultAutoCommit")));

        return dataSource;
	}


    @Bean
    @Autowired
    public DataSourceTransactionManager txManager(DataSource dataSource){
        DataSourceTransactionManager txManager = new DataSourceTransactionManager();
        txManager.setDataSource(dataSource);
        return txManager;
    }


	@Bean
    @Autowired
	public SqlSessionFactoryBean sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setTypeAliasesPackage("com.martmoa.surffer.domain");
		sessionFactory.setConfigLocation(new DefaultResourceLoader().getResource("classpath:mybatis/config/config_mybatis.xml"));
		sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/sql/*.xml"));
		return sessionFactory;
	}

}
