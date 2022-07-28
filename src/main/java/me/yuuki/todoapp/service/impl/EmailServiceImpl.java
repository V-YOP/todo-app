package me.yuuki.todoapp.service.impl;

import me.yuuki.todoapp.exception.ClientException;
import me.yuuki.todoapp.exception.ServerException;
import me.yuuki.todoapp.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    /**
     * 方便未配置邮箱服务时调用直接抛异常，这玩意切记别放到try里面，让它失败
     */
    private Supplier<JavaMailSender> mailSenderSupplier;

    @Autowired
    public void setMailSenderSupplier(Optional<JavaMailSender> javaMailSender) {
        JavaMailSender mailSender = javaMailSender.orElse(null);
        if (mailSender == null) {
            this.mailSenderSupplier = () -> {
                throw new ServerException("邮箱服务未配置，请联系开发者！");
            };
        } else {
            this.mailSenderSupplier = () -> mailSender;
        }
    }

    @Override
    public void sendEmail(String header, String content, String to) {
        JavaMailSender sender = mailSenderSupplier.get();
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setTo(to);
            helper.setSubject(header);
            helper.setText(content, true);
            sender.send(message);
        } catch (Exception e) {
            logger.error(String.format("邮件发送失败！header: %s, content: %s, to: %s", header, content, to), e);
            throw new ServerException("邮件发送失败！如果此情况持续发生请联系开发者。");
        }
    }
}
