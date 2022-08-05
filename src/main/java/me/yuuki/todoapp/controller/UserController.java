package me.yuuki.todoapp.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.wf.captcha.utils.CaptchaUtil;
import me.yuuki.todoapp.dto.Result;
import me.yuuki.todoapp.entity.User;
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
    public Result<String> login(
            @Email
            @RequestParam String email,
            @RequestParam String passwd) {
        logger.info("用户登陆, email: {}", email);
        User user = userService.canLogin(email, passwd).orElseThrow(() -> {
            logger.info("用户 {} 登陆失败", email);
            return new ClientException("用户名或密码错误！");
        });
        StpUtil.login(user.getEmail(),
                SaLoginModel.create()
                        .setExtra("id", user.getUserId()));
        logger.info("用户 {} 登录成功", email);
        return Result.ok(StpUtil.getTokenValueNotCut());
    }

    /**
     * 注册接口，必须使用验证码等手段防止有人捣乱
     * @param email 用户邮箱，需能够接收邮件
     * @param verCode 验证码
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
        String randomPasswd = UUID.randomUUID().toString().replaceAll("-","").substring(0, 8);
        userService.signup(email, randomPasswd, true);
        return Result.ok(null);
    }

    @GetMapping("isLogin")
    public boolean isLogin() {
        return StpUtil.isLogin();
    }


    @SaCheckLogin
    @GetMapping("status")
    public Result<SaTokenInfo> status() {
        return Result.ok(StpUtil.getTokenInfo());
    }
}
