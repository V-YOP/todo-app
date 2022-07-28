package me.yuuki.todoapp.service;

import me.yuuki.todoapp.entity.User;
import me.yuuki.todoapp.entity.UserExample;
import me.yuuki.todoapp.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootTest(properties = "spring.profiles.active=test")
@Transactional
@Rollback
class UserServiceTest {

    private String randomEmail(int length) {
        String tmp = "";
        for (int i = 0; i < 1 + length / 32; i++) {
            tmp += UUID.randomUUID().toString();
        }
        return tmp.substring(0, length) + "@test.com";
    }

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    private User getUserByEmail(String email) {
        UserExample example = new UserExample();
        example.or().andEmailEqualTo(email);
        List<User> users = userMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(users)) {
            return null;
        }
        if (users.size() != 1) {
            throw new RuntimeException("impossible");
        }
        return users.get(0);
    }


    @Test
    void test() {
        String email = randomEmail(16);;
        userService.signup(email, "wochao");
        User user = getUserByEmail(email);
        assert !user.getPasswd().equals("wochao");
        System.out.println(user.getPasswd());
        assert userService.canLogin(email, "wochao").isPresent();
        assert !userService.canLogin(email, "wochAo").isPresent();
    }

    @Test
    void changePasswd() {
        String email = randomEmail(16);;
        userService.signup(email, "wochao");
        assert userService.canLogin(email, "wochao").isPresent();
        assert userService.changePasswd(email, "wochao", "ruaruarua");
        assert userService.canLogin(email, "ruaruarua").isPresent();
        assert !userService.changePasswd(email, "www", "abccccccc");
    }

}