package com.backend.easy_to_web.project.infrastructure.external;

import com.backend.easy_to_web.common.application.port.LinkGenerator;
import com.backend.easy_to_web.config.properties.ProjectInviteProperties;
import com.backend.easy_to_web.config.properties.ServerProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProjectInvitationLinkGenerator implements LinkGenerator {

  private final ServerProperties serverProperties;
  private final ProjectInviteProperties projectInviteProperties;

  @Override
  public String generate(List<String> variable) {
    String baseUrl = serverProperties.getFeDomain() + projectInviteProperties.getMailPath();
    return String.format(baseUrl, variable.toArray());
  }
}
