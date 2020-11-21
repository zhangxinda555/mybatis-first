package com.lagou;

import com.lagou.annotation.SpringBootApplication;
import com.lagou.application.SpringApplication;

@SpringBootApplication
public class ServerBootstrap {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ServerBootstrap.class, args);
    }



}
