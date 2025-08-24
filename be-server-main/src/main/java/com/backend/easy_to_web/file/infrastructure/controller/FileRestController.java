package com.backend.easy_to_web.file.infrastructure.controller;

import com.backend.easy_to_web.common.application.handler.UseCasePublisher;
import com.backend.easy_to_web.common.infrastructure.controller.BaseController;
import com.backend.easy_to_web.common.infrastructure.controller.rest.Response;
import com.backend.easy_to_web.config.properties.FileProperties;
import com.backend.easy_to_web.file.domain.model.ContentType;
import com.backend.easy_to_web.file.domain.result.LoadFileResult;
import com.backend.easy_to_web.file.domain.result.SaveFileResult;
import com.backend.easy_to_web.file.domain.usecase.EncodeFileUseCase;
import com.backend.easy_to_web.file.domain.usecase.LoadFileUseCase;
import com.backend.easy_to_web.file.domain.usecase.SaveFileUseCase;
import com.backend.easy_to_web.file.infrastructure.controller.doc.FileRestControllerDoc;
import com.backend.easy_to_web.file.infrastructure.controller.rest.FileLoadRest.FileLoadInput;
import com.backend.easy_to_web.file.infrastructure.controller.rest.FileSaveRest.FileSaveInput;
import com.backend.easy_to_web.file.infrastructure.controller.rest.FileSaveRest.FileSaveOutput;
import com.backend.easy_to_web.utils.WebUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/file")
public class FileRestController extends BaseController implements FileRestControllerDoc {

  private final UseCasePublisher publisher;
  private final FileProperties fileProperties;


  @Override
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Response<FileSaveOutput>> save(
      @RequestPart(name = "info") FileSaveInput input,
      @RequestPart(name = "file") MultipartFile file
  ) {
    SaveFileUseCase command = input.toCommand(file);
    SaveFileResult result = publisher.publish(SaveFileResult.class, command);

    if (result.isComplete()){
      EncodeFileUseCase encodeCommand = new EncodeFileUseCase(result.getFileId(), ContentType.WEBP);
      publisher.publish(encodeCommand);
    }

    HttpStatus status = !result.isComplete() ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK;
    return ResponseEntity.status(status)
        .body(respond(FileSaveOutput.of(result, fileProperties)));
  }

  @Override
  @GetMapping(value = "/{fileId}")
  public ResponseEntity<Resource> load(@PathVariable(name = "fileId") UUID fileId,
      @ModelAttribute FileLoadInput input) {
    LoadFileUseCase command = input.toCommand(fileId);
    LoadFileResult result = publisher.publish(LoadFileResult.class, command);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + WebUtils.convertForUrl(result.getFileName()) + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(result.getFile());
  }

}
