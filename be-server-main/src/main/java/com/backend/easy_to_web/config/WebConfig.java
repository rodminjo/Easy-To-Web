package com.backend.easy_to_web.config;

import com.backend.easy_to_web.config.converter.OctetStreamReadConverter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final OctetStreamReadConverter octetStreamReadConverter;

  /**
   * Content-Type 'application/octet-stream' is not supported 해결을 위한 converter
   * 파일 업로드시 info 와 file을 동시에 전송할 경우 발생하는 오류
   */
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(octetStreamReadConverter);
  }

}
