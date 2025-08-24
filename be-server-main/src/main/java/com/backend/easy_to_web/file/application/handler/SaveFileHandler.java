package com.backend.easy_to_web.file.application.handler;

import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import com.backend.easy_to_web.file.domain.port.out.ChunkUploader;
import com.backend.easy_to_web.file.domain.port.out.FileRepository;
import com.backend.easy_to_web.file.domain.port.out.FileStorage;
import com.backend.easy_to_web.file.domain.result.SaveFileResult;
import com.backend.easy_to_web.file.domain.usecase.SaveFileUseCase;
import com.backend.easy_to_web.file.domain.model.ChunkFile;
import com.backend.easy_to_web.file.domain.model.File;
import com.backend.easy_to_web.file.domain.model.MergeFile;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Handler
public class SaveFileHandler implements UseCaseHandler<SaveFileResult, SaveFileUseCase> {

  private final ChunkUploader chunkUploader;
  private final FileStorage fileStorage;
  private final FileRepository fileRepository;


  @Override
  public Class<SaveFileUseCase> getUseCaseClass() {
    return SaveFileUseCase.class;
  }

  @Override
  @Transactional
  public SaveFileResult handle(SaveFileUseCase command) {
    // 1. 청크 저장
    ChunkFile chunkFile = command.toChunkFile();
    chunkUploader.saveChunk(chunkFile);

    // 2. 업로드 완료 여부 확인
    if (chunkFile.isLastChunk() && chunkUploader.isUploadComplete(chunkFile.getId(), chunkFile.getTotalChunks())) {

      // 3. 병합
      MergeFile merged = chunkUploader.mergeChunks(chunkFile.getId(), chunkFile.getFileName());

      // 4. UUID 중복 확인 및 새 ID 생성
      UUID newFileId = UUID.randomUUID();;

      // 5. 병합된 파일 저장
      String savedFilePath = fileStorage.store(newFileId, merged.getPath());
      File savedFile = merged.toFile(newFileId, savedFilePath);

      // 6. 청크 삭제
      chunkUploader.deleteChunksAndMerge(chunkFile.getId());

      // 7. 저장소에 파일 도메인 저장
      fileRepository.save(savedFile);

      // 8. 저장 결과 반환
      return new SaveFileResult(
          true,
          chunkFile.getChunkNumber(), // 현재 업로드된 청크 수 (마지막)
          chunkFile.getTotalChunks(),
          savedFile.getId()
      );
    } else {
      // 아직 마지막 청크가 아님
      return new SaveFileResult(
          false,
          chunkFile.getChunkNumber(),
          chunkFile.getTotalChunks(),
          null
      );
    }
  }
}
