package com.backend.easy_to_web.common.infrastructure.controller.doc;

import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

public final class ApiExceptionExplainParser {

  public static String errorExampleString(ExceptionMessage exceptionMessage) {
    return String.format("""
        {
          "data": null,
          "errors" : {
            "errorCode": "%s",
            "errorDescription": "%s",
            "timeStamp": "2025-01-24T15:30:45.11"
          }
        }
        """, exceptionMessage.getErrorCode(), exceptionMessage.getDescription());
  }

  // 어노테이션을 가진 메서드 확인
  public static void parse(Operation operation, HandlerMethod handlerMethod) {
    ApiResponseExplanations annotation = handlerMethod.getMethodAnnotation(
        ApiResponseExplanations.class);

    if (annotation != null) {
      generateExceptionResponseDocs(operation, annotation.errors());
    }
  }

  // 어노테이션으로 적혀있는 메서드 번호순으로 정렬
  private static void generateExceptionResponseDocs(Operation operation,
      ApiExceptionExplanation[] exceptions) {
    ApiResponses responses = operation.getResponses();

    Map<Integer, List<ExampleHolder>> holders = Arrays.stream(exceptions)
        .map(ExampleHolder::from)
        .collect(Collectors.groupingBy(ExampleHolder::httpStatus));

    addExamplesToResponses(responses, holders);
  }

  // ExceptionMessage 로부터 ExampleHolder 생성
  @Builder(access = AccessLevel.PRIVATE)
  private record ExampleHolder(int httpStatus, String name, String mediaType, String description,
                               Example holder) {

    static ExampleHolder from(ApiExceptionExplanation annotation) {
      ExceptionMessage errorCode = annotation.value();

      return ExampleHolder.builder()
          .httpStatus(errorCode.getHttpStatus().value())
          .name(StringUtils.hasText(annotation.name()) ? annotation.name() : errorCode.name())
          .mediaType(annotation.mediaType())
          .description(annotation.description())
          .holder(createExample(errorCode, annotation.summary(), annotation.description()))
          .build();
    }

    // Example 반환
    private static Example createExample(ExceptionMessage errorCode, String summary,
        String description) {
      Example example = new Example();
      example.setValue(errorExampleString(errorCode));
      example.setSummary(summary);
      example.setDescription(description);

      return example;
    }
  }

  // Example 추가
  private static void addExamplesToResponses(ApiResponses responses,
      Map<Integer, List<ExampleHolder>> holders) {
    holders.forEach((httpStatus, exampleHolders) -> {
      Content content = new Content();
      MediaType mediaType = new MediaType();
      ApiResponse response = new ApiResponse();

      exampleHolders.forEach(holder -> mediaType.addExamples(holder.name(), holder.holder()));
      content.addMediaType("application/json", mediaType);
      response.setContent(content);

      responses.addApiResponse(String.valueOf(httpStatus), response);
    });
  }
}
