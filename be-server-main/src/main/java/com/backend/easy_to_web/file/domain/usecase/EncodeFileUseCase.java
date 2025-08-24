package com.backend.easy_to_web.file.domain.usecase;

import com.backend.easy_to_web.common.application.usecase.UseCase;
import com.backend.easy_to_web.file.domain.model.ContentType;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class EncodeFileUseCase implements UseCase {

  UUID id;
  ContentType contentType;

}
