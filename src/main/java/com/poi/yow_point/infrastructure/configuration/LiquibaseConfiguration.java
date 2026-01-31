package com.poi.yow_point.infrastructure.configuration;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@ConditionalOnProperty(prefix = "spring.liquibase", name = "enabled", matchIfMissing = true)
public class LiquibaseConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(LiquibaseConfiguration.class);

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.liquibase.change-log}")
    private String changeLog;

    @Value("${spring.liquibase.force-unlock:false}")
    private boolean forceUnlock;

    @Bean
    public SpringLiquibase liquibase() {
        if (forceUnlock) {
            clearLiquibaseLock();
        }
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(changeLog);
        liquibase.setDataSource(dataSource());
        return liquibase;
    }

    private void clearLiquibaseLock() {
        logger.info("Forcing Liquibase unlock...");
        try (Connection connection = dataSource().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("UPDATE databasechangeloglock SET LOCKED = false, LOCKGRANTED = null, LOCKEDBY = null WHERE ID = 1");
            logger.info("Liquibase lock cleared successfully.");
        } catch (SQLException e) {
            logger.warn("Could not clear Liquibase lock: {}. This is expected if the table does not exist yet.", e.getMessage());
        }
    }

    private DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }
}
