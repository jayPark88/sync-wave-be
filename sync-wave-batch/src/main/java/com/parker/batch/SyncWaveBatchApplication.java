package com.parker.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@EntityScan(basePackages = {"com.parker.common.jpa.entity"})
@EnableJpaRepositories(basePackages = "com.parker.common.jpa.repository")
@ComponentScan(basePackages = {"com.parker.batch", "com.parker.common"})
public class SyncWaveBatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(SyncWaveBatchApplication.class, args);
    }
}
