package com.backend.easy_to_web.common.infrastructure.controller;

import com.backend.easy_to_web.common.domain.exception.BaseException;
import com.backend.easy_to_web.common.domain.exception.CustomAccessDeniedException;
import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.ErrorResponse;
import com.backend.easy_to_web.common.infrastructure.controller.rest.Response;
import com.backend.easy_to_web.utils.MapperUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice extends BaseController {

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Response<ErrorResponse>> AuthenticationExceptionHandler(AuthenticationException e){
    ExceptionMessage error = e instanceof CustomAuthenticationException ?
        ((CustomAuthenticationException) e).getExceptionMessage() :
        ExceptionMessage.UNEXPECTED_AUTHORIZED_ERROR;

    return exceptionRespondEntity(error);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Response<ErrorResponse>> AccessDeniedExceptionHandler(AccessDeniedException e){
    ExceptionMessage error = e instanceof CustomAccessDeniedException ?
        ((CustomAccessDeniedException) e).getExceptionMessage() :
        ExceptionMessage.UNEXPECTED_ACCESS_DENIED;

    return exceptionRespondEntity(error);
  }

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<Response<ErrorResponse>> BaseExceptionHandler(BaseException e){
    return exceptionRespondEntity(e.getExceptionMessage(), e.getMessage());
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Response<ErrorResponse>> noResourceFoundExceptionHandler(NoResourceFoundException e){
    return exceptionRespondEntity(ExceptionMessage.RESOURCE_NOT_FOUND);

  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Response<ErrorResponse>> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e)
      throws JsonProcessingException {
      Map<String, String> validationErrors = new HashMap<>();
      List<ObjectError> validationErrorList = e.getBindingResult().getAllErrors();

      validationErrorList.forEach((error) -> {
        String fieldName = ((FieldError) error).getField();
        String validationMsg = error.getDefaultMessage();
        validationErrors.put(fieldName, validationMsg);
      });

    String errorListStr = MapperUtils.getMapper().writeValueAsString(validationErrors);
    return exceptionRespondEntity(ExceptionMessage.INPUT_VALUE_INVALID, errorListStr);
  }

  @ExceptionHandler(PropertyReferenceException.class)
  public ResponseEntity<Response<ErrorResponse>> propertyReferenceExceptionHandler(PropertyReferenceException e){
    return exceptionRespondEntity(ExceptionMessage.PROPERTY_REFERENCE_INVALID);

  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Response<ErrorResponse>> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException e){
    return exceptionRespondEntity(ExceptionMessage.METHOD_ARGUMENT_MISMATCH);

  }

  @ExceptionHandler(MismatchedInputException.class)
  public ResponseEntity<Response<ErrorResponse>> mismatchedInputExceptionHandler(MismatchedInputException e){
    return exceptionRespondEntity(ExceptionMessage.METHOD_ARGUMENT_MISMATCH);

  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<Response<ErrorResponse>> exceptionHandler(Exception e){
    log.error("unexpected 예외 발생: {}, 예외 메시지: {}", e.getClass().getSimpleName(), e.getMessage(), e);
    return exceptionRespondEntity(ExceptionMessage.UNEXPECTED_INTERNAL_SERVER_ERROR);
  }
}
