package com.backend.easy_to_web.common.infrastructure.external;

import com.backend.easy_to_web.common.domain.exception.CustomIllegalStateException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.application.port.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEmailSender implements EmailSender {

  private static final String SENDER_ADDRESS = "easytoweb@gmail.com";
  private static final String SENDER_NAME = "EasyToWeb_Admin";

  private final JavaMailSender javaMailSender;

  protected abstract String getSubject();
  protected abstract String getTemplatePath();


  @Override
  public void send(String to, Map<String, String> variables) {
    try {
      MimeMessage mimeMessage = createMessage(to, getSubject(), getTemplatePath(), variables);
      javaMailSender.send(mimeMessage);

    } catch (MessagingException | UnsupportedEncodingException e) {
      log.error("email 작성 오류 : {}", e.getMessage());
      throw new CustomIllegalStateException(ExceptionMessage.MAIL_WRITE_FAILED);

    } catch (MailException e) {
      log.error("email 발송 오류 : {}", e.getMessage());
      throw new CustomIllegalStateException(ExceptionMessage.MAIL_SEND_FAILED);

    } catch (URISyntaxException | IOException e) {
      log.error("email 파일 오류 : {}", e.getMessage());
      throw new CustomIllegalStateException(ExceptionMessage.MAIL_FILE_ERROR);
    }
  }

  private MimeMessage createMessage(String to, String subject, String templatePath, Map<String, String> variables)
      throws MessagingException, IOException, URISyntaxException {

    MimeMessage message = javaMailSender.createMimeMessage();
    message.addRecipients(RecipientType.TO, to); // 메일 받을 사용자
    message.setSubject(subject); // 이메일 제목

    // 메일 템플릿 가져오기
    ClassPathResource resource = new ClassPathResource(templatePath);
    if (!resource.exists()) {
      throw new IOException("Invalid email file address");
    }

    URL url = resource.getURL();
    Path path = Paths.get(url.toURI());
    String htmlTemplate = Files.readString(path);

    // 메일 템플릿에 값 매핑 입력
    for (Map.Entry<String, String> entry : variables.entrySet()) {
      htmlTemplate = htmlTemplate.replace("{{%" + entry.getKey() + "}}", entry.getValue());
    }

    // 메일 내용, charset타입, subtype
    message.setText(htmlTemplate, "utf-8", "html");

    // 보내는 사람의 이메일 주소, 보내는 사람 이름
    message.setFrom(new InternetAddress(SENDER_ADDRESS, SENDER_NAME));

    return message;
  }


}
