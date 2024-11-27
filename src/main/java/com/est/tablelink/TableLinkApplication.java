package com.est.tablelink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TableLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(TableLinkApplication.class, args);
    }

}
