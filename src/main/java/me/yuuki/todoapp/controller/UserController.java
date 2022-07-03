package me.yuuki.todoapp.controller;

import me.yuuki.todoapp.dto.Result;
import me.yuuki.todoapp.exception.ClientException;
import me.yuuki.todoapp.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 关于登陆，登出等的借口，这个接口不应当鉴权
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("login")
    public Result<Void> login(
            @RequestParam String userId,
            @RequestParam String passwd,
            @RequestParam(defaultValue = "false") Boolean rememberMe) {
        Subject subject = SecurityUtils.getSubject();
        subject.login(new UsernamePasswordToken(userId, passwd, rememberMe));
        return Result.ok(null);
    }

    // TODO 暂且不暴露注册接口
    // @PostMapping("signup")
    public ResponseEntity<Void> signup(
            @RequestParam String userId,
            @RequestParam String userName,
            @RequestParam String passwd) {
        userService.signup(userId,userName,passwd);
        return ResponseEntity.ok(null);
    }

    @GetMapping("isLogin")
    public boolean isLogin() {
        return SecurityUtils.getSubject().getPrincipal() != null;
    }

    @GetMapping("status")
    @RequiresAuthentication
    public Result<Map<String, String>> status() {
        return Result.ok(new HashMap<String,String>(){{

        }});
    }

    @GetMapping("logout")
    public Result<Void> logout() {
        if (!isLogin())
            throw new ClientException("你并未登陆！");
        SecurityUtils.getSubject().logout();
        return Result.ok(null);
    }
}
