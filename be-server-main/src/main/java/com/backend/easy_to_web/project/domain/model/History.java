package com.backend.easy_to_web.project.domain.model;

import com.backend.easy_to_web.common.domain.model.Domain;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class History extends Domain {

  long id;
  private byte[] content;
  private UUID projectId;
  private LocalDateTime editTime;
  private Set<UUID> editor;

  @Builder
  public History(LocalDateTime createdDate, LocalDateTime modifiedDate, String createdBy,
      String updatedBy, long id, byte[] content, UUID projectId, LocalDateTime editTime, Set<UUID> editor) {
    super(createdDate, modifiedDate, createdBy, updatedBy);
    this.id = id;
    this.projectId = projectId;
    this.content = content;
    this.editTime = editTime;
    this.editor = editor;
  }

  public List<String> editorToNickname(Map<UUID, String> nicknameMap){
    return editor.stream()
        .map(e -> nicknameMap.getOrDefault(e, e.toString()))
        .toList();
  }
}
