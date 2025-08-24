package com.backend.easy_to_web.utils;

import com.backend.easy_to_web.common.domain.exception.CustomIllegalStateException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityScopeUtils {

  // requestContextHolder.getRequestAttribute를 이용해 현재 요청 정보 받아옴
  public static Authentication getAuthentication() {
    try{
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null){throw new NullPointerException("anotherThread");}
      return authentication;

    }catch(Exception ex){
      return AsyncScopeUtils.getAuthentication();

    }
  }

  public static void setAuthentication(Authentication authentication) {
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  public static void removeAuthentication() {
    SecurityContextHolder.clearContext();
  }

  public static UUID getAccountId(){
    try {
      Authentication authentication = getAuthentication();
      String accountId = authentication.getPrincipal().toString();
      return UUID.fromString(accountId);

    } catch (Exception e){
      throw new CustomIllegalStateException(ExceptionMessage.USER_NOT_LOGIN);

    }
  }
}
