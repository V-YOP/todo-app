package me.yuuki.todoapp.service;

import me.yuuki.todoapp.entity.User;
import me.yuuki.todoapp.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest(properties = "spring.profiles.active=test")
@Transactional
@Rollback
class UserServiceTest {

    private String randomStr(int length) {
        String tmp = "";
        for (int i = 0; i < 1 + length / 32; i++) {
            tmp += UUID.randomUUID().toString();
        }
        return tmp.substring(0, length);
    }

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @Test
    void test() {
        String userId = randomStr(32);
        userService.signup(userId, "a really bad man", "wochao");
        User user = userMapper.selectByPrimaryKey(userId);
        assert !user.getPasswd().equals("wochao");
        System.out.println(user.getPasswd());
        assert userService.canLogin(userId, "wochao");
        assert !userService.canLogin(userId, "wochAo");
    }

    @Test
    void changePasswd() {
        String userId = randomStr(32);
        userService.signup(userId, "a really bad man", "wochao");
        assert userService.canLogin(userId, "wochao");
        assert userService.changePasswd(userId, "wochao", "ruaruarua");
        assert userService.canLogin(userId, "ruaruarua");
        assert !userService.changePasswd(userId, "www", "abccccccc");
    }
}