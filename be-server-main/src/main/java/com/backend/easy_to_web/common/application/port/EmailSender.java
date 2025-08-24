package com.backend.easy_to_web.common.application.port;

import java.util.Map;

public interface EmailSender {

  void send(String to, Map<String, String> variables);

}
