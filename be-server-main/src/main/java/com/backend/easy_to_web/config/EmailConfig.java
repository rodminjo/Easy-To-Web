package com.backend.easy_to_web.config;

import com.backend.easy_to_web.config.properties.MailProperties;
import com.backend.easy_to_web.config.properties.SmtpProperties;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


@RequiredArgsConstructor
@Configuration
public class EmailConfig {

  private final MailProperties mailProperties;
  private final SmtpProperties smtpProperties;


  @Profile({"!local"})
  @Bean
  public JavaMailSender javaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(mailProperties.getHost());
    mailSender.setPort(mailProperties.getPort());
    mailSender.setUsername(mailProperties.getUsername());
    mailSender.setPassword(mailProperties.getPassword());
    mailSender.setDefaultEncoding("UTF-8");
    mailSender.setJavaMailProperties(getMailProperties());

    return mailSender;
  }


  private Properties getMailProperties() {
    Properties properties = new Properties();
    properties.put("mail.smtp.auth", smtpProperties.getAuth());
    properties.put("mail.smtp.starttls.enable", smtpProperties.getStarttls().getEnable());
    properties.put("mail.smtp.starttls.required", smtpProperties.getStarttls().getRequired());
    properties.put("mail.smtp.connectiontimeout", smtpProperties.getConnectionTimeout());
    properties.put("mail.smtp.timeout", smtpProperties.getTimeout());
    properties.put("mail.smtp.writetimeout", smtpProperties.getWriteTimeout());

    return properties;
  }
}
