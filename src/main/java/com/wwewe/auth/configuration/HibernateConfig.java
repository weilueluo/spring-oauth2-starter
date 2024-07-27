package com.wwewe.auth.configuration;

import jakarta.persistence.Entity;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.schema.Action;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.util.AnnotatedTypeScanner;

@Configuration
@Slf4j
public class HibernateConfig {


    @Value("${wwewe.app.base-package}")
    private String basePackage;

    @Value("${wwewe.postgres.jdbc-url}")
    private String jdbcUrl;

    @Value("${wwewe.postgres.username}")
    private String username;

    @Value("${wwewe.postgres.password}")
    private String password;

    @Bean
    public SessionFactory sessionFactory() {

        var configuration = new org.hibernate.cfg.Configuration()
                .setProperty(AvailableSettings.JAKARTA_JDBC_URL, jdbcUrl)
                .setProperty(AvailableSettings.JAKARTA_JDBC_USER, username)
                .setProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD, password)
                .setProperty(AvailableSettings.CONNECTION_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider")
                .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.UPDATE)
                .setProperty(AvailableSettings.SHOW_SQL, Boolean.TRUE.toString())
                .setProperty(AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString())
                .setProperty(AvailableSettings.HIGHLIGHT_SQL, Boolean.TRUE.toString());

        new AnnotatedTypeScanner(Entity.class)
                .findTypes(basePackage)
                .forEach(clazz -> {
                    log.info("Adding entity to Hibernate: {}", clazz.getName());
                    configuration.addAnnotatedClass(clazz);
                });

        return configuration.buildSessionFactory();
    }
}
