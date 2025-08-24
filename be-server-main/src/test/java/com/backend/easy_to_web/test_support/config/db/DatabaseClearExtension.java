package com.backend.easy_to_web.test_support.config.db;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DatabaseClearExtension implements AfterEachCallback {

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    DatabaseCleaner databaseCleaner = getDataCleaner(context);
    databaseCleaner.clear();

    RedisCleaner cacheCleaner = getCacheCleaner(context);
    cacheCleaner.clear();
  }

  private DatabaseCleaner getDataCleaner(ExtensionContext extensionContext) {
    return SpringExtension.getApplicationContext(extensionContext)
        .getBean(DatabaseCleaner.class);
  }

  private RedisCleaner getCacheCleaner(ExtensionContext extensionContext) {
    return SpringExtension.getApplicationContext(extensionContext)
        .getBean(RedisCleaner.class);
  }



}
