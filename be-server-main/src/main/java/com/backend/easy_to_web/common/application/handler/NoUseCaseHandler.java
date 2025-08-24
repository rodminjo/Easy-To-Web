package com.backend.easy_to_web.common.application.handler;

public interface NoUseCaseHandler<R> {

    Class<R> getReturnClass();

    R handle();

}
