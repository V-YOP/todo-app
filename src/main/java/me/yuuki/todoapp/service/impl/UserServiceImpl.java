package me.yuuki.todoapp.service.impl;

import me.yuuki.todoapp.entity.User;
import me.yuuki.todoapp.exception.ClientException;
import me.yuuki.todoapp.mapper.UserMapper;
import me.yuuki.todoapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    private final String salt = "1741a0d29ro!fF=-c1~`%^&*pyDDC1'?f0d8a7559615b51 world..750edew qe30';[;].i1ab93flo]o;??;!@#$%^&*";
    /**
     * 将byte转为16进制
     *
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                // 1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

    /**
     * 利用java原生的摘要实现SHA256加密
     *
     * @param str  加密后的报文
     * @return
     */
    private String getSHA256(String str) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            return byte2Hex(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException("这不应该发生");
        }
    }

    private String encrypt(String str) {
        return getSHA256(getSHA256(str + salt) + salt + str);
    }


    @Override
    public void signup(String userId, String userName, String passwd) {
        if (userMapper.selectByPrimaryKey(userId) != null) {
            throw new ClientException("该用户ID已被使用！");
        }
        User user = new User();
        user.setUserId(userId);
        user.setUsername(userName);
        user.setPasswd(encrypt(passwd));
        userMapper.insert(user);
    }

    @Override
    public boolean canLogin(String userId, String passwd) {
        User user = userMapper.selectByPrimaryKey(userId);
        return user != null && Objects.equals(encrypt(passwd), user.getPasswd());
    }

    @Override
    public boolean changePasswd(String userId, String oldPasswd, String newPasswd) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (!Objects.equals(encrypt(oldPasswd), user.getPasswd())) {
            return false;
        }
        user.setPasswd(encrypt(newPasswd));
        userMapper.updateByPrimaryKey(user);
        return true;
    }

}
