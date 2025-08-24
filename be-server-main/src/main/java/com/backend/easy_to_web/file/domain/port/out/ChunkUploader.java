package com.backend.easy_to_web.file.domain.port.out;

import com.backend.easy_to_web.file.domain.model.ChunkFile;
import com.backend.easy_to_web.file.domain.model.MergeFile;
import java.util.UUID;

public interface ChunkUploader {

  void saveChunk(ChunkFile file);

  boolean isUploadComplete(UUID chunkId, int totalChunks);

  MergeFile mergeChunks(UUID chunkId, String originalName);

  void deleteChunksAndMerge(UUID chunkId);

}
