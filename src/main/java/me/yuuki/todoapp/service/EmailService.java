package me.yuuki.todoapp.service;

import java.util.List;

/**
 * 发送邮件的service，主要用于注册，提醒等
 */
public interface EmailService {
    void sendEmail(String header, String content, String to);
}
