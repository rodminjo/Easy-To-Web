package com.backend.easy_to_web.project.infrastructure.controller.rest;

import io.swagger.v3.oas.annotations.media.Schema;

public class ProjectGetPublishRest {


    @Schema(description = "프로젝트 게시물 조회 응답 데이터")
    public record ProjectGetPublishOutput(
        @Schema(description = "프로젝트 콘텐츠", example = "Base64 형식")
        String content

    ) {

    }

}
