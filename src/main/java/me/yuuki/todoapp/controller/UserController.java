package me.yuuki.todoapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 关于登陆，登出等的借口，这个接口不应当鉴权
 */
@RestController
@RequestMapping("/user")
public class UserController {
}
