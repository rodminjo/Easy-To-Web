package com.backend.easy_to_web.common.application.handler;

import com.backend.easy_to_web.common.application.usecase.UseCase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
public class UseCaseHandlerRegistry {

    private final Map<Class<? extends UseCase>, UseCaseHandler<?, ? extends UseCase>> registryForUseCaseHandlers = new HashMap<>();
    private final Map<Class<? extends UseCase>, VoidUseCaseHandler<? extends UseCase>> registryForVoidUseCaseHandlers = new HashMap<>();
    private final Map<Class<?>, NoUseCaseHandler<?>> registryForNoUseCaseHandlers = new HashMap<>();


    @SuppressWarnings("unchecked")
    public UseCaseHandlerRegistry(
        List<UseCaseHandler<?, ?>> useCaseHandlers,
        List<NoUseCaseHandler<?>> noUseCaseHandlers,
        List<VoidUseCaseHandler<?>> voidUseCaseHandlers
    ) {
        for (var useCaseHandler : useCaseHandlers){
            register((Class<UseCase>) useCaseHandler.getUseCaseClass(), (UseCaseHandler<Object, UseCase>) useCaseHandler);
        }

        for (var useCaseHandler : noUseCaseHandlers){
            register((Class<UseCase>) useCaseHandler.getReturnClass(), (NoUseCaseHandler<UseCase>)  useCaseHandler);
        }

        for (var useCaseHandler : voidUseCaseHandlers){
            register((Class<UseCase>) useCaseHandler.getVoidUseCaseClass(), (VoidUseCaseHandler<UseCase>) useCaseHandler);
        }
    }

    public <R, T extends UseCase> void register(Class<T> key, UseCaseHandler<R, T> useCaseHandler) {
        log.info("Use case {} is registered by handler {}", key.getSimpleName(), useCaseHandler.getClass().getSimpleName());
        registryForUseCaseHandlers.put(key, useCaseHandler);
    }

    public <T extends UseCase> void register(Class<T> key, VoidUseCaseHandler<T> useCaseHandler) {
        log.info("Use case {} is registered by void handler {}", key.getSimpleName(), useCaseHandler.getClass().getSimpleName());
        registryForVoidUseCaseHandlers.put(key, useCaseHandler);
    }

    public <R> void register(Class<R> key, NoUseCaseHandler<R> useCaseHandler) {
        log.info("Use case {} is registered by no param handler {}", key.getSimpleName(), useCaseHandler.getClass().getSimpleName());
        registryForNoUseCaseHandlers.put(key, useCaseHandler);
    }

    public UseCaseHandler<?, ? extends UseCase> detectUseCaseHandlerFrom(Class<? extends UseCase> useCaseClass) {
        return registryForUseCaseHandlers.get(useCaseClass);
    }

    public VoidUseCaseHandler<? extends UseCase> detectVoidUseCaseHandlerFrom(Class<? extends UseCase> useCaseClass) {
        return registryForVoidUseCaseHandlers.get(useCaseClass);
    }

    public NoUseCaseHandler<?> detectNoUseCaseHandlerFrom(Class<?> returnClass) {
        return registryForNoUseCaseHandlers.get(returnClass);
    }
}
