package com.badsmell;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import java.sql.SQLOutput;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-06 21:36
 */

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class BSServerMain {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(BSServerMain.class, args);
    }
}