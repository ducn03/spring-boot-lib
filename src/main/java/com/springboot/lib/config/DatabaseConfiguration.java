package com.springboot.lib.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.springboot.jpa.repository",
        entityManagerFactoryRef = "springbootEntityManagerFactory",
        transactionManagerRef = "springbootTransactionManager"
)
@EnableJpaAuditing
public class DatabaseConfiguration {

    private final Logger logger = LoggerFactory.getLogger(DatabaseConfiguration.class);

    @Value("${spring.datasource.springboot.jdbc-url}")
    private String dataSourceUrl;

    @Bean("springbootDataSource")
    @ConfigurationProperties("spring.datasource.springboot")
    public DataSource hcDataSource() {
        logger.info("starting connect to data source {}", dataSourceUrl);
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "springbootEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(EntityManagerFactoryBuilder entityManagerFactoryBuilder,
                         @Qualifier("springbootDataSource") DataSource dataSource) {

        return entityManagerFactoryBuilder.dataSource(dataSource).packages("com.springboot.jpa.domain").build();
    }

    @Bean(name = "springbootTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("springbootEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }

}
