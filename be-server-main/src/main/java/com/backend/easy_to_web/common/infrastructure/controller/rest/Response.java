package com.backend.easy_to_web.common.infrastructure.controller.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Schema(description = "응답")
public class Response<T> {

    private T data;

    @Schema(description = "에러 데이터", example = "null")
    private ErrorResponse errors;

    public Response(ErrorResponse errors) {
        this.errors = errors;
    }

    public Response(T data) {
        this.data = data;
    }
}