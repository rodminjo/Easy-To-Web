package com.backend.easy_to_web.medium;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import com.backend.easy_to_web.test_support.model.MediumTest;
import com.backend.easy_to_web.test_support.only.async.controller.AsyncTestController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
    AsyncTestController.class
})
public class SecurityContextAsyncMediumTest extends MediumTest {

  @Mock
  JwtManager jwtManager;

  @Test
  @DisplayName("메인쓰레드가 아닌 자식쓰레드에서도 계정정보를 불러올 수 있다")
  void multiThreadAuditingTest() throws Exception {
    mockMvc.perform(get("/test/async/"))
        .andExpect(content().string("Success"))
        .andDo(print());
  }
}
