package me.yuuki.todoapp.controller;

import com.wf.captcha.utils.CaptchaUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class CaptchaController {

    /**
     * 生成图片验证码并返回到前端，验证码的值将存储在session中
     *
     * 验证码设置为 7 位，字母数字混合，大小写不敏感
     */
    @RequestMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        CaptchaUtil.out(190, 50, 7, request, response);
    }
}