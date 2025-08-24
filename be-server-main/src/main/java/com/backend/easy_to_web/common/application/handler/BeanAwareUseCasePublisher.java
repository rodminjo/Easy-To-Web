package com.backend.easy_to_web.common.application.handler;

import com.backend.easy_to_web.common.application.usecase.UseCase;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.domain.exception.UseCaseHandlerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeanAwareUseCasePublisher implements UseCasePublisher {

    private final UseCaseHandlerRegistry registry;


    @Override
    @SuppressWarnings("unchecked")
    public <R, T extends UseCase> R publish(Class<R> returnClass, T useCase) {
        var useCaseHandler = (UseCaseHandler<R, T>) registry.detectUseCaseHandlerFrom(useCase.getClass());
        validateUseCaseHandlerDetection(useCase, useCaseHandler);
        return useCaseHandler.handle(useCase);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R, T extends UseCase> void publish(T useCase) {
        var voidUseCaseHandler = (VoidUseCaseHandler<T>) registry.detectVoidUseCaseHandlerFrom(useCase.getClass());
        if (Objects.isNull(voidUseCaseHandler)) {
            var useCaseHandler = (UseCaseHandler<R, T>) registry.detectUseCaseHandlerFrom(useCase.getClass());
            validateUseCaseHandlerDetection(useCase, useCaseHandler);
            useCaseHandler.handle(useCase);
        } else {
            validateVoidUseCaseHandlerDetection(useCase, voidUseCaseHandler);
            voidUseCaseHandler.handleVoid(useCase);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R publish(Class<R> returnClass) {
        var useCaseHandler = (NoUseCaseHandler<R>) registry.detectNoUseCaseHandlerFrom(returnClass);
        validateNoParamUseCaseHandlerDetection(useCaseHandler);
        return useCaseHandler.handle();
    }

    private <R, T extends UseCase> void validateUseCaseHandlerDetection(T useCase, UseCaseHandler<R, T> useCaseHandler) {
        if (Objects.isNull(useCaseHandler)) {
            log.error("Use case handler cannot be detected for the use case: {}, handlers: {}", useCase, registry.getRegistryForUseCaseHandlers());
            throw new UseCaseHandlerException(ExceptionMessage.USE_CASE_HANDLER_NOT_DETECTED_ERROR);
        }
    }

    private <T extends UseCase> void validateVoidUseCaseHandlerDetection(T useCase, VoidUseCaseHandler<T> useCaseHandler) {
        if (Objects.isNull(useCaseHandler)) {
            log.error("Void use case handler cannot be detected for the use case: {}, handlers: {}", useCase, registry.getRegistryForVoidUseCaseHandlers());
            throw new UseCaseHandlerException(ExceptionMessage.USE_CASE_HANDLER_NOT_DETECTED_ERROR);
        }
    }

    private <R> void validateNoParamUseCaseHandlerDetection(NoUseCaseHandler<R> useCaseHandler) {
        if (Objects.isNull(useCaseHandler)) {
            log.error("Void use case handler cannot be detected for the handlers: {}", registry.getRegistryForNoUseCaseHandlers());
            throw new UseCaseHandlerException(ExceptionMessage.USE_CASE_HANDLER_NOT_DETECTED_ERROR);
        }
    }
}
