package me.yuuki.todoapp.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import me.yuuki.todoapp.entity.User;
import me.yuuki.todoapp.entity.UserExample;
import me.yuuki.todoapp.exception.ClientException;
import me.yuuki.todoapp.mapper.UserMapper;
import me.yuuki.todoapp.service.EmailService;
import me.yuuki.todoapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    private EmailService emailService;

    @Autowired
    public void setEmailServiceSupplier(EmailService emailService) {
        this.emailService = emailService;
        System.out.println(emailService);
    }

    /**
     * 注册用户，使用事务保证发送邮件失败后会回滚
     *
     * @param email     用户 email
     * @param passwd    用户密码
     * @param sendEmail
     * @throws me.yuuki.todoapp.exception.ClientException 如果用户ID已存在，或用户名，用户密码不合法
     */
    @Override
    @Transactional
    public void signup(String email, String passwd, boolean sendEmail) {
        getUserByEmail(email).ifPresent(user -> {
            throw new ClientException("该邮箱已被使用，请直接登录或者重置密码！");
        });

        User user = new User();
        user.setEmail(email);
        user.setPasswd(BCrypt.hashpw(passwd));
        userMapper.insert(user);
        if (sendEmail) {
            emailService.sendEmail(
                    "【YouDo】注册成功！请查收你的密码",
                    String.format("<p>你的密码是：</p><h2>%s</h2><p>请尽快登陆并修改密码！</p>", passwd),
                    email);
        }
    }

    @Override
    public Optional<User> canLogin(String email, String passwd) {
        User user = getUserByEmail(email)
                .orElseThrow(() -> new ClientException("用户名或密码错误！"));
        return Optional.of(user)
                .filter(u -> BCrypt.checkpw(passwd, u.getPasswd()));
    }

    @Override
    public boolean changePasswd(String email, String oldPasswd, String newPasswd) {
        User user = getUserByEmail(email)
                .orElseThrow(() -> new ClientException("用户名或密码错误！"));

        if (!BCrypt.checkpw(oldPasswd, user.getPasswd())) {
            return false;
        }
        user.setPasswd(BCrypt.hashpw(newPasswd));
        userMapper.updateByPrimaryKey(user);
        return true;
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
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
