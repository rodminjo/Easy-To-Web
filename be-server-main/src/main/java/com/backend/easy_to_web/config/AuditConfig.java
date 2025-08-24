package com.backend.easy_to_web.config;

import com.backend.easy_to_web.utils.SecurityScopeUtils;
import com.backend.easy_to_web.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

@Configuration
@RequiredArgsConstructor
@EnableJpaAuditing
public class AuditConfig implements AuditorAware<String> {


  @Override
  public Optional<String> getCurrentAuditor() {
    Authentication authentication = SecurityScopeUtils.getAuthentication();
    if (authentication == null){
      return Optional.of("system");
    }

    String principal = String.valueOf(authentication.getPrincipal());

    if (principal.equals("anonymousUser")){
      WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
      principal = principal + ": " + details.getRemoteAddress();
    }

    return Optional.of(principal);
  }
}
