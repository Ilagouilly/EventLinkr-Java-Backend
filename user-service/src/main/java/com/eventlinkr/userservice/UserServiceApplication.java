package com.eventlinkr.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        SpringApplication.run(UserServiceApplication.class, args);
    }
}