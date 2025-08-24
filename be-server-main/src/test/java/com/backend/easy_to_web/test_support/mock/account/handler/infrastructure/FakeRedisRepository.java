package com.backend.easy_to_web.test_support.mock.account.handler.infrastructure;

import com.backend.easy_to_web.common.application.port.RedisRepository;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalStateException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.utils.MapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FakeRedisRepository implements RedisRepository {

  private final boolean isException;

  // 데이터와 만료 시간 관리
  private final Map<String, String> store = new ConcurrentHashMap<>();
  private final Map<String, Long> expirationTimes = new ConcurrentHashMap<>();
  private final ObjectMapper objectMapper = MapperUtils.getMapper();

  @Override
  public <V> V get(String key, Class<V> valueType){
    doException();

    // 만료된 데이터 제거
    if (isExpired(key)) {
      delete(key);
      return null;
    }

    String jsonResult = store.get(key);
    if (jsonResult == null || jsonResult.isBlank()) {
      return null;
    }
    try {
      return objectMapper.readValue(jsonResult, valueType);
    } catch (Exception e){
      log.info(e.getMessage());
    }
    return null;
  }

  @Override
  public <V> V getAndDelete(String key, Class<V> valueType) {
    doException();

    if (isExpired(key)) {
      delete(key);
      return null;
    }

    String jsonResult = store.remove(key); // store에서 제거
    expirationTimes.remove(key);           // 만료시간도 제거

    if (jsonResult == null || jsonResult.isBlank()) {
      return null;
    }

    try {
      return objectMapper.readValue(jsonResult, valueType);
    } catch (Exception e){
      log.info(e.getMessage());
    }
    return null;
  }

  @Override
  public <V> void set(String key, V value){
    doException();
    try {
      String jsonValue = objectMapper.writeValueAsString(value);
      store.put(key, jsonValue);
      // 만료 시간 제거 (영구 저장)
      expirationTimes.remove(key);

    } catch (Exception e){
      log.info(e.getMessage());
    }
  }

  @Override
  public <V> void setWithExpired(String key, V value, long durationMillis){
    doException();
    set(key, value);

    // 만료 시간 설정
    long expirationTime = System.currentTimeMillis() + durationMillis;
    expirationTimes.put(key, expirationTime);
  }

  @Override
  public void delete(String key) {
    store.remove(key);
    expirationTimes.remove(key);
  }


  // 만료 여부 확인
  private boolean isExpired(String key) {
    Long expirationTime = expirationTimes.get(key);
    return expirationTime != null && System.currentTimeMillis() > expirationTime;
  }

  // test 용 exception create
  private void doException() {
    if (isException){
      throw new CustomIllegalStateException(ExceptionMessage.REDIS_FAILED);
    }
  }
}