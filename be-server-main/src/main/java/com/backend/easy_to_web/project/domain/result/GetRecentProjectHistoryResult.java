package com.backend.easy_to_web.project.domain.result;

import com.backend.easy_to_web.common.application.result.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class GetRecentProjectHistoryResult implements Result {

  byte[] snapshot;

}
