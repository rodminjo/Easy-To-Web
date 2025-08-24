package com.backend.easy_to_web.common.infrastructure.controller.rest;

import com.backend.easy_to_web.common.application.usecase.UseCase;

public interface BaseInput<T extends UseCase> {

  T toCommand();
}
