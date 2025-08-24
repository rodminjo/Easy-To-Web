package com.backend.easy_to_web.common.infrastructure.controller.rest;

import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class DataResponse<T> {

    @Getter
    private List<T> items = List.of();

    @Getter
    private Integer page;

    private Integer size;

    @Getter
    private Long totalSize;

    public DataResponse(List<T> items) {
        this.items = items;
    }

    public Integer getSize() {
        return Objects.nonNull(size) ? size : items.size();
    }

}