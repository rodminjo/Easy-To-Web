package com.backend.easy_to_web.test_support.mock.properties;

import com.backend.easy_to_web.config.properties.MailProperties;
import com.backend.easy_to_web.config.properties.SecurityProperties;
import com.backend.easy_to_web.config.properties.ProjectSocketProperties;
import lombok.Getter;

@Getter
public class PropertiesContainer {
  private final SecurityProperties securityProperties;
  private final MailProperties mailProperties;
  private final ProjectSocketProperties ProjectSocketProperties;

  public PropertiesContainer() {
    long expiredTime = 60 * 60 * 1000 * 24L;
    this.securityProperties =
        new SecurityProperties("testSecretKey", expiredTime, expiredTime);
    this.mailProperties = new MailProperties("localhost", 587,
        "testID", "testPW", 3
    );

    this.ProjectSocketProperties = new ProjectSocketProperties("/test-room", "test_");

  }
}
