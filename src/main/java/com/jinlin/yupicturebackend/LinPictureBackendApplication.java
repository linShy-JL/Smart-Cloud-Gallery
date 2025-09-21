package com.jinlin.yupicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.jinlin.yupicturebackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class LinPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinPictureBackendApplication.class, args);
    }

}
