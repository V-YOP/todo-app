package me.yuuki.todoapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonSerDeConfig {
    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 使能够处理 Optional 等对象的序列化/反序列化
        objectMapper.registerModule(new Jdk8Module());
        return objectMapper;
    }
}
