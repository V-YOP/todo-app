package me.yuuki.todoapp.controller;

import com.wf.captcha.utils.CaptchaUtil;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
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
            @Email
            @RequestParam String email,
            @RequestParam String passwd,
            @RequestParam(defaultValue = "false") Boolean rememberMe) {
        Subject subject = SecurityUtils.getSubject();
        subject.login(new UsernamePasswordToken(email, passwd, rememberMe));
        return Result.ok(null);
    }

    /**
     * 注册接口，必须使用验证码等手段防止有人捣乱
     * @param email
     * @param verCode
     * @return
     */
    @PostMapping("signup")
    public ResponseEntity<Void> signup(
            @Email
            @RequestParam String email,
            @RequestParam String passwd,
            @RequestParam String verCode,
            HttpServletRequest request) {
        if (!CaptchaUtil.ver(verCode, request)) {
            CaptchaUtil.clear(request);
            throw new ClientException("验证码不正确！");
        }


        userService.signup(email, passwd);
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
