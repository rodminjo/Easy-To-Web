package com.backend.easy_to_web.test_support.model;

import com.backend.easy_to_web.common.infrastructure.external.SystemClockHolder;
import com.backend.easy_to_web.config.properties.SecurityProperties;
import com.backend.easy_to_web.test_support.config.TestSecurityConfig;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

// AutoConfigureMockMvc 는 WebMvcTest 에 들어 있음
@Tag("mediumTest")
@ContextConfiguration(classes = {
    SecurityProperties.class,
    SystemClockHolder.class
})
@Import({TestSecurityConfig.class})
@WebMvcTest
public class MediumTest {

  @Autowired
  public MockMvc mockMvc;
}
