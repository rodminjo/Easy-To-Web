package com.backend.easy_to_web.project.domain.result;

import com.backend.easy_to_web.common.application.result.Result;
import com.backend.easy_to_web.project.domain.model.History;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GetProjectHistoryListResult implements Result {

  long totalCount;
  List<HistoryDto> historyList;

  public GetProjectHistoryListResult(long totalCount, List<HistoryDto> historyList) {
    this.totalCount = totalCount;
    this.historyList = historyList;
  }

  @Builder
  public record HistoryDto(
      long id,
      LocalDateTime editTime,
      List<String> editor
  ){

    public static HistoryDto toDto(History ph, Map<UUID, String> nicknameMap){
      List<String> nicknameList = ph.editorToNickname(nicknameMap);
      return new HistoryDto(ph.getId(), ph.getEditTime(), nicknameList);
    }
  }
}
