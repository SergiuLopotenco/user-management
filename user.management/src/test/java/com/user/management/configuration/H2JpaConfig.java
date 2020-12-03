package com.user.management.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.user.management.domai.model")
@PropertySource("classpath:/persistence.properties")
@EnableTransactionManagement
public class H2JpaConfig {
}
