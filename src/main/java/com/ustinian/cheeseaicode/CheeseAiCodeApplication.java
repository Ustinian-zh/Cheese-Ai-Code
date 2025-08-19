package com.ustinian.cheeseaicode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class CheeseAiCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheeseAiCodeApplication.class, args);
    }

}
