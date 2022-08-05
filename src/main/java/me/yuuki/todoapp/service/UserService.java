package me.yuuki.todoapp.service;

import me.yuuki.todoapp.entity.User;

import java.util.Optional;

/**
 * 操作用户的Service，提供简单的注册，登陆，修改密码的操作。该Service不包含关于Session的操作！
 */
public interface UserService {
    /**
     * 注册用户
     *
     * @param email     用户 email
     * @param passwd    用户密码
     * @param sendEmail
     * @throws me.yuuki.todoapp.exception.ClientException 如果用户ID已存在，或用户名，用户密码不合法
     */
    void signup(String email, String passwd, boolean sendEmail);

    /**
     * 检查email和密码是否匹配
     * @param email  用户 email
     * @param passwd 用户密码
     * @return 若密码正确则返回用户信息
     */
    Optional<User> canLogin(String email, String passwd);

    /**
     * 更改密码
     * @param email     用户 email
     * @param oldPasswd 旧密码
     * @param newPasswd 新密码
     * @return 如果更改密码成功则返回true，否则返回false
     * @throws me.yuuki.todoapp.exception.ClientException 如果新密码不合法
     */
    boolean changePasswd(String email, String oldPasswd, String newPasswd);

    /**
     * 根据邮箱获取用户
     */
    Optional<User> getUserByEmail(String email);
}
