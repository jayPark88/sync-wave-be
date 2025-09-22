package com.parker.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication
@EntityScan(basePackages = {"com.parker.common.jpa.entity"})
@EnableJpaRepositories(basePackages = "com.parker.common.jpa.repository")
@ComponentScan(basePackages = {"com.parker.service", "com.parker.common"})
public class SyncWaveServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SyncWaveServiceApplication.class, args);
    }
}
