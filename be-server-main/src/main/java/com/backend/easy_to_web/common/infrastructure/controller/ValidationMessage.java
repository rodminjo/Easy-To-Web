package com.backend.easy_to_web.common.infrastructure.controller;

public class ValidationMessage {

  // null 관련
  public static final String NOT_NULL = "널값은 유효하지 않습니다.";

  // 문자열 관련
  public static final String STRING_REQUIRED = "필수 입력값입니다.";

  // 아이디 관련 메세지 및 정규식
  public static final String NAME_REQUIRED = "이름는 필수 입력값입니다.";
  public static final String NAME_PATTERN = "이름은 문자만 사용하여 4~20자리여야 합니다.";
  public static final String NAME_FORMAT = "문자 4~20자리";
  public static final String NAME_PATTERN_REGEX = "^[가-힣a-zA-Z]{4,20}$";

  // 이메일 관련 메세지
  public static final String EMAIL_REQUIRED = "이메일은 필수 입력값입니다.";
  public static final String EMAIL_INVALID = "유효하지 않은 이메일 형식입니다.";

  // 비밀번호 관련 메세지 및 정규식
  public static final String PASSWORD_REQUIRED = "비밀번호는 필수 입력값입니다.";
  public static final String PASSWORD_PATTERN = "비밀번호는 8~16자리수여야 합니다. 영문 대소문자, 숫자, 특수문자를 1개 이상 포함해야 합니다.";
  public static final String PASSWORD_FORMAT = "영문, 숫자, 특수문자를 포함한 8~16글자 이상";
  public static final String PASSWORD_PATTERN_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,16}$";

  // 전화번호 관련 메세지 및 정규식
  public static final String PHONE_NUMBER_PATTERN = "전화번호는 숫자만 사용하여 10~11자리 입니다.";
  public static final String PHONE_NUMBER_PATTERN_REGEX = "^[0-9]{10,11}$";

  // 성별 포맷 메세지
  public static final String GENDER_PATTERN = "'MALE','FEMALE'";


  // 생년월일 관련 메세지
  public static final String BIRTH_DATE_PAST = "생년월일은 현재보다 빠를 수 없습니다.";
  public static final String BIRTH_DATE_FORMAT = "yyyy-MM-dd";

  // 인증번호 관련 메세지
  public static final String CERTIFICATION_CODE_REQUIRED = "인증번호는 필수 입력값입니다";


}
