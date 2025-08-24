package com.backend.easy_to_web.common.infrastructure.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

@Schema(description = "페이지네이션 요청 객체")
public record CustomPageable(
    @Schema(description = "페이지 번호 (0부터 시작)", example = "0")
    int page,

    @Schema(description = "페이지 크기", example = "10")
    int size,

    @Schema(description = "정렬 기준 (예: id,desc)", example = "[id,desc]")
    String sort

    ){
    @JsonCreator
    public CustomPageable(
        @JsonProperty("page") int page,
        @JsonProperty("size") int size,
        @JsonProperty("sort") String sort
    ) {
        this.page = page;
        this.size = size;
        this.sort = sort;
    }

    public Pageable toPageable() {
        if (!StringUtils.hasText(sort)) {
            return PageRequest.of(page, size);
        }

        String[] sortParams = sort.split(",");
        if (sortParams.length == 2) {
            String field = sortParams[0].trim();
            Sort.Direction direction = sortParams[1].trim().equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
            return PageRequest.of(page, size, Sort.by(new Sort.Order(direction, field)));
        }

        return PageRequest.of(page, size, Sort.by(Sort.Order.asc(sort.trim())));
    }
}
