package com.backend.easy_to_web.test_support.only.async.service;

import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;

public class AsyncTestService {

  /**
   * SecurityScopeUtils 를 사용하여 Authentication 을 가져올 때 비동기 상황에서 가져오는 것을 테스트하기 위한 메서드
   * @return Success / Failure
   */
  @Async("threadPoolTaskExecutor")
  public CompletableFuture<String> asyncTestLogic(){
    if (SecurityScopeUtils.getAuthentication() != null){
      return CompletableFuture.completedFuture("Success");
    }

    return CompletableFuture.completedFuture("Failed");
  }
}
