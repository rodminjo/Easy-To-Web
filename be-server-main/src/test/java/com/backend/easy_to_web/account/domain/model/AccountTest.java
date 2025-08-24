package com.backend.easy_to_web.account.domain.model;

import com.backend.easy_to_web.test_support.model.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountTest extends UnitTest {

  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private final UUID id = UUID.randomUUID();
  private final String email = "test@example.com";
  private final String password = "originalPassword";
  private final String nickname = "TestUser";
  private final String profileUrl = "https://example.com/profile";
  private final Set<Role> roles = Set.of();

  @Test
  @DisplayName("비밀번호 변경 테스트")
  void changePassword_ShouldUpdatePassword() {
    // given
    String newPassword = "newSecurePassword";
    Account account = Account.builder()
        .id(id)
        .email(email)
        .password(password)
        .nickname(nickname)
        .profileUrl(profileUrl)
        .roles(roles)
        .build();

    // when
    account.changePassword(newPassword, passwordEncoder);

    // then
    assertNotEquals(password, account.getPassword(), "Password should be updated");
    assertTrue(passwordEncoder.matches(newPassword, account.getPassword()), "Encoded password should match the new password");
  }
}
