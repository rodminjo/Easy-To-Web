package com.backend.easy_to_web.common.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionMessage {
  UNEXPECTED_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"50000", "죄송합니다. 잠시후 시도해주세요."),
  USE_CASE_HANDLER_NOT_DETECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"50001", "useCaseHandler.notDetected"),
  AUTHENTICATED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"50002", "unexpected authentication error occurred"),
  ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"50003", "unexpected access denied error occurred"),
  MAIL_WRITE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "50004", "이메일 작성 오류가 발생했습니다. 관리자에게 문의해주세요"),
  MAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "50005", "이메일 전송 오류가 발생했습니다. 관리자에게 문의해주세요"),
  MAIL_FILE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50006", "이메일 파일 오류가 발생했습니다. 관리자에게 문의해주세요"),
  REDIS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "50007", "데이터베이스 오류가 발생했습니다. 관리자에게 문의해주세요"),
  FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "50008", "파일 업로드 중 오류가 발생했습니다. 관리자에게 문의해주세요"),
  CREATE_DIRECTORY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "50009", "파일 업로드 중 오류가 발생했습니다. 관리자에게 문의해주세요"),
  FILE_LOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "50010", "파일 다운로드 중 오류가 발생했습니다. 관리자에게 문의해주세요"),
  FILE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "50011", "파일 업로드 중 오류가 발생했습니다. 관리자에게 문의해주세요"),
  FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "50012", "파일 삭제 중 오류가 발생했습니다. 관리자에게 문의해주세요"),
  IMAGE_ENCODE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "50013", "이미지 파일 인코딩 실패"),
  FILE_ENCODE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "50014", "파일 인코딩 중 오류가 발생했습니다. 관리자에게 문의해주세요"),

  INFO_REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "40001", "리프레시 토큰이 존재하지 않습니다."),
  EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "40002", "이미 가입된 이메일입니다."),
  CERTIFICATION_FAILED(HttpStatus.BAD_REQUEST, "40003", "인증정보가 존재하지 않습니다."),
  EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "40004", "이메일 정보가 존재하지 않습니다."),
  INPUT_VALUE_INVALID(HttpStatus.BAD_REQUEST, "40005", "{\\\"errorFieldName\\\":\\\"errorMsg\\\"}"),
  PROJECT_NOT_FOUND(HttpStatus.BAD_REQUEST, "40006", "프로젝트가 존재하지 않습니다."),
  PROPERTY_REFERENCE_INVALID(HttpStatus.BAD_REQUEST, "40007", "sort 값이 올바르지 않습니다"),
  PROJECT_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "40008", "프로젝트를 찾을 수 없거나 권한이 없습니다."),
  PROJECT_HISTORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "40009", "프로젝트 히스토리를 찾을 수 없습니다."),
  FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "40010", "파일을 찾을 수 없습니다."),
  METHOD_ARGUMENT_MISMATCH(HttpStatus.BAD_REQUEST, "40011", "입력 값이 올바르지 않습니다"),
  FILE_ENCODER_NOT_SUPPORT(HttpStatus.BAD_REQUEST, "40012", "지원하지 않는 인코딩 타입입니다."),
  PROJECT_MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "40013", "해당 회원이 프로젝트에 존재하지 않습니다."),
  PROJECT_OWNER_NOT_UPDATABLE(HttpStatus.BAD_REQUEST, "40014", "프로젝트 소유회원은 변경할 수 없습니다."),
  PERMISSION_TOO_LOW_TO_UPDATE(HttpStatus.BAD_REQUEST, "40015", "요청자의 권한이 낮아 권한을 변경할 수 없습니다."),
  PROJECT_MEMBER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "40016", "참가중인 회원입니다."),
  PROJECT_PUBLISH_NOT_FOUND(HttpStatus.BAD_REQUEST, "40017", "프로젝트가 게시중이 아닙니다."),
  INVALID_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "40018", "지원하지 않는 OAuth 인증입니다."),
  ACCOUNT_NOT_FOUND(HttpStatus.BAD_REQUEST, "40019", "매칭되는 계정이 존재하지 않습니다."),

  UNEXPECTED_AUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED, "40100", "인증 오류. 관리자에게 문의해주세요."),
  ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,"40101", "엑세스 토큰이 만료되었습니다."),
  USER_NOT_LOGIN(HttpStatus.UNAUTHORIZED,"40102", "로그인이 필요합니다."),
  LOGIN_INFO_NOT_FOUND(HttpStatus.UNAUTHORIZED, "40103", "로그인 정보가 누락되었습니다."),
  REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "40104", "리프레시 토큰이 존재하지 않습니다."),
  LOGIN_INFO_INVALID(HttpStatus.UNAUTHORIZED, "40105", "일치하는 계정이 존재하지 않습니다. 다시 확인해주세요."),
  REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "40106", "리프레시 토큰이 만료되었습니다."),
  REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "40107", "리프레시 토큰이 유효하지 않습니다."),
  USER_AGENT_INVALID(HttpStatus.UNAUTHORIZED, "40108", "접속 환경이 유효하지 않습니다."),
  OAUTH_INVALID(HttpStatus.UNAUTHORIZED, "40109", "OAuth 접속 정보가 유효하지 않습니다."),

  UNEXPECTED_ACCESS_DENIED(HttpStatus.FORBIDDEN, "40300", "인가 오류. 관리자에게 문의해주세요"),
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "40301", "권한이 없습니다."),

  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "40401", "해당하는 요청이 존재하지 않습니다."),
  PUBLISH_NOT_FOUND(HttpStatus.NOT_FOUND, "40402", "해당하는 게시글이 존재하지 않습니다.");

  private final HttpStatus httpStatus;
  private final String errorCode;
  private final String description;


  ExceptionMessage(HttpStatus httpStatus, String errorId, String description) {
    this.httpStatus = httpStatus;
    this.errorCode = errorId;
    this.description = description;
  }
}
