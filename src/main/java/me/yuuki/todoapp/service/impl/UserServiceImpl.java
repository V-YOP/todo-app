package me.yuuki.todoapp.service.impl;

import me.yuuki.todoapp.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public void signup(String userId, String userName, String passwd) {

    }

    @Override
    public boolean canLogin(String userId, String passwd) {
        return false;
    }

    @Override
    public boolean changePasswd(String userId, String oldPasswd, String newPasswd) {
        return false;
    }
}
