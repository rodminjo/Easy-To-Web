package com.backend.easy_to_web.common.application.handler;


import com.backend.easy_to_web.common.application.usecase.UseCase;

public interface UseCaseHandler<R, T extends UseCase> {

    Class<T> getUseCaseClass();

    R handle(T useCase);

}
