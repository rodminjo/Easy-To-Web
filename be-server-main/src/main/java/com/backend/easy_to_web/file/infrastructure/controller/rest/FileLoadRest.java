package com.backend.easy_to_web.file.infrastructure.controller.rest;

import com.backend.easy_to_web.file.domain.usecase.LoadFileUseCase;
import com.backend.easy_to_web.file.domain.model.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public class FileLoadRest {

  @Schema(description = "파일 요청 데이터")
  public record FileLoadInput(

      @Schema(description = "인코딩 포멧", example = "WEBP")
      ContentType format

  ) {

    public LoadFileUseCase toCommand(UUID id){
      return new LoadFileUseCase(id, format);
    }

  }

}
