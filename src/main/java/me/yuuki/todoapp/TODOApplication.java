package me.yuuki.todoapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@MapperScan(basePackages = {
        "me.yuuki.todoapp.mapper"
})
public class TODOApplication {

    public static void main(String[] args) {
        SpringApplication.run(TODOApplication.class, args);
    }

}
