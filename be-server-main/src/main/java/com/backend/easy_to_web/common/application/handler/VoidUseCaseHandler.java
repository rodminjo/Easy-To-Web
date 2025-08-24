package com.backend.easy_to_web.common.application.handler;


import com.backend.easy_to_web.common.application.usecase.UseCase;

public interface VoidUseCaseHandler<T extends UseCase> {

    Class<T> getVoidUseCaseClass();

    void handleVoid(T useCase);

}
