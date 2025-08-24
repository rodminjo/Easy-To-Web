package com.backend.easy_to_web.account.infrastructure.controller.doc;

import com.backend.easy_to_web.account.infrastructure.controller.rest.AccountJoinRest.AccountJoinInput;
import com.backend.easy_to_web.account.infrastructure.controller.rest.AccountJoinRest.AccountJoinOutput;
import com.backend.easy_to_web.account.infrastructure.controller.rest.AccountPasswordChangeRest.AccountPasswordChangeInput;
import com.backend.easy_to_web.account.infrastructure.controller.rest.GetProfileRest.GetProfileOutput;
import com.backend.easy_to_web.account.infrastructure.controller.rest.JoinMailSendRest.JoinMailSendInput;
import com.backend.easy_to_web.account.infrastructure.controller.rest.JoinMailSendRest.JoinMailSendOutput;
import com.backend.easy_to_web.account.infrastructure.controller.rest.MailCertificationRest.MailCertificationInput;
import com.backend.easy_to_web.account.infrastructure.controller.rest.MailCertificationRest.MailCertificationOutput;
import com.backend.easy_to_web.account.infrastructure.controller.rest.PasswordMailSendRest.PasswordMailSendInput;
import com.backend.easy_to_web.account.infrastructure.controller.rest.PasswordMailSendRest.PasswordMailSendOutput;
import com.backend.easy_to_web.account.infrastructure.controller.rest.UpdateProfileRest.UpdateProfileInput;
import com.backend.easy_to_web.common.infrastructure.controller.doc.ApiExceptionExplanation;
import com.backend.easy_to_web.common.infrastructure.controller.doc.ApiResponseExplanations;
import com.backend.easy_to_web.common.infrastructure.controller.rest.Response;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Account API", description = "계정 API")
public interface AccountRestControllerDoc {

  @Operation(summary = "인증 이메일 전송", description = "인증 이메일 전송 API")
  @ApiResponse(responseCode = "200", description = "전송 성공", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.EMAIL_ALREADY_EXISTS),
      @ApiExceptionExplanation(value = ExceptionMessage.REDIS_FAILED),
      @ApiExceptionExplanation(value = ExceptionMessage.MAIL_WRITE_FAILED),
      @ApiExceptionExplanation(value = ExceptionMessage.MAIL_SEND_FAILED),
      @ApiExceptionExplanation(value = ExceptionMessage.MAIL_FILE_ERROR)
  })
  Response<JoinMailSendOutput> sendCertificationMailForJoin(JoinMailSendInput input);

  @Operation(summary = "비밀번호 변경 이메일 전송", description = "비밀번호 변경 이메일 전송 API")
  @ApiResponse(responseCode = "200", description = "전송 성공", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.EMAIL_NOT_FOUND),
      @ApiExceptionExplanation(value = ExceptionMessage.REDIS_FAILED),
      @ApiExceptionExplanation(value = ExceptionMessage.MAIL_WRITE_FAILED),
      @ApiExceptionExplanation(value = ExceptionMessage.MAIL_SEND_FAILED),
      @ApiExceptionExplanation(value = ExceptionMessage.MAIL_FILE_ERROR)
  })
  Response<PasswordMailSendOutput> sendCertificationMailForPassword(PasswordMailSendInput input);


  @Operation(summary = "이메일 인증번호 검증", description = "이메일 인증번호 검증 API")
  @ApiResponse(responseCode = "200", description = "검증 완료", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.REDIS_FAILED),
  })
  Response<MailCertificationOutput> verifyCertificationMail(MailCertificationInput input);


  @Operation(summary = "회원가입", description = "회원가입 API")
  @ApiResponse(responseCode = "201", description = "가입 성공", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.REDIS_FAILED),
      @ApiExceptionExplanation(value = ExceptionMessage.CERTIFICATION_FAILED),
      @ApiExceptionExplanation(value = ExceptionMessage.EMAIL_ALREADY_EXISTS),
  })
  Response<AccountJoinOutput> join(AccountJoinInput input);


  @Operation(summary = "비밀번호 변경", description = "비밀번호 변경 API")
  @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.INPUT_VALUE_INVALID),
      @ApiExceptionExplanation(value = ExceptionMessage.REDIS_FAILED),
      @ApiExceptionExplanation(value = ExceptionMessage.CERTIFICATION_FAILED),
      @ApiExceptionExplanation(value = ExceptionMessage.EMAIL_ALREADY_EXISTS),
  })
  Response<Boolean> passwordChange(AccountPasswordChangeInput input);


  @Operation(summary = "프로필 정보 가져오기", description = "프로필 정보 조회 API")
  @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
      @ApiExceptionExplanation(value = ExceptionMessage.LOGIN_INFO_INVALID),
  })
  Response<GetProfileOutput> profile();


  @Operation(summary = "프로필 정보 수정", description = "프로필 정보 수정 API")
  @ApiResponse(responseCode = "200", description = "변경 성공", useReturnTypeSchema = true)
  @ApiResponseExplanations(errors = {
      @ApiExceptionExplanation(value = ExceptionMessage.USER_NOT_LOGIN),
      @ApiExceptionExplanation(value = ExceptionMessage.LOGIN_INFO_INVALID),
  })
  Response<Boolean> updateProfile(UpdateProfileInput input);
}
