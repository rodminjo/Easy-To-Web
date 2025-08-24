package com.backend.easy_to_web.project.domain.result;

import com.backend.easy_to_web.common.application.result.Result;
import com.backend.easy_to_web.project.domain.model.Project;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.project.domain.model.ProjectStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GetProjectResult implements Result {

  private UUID id;
  private String title; // 프로젝트 이름
  private String description; // 프로젝트 설명
  private ProjectStatus status; // 프로젝트 상태 (예: 작성 중, 완료 등)
  private LocalDateTime createDate;
  private String publishUrl;

  private List<ProjectMemberDto> members;

  public GetProjectResult(Project project, List<ProjectMemberDto> members) {
    this.id = project.getId();
    this.title = project.getTitle();
    this.description = project.getDescription();
    this.status = project.getStatus();
    this.createDate = project.getCreatedDate();
    this.members = members;
  }

  public void withPublishUrl(String url){
    this.publishUrl = url;
  }

  @Builder
  public record ProjectMemberDto(
      UUID accountId,
      String email,
      String nickname,
      String profileUrl,
      ProjectPermission permission
  ) {

  }
}
