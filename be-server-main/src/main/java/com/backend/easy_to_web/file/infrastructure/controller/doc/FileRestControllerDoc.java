package com.backend.easy_to_web.file.infrastructure.controller.doc;

import com.backend.easy_to_web.common.infrastructure.controller.doc.ApiExceptionExplanation;
import com.backend.easy_to_web.common.infrastructure.controller.doc.ApiResponseExplanations;
import com.backend.easy_to_web.common.infrastructure.controller.rest.Response;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.file.infrastructure.controller.rest.FileLoadRest.FileLoadInput;
import com.backend.easy_to_web.file.infrastructure.controller.rest.FileSaveRest.FileSaveInput;
import com.backend.easy_to_web.file.infrastructure.controller.rest.FileSaveRest.FileSaveOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

@Tag(name = "File API", description = "파일 관련 API")
public interface FileRestControllerDoc {

  @Operation(summary = "파일 저장", description = "파일 저장 API")
  @ApiResponses({
      @ApiResponse(responseCode = "206", description = "청크 전송 성공", useReturnTypeSchema = true),
      @ApiResponse(responseCode = "200", description = "저장 성공", useReturnTypeSchema = true)
  })
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.FILE_UPLOAD_FAILED),
      @ApiExceptionExplanation(value = ExceptionMessage.CREATE_DIRECTORY_FAILED),
      @ApiExceptionExplanation(value = ExceptionMessage.FILE_SAVE_FAILED),
      @ApiExceptionExplanation(value = ExceptionMessage.FILE_DELETE_FAILED),
      @ApiExceptionExplanation(value = ExceptionMessage.FILE_LOAD_FAILED),
  })
  ResponseEntity<Response<FileSaveOutput>> save(FileSaveInput input, MultipartFile file);


  @Operation(summary = "파일 조회", description = "파일 조회 API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true)
  })
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.FILE_LOAD_FAILED),
      @ApiExceptionExplanation(value = ExceptionMessage.FILE_NOT_FOUND)
  })
  ResponseEntity<Resource> load(UUID fileId, FileLoadInput input);


}
