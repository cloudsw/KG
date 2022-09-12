package com.kg.matchingsystem;

import com.kg.matchingsystem.service.impl.MatchingServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MatchingSystemApplication {
    public static void main(String[] args) {
        MatchingServiceImpl.matchingPool.start();  //启动线程
        SpringApplication.run(MatchingSystemApplication.class);
    }
}