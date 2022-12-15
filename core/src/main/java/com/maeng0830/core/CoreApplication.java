package com.maeng0830.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @ComponentScan이 포함되어있다. -> 별도로 @ComponentScan을 사용하는 Configuration을 만들 필요가 없는 것이다.
@SpringBootApplication
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

}
