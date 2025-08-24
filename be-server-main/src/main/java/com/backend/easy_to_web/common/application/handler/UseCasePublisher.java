package com.backend.easy_to_web.common.application.handler;

import com.backend.easy_to_web.common.application.usecase.UseCase;

public interface UseCasePublisher {

    <R, T extends UseCase> R publish(Class<R> returnClass, T useCase);

    <R, T extends UseCase> void publish(T useCase);

    <R> R publish(Class<R> returnClass);
}
