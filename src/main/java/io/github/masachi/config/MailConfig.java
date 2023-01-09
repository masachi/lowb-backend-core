package io.github.masachi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

//@Configuration
@PropertySource({"classpath:core.properties"})
public class MailConfig {

    /**
     * 以下注解来自core包
     */
    @Value("${spring.mail.host}")
    private String mailHost;
    @Value("${spring.mail.username}")
    private String mailUsername;
    @Value("${spring.mail.password}")
    private String mailPassword;
    @Value("${spring.mail.default-encoding}")
    private String mailEncoding;
    /**
     * 以下注解来自本地配置
     */
    @Value("${mail.smtp.auth}")
    private String mailAuth;
    @Value("${mail.smtp.socketFactory.class}")
    private String mailSsl;
    @Value("${mail.smtp.socketFactory.port}")
    private String mailSslPort;
    @Value("${mail.smtp.port}")
    private String mailPort;

    @Bean
    public JavaMailSenderImpl mailSender() {
        Properties p = new Properties();
        p.put("mail.smtp.auth", mailAuth);
        p.put("mail.smtp.socketFactory.class", mailSsl);
        p.put("mail.smtp.socketFactory.port", mailSslPort);
        p.put("mail.smtp.port", mailPort);

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(mailHost);
        javaMailSender.setUsername(mailUsername);
        javaMailSender.setPassword(mailPassword);
        javaMailSender.setDefaultEncoding(mailEncoding);
        javaMailSender.setJavaMailProperties(p);
        return javaMailSender;
    }
}
