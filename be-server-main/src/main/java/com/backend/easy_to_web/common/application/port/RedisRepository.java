package com.backend.easy_to_web.common.application.port;

public interface RedisRepository {

  <V> V get(String key, Class<V> valueType);

  <V> V getAndDelete(String key, Class<V> valueType);

  <V> void set(String key, V value);

  <V> void setWithExpired(String key, V value, long durationMillis);

  void delete(String key);

}
