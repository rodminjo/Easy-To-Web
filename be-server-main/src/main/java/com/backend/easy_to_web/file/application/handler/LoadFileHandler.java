package com.backend.easy_to_web.file.application.handler;

import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.file.domain.result.LoadFileResult;
import com.backend.easy_to_web.file.domain.usecase.LoadFileUseCase;
import com.backend.easy_to_web.file.domain.model.File;
import com.backend.easy_to_web.file.domain.port.out.FileRepository;
import com.backend.easy_to_web.file.domain.port.out.FileStorage;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

@RequiredArgsConstructor
@Handler
public class LoadFileHandler implements UseCaseHandler<LoadFileResult, LoadFileUseCase> {

  private final FileStorage fileStorage;
  private final FileRepository fileRepository;


  @Override
  public Class<LoadFileUseCase> getUseCaseClass() {
    return LoadFileUseCase.class;
  }

  @Override
  public LoadFileResult handle(LoadFileUseCase useCase) {
    File target;

    if (useCase.getFormat() != null) {
      target = fileRepository.findByOriginalFileIdAndContentType(useCase.getId(), useCase.getFormat())
          .orElseGet(() -> fileRepository.findById(useCase.getId())
              .orElseThrow(() -> new CustomIllegalArgumentException(ExceptionMessage.FILE_NOT_FOUND)));
    } else {
      target = fileRepository.getById(useCase.getId());
    }

    InputStream load = fileStorage.load(target);
    Resource resource = new InputStreamResource(load);
    return new LoadFileResult(resource, target.getOriginalName());
  }
}
