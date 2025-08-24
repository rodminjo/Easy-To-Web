package com.backend.easy_to_web.project.infrastructure.external;


import com.backend.easy_to_web.common.infrastructure.external.AbstractEmailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ProjectInviteEmailSender extends AbstractEmailSender {

  public static final String EMAIL_SUBJECT = "[Easy To Web] 프로젝트 초대 이메일 입니다";
  public static final String TEMPLATE_EMAIL_PATH = "email/invite-mail.html";

  public ProjectInviteEmailSender(JavaMailSender javaMailSender) {
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
