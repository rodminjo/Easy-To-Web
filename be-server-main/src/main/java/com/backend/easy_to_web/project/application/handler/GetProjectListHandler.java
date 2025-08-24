package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.common.application.handler.NoUseCaseHandler;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalStateException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.config.properties.FileProperties;
import com.backend.easy_to_web.project.domain.result.GetProjectListResult;
import com.backend.easy_to_web.project.domain.result.GetProjectListResult.ProjectInfoDto;
import com.backend.easy_to_web.project.domain.model.Project;
import com.backend.easy_to_web.project.domain.model.Member;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.project.domain.port.out.ProjectMemberRepository;
import com.backend.easy_to_web.project.domain.port.out.ProjectRepository;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Handler
public class GetProjectListHandler implements NoUseCaseHandler<GetProjectListResult> {

  private final FileProperties fileProperties;
  private final ProjectRepository projectRepository;
  private final ProjectMemberRepository projectMemberRepository;


  @Override
  public GetProjectListResult handle() {
    String accountIdStr = (String) SecurityScopeUtils.getAuthentication().getPrincipal();
    if (accountIdStr == null) {
      throw new CustomIllegalStateException(ExceptionMessage.USER_NOT_LOGIN);
    }

    // 계정 id 로 참여 데이터 가져오기
    UUID accountId = UUID.fromString(accountIdStr);
    List<Member> allMember = projectMemberRepository.findAllByAccountId(accountId);

    // permission 별 계정 데이터 세팅
    Map<ProjectPermission, List<UUID>> projectIdsMap = new HashMap<>();
    for (ProjectPermission permission : ProjectPermission.values()) {
      projectIdsMap.put(permission, new ArrayList<>());
    }

    // permission 별 계정 데이터 매핑과 참여 날짜 매핑
    Map<UUID, LocalDateTime> projectJoinDates = new HashMap<>();
    allMember.forEach(member -> {
      projectIdsMap.get(member.getPermission()).add(member.getProjectId());
      projectJoinDates.put(member.getProjectId(), member.getCreatedDate());
    });

    // 결과 입력
    GetProjectListResult result = new GetProjectListResult();
    projectIdsMap.forEach((key, value) -> {
      List<Project> allByProjectIds = projectRepository.findAllByProjectIds(value);
      List<ProjectInfoDto> infoList = result.getProjectInfos().get(key);

      for (Project project : allByProjectIds) {

        String thumbnail = project.getThumbnailFileId() != null ?
                fileProperties.getDownloadPath() + "/" + project.getThumbnailFileId() :
                null;


        infoList.add(ProjectInfoDto.builder()
            .id(project.getId())
            .thumbnailUrl(thumbnail)
            .title(project.getTitle())
            .description(project.getDescription())
            .status(project.getStatus())
            .memberCount(project.getMembers().size())
            .joinedDate(projectJoinDates.get(project.getId()))
            .build()
        );
      }
    });

    return result;
  }

  @Override
  public Class<GetProjectListResult> getReturnClass() {
    return GetProjectListResult.class;
  }
}
