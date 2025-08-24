package com.backend.easy_to_web.file.domain.model;

import lombok.Getter;

@Getter
public enum ContentType {
  // 이미지
  JPG("jpg", "image/jpeg"),
  JPEG("jpeg", "image/jpeg"),
  PNG("png", "image/png"),
  GIF("gif", "image/gif"),
  WEBP("webp", "image/webp"),
  AVIF("avif", "image/avif"),
  HEIF("heif", "image/heif"),

  // 비디오
  MP4("mp4", "video/mp4"),
  WEBM("webm", "video/webm"),
  OGG("ogg", "video/ogg"),
  MOV("mov", "video/quicktime"),
  MKV("mkv", "video/x-matroska"),
  HLS("m3u8", "application/vnd.apple.mpegurl"),
  DASH("mpd", "application/dash+xml"),

  // 문서
  PDF("pdf", "application/pdf"),
  TXT("txt", "text/plain"),

  // 없음
  NONE("", "");

  private final String extension;
  private final String mimeType;

  ContentType(String extension, String mimeType) {
    this.extension = extension;
    this.mimeType = mimeType;
  }

  public static ContentType fromExtension(String extension) {
    for (ContentType type : ContentType.values()) {
      if (type.getExtension().equalsIgnoreCase(extension)) {
        return type;
      }
    }
    return NONE;
  }
}
