package com.backend.easy_to_web.project.domain.result;

import com.backend.easy_to_web.common.application.result.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetProjectPublishResult implements Result {
    String content;
}
