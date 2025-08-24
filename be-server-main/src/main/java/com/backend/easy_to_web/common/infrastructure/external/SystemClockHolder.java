package com.backend.easy_to_web.common.infrastructure.external;

import com.backend.easy_to_web.common.application.port.ClockHolder;
import java.util.Date;
import org.springframework.stereotype.Component;


@Component
public class SystemClockHolder implements ClockHolder {

  @Override
  public Date getNow() {
    return new Date();
  }
}
