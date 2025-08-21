package com.ustinian.cheeseaicode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
@MapperScan("com.ustinian.cheeseaicode.mapper")
public class CheeseAiCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheeseAiCodeApplication.class, args);
    }

}
