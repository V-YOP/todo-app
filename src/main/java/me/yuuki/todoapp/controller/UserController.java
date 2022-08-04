package me.yuuki.todoapp.controller;

import com.wf.captcha.utils.CaptchaUtil;
import me.yuuki.todoapp.dto.Result;
import me.yuuki.todoapp.exception.ClientException;
import me.yuuki.todoapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 关于登陆，登出等的借口，这个接口不应当鉴权
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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

        logger.info("用户登陆, email: {}, rememberMe: {}", email, rememberMe);

        logger.info("用户登录成功, email: {}", email);
        return Result.ok(null);
    }

    /**
     * 注册接口，必须使用验证码等手段防止有人捣乱
     * @param email
     * @param verCode
     * @return
     */
    @PostMapping("signup")
    public Result<Void> signup(
            @Email
            @RequestParam String email,
            @RequestParam String verCode,
            HttpServletRequest request) {
        if (!CaptchaUtil.ver(verCode, request)) {
            CaptchaUtil.clear(request);
            throw new ClientException("验证码不正确！");
        }
        CaptchaUtil.clear(request);
        String randomPasswd = UUID.randomUUID().toString().replaceAll("-","").substring(0, 12);
        userService.signup(email, randomPasswd);

        return Result.ok(null);
    }

    @GetMapping("isLogin")
    public boolean isLogin() {;
        return true;
    }

    @GetMapping("status")
    public Result<Map<String, String>> status() {
        return Result.ok(new HashMap<String,String>(){{

        }});
    }

    @GetMapping("logout")
    public Result<Void> logout() {
        if (!isLogin())
            throw new ClientException("你并未登陆！");

        return Result.ok(null);
    }
}
