package com.backend.easy_to_web.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "file")
public class FileProperties {

  // 업로드 최대 크기 (byte 단위)
  private int maxUploadSize;

  // 청크 파일 저장 위치
  private String chunkDir;

  // 병합 파일 접미사
  private String mergedSuffix;

  // 최종 파일 저장 위치
  private String storageDir;

  // 파일 다운로드 api path
  private String downloadPath;

  // 인코딩 파일 임시 저장 위치
  private String encodeDir;

}
