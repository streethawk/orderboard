package com.harshsetia.orderboard.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import org.hsqldb.util.DatabaseManagerSwing;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * Created by harshsetia on 11/02/2017.
 */
@ComponentScan({ "com.harshsetia.orderboard" })
@Configuration
public class SpringRootConfig {

    @Autowired
    DataSource dataSource;

    @Bean
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @PostConstruct
    public void startDBManager() {
        DatabaseManagerSwing.main(new String[] { "--url", "jdbc:h2:mem:testdb", "--user", "sa", "--password", "" });

    }
}
