package com.backend.easy_to_web.account.infrastructure.external;

import com.backend.easy_to_web.common.infrastructure.external.AbstractEmailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class JoinCertificationEmailSender extends AbstractEmailSender {

  public static final String EMAIL_SUBJECT = "[Easy To Web] 회원가입을 위한 이메일 인증코드 입니다";
  public static final String TEMPLATE_EMAIL_PATH = "email/auth-mail.html";

  public JoinCertificationEmailSender(JavaMailSender javaMailSender) {
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
