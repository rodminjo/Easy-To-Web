package com.backend.easy_to_web.project.infrastructure.controller.doc;

import com.backend.easy_to_web.common.infrastructure.controller.CustomPageable;
import com.backend.easy_to_web.common.infrastructure.controller.doc.ApiExceptionExplanation;
import com.backend.easy_to_web.common.infrastructure.controller.doc.ApiResponseExplanations;
import com.backend.easy_to_web.common.infrastructure.controller.rest.Response;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectCreateRest.ProjectCreateInput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectCreateRest.ProjectCreateOutput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectExitRest.ProjectExitInput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectGetHistoryListRest.ProjectGetHistoryListInput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectGetHistoryListRest.ProjectGetHistoryListOutput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectGetHistoryRest.ProjectGetHistoryInput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectGetHistoryRest.ProjectGetHistoryOutput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectGetListRest.ProjectGetListOutput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectGetOneRest.ProjectGetOneInput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectGetOneRest.ProjectGetOneOutput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectGetPublishRest.ProjectGetPublishOutput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectInviteAcceptRest.ProjectInviteAcceptInput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectInviteAcceptRest.ProjectInviteAcceptOutput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectInviteRest.ProjectInviteInput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectPublishRest.ProjectPublishInput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectPublishRest.ProjectPublishOutput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectRemoveMemberRest.ProjectRemoveMemberInput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectRemoveRest.ProjectRemoveInput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectRepublishRest.ProjectRepublishInput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectUnpublishRest.ProjectUnpublishInput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectUpdateMemberPermissionRest.ProjectUpdateMemberPermissionInput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.ProjectUpdateRest.ProjectUpdateInput;
import com.backend.easy_to_web.project.infrastructure.controller.rest.UpdateProjectThumbnailRest.UpdateProjectThumbnailInput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;


@Tag(name = "Project API", description = "프로젝트 API")
public interface ProjectRestControllerDoc {

  @Operation(summary = "프로젝트 목록", description = "프로젝트 목록 조회 API")
  @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
  })
  Response<ProjectGetListOutput> getList();

  @Operation(summary = "프로젝트 단건 조회", description = "프로젝트 단건 조회 API")
  @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_ACCESS_DENIED),
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_NOT_FOUND),
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
  })
  Response<ProjectGetOneOutput> getOne(ProjectGetOneInput input);


  @Operation(summary = "프로젝트 생성", description = "프로젝트 생성 API")
  @ApiResponse(responseCode = "200", description = "생성 성공", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
  })
  Response<ProjectCreateOutput> create(ProjectCreateInput input);


  @Operation(summary = "프로젝트 정보 수정", description = "프로젝트 정보 수정 API")
  @ApiResponse(responseCode = "200", description = "수정 완료", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_ACCESS_DENIED),
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_NOT_FOUND),
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
  })
  Response<Void> update(ProjectUpdateInput input);

  @Operation(summary = "프로젝트 삭제", description = "프로젝트 삭제 API")
  @ApiResponse(responseCode = "200", description = "삭제 완료", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_ACCESS_DENIED),
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_NOT_FOUND),
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
  })
  Response<Void> remove(ProjectRemoveInput input);



  @Operation(summary = "프로젝트 히스토리 목록", description = "프로젝트 히스토리 목록 조회 API")
  @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.PROPERTY_REFERENCE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_NOT_FOUND),
  })
  Response<ProjectGetHistoryListOutput> getHistoryList(ProjectGetHistoryListInput input, CustomPageable pageable);


  @Operation(summary = "프로젝트 히스토리 단건 조회", description = "프로젝트 히스토리 단건 조회 API")
  @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_HISTORY_NOT_FOUND),
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_NOT_FOUND),
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
  })
  Response<ProjectGetHistoryOutput> getHistory(ProjectGetHistoryInput input);


  @Operation(summary = "프로젝트 멤버 권한 수정", description = "프로젝트 멤버 권한 수정 API")
  @ApiResponse(responseCode = "200", description = "수정 완료", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.PERMISSION_TOO_LOW_TO_UPDATE),
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_OWNER_NOT_UPDATABLE),
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_MEMBER_NOT_FOUND),
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_ACCESS_DENIED),
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_NOT_FOUND),
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
  })
  Response<Void> updateMemberPermission(ProjectUpdateMemberPermissionInput input);


  @Operation(summary = "프로젝트 멤버 방출", description = "프로젝트 멤버 방출 API")
  @ApiResponse(responseCode = "200", description = "방출 완료", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.PERMISSION_TOO_LOW_TO_UPDATE),
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_OWNER_NOT_UPDATABLE),
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_MEMBER_NOT_FOUND),
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_ACCESS_DENIED),
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_NOT_FOUND),
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
  })
  Response<Void> removeMember(ProjectRemoveMemberInput input);



  @Operation(summary = "프로젝트 탈퇴", description = "프로젝트 탈퇴 API")
  @ApiResponse(responseCode = "200", description = "탈퇴 완료", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_MEMBER_NOT_FOUND),
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
  })
  Response<Void> exit(ProjectExitInput input);


  @Operation(summary = "프로젝트 초대", description = "프로젝트 초대 API")
  @ApiResponse(responseCode = "200", description = "초대 완료", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
  })
  Response<Void> invite(ProjectInviteInput input);

  @Operation(summary = "프로젝트 초대 승인", description = "프로젝트 초대 승인 API")
  @ApiResponse(responseCode = "200", description = "참가 완료", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
  })
  Response<ProjectInviteAcceptOutput> inviteAccept(UUID projectId, ProjectInviteAcceptInput input);


  @Operation(summary = "프로젝트 게시 요청", description = "프로젝트 게시 요청 API")
  @ApiResponse(responseCode = "200", description = "게시 완료", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
  })
  Response<ProjectPublishOutput> publish(ProjectPublishInput input);


  @Operation(summary = "프로젝트 게시 취소", description = "프로젝트 게시 취소 API")
  @ApiResponse(responseCode = "200", description = "게시 완료", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
  })
  Response<Void> unpublish(ProjectUnpublishInput input);


  @Operation(summary = "프로젝트 최신화", description = "프로젝트 최신화 API")
  @ApiResponse(responseCode = "200", description = "갱신 완료", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
  })
  Response<Void> republish(ProjectRepublishInput input);

  @Operation(summary = "프로젝트 게시 조회", description = "프로젝트 게시 조회 API")
  @ApiResponse(responseCode = "200", description = "조회 완료", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
  })
  Response<ProjectGetPublishOutput> getPublish(String url);


  @Operation(summary = "프로젝트 썸네일 수정", description = "프로젝트 썸네일 수정 API")
  @ApiResponse(responseCode = "200", description = "수정 완료", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_ACCESS_DENIED),
      @ApiExceptionExplanation(value = ExceptionMessage.PROJECT_NOT_FOUND),
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
  })
  Response<Void> updateThumbnail(UpdateProjectThumbnailInput input);

}
