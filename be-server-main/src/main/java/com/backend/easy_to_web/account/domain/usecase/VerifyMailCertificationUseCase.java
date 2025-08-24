package com.backend.easy_to_web.account.domain.usecase;

import com.backend.easy_to_web.common.application.usecase.UseCase;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class VerifyMailCertificationUseCase implements UseCase {
  private String type;
  private String email;
  private String certificationCode;
}
