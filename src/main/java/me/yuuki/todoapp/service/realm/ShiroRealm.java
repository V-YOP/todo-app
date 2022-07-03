package me.yuuki.todoapp.service.realm;

import me.yuuki.todoapp.exception.ClientException;
import me.yuuki.todoapp.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * register by {@link me.yuuki.todoapp.config.ShiroConfig}
 */
public class ShiroRealm extends AuthorizingRealm {

    UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取授权信息，即权限，principal可以认为是用户名，在试图获取用户的role和permission时会进到该方法
     * <p>
     * 该方法本来应当做的事情是，根据用户名去获取用户的角色（以及依此获取权限）
     * <p>
     * 但考虑到当前不需要一个特定的权限系统，只需要能处理登陆和未登陆情况即可，因此什么都不做
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return new SimpleAuthorizationInfo();
    }

    /**
     * 获取鉴权信息，即登陆，在login时进行调用
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (token == null)
            throw new AuthenticationException("token can't be null!");
        if (!(token instanceof UsernamePasswordToken))
            throw new AuthenticationException("token should be instance of org.apache.shiro.authc" +
                    ".UsernamePasswordToken !");
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;

        String username = usernamePasswordToken.getUsername();
        String password = String.valueOf(usernamePasswordToken.getPassword());
        String strToken = userService.canLogin(username, password)
                .orElseThrow(() -> new AuthenticationException("login failed"));
        usernamePasswordToken.setPassword(strToken.toCharArray());
        SimpleAuthenticationInfo account = new SimpleAuthenticationInfo(username, strToken, this.getName());
        return account;
    }
}
