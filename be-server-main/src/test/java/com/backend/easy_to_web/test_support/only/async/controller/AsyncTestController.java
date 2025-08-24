package com.backend.easy_to_web.test_support.only.async.controller;

import com.backend.easy_to_web.test_support.only.async.service.AsyncTestService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AsyncTestController {

  @GetMapping("/test/async/")
  public String async() throws ExecutionException, InterruptedException, TimeoutException {
    AsyncTestService asyncTestService = new AsyncTestService();
    return asyncTestService.asyncTestLogic().get(10, TimeUnit.SECONDS);

  }

}
