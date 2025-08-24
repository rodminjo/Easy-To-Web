package com.backend.easy_to_web.config;

import com.backend.easy_to_web.utils.MapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  @Bean
  public ObjectMapper serializingObjectMapper() {
    return MapperUtils.getMapper();
  }

}
