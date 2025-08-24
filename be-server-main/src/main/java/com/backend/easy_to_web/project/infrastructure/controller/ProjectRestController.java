package com.backend.easy_to_web.project.infrastructure.controller;


import com.backend.easy_to_web.common.application.handler.UseCasePublisher;
import com.backend.easy_to_web.common.infrastructure.controller.BaseController;
import com.backend.easy_to_web.common.infrastructure.controller.CustomPageable;
import com.backend.easy_to_web.common.infrastructure.controller.rest.Response;
import com.backend.easy_to_web.config.properties.FileProperties;
import com.backend.easy_to_web.file.domain.usecase.DeleteFileUseCase;
import com.backend.easy_to_web.project.domain.result.CreateProjectResult;
import com.backend.easy_to_web.project.domain.result.GetProjectHistoryListResult;
import com.backend.easy_to_web.project.domain.result.GetProjectHistoryResult;
import com.backend.easy_to_web.project.domain.result.GetProjectListResult;
import com.backend.easy_to_web.project.domain.result.GetProjectResult;
import com.backend.easy_to_web.project.domain.result.GetProjectPublishResult;
import com.backend.easy_to_web.project.domain.result.AcceptProjectInvitationResult;
import com.backend.easy_to_web.project.domain.result.PublishProjectResult;
import com.backend.easy_to_web.project.domain.result.UpdateProjectThumbnailResult;
import com.backend.easy_to_web.project.domain.usecase.CreateProjectUseCase;
import com.backend.easy_to_web.project.domain.usecase.ExitProjectUseCase;
import com.backend.easy_to_web.project.domain.usecase.GetProjectHistoryUseCase;
import com.backend.easy_to_web.project.domain.usecase.GetProjectHistoryListUseCase;
import com.backend.easy_to_web.project.domain.usecase.GetProjectUseCase;
import com.backend.easy_to_web.project.domain.usecase.GetProjectPublishUseCase;
import com.backend.easy_to_web.project.domain.usecase.AcceptProjectInvitationUseCase;
import com.backend.easy_to_web.project.domain.usecase.InviteProjectUseCase;
import com.backend.easy_to_web.project.domain.usecase.PublishProjectUseCase;
import com.backend.easy_to_web.project.domain.usecase.RemoveProjectUseCase;
import com.backend.easy_to_web.project.domain.usecase.RemoveProjectMemberUseCase;
import com.backend.easy_to_web.project.domain.usecase.RepublishProjectUseCase;
import com.backend.easy_to_web.project.domain.usecase.UnpublishProjectUseCase;
import com.backend.easy_to_web.project.domain.usecase.UpdateProjectThumbnailUseCase;
import com.backend.easy_to_web.project.domain.usecase.UpdateProjectUseCase;
import com.backend.easy_to_web.project.domain.usecase.UpdateProjectMemberPermissionUseCase;
import com.backend.easy_to_web.project.infrastructure.controller.doc.ProjectRestControllerDoc;
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
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/project")
public class ProjectRestController extends BaseController implements ProjectRestControllerDoc {

  private final UseCasePublisher publisher;


  @Override
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/list")
  public Response<ProjectGetListOutput> getList() {
    GetProjectListResult result = publisher.publish(GetProjectListResult.class);
    return respond(new ProjectGetListOutput(result.getProjectInfos()));
  }


  @Override
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("")
  public Response<ProjectGetOneOutput> getOne(@Valid @ModelAttribute ProjectGetOneInput input) {
    GetProjectUseCase command = input.toCommand();
    GetProjectResult result = publisher.publish(GetProjectResult.class, command);
    return respond(ProjectGetOneOutput.fromResult(result));
  }

  @Override
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("")
  public Response<ProjectCreateOutput> create(@Valid @RequestBody ProjectCreateInput input) {
    CreateProjectUseCase command = input.toCommand();
    CreateProjectResult result = publisher.publish(CreateProjectResult.class, command);
    return respond(new ProjectCreateOutput(result.getProjectId()));
  }

  @Override
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("")
  public Response<Void> update(@Valid @RequestBody ProjectUpdateInput input) {
    UpdateProjectUseCase command = input.toCommand();
    publisher.publish(command);
    return voidRespond();
  }

  @Override
  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping("")
  public Response<Void> remove(@Valid @ModelAttribute ProjectRemoveInput input) {
    RemoveProjectUseCase command = input.toCommand();
    publisher.publish(command);
    return voidRespond();
  }


  @Override
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/history/list")
  public Response<ProjectGetHistoryListOutput> getHistoryList(
      @Valid @ModelAttribute ProjectGetHistoryListInput input, CustomPageable pageable) {
    GetProjectHistoryListUseCase command = input.toCommand(pageable);
    GetProjectHistoryListResult result = publisher.publish(GetProjectHistoryListResult.class, command);
    return respond(new ProjectGetHistoryListOutput(result.getTotalCount(), result.getHistoryList()));
  }

  @Override
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/history")
  public Response<ProjectGetHistoryOutput> getHistory(
      @Valid @ModelAttribute ProjectGetHistoryInput input) {
    GetProjectHistoryUseCase command = input.toCommand();
    GetProjectHistoryResult result = publisher.publish(GetProjectHistoryResult.class, command);
    return respond(new ProjectGetHistoryOutput(result.getContent(), result.getEditTime(), result.getEditor()));
  }

  @Override
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/member/permission")
  public Response<Void> updateMemberPermission(
      @Valid @RequestBody ProjectUpdateMemberPermissionInput input) {
    UpdateProjectMemberPermissionUseCase command = input.toCommand();
    publisher.publish(command);
    return voidRespond();
  }

  @Override
  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping("/member")
  public Response<Void> removeMember(@Valid @RequestBody ProjectRemoveMemberInput input) {
    RemoveProjectMemberUseCase command = input.toCommand();
    publisher.publish(command);
    return voidRespond();
  }


  @Override
  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping("/exit")
  public Response<Void> exit(ProjectExitInput input) {
    ExitProjectUseCase command = input.toCommand();
    publisher.publish(command);
    return voidRespond();
  }

  @Override
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/invite")
  public Response<Void> invite(@Valid @RequestBody ProjectInviteInput input) {
    InviteProjectUseCase command = input.toCommand();
    publisher.publish(command);
    return voidRespond();
  }

  @Override
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/invite/{projectId}")
  public Response<ProjectInviteAcceptOutput> inviteAccept(
      @PathVariable(name = "projectId") UUID projectId,
      @Valid @RequestBody ProjectInviteAcceptInput input
  ) {
    AcceptProjectInvitationUseCase command = input.toCommand(projectId);
    AcceptProjectInvitationResult result = publisher.publish(AcceptProjectInvitationResult.class, command);
    return respond(new ProjectInviteAcceptOutput(result.getProjectId()));
  }

  @Override
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/publish")
  public Response<ProjectPublishOutput> publish(@Valid @RequestBody ProjectPublishInput input) {
    PublishProjectUseCase command = input.toCommand();
    PublishProjectResult result = publisher.publish(PublishProjectResult.class, command);
    return respond(new ProjectPublishOutput(result.getUrl()));
  }

  @Override
  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping("/publish")
  public Response<Void> unpublish(@Valid @ModelAttribute ProjectUnpublishInput input) {
    UnpublishProjectUseCase command = input.toCommand();
    publisher.publish(command);
    return voidRespond();
  }

  @Override
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/publish/refresh")
  public Response<Void> republish(@Valid @RequestBody ProjectRepublishInput input) {
    RepublishProjectUseCase command = input.toCommand();
    publisher.publish(command);
    return voidRespond();
  }

  @Override
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/publish/{url}")
  public Response<ProjectGetPublishOutput> getPublish(@PathVariable(name = "url") String url) {
    GetProjectPublishUseCase command = new GetProjectPublishUseCase(url);
    GetProjectPublishResult result = publisher.publish(GetProjectPublishResult.class, command);
    return respond(new ProjectGetPublishOutput(result.getContent()));
  }

  @Override
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/thumbnail")
  public Response<Void> updateThumbnail(@Valid @RequestBody UpdateProjectThumbnailInput input) {
    UpdateProjectThumbnailUseCase command = input.toCommand();
    UpdateProjectThumbnailResult result = publisher.publish(UpdateProjectThumbnailResult.class, command);

    if (result.getPastProjectThumbnailFileId() != null){
      DeleteFileUseCase deleteFileUseCase = new DeleteFileUseCase(result.getPastProjectThumbnailFileId());
      publisher.publish(deleteFileUseCase);
    }

    return voidRespond();
  }


}
