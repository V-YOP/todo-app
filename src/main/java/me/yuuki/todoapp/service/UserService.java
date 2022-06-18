package me.yuuki.todoapp.service;

/**
 * 操作用户的Service，提供简单的注册，登陆，修改密码的操作。该Service不包含关于Session的操作！
 */
public interface UserService {
    /**
     * 用户注册
     * @param userId 用户ID
     * @param userName 用户名
     * @param passwd 用户密码
     * @throws me.yuuki.todoapp.exception.ClientException 如果用户ID已存在，或用户名，用户密码不合法
     */
    void signup(String userId, String userName, String passwd);

    /**
     * 用户ID和密码是否匹配
     * @param userId 用户ID
     * @param passwd 用户密码
     * @return 若密码正确则返回 true，否则返回false
     */
    boolean canLogin(String userId, String passwd);

    /**
     * 更改密码
     * @param userId 用户ID
     * @param oldPasswd 旧密码
     * @param newPasswd 新密码
     * @return 如果更改密码成功则返回true，否则返回false
     * @throws me.yuuki.todoapp.exception.ClientException 如果新密码不合法
     */
    boolean changePasswd(String userId, String oldPasswd, String newPasswd);
}
