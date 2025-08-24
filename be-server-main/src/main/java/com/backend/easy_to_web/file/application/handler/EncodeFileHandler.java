package com.backend.easy_to_web.file.application.handler;

import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.VoidUseCaseHandler;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.file.domain.model.ContentType;
import com.backend.easy_to_web.file.domain.model.EncodeFile;
import com.backend.easy_to_web.file.domain.model.File;
import com.backend.easy_to_web.file.domain.port.out.FileEncoder;
import com.backend.easy_to_web.file.domain.port.out.FileRepository;
import com.backend.easy_to_web.file.domain.port.out.FileStorage;
import com.backend.easy_to_web.file.domain.usecase.EncodeFileUseCase;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

@Slf4j
@RequiredArgsConstructor
@Handler
public class EncodeFileHandler implements VoidUseCaseHandler<EncodeFileUseCase> {

  private final FileStorage fileStorage;
  private final FileRepository fileRepository;
  private final List<FileEncoder> encoderList;

  @Override
  public Class<EncodeFileUseCase> getVoidUseCaseClass() {
    return EncodeFileUseCase.class;
  }

  @Override
  @Async("threadPoolTaskExecutor")
  public void handleVoid(EncodeFileUseCase useCase) {
    File target = fileRepository.findById(useCase.getId())
        .orElseThrow(() -> new CustomIllegalArgumentException(ExceptionMessage.FILE_NOT_FOUND));

    FileEncoder encoder = findEncoder(target, useCase.getContentType())
        .orElseThrow(() -> new CustomIllegalArgumentException(ExceptionMessage.FILE_ENCODER_NOT_SUPPORT));

    EncodeFile encodeResult = encoder.encode(target, useCase.getContentType());
    String storePath = fileStorage.store(encodeResult.getId(), encodeResult.getPath());
    File encodedFile = encodeResult.toFile(encodeResult.getId(), storePath);
    fileRepository.save(encodedFile);
  }


  private Optional<FileEncoder> findEncoder(File file, ContentType contentType) {
    for (FileEncoder en : encoderList) {
      if (en.supports(file, contentType)) {
        return Optional.of(en);
      }
    }

    return Optional.empty();
  }

}
