package com.backend.easy_to_web.common.infrastructure.controller.doc;

import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiExceptionExplanation {
  ExceptionMessage value();

  String name() default "";

  String mediaType() default "application/json";

  String summary() default "";

  String description() default "";
}
