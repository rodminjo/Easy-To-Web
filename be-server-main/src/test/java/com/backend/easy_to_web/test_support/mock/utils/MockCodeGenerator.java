package com.backend.easy_to_web.test_support.mock.utils;

import com.backend.easy_to_web.common.application.port.CodeGenerator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MockCodeGenerator implements CodeGenerator {

  private final String codeStr;

  @Override
  public String generateCode(int length) {
    return codeStr;
  }
}
