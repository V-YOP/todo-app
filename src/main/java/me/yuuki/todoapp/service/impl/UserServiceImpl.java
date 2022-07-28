package me.yuuki.todoapp.service.impl;

import com.aliyun.oss.ClientException;
import me.yuuki.todoapp.entity.User;
import me.yuuki.todoapp.entity.UserExample;
import me.yuuki.todoapp.mapper.UserMapper;
import me.yuuki.todoapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (byte aByte : bytes) {
            String temp = Integer.toHexString(aByte & 0xFF);
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

    /**
     * 注册用户
     *
     * @param email  用户 email
     * @param passwd 用户密码
     * @throws me.yuuki.todoapp.exception.ClientException 如果用户ID已存在，或用户名，用户密码不合法
     */
    @Override
    public void signup(String email, String passwd) {
        User user = new User();
        user.setEmail(email);
        user.setPasswd(encrypt(passwd));
        userMapper.insert(user);
    }

    @Override
    public Optional<String> canLogin(String email, String passwd) {
        User user = getUserByEmail(email)
                .orElseThrow(() -> new ClientException("用户不存在！"));
        String encryptedPasswd = encrypt(passwd);
        return Optional.of(encryptedPasswd)
                .filter(v -> user != null && Objects.equals(encryptedPasswd, user.getPasswd()));
    }

    @Override
    public boolean changePasswd(String email, String oldPasswd, String newPasswd) {
        User user = getUserByEmail(email)
                .orElseThrow(() -> new ClientException("用户不存在！"));

        if (!Objects.equals(encrypt(oldPasswd), user.getPasswd())) {
            return false;
        }
        user.setPasswd(encrypt(newPasswd));
        userMapper.updateByPrimaryKey(user);
        return true;
    }

    private Optional<User> getUserByEmail(String email) {
        UserExample example = new UserExample();
        example.or().andEmailEqualTo(email);
        List<User> users = userMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(users)) {
            return Optional.empty();
        }
        if (users.size() != 1) {
            throw new RuntimeException("impossible");
        }
        return Optional.of(users.get(0));
    }
}
