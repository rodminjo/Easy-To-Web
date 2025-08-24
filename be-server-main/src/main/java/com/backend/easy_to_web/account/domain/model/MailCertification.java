package com.backend.easy_to_web.account.domain.model;

import com.backend.easy_to_web.common.domain.model.Domain;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MailCertification extends Domain {

  private String type;
  private String email;
  private String certificationCode;
  private LocalDateTime expirationDate;

  @Builder
  public MailCertification(LocalDateTime createdDate, LocalDateTime modifiedDate, String createdBy,
      String updatedBy, String type, String email, String certificationCode,
      LocalDateTime expirationDate) {
    super(createdDate, modifiedDate, createdBy, updatedBy);
    this.type = type;
    this.email = email;
    this.certificationCode = certificationCode;
    this.expirationDate = expirationDate;
  }

  public static MailCertification createCertification(String type, String email,
      String certificationCode, int expirationInMinutes) {
    LocalDateTime now = LocalDateTime.now();
    return MailCertification.builder()
        .type(type)
        .email(email)
        .certificationCode(certificationCode)
        .expirationDate(expirationInMinutes > 0 ? now.plusMinutes(expirationInMinutes) : now)
        .build();
  }

  public static String createKey(String type, String email) {
    return type + ":" + email;
  }

  public String createKey() {
    return createKey(type, email);
  }

  public long calDurationMillis() {
    LocalDateTime now = LocalDateTime.now();
    if (expirationDate.isBefore(now)) {
      return 0;
    }
    Duration duration = Duration.between(now, expirationDate);
    return duration.toMillis();
  }

  public boolean verify(String code) {
    return this.certificationCode.equals(code);

  }

  public boolean expired() {
    return LocalDateTime.now().isAfter(this.expirationDate);

  }
}
