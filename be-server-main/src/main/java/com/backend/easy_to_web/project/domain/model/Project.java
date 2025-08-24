package com.backend.easy_to_web.project.domain.model;

import com.backend.easy_to_web.common.domain.model.Domain;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Project extends Domain {

  private UUID id;
  private UUID thumbnailFileId;
  private String title; // 프로젝트 이름
  private String description; // 프로젝트 설명
  private ProjectStatus status; // 프로젝트 상태 (예: 작성 중, 완료 등)

  private Set<Member> members;

  @Builder
  public Project(LocalDateTime createdDate, LocalDateTime modifiedDate,
      String createdBy, String updatedBy, UUID id, UUID thumbnailFileId, String title, String description,
      ProjectStatus status, Set<Member> members) {
    super(createdDate, modifiedDate, createdBy, updatedBy);
    this.id = id;
    this.thumbnailFileId = thumbnailFileId;
    this.title = title;
    this.description = description;
    this.status = status;
    this.members = members;
  }

  public void updateInfo(String title, String description){
    this.title = title;
    this.description = description;
  }

  public void updateStatus(ProjectStatus status){
    this.status = status;
  }

  public void updateThumbnailFileId(UUID thumbnailFileId){
    this.thumbnailFileId = thumbnailFileId;
  }

  public boolean isPublished(){
    return this.status.equals(ProjectStatus.OPEN);
  }


  public void ensureHasPermission(UUID accountId, ProjectPermission requiredPermission) {
    Member member = getMember(accountId);
    if (!member.getPermission().isValidPermission(requiredPermission)) {
      throw new CustomIllegalArgumentException(ExceptionMessage.PROJECT_ACCESS_DENIED, "필요 권한 - " + requiredPermission.name());
    }
  }

  public Member getMember(UUID accountId) {
    return findMember(accountId)
        .orElseThrow(() -> new CustomIllegalArgumentException(ExceptionMessage.PROJECT_MEMBER_NOT_FOUND));
  }

  public List<UUID> getMemberIds() {
    return this.members.stream()
        .map(Member::getAccountId)
        .toList();
  }

  public Map<UUID, ProjectPermission> getMemberIdPermissionMap() {
    return this.members.stream()
        .collect(Collectors.toMap(
            Member::getAccountId,
            Member::getPermission
        ));
  }

  public Optional<Member> findMember(UUID accountId) {
    return members.stream()
        .filter(member -> member.getAccountId().equals(accountId))
        .findFirst();
  }


}
