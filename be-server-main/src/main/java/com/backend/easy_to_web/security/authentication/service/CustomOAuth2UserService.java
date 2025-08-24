package com.backend.easy_to_web.security.authentication.service;


import com.backend.easy_to_web.account.domain.result.LoginResult;
import com.backend.easy_to_web.common.application.handler.UseCasePublisher;
import java.util.List;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;
  private final UseCasePublisher useCasePublisher;

  public CustomOAuth2UserService(UseCasePublisher useCasePublisher) {
    this.delegate = new DefaultOAuth2UserService();
    this.useCasePublisher = useCasePublisher;
  }

  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = delegate.loadUser(userRequest);

    /* OAuth2 서비스 구분코드 (구글, 카카오, 네이버) */
    String registrationId = userRequest
        .getClientRegistration()
        .getRegistrationId();


    /* OAuth2 로그인 진행시 키가 되는 필드값(PK), 구글의 기본코드는 sub, 네이버는 이 메서드로 보이지 않아 직접 입력 필요 */
    String userNameAttributeName = userRequest.getClientRegistration()
        .getProviderDetails()
        .getUserInfoEndpoint()
        .getUserNameAttributeName();

    /* userProfile 객체 생성 */
    OAuthUserProfile userProfile = OAuthUserProfile
        .of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

    LoginResult result = useCasePublisher.publish(LoginResult.class, userProfile.toCommand());

    return new OAuthAccountContext(
        result,
        result.getRoles(),
        userProfile.getAttributes(),
        userProfile.getNameAttributeKey()
    );

  }
}
