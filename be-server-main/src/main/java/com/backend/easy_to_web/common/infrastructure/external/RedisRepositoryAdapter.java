package com.backend.easy_to_web.common.infrastructure.external;

import com.backend.easy_to_web.common.domain.exception.CustomIllegalStateException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.application.port.RedisRepository;
import com.backend.easy_to_web.utils.MapperUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RedisRepositoryAdapter implements RedisRepository {

  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  public <V> V get(String key, Class<V> valueType){
    try {
      String jsonResult = (String) redisTemplate.opsForValue().get(key);

      if (StringUtils.isBlank(jsonResult)) {
        return null;
      } else {
        ObjectMapper mapper = MapperUtils.getMapper();
        return mapper.readValue(jsonResult, valueType);
      }

    } catch (JsonProcessingException e) {
      log.error("이메일 인증 중 redis 에러 : {}", e.getMessage());
      throw new CustomIllegalStateException(ExceptionMessage.REDIS_FAILED);

    }
  }

  @Override
  public <V> V getAndDelete(String key, Class<V> valueType) {
    try {
      String jsonResult = (String) redisTemplate.opsForValue().get(key);

      if (StringUtils.isBlank(jsonResult)) {
        return null;
      }

      redisTemplate.delete(key);

      ObjectMapper mapper = MapperUtils.getMapper();
      return mapper.readValue(jsonResult, valueType);

    } catch (JsonProcessingException e) {
      log.error("Redis 역직렬화 에러 : {}", e.getMessage());
      throw new CustomIllegalStateException(ExceptionMessage.REDIS_FAILED);
    }
  }

  @Override
  public <V> void set(String key, V value){
    try {
      ObjectMapper mapper = MapperUtils.getMapper();
      String valueStr = mapper.writeValueAsString(value);
      redisTemplate.opsForValue().set(key, valueStr);

    } catch (JsonProcessingException e) {
      log.error("Redis 저장 실패: {}", e.getMessage());
      throw new CustomIllegalStateException(ExceptionMessage.REDIS_FAILED);
    }
  }

  @Override
  public <V> void setWithExpired(String key, V value, long durationMillis) {
    try {
      ObjectMapper mapper = MapperUtils.getMapper();
      String valueStr = mapper.writeValueAsString(value);
      redisTemplate.opsForValue().set(key, valueStr, Duration.ofMillis(durationMillis));

    } catch (JsonProcessingException e) {
      log.error("Redis 유효기간 저장 실패: {}", e.getMessage());
      throw new CustomIllegalStateException(ExceptionMessage.REDIS_FAILED);
    }
  }

  @Override
  public void delete(String key) {
    redisTemplate.delete(key);
  }

}
