package com.backend.easy_to_web.account.infrastructure.external;

import com.backend.easy_to_web.common.infrastructure.external.AbstractEmailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class PasswordCertificationEmailSender extends AbstractEmailSender {

  public static final String EMAIL_SUBJECT = "[Easy To Web] 비밀번호 변경을 위한 이메일 인증코드 입니다";
  public static final String TEMPLATE_EMAIL_PATH = "email/auth-mail.html";

  public PasswordCertificationEmailSender(JavaMailSender javaMailSender) {
    super(javaMailSender);
  }


  @Override
  protected String getSubject() {
    return EMAIL_SUBJECT;
  }

  @Override
  protected String getTemplatePath() {
    return TEMPLATE_EMAIL_PATH;
  }

}
