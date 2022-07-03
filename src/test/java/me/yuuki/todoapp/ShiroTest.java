package me.yuuki.todoapp;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "spring.profiles.active=test")
public class ShiroTest {


    @Test
    public void login() {
        SecurityUtils.getSubject().login(
                new UsernamePasswordToken("hello", "123")
        );
    }

}
