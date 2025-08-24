package com.backend.easy_to_web.test_support.config.db;


import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class RedisCleaner {

  private final StringRedisTemplate redisTemplate;


  @Transactional
  public void clear() {
    Set<String> keys = redisTemplate.keys("*"); // 모든 키 조회
    if (!keys.isEmpty()) {
      redisTemplate.delete(keys); // 모든 키 삭제
    }
  }
}
