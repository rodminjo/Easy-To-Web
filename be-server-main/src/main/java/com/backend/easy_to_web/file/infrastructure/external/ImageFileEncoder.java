package com.backend.easy_to_web.file.infrastructure.external;

import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalStateException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.config.properties.FileProperties;
import com.backend.easy_to_web.file.domain.model.ContentType;
import com.backend.easy_to_web.file.domain.model.EncodeFile;
import com.backend.easy_to_web.file.domain.model.File;
import com.backend.easy_to_web.file.domain.port.out.FileEncoder;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ImageFileEncoder implements FileEncoder {

  private static final int MAX_DIMENSION = 4000;
  private final Set<ContentType> ACCEPT_TYPE_SET = Set.of(ContentType.JPG, ContentType.JPEG, ContentType.PNG, ContentType.GIF, ContentType.WEBP);
  private final Set<ContentType> ENCODE_TYPE_SET = Set.of(ContentType.WEBP);
  private final Path encodeDir;

  static {
    OpenCV.loadLocally(); // OpenCV 라이브러리 로딩
  }

  public ImageFileEncoder(FileProperties properties) {
    this.encodeDir = Paths.get(properties.getEncodeDir()); // 예: "/upload/encoded"
    try {
      Files.createDirectories(encodeDir);
    } catch (IOException e) {
      log.error("Failed to create encode directory", e);
      throw new CustomIllegalStateException(ExceptionMessage.FILE_ENCODE_FAILED);
    }
  }

  @Override
  public EncodeFile encode(File file, ContentType encodeType) {
    if (!supports(file, encodeType)) {
      throw new CustomIllegalArgumentException(ExceptionMessage.FILE_ENCODER_NOT_SUPPORT);
    }

    java.io.File inputFile = new java.io.File(file.getPath());

    try (InputStream inputStream = Files.newInputStream(inputFile.toPath());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

      byte[] buffer = new byte[8192]; // 8KB 버퍼
      int bytesRead;

      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }

      byte[] imageBytes = outputStream.toByteArray();

      // WebP로 인코딩
      byte[] webpBytes = encodeToWebP(imageBytes);

      // 저장 경로 설정
      String encodeFileName = UUID.randomUUID().toString();
      Path encodeFile = encodeDir.resolve(encodeFileName);
      try (FileOutputStream fos = new FileOutputStream(encodeFile.toFile())) {
        fos.write(webpBytes);
      }

      long encodedSize = webpBytes.length;

      return EncodeFile.fromFile(file, UUID.randomUUID(), encodeFile.toString(), encodedSize, encodeType);

    } catch (IOException e) {
      log.error("failed image convert", e);
      throw new CustomIllegalStateException(ExceptionMessage.IMAGE_ENCODE_FAILED);
    }
  }

  private Mat resizeIfTooLarge(Mat image) {
    int width = image.cols();
    int height = image.rows();

    // 이미지 크기가 최대 해상도를 초과하는 경우 리사이징
    if (width > MAX_DIMENSION || height > MAX_DIMENSION) {
      double scale = Math.min((double) MAX_DIMENSION / width, (double) MAX_DIMENSION / height);
      Size newSize = new Size(width * scale, height * scale);
      Mat resized = new Mat();
      Imgproc.resize(image, resized, newSize);
      return resized;
    }

    return image;
  }

  private byte[] encodeToWebP(byte[] data) {
    // 바이너리 데이터를 Mat 객체로 변환
    Mat matImage = Imgcodecs.imdecode(new MatOfByte(data), Imgcodecs.IMREAD_UNCHANGED);

    if (matImage.empty()) {
      log.error("디코딩 실패: 지원하지 않거나 손상된 이미지 포맷입니다.");
      throw new CustomIllegalStateException(ExceptionMessage.IMAGE_ENCODE_FAILED);
    }

    // 이미지 크기가 너무 크면 리사이징
    matImage = resizeIfTooLarge(matImage);

    // WebP 품질 설정 (0 ~ 100)
    MatOfInt parameters = new MatOfInt(Imgcodecs.IMWRITE_WEBP_QUALITY, 100);

    // WebP로 인코딩
    MatOfByte output = new MatOfByte();
    boolean result = Imgcodecs.imencode(".webp", matImage, output, parameters);

    if (!result) {
      log.error("WebP 인코딩 실패");
      throw new CustomIllegalStateException(ExceptionMessage.IMAGE_ENCODE_FAILED);
    }

    return output.toArray();
  }




  @Override
  public boolean supports(File file, ContentType encodeType) {
    ContentType contentType = file.getContentType();
    return ACCEPT_TYPE_SET.contains(contentType) && ENCODE_TYPE_SET.contains(encodeType);
  }
}
