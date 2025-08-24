package com.backend.easy_to_web.config;

import com.backend.easy_to_web.config.properties.RedisProperties;
import com.backend.easy_to_web.utils.SystemUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;


@Slf4j
@RequiredArgsConstructor
@Profile({"local", "test"})
@Configuration
public class EmbeddedRedisConfig {

  private final RedisProperties redisProperties;
  private RedisServer redisServer;


  @PostConstruct
  public void startRedis() throws IOException {
    int port = isRedisRunning() ? findAvailablePort() : redisProperties.getPort();
    if (SystemUtils.isArmArchitecture()) {
      log.info("Using Embedded Redis with ARM Architecture");
      redisServer = new RedisServer(port, Objects.requireNonNull(getRedisServerExecutable()));
    } else {
      log.info("Using Embedded Redis");
      redisServer = RedisServer.newRedisServer()
          .port(port)
          .setting(redisProperties.getMaxmemory())
          .build();
    }
    redisServer.start();
  }

  @PreDestroy
  public void stopRedis() throws IOException {
    redisServer.stop();
  }

  public int findAvailablePort() throws IOException {
    for (int port = 10000; port <= 65535; port++) {
      Process process = executeGrepProcessCommand(port);
      if (!isRunning(process)) {
        return port;
      }
    }

    throw new IllegalStateException("사용가능한 포트가 없습니다.");
  }

  private boolean isRedisRunning() throws IOException {
    return isRunning(executeGrepProcessCommand(redisProperties.getPort()));
  }

  private Process executeGrepProcessCommand(int redisPort) throws IOException {
    String command = String.format("netstat -nat | grep LISTEN|grep %d", redisPort);
    String[] shell = {"/bin/sh", "-c", command};

    return Runtime.getRuntime().exec(shell);
  }

  private boolean isRunning(Process process) {
    String line;
    StringBuilder pidInfo = new StringBuilder();

    try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      while ((line = input.readLine()) != null) {
        pidInfo.append(line);
      }
    } catch (Exception e) {
      throw new IllegalStateException("EMBEDDED Redis 실행 오류");
    }
    return StringUtils.hasText(pidInfo.toString());
  }

  private File getRedisServerExecutable() {
    try {
      String resourcePath = "binary/redis/redis-server-7.2.3-mac-arm64";
      URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
      if (resourceUrl == null) {
        throw new IllegalStateException("EMBEDDED Redis 실행파일을 찾을 수 없습니다");
      }
      return new File(resourceUrl.getFile());
    } catch (Exception e) {
      throw new IllegalStateException("EMBEDDED Redis 실행파일을 찾을 수 없습니다", e);
    }
  }

}
