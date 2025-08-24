package com.backend.easy_to_web.common.infrastructure.external;

import com.backend.easy_to_web.common.application.port.CodeGenerator;
import com.backend.easy_to_web.utils.RandomUtils;
import org.springframework.stereotype.Component;

@Component
public class RandomCodeGenerator implements CodeGenerator {

  @Override
  public String generateCode(int length) {
    return RandomUtils.generateRandomMixNumNStr(length);
  }
}
