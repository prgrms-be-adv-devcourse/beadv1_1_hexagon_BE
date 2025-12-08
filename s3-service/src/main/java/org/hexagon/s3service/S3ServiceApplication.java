package org.hexagon.s3service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class S3ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(S3ServiceApplication.class, args);
    }

}
