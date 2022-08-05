package me.yuuki.todoapp;

import me.yuuki.todoapp.service.UserService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@MapperScan(basePackages = {
        "me.yuuki.todoapp.mapper"
})
public class TODOApplication {

    public static void main(String[] args) {
        SpringApplication.run(TODOApplication.class, args);
    }

}
