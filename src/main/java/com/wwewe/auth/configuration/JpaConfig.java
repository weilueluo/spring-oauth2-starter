package com.wwewe.auth.configuration;

/*
* Originally for spring data jpa, but I don't like it,
* so switch to spring + hibernate instead and commented out this class
* */

//@Configuration
//@EnableJpaRepositories("com.wwewe.wwewe.repository")
//@EnableTransactionManagement  // for @Transactional
public class JpaConfig {

//
//    @Value("${wwewe.postgres.jdbc-url}")
//    private String jdbcUrl;
//
//    @Value("${wwewe.postgres.username}")
//    private String username;
//
//    @Value("${wwewe.postgres.password}")
//    private String password;
//
//    @Bean
//    public DataSource dataSource() {
//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl(jdbcUrl);
//        config.setUsername(username);
//        config.setPassword(password);
//
//        return new HikariDataSource(config);
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setGenerateDdl(true);
//        vendorAdapter.setShowSql(true);
//
//        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//        factory.setJpaVendorAdapter(vendorAdapter);
//        factory.setPackagesToScan("com.wwewe.wwewe");
//        factory.setDataSource(dataSource());
//
//        return factory;
//    }
//
//    @Bean
//    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
//        JpaTransactionManager txManager = new JpaTransactionManager();
//        txManager.setEntityManagerFactory(entityManagerFactory);
//
//        return txManager;
//    }
}
