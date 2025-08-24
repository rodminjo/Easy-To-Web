package com.backend.easy_to_web.file.domain.usecase;

import com.backend.easy_to_web.common.application.usecase.UseCase;
import com.backend.easy_to_web.file.domain.model.ChunkFile;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;


@AllArgsConstructor
@Getter
public class DeleteFileUseCase implements UseCase {

  private final UUID id;

}
