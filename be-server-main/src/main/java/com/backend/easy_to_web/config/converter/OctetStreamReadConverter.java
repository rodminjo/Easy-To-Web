package com.backend.easy_to_web.config.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

/**
 * Content-Type 'application/octet-stream' is not supported 해결을 위한 converter
 * 파일 업로드시 info 와 file을 동시에 전송할 경우 타입을 지정하지 않으면 octet-stream 으로 설정하게 된다
 * Swagger에서는 타입을 설정해줄 수 없어 해당 오류 발생. 컨트롤 할 수 있도록 converter 추가
 */
@Component
public class OctetStreamReadConverter extends AbstractJackson2HttpMessageConverter {

  public OctetStreamReadConverter(ObjectMapper objectMapper) {
    super(objectMapper, MediaType.APPLICATION_OCTET_STREAM);
  }

  // 기존 application/octet-stream 타입을 쓰기로 다루는 메시지 컨버터가 이미 존재 (ByteArrayHttpMessageConverter)
  // 따라서 해당 컨버터는 쓰기 작업에는 이용하면 안됨
  @Override
  public boolean canWrite(Class<?> clazz, MediaType mediaType) {
    return false;
  }

  @Override
  public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
    return false;
  }

  @Override
  protected boolean canWrite(MediaType mediaType) {
    return false;
  }
}
