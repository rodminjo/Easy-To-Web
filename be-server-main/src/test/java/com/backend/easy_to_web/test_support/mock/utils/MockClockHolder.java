package com.backend.easy_to_web.test_support.mock.utils;

import com.backend.easy_to_web.common.application.port.ClockHolder;
import java.util.Date;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MockClockHolder implements ClockHolder {

  private final Date date;
  @Override
  public Date getNow() {
    return date;
  }
}
