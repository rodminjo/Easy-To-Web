package com.backend.easy_to_web.common.application.port;

import java.util.List;

public interface LinkGenerator {
  String generate(List<String> variable);
}
