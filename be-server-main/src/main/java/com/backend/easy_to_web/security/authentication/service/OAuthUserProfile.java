package com.backend.easy_to_web.security.authentication.service;

import com.backend.easy_to_web.account.domain.model.SNSProvider;
import com.backend.easy_to_web.account.domain.usecase.LoginOrRegisterAccountUseCase;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthUserProfile {

  private final String nameAttributeKey; // 고유 인증 키
  private final String name;
  private final String email;
  private final String picture;
  private final SNSProvider provider;
  private final Map<String, Object> attributes;


  public static OAuthUserProfile of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
    SNSProvider provider = SNSProvider.valueOf(registrationId.toUpperCase());
    return switch (provider) {
      case GOOGLE -> fromGoogle(userNameAttributeName, provider, attributes);
      case NAVER -> fromNaver("id", provider, attributes);
      case KAKAO -> fromKakao(userNameAttributeName, provider, attributes);
    };
  }

  public LoginOrRegisterAccountUseCase toCommand(){
    return LoginOrRegisterAccountUseCase.builder()
        .nameAttribute(attributes.get(nameAttributeKey).toString())
        .name(name)
        .email(email)
        .picture(picture)
        .provider(provider)
        .build();
  }

  private static OAuthUserProfile fromGoogle(String nameAttributeKey, SNSProvider provider, Map<String, Object> attributes) {
    return commonBuilder(nameAttributeKey, provider, attributes,
        (String) attributes.get("name"),
        (String) attributes.get("email"),
        (String) attributes.get("picture"));
  }

  private static OAuthUserProfile fromNaver(String nameAttributeKey, SNSProvider provider, Map<String, Object> attributes) {
    Map<String, Object> response = cast(attributes.get("response"));
    return commonBuilder(nameAttributeKey, provider, response,
        (String) response.get("name"),
        (String) response.get("email"),
        (String) response.get("profile_image"));
  }

  private static OAuthUserProfile fromKakao(String nameAttributeKey, SNSProvider provider, Map<String, Object> attributes) {
    return commonBuilder(nameAttributeKey, provider, attributes,
        (String) attributes.get("name"),
        (String) attributes.get("email"),
        (String) attributes.get("picture"));
  }

  private static OAuthUserProfile commonBuilder(String nameAttributeKey, SNSProvider provider, Map<String, Object> attributes,
      String name, String email, String picture) {
    return OAuthUserProfile.builder()
        .nameAttributeKey(nameAttributeKey)
        .name(name)
        .email(email)
        .picture(picture)
        .provider(provider)
        .attributes(attributes)
        .build();
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> cast(Object obj) {
    return (Map<String, Object>) obj;
  }
}
