package com.backend.easy_to_web.file.infrastructure.controller.rest;

import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.config.properties.FileProperties;
import com.backend.easy_to_web.file.domain.result.SaveFileResult;
import com.backend.easy_to_web.file.domain.usecase.SaveFileUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public class FileSaveRest {

  @Schema(description = "파일 저장 요청 데이터")
  public record FileSaveInput(

      @Schema(description = "chunk id", example = "UUID")
      UUID id,

      @Schema(description = "chunk 순번", example = "1")
      @NotBlank(message = ValidationMessage.STRING_REQUIRED)
      int chunkNumber,

      @Schema(description = "총 chunk 갯수", example = "100")
      @NotBlank(message = ValidationMessage.STRING_REQUIRED)
      int totalChunks,

      @Schema(description = "파일 이름", example = "File_Name.jpg")
      @NotBlank(message = ValidationMessage.STRING_REQUIRED)
      String fileName,

      @Schema(description = "파일 확장자", example = "video/mp4")
      @NotBlank(message = ValidationMessage.STRING_REQUIRED)
      String contentType,

      @Schema(description = "파일 사이즈 (byte)", example = "100")
      @NotBlank(message = ValidationMessage.STRING_REQUIRED)
      long fileSize

  ) {


    public SaveFileUseCase toCommand(MultipartFile file) {
      return new SaveFileUseCase(this.id, this.chunkNumber, this.totalChunks, this.fileName,
          this.contentType, this.fileSize, file);
    }
  }

  @Schema(description = "파일 저장 응답 데이터")
  public record FileSaveOutput(
      @Schema(description = "total 업로드 완료 여부", example = "false")
      boolean isComplete,

      @Schema(description = "현재까지 업로드 chunks", example = "1")
      int uploadedChunks,

      @Schema(description = "업로드해야 할 total chunks 갯수", example = "10")
      int totalChunks,

      @Schema(description = "file id (완료시 표시)", example = "UUID")
      UUID fileId,

      @Schema(description = "파일 다운로드 링크", example = "/api/upload")
      String fileUrl
  ) {

    public static FileSaveOutput of(SaveFileResult result, FileProperties properties) {
      return new FileSaveOutput(
          result.isComplete(),
          result.getUploadedChunks(),
          result.getTotalChunks(),
          result.getFileId(),
          result.isComplete() ? properties.getDownloadPath() + "/" + result.getFileId() : null
      );
    }

  }
}
