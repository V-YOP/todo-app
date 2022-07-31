package me.yuuki.todoapp.service.impl;

import me.yuuki.todoapp.exception.ClientException;
import me.yuuki.todoapp.exception.ServerException;
import me.yuuki.todoapp.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    /**
     * 方便未配置邮箱服务时调用直接抛异常，这玩意切记别放到try里面，让它失败
     */
    private Supplier<JavaMailSenderImpl> mailSenderSupplier;

    @Autowired
    public void setMailSenderSupplier(Optional<JavaMailSenderImpl> mailSender) {
        if (mailSender.isPresent()) {
            this.mailSenderSupplier = mailSender::get;
        } else {
            this.mailSenderSupplier = () -> {
                throw new ServerException("邮箱服务不可用！");
            };
        }
    }

    /**
     * 发送邮件，加锁和加sleep，以保证不会短时间内发送大量邮件，兼作限流
     */
    @Override
    public synchronized void sendEmail(String header, String content, String to) {
        JavaMailSenderImpl javaMailSender = mailSenderSupplier.get();
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            // qq必须设置from才允许发送邮件
            helper.setFrom(Objects.requireNonNull(javaMailSender.getUsername()));
            helper.setTo(to);
            helper.setSubject(header);
            helper.setText(content, true);
            javaMailSender.send(message);
            Thread.sleep(3000);
        } catch (Exception e) {
            logger.error(String.format("邮件发送失败！header: %s, content: %s, to: %s", header, content, to), e);
            throw new ServerException("邮件发送失败！如果此情况持续发生请联系开发者。");
        }
    }
}
