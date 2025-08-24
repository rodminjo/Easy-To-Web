package com.backend.easy_to_web.project.domain.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {

  private UUID projectId;
  private UUID accountId;
  private String certificationCode;
  private LocalDateTime expirationDate;

  public static String getKey(UUID projectId, UUID accountId) {
    return String.format("invite:%s:%s", projectId, accountId);
  }

  public long calDurationMillis() {
    LocalDateTime now = LocalDateTime.now();
    if (expirationDate.isBefore(now)) {
      return 0;
    }
    Duration duration = Duration.between(now, expirationDate);
    return duration.toMillis();
  }

  public boolean isValidCode(String code){
    return certificationCode.equals(code);
  }
}
