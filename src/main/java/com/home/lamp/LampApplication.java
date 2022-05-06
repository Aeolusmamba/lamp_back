package com.home.lamp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
public class LampApplication {

    public static void main(String[] args){
        SpringApplication.run(LampApplication.class, args);
    }

}
