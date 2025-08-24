package com.backend.easy_to_web.file.application.handler;

import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.VoidUseCaseHandler;
import com.backend.easy_to_web.file.domain.model.File;
import com.backend.easy_to_web.file.domain.port.out.FileRepository;
import com.backend.easy_to_web.file.domain.port.out.FileStorage;
import com.backend.easy_to_web.file.domain.usecase.DeleteFileUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Handler
public class DeleteFileHandler implements VoidUseCaseHandler<DeleteFileUseCase> {

  private final FileStorage fileStorage;
  private final FileRepository fileRepository;

  @Override
  public Class<DeleteFileUseCase> getVoidUseCaseClass() {
    return DeleteFileUseCase.class;
  }

  @Override
  @Async("threadPoolTaskExecutor")
  @Transactional
  public void handleVoid(DeleteFileUseCase useCase) {
    // 연관 파일 조회
    File file = fileRepository.getById(useCase.getId());
    List<File> children = fileRepository.findAllByOriginalFileId(file.getId());

    // 실질 파일 삭제
    children.forEach(child ->{fileStorage.delete(child.getId());});
    fileStorage.delete(file.getId());

    // 도메인 삭제
    fileRepository.deleteAllByOriginalFileId(file.getId());
    fileRepository.delete(file);

  }
}
