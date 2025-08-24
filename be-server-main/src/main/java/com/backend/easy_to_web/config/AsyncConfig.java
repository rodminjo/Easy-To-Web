package com.backend.easy_to_web.config;

import com.backend.easy_to_web.utils.AsyncScopeUtils;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {


  @Bean(name = "threadPoolTaskExecutor", destroyMethod = AbstractBeanDefinition.SCOPE_DEFAULT)
  public Executor executorWithSession() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(3);
    taskExecutor.setMaxPoolSize(30);
    taskExecutor.setQueueCapacity(10);
    taskExecutor.setThreadNamePrefix("ExecutorWithSession-");
    taskExecutor.initialize();

    return new HandlingExecutor(taskExecutor);
  }

  public static class HandlingExecutor implements AsyncTaskExecutor {
    private final AsyncTaskExecutor executor;

    public HandlingExecutor(AsyncTaskExecutor executor) {
      this.executor = executor;
    }

    @Override
    public void execute(Runnable task) {
      executor.execute(createWrappedRunnable(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
      return executor.submit(createWrappedRunnable(task));
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
      return executor.submit(createCallable(task));
    }

    private <T> Callable<T> createCallable(final Callable<T> task) {
      Authentication authentication = SecurityScopeUtils.getAuthentication();

      return new AttributeAwareTask<>(new Callable<T>() {
        @Override
        public T call() throws Exception {
          try {
            return task.call();
          } catch (Exception ex) {
            handle(ex);
            throw ex;
          }
        }
      }, authentication);
    }

    private Runnable createWrappedRunnable(final Runnable task) {
      Authentication authentication = SecurityScopeUtils.getAuthentication();

      return new AttributeAwareTask<>(new Runnable() {
        @Override
        public void run() {
          try {
            task.run();
          } catch (Exception ex) {
            handle(ex);
          }
        }
      }, authentication);
    }

    private void handle(Exception ex) {
      log.info("Failed to execute callable. : {}", ex.getMessage());
      log.error("Failed to execute callable. ",ex);
    }

    public void destroy() {
      if(executor instanceof ThreadPoolTaskExecutor){
        ((ThreadPoolTaskExecutor) executor).shutdown();
      }
    }
  }

  public static class AttributeAwareTask<T> implements Callable<T>, Runnable {
    private Callable<T> callable;
    private Runnable runnable;
    private Authentication authentication;

    public AttributeAwareTask(Callable<T> task, Authentication authentication) {
      this.callable = task;
      this.authentication = authentication;
    }

    public AttributeAwareTask(Runnable task, Authentication authentication) {
      this.runnable = task;
      this.authentication = authentication;
    }

    @Override
    public T call() throws Exception {
      AsyncScopeUtils.setAuthentication(authentication);
      try {
        return callable.call();
      } finally {
        AsyncScopeUtils.removeAuthentication();
      }
    }

    @Override
    public void run() {
      AsyncScopeUtils.setAuthentication(authentication);
      try {
        runnable.run();
      } finally {
        AsyncScopeUtils.removeAuthentication();
      }
    }
  }


}
