package com.backend.easy_to_web.account.application.service;

import com.backend.easy_to_web.account.domain.model.MailCertification;
import com.backend.easy_to_web.common.application.port.CodeGenerator;
import com.backend.easy_to_web.common.application.port.EmailSender;
import com.backend.easy_to_web.common.application.port.RedisRepository;
import com.backend.easy_to_web.config.properties.MailProperties;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class CertificationMailService {

  private final RedisRepository redisRepository;
  private final CodeGenerator codeGenerator;
  private final MailProperties properties;

  public void sendAndSaveCertificationCode(String prefix, String email, EmailSender emailSender) {
    // 인증 이메일 발송
    String certificationCode = codeGenerator.generateCode(6);
    Map<String, String> variables = Map.of("certificationCode", certificationCode);
    emailSender.send(email, variables);

    // certification 객체 redis 에 저장
    MailCertification certification = MailCertification.createCertification(
        prefix,
        email,
        certificationCode,
        properties.getAuthCodeExpirationMin()
    );
    saveCertificationInCache(certification);
  }

  public boolean isValidCertification(String type, String email, String certificationCode){
    MailCertification data = getCertification(type, email);
    return !ObjectUtils.isEmpty(data) && !data.expired() && data.verify(certificationCode);
  }

  private MailCertification getCertification(String type, String email){
    return redisRepository.get(MailCertification.createKey(type, email), MailCertification.class);
  }

  // Redis 저장
  private void saveCertificationInCache(MailCertification verification) {
    String key = verification.createKey();
    long milliSec = verification.calDurationMillis();
    redisRepository.setWithExpired(key, verification, milliSec);
  }
}
