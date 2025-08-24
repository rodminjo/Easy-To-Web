package com.backend.easy_to_web.Integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.backend.easy_to_web.account.application.handler.JoinMailSendHandler;
import com.backend.easy_to_web.account.application.handler.SendPasswordChangeMailHandler;
import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.model.MailCertification;
import com.backend.easy_to_web.account.domain.port.out.AccountRepository;
import com.backend.easy_to_web.account.infrastructure.controller.rest.AccountJoinRest.AccountJoinInput;
import com.backend.easy_to_web.account.infrastructure.controller.rest.AccountJoinRest.AccountJoinOutput;
import com.backend.easy_to_web.account.infrastructure.controller.rest.AccountPasswordChangeRest.AccountPasswordChangeInput;
import com.backend.easy_to_web.account.infrastructure.controller.rest.JoinMailSendRest.JoinMailSendOutput;
import com.backend.easy_to_web.account.infrastructure.controller.rest.MailCertificationRest.MailCertificationInput;
import com.backend.easy_to_web.account.infrastructure.controller.rest.MailCertificationRest.MailCertificationOutput;
import com.backend.easy_to_web.account.infrastructure.controller.rest.PasswordMailSendRest.PasswordMailSendOutput;
import com.backend.easy_to_web.account.infrastructure.external.JoinCertificationEmailSender;
import com.backend.easy_to_web.account.infrastructure.external.PasswordCertificationEmailSender;
import com.backend.easy_to_web.common.application.port.RedisRepository;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.Response;
import com.backend.easy_to_web.test_support.config.db.DatabaseClearExtension;
import com.backend.easy_to_web.test_support.model.IntegrationTest;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

@Slf4j
@ExtendWith({DatabaseClearExtension.class})
@Sql(value = "classpath:sql/AccountRestControllerIntegrationTest.sql")
public class AccountRestControllerIntegrationTest extends IntegrationTest {

	@RegisterExtension
	static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
		.withConfiguration(GreenMailConfiguration.aConfig().withUser("greenMail", "1q2w3e!!"))
		.withPerMethodLifecycle(false);

	private WebTestClient webTestClient;

	@Autowired
	private RedisRepository redisRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AccountRepository accountRepository;

	@BeforeEach
	void setUp() {
		this.webTestClient = MockMvcWebTestClient.bindToApplicationContext(context).build();
	}


	@Test
	@DisplayName("회원가입 인증메일 전송 요청 - 값이 제대로 들어오지 않을 경우 validation 에러를 반환한다")
	void shouldReturnValidationErrorWhenInputIsNotValid() {
		// given
		ExceptionMessage expectedError = ExceptionMessage.INPUT_VALUE_INVALID;

		// when: API 호출
		sendGetRequest("/api/account/mail/join", "")

			// then: 응답 검증
			.expectStatus().isEqualTo(expectedError.getHttpStatus()) // 400 응답 코드 확인
			.expectBody(new ParameterizedTypeReference<Response<JoinMailSendOutput>>() {
			}) // 응답 본문 타입 지정
			.consumeWith(response -> {
				inputErrorDataValidate(response, "email");
			});
	}

	@Test
	@DisplayName("회원가입 인증메일 전송 요청 - 이메일이 이미 가입되어 있다면 에러를 반환한다")
	void shouldReturnErrorWhenEmailIsAlreadyRegistered() {
		// given
		ExceptionMessage expectedError = ExceptionMessage.EMAIL_ALREADY_EXISTS;
		// when: API 호출
		sendGetRequest("/api/account/mail/join", "example1@gmail.com")

			// then: 응답 검증
			.expectStatus().isEqualTo(expectedError.getHttpStatus()) // 400 응답 코드 확인
			.expectBody(new ParameterizedTypeReference<Response<JoinMailSendOutput>>() {
			}) // 응답 본문 타입 지정
			.consumeWith(response -> {
				errorDataValidate(response, expectedError);
			});
	}

  @Test
  @DisplayName("회원가입 인증메일 전송 요청 - 문제가 없다면 이메일을 전송한다")
  void shouldSendJoinCertificationMail() throws MessagingException {
    // given
    String email = "example_new@gmail.com";
    // when & then
    sendGetRequest("/api/account/mail/join", email)

        // then: 응답 검증
        .expectStatus().isEqualTo(HttpStatus.OK)
        .expectBody(new ParameterizedTypeReference<Response<JoinMailSendOutput>>() {
        })
        .consumeWith(response -> {
          successDataValidate(response);
          assertThat(Objects.requireNonNull(response.getResponseBody()).getData().type())
              .isEqualTo(JoinMailSendHandler.EMAIL_PREFIX);
        });

    // 메일 찾아오기
    MimeMessage receivedMail = findReceivedMail(JoinCertificationEmailSender.EMAIL_SUBJECT);

    // 받는 사람 테스트
    String receiver = receivedMail.getRecipients(RecipientType.TO)[0].toString();
    assertThat(receiver.equals(email)).isTrue();
  }

	@Test
	@DisplayName("비밀번호 변경 인증메일 전송 요청 - 값이 제대로 들어오지 않을 경우 validation 에러를 반환한다")
	void shouldReturnValidationErrorWhenInputIsNotValidForPasswordChange() {
		// given
		String email = "";
		ExceptionMessage expectedError = ExceptionMessage.INPUT_VALUE_INVALID;

		// when: API 호출
		sendGetRequest("/api/account/mail/password", email)

			// then: 응답 검증
			.expectStatus().isEqualTo(expectedError.getHttpStatus()) // 400 응답 코드 확인
			.expectBody(new ParameterizedTypeReference<Response<PasswordMailSendOutput>>() {
			}) // 응답 본문 타입 지정
			.consumeWith(response -> {
				inputErrorDataValidate(response, "email");
			});
	}

	@Test
	@DisplayName("비밀번호 변경 인증메일 전송 요청 - 가입된 계정이 없다면 에러를 반환한다.")
	void shouldReturnErrorWhenAccountIsNotFoundForPasswordChange() {
		// given
		String email = "example_new@gmail.com";
		ExceptionMessage expectedError = ExceptionMessage.EMAIL_NOT_FOUND;
		// when: API 호출
		sendGetRequest("/api/account/mail/password", email)

			// then: 응답 검증
			.expectStatus().isEqualTo(expectedError.getHttpStatus()) // 400 응답 코드 확인
			.expectBody(new ParameterizedTypeReference<Response<PasswordMailSendOutput>>() {
			}) // 응답 본문 타입 지정
			.consumeWith(response -> {
				errorDataValidate(response, expectedError);
			});
	}

  @Test
  @DisplayName("비밀번호 변경 인증메일 전송 요청 - 문제가 없다면 이메일을 전송한다")
  void shouldSendPasswordChangeCertificationMail() throws MessagingException {
    // given
    String email = "example1@gmail.com";

    // when
    sendGetRequest("/api/account/mail/password", email)

        // then: 응답 검증
        .expectStatus().isEqualTo(HttpStatus.OK)
        .expectBody(new ParameterizedTypeReference<Response<PasswordMailSendOutput>>() {
        })
        .consumeWith(response -> {
          successDataValidate(response);
          assertThat(Objects.requireNonNull(response.getResponseBody()).getData().type())
              .isEqualTo(SendPasswordChangeMailHandler.EMAIL_PREFIX);
        });

    // 받는 사람 테스트
    MimeMessage receivedMail = findReceivedMail(PasswordCertificationEmailSender.EMAIL_SUBJECT);
    assertThat(receivedMail.getRecipients(RecipientType.TO)[0].toString().equals(email)).isTrue();
  }

	@Test
	@DisplayName("메일 인증 확인 - 값이 유효하지 않다면 에러를 반환한다")
	void shouldReturnValidationErrorWhenInputIsNotValidForMailCertification() {
		// given
		MailCertificationInput input = new MailCertificationInput("", "email", "");
		ExceptionMessage expectedError = ExceptionMessage.INPUT_VALUE_INVALID;

		// when: API 호출
		sendPostRequest("/api/account/mail/certification", input)
			// then: 응답 검증
			.expectStatus().isEqualTo(expectedError.getHttpStatus()) // 400 응답 코드 확인
			.expectBody(new ParameterizedTypeReference<Response<MailCertificationOutput>>() {
			}) // 응답 본문 타입 지정
			.consumeWith(response -> {
				inputErrorDataValidate(response, "email", "type", "certificationCode");
			});
	}

	@Test
	@DisplayName("메일 인증 확인 - 인증정보가 없거나 다르다면 false 를 반환한다")
	void shouldReturnFalseWhenCertificationInfoIsNotFoundOrDifferent() {
		// given
		String type = "testType";
		String email = "example1@gmail.com";
		String certificationCode = "valid";

		MailCertificationInput input = new MailCertificationInput(type, email, certificationCode);

		// when: API 호출
		sendPostRequest("/api/account/mail/certification", input)

			// then: 응답 검증
			.expectStatus().isEqualTo(HttpStatus.OK)
			.expectBody(new ParameterizedTypeReference<Response<MailCertificationOutput>>() {
			})
			.consumeWith(response -> {
				successDataValidate(response);
				assertThat(Objects.requireNonNull(response.getResponseBody()).getData().isValid())
					.isFalse();
			});
	}

	@Test
	@DisplayName("메일 인증 확인 - 인증 정보가 일치하면 true 를 반환한다")
	void shouldReturnTrueWhenValidCertificationCode() {
		// given
		String type = "testType";
		String email = "example2@gmail.com";
		String certificationCode = "valid";
		MailCertification certification =
			MailCertification.createCertification(type, email, certificationCode, 3);
		saveInRedis(certification);

		MailCertificationInput input = new MailCertificationInput(type, email, certificationCode);

		// when: API 호출
		sendPostRequest("/api/account/mail/certification", input)
			// then: 응답 검증
			.expectStatus().isEqualTo(HttpStatus.OK)
			.expectBody(new ParameterizedTypeReference<Response<MailCertificationOutput>>() {
			})
			.consumeWith(response -> {
				successDataValidate(response);
				assertThat(Objects.requireNonNull(response.getResponseBody()).getData().isValid())
					.isTrue();
			});
	}

	@Test
	@DisplayName("회원 가입 - 값이 유효하지 않다면 에러를 반환한다")
	void shouldReturnValidationErrorWhenInputIsNotValidForJoin() {
		// given
		ExceptionMessage expectedError = ExceptionMessage.INPUT_VALUE_INVALID;
		String type = "";
		String email = "example_new";
		String certificationCode = "";
		String password = "invalid_password";

		AccountJoinInput input = new AccountJoinInput(type, email, certificationCode, password);

		// when: API 호출
		sendPostRequest("/api/account/join", input)

			// then: 응답 검증
			.expectStatus().isEqualTo(expectedError.getHttpStatus())
			.expectBody(new ParameterizedTypeReference<Response<AccountJoinOutput>>() {
			})
			.consumeWith(response -> {
				inputErrorDataValidate(response, "email", "type", "certificationCode", "password");
			});
	}

	@Test
	@DisplayName("회원 가입 - 인증정보가 일치하지 않다면 에러를 반환한다.")
	void shouldReturnErrorWhenCertificationIsInvalid() {
		ExceptionMessage expectedError = ExceptionMessage.CERTIFICATION_FAILED;
		// given
		String type = JoinMailSendHandler.EMAIL_PREFIX;
		String email = "example_new@gmail.com";
		String certificationCode = "validCode";
		String password = "password1234!";

		AccountJoinInput input = new AccountJoinInput(type, email, certificationCode, password);

		// when: API 호출
		sendPostRequest("/api/account/join", input)
			// then: 응답 검증
			.expectStatus().isEqualTo(expectedError.getHttpStatus())
			.expectBody(new ParameterizedTypeReference<Response<AccountJoinOutput>>() {
			})
			.consumeWith(response -> {
				errorDataValidate(response, expectedError);
			});
	}

	@Test
	@DisplayName("회원 가입 - 인증정보가 일치하면 가입된 이메일 정보를 반환한다.")
	void shouldReturnAccountWhenValidCertificationCode() {
		// given
		String type = JoinMailSendHandler.EMAIL_PREFIX;
		String email = "example_new@gmail.com";
		String certificationCode = "validCode";
		MailCertification certification =
			MailCertification.createCertification(type, email, certificationCode, 3);
		saveInRedis(certification);

		String password = "password1234!";
		AccountJoinInput input = new AccountJoinInput(type, email, certificationCode, password);

		// when: API 호출
		sendPostRequest("/api/account/join", input)

			// then: 응답 검증
			.expectStatus().isEqualTo(HttpStatus.CREATED)
			.expectBody(new ParameterizedTypeReference<Response<AccountJoinOutput>>() {
			})
			.consumeWith(response -> {
				successDataValidate(response);
				assertThat(Objects.requireNonNull(response.getResponseBody()).getData().email())
					.isEqualTo(email);
			});
	}

	@Test
	@DisplayName("비밀번호 재발급 - 값이 유효하지 않다면 에러를 반환한다")
	void shouldReturnValidationErrorWhenInputIsNotValidForPasswordReset() {
		// given
		ExceptionMessage expectedError = ExceptionMessage.INPUT_VALUE_INVALID;
		String type = "";
		String email = "example1";
		String certificationCode = "";
		String password = "password";
		AccountPasswordChangeInput input =
			new AccountPasswordChangeInput(type, email, certificationCode, password);

		// when: API 호출
		sendPostRequest("/api/account/password", input)
			// then: 응답 검증
			.expectStatus().isEqualTo(expectedError.getHttpStatus())
			.expectBody(new ParameterizedTypeReference<Response<Boolean>>() {
			})
			.consumeWith(response -> {
				inputErrorDataValidate(response, "type", "email", "certificationCode", "password");
			});
	}

	@Test
	@DisplayName("비밀번호 재발급 - 인증정보가 일치하지 않다면 에러를 반환한다.")
	void shouldReturnErrorWhenCertificationIsInvalidForPasswordReset() {
		// given
		ExceptionMessage expectedError = ExceptionMessage.CERTIFICATION_FAILED;
		String type = SendPasswordChangeMailHandler.EMAIL_PREFIX;
		String email = "example1@gamil.com";
		String certificationCode = "validCode";
		String password = "password1234!";
		AccountPasswordChangeInput input = new AccountPasswordChangeInput(type, email,
			certificationCode, password);

		// when: API 호출
		sendPostRequest("/api/account/password", input)
			// then: 응답 검증
			.expectStatus().isEqualTo(expectedError.getHttpStatus())
			.expectBody(new ParameterizedTypeReference<Response<Boolean>>() {
			})
			.consumeWith(response -> {
				errorDataValidate(response, expectedError);
			});
	}

	@Test
	@DisplayName("비밀번호 재발급 - 인증정보가 일치하면 true 를 반환한다.")
	void shouldReturnTrueWhenCertificationIsValid() {
// given
		String type = SendPasswordChangeMailHandler.EMAIL_PREFIX;
		String email = "example1@gmail.com";
		String certificationCode = "validCode";

		MailCertification certification =
			MailCertification.createCertification(type, email, certificationCode, 3);
		saveInRedis(certification);

		String password = "password1234!";
		AccountPasswordChangeInput input = new AccountPasswordChangeInput(type, email,
			certificationCode, password);

		// when: API 호출
		sendPostRequest("/api/account/password", input)
			// then: 응답 검증
			.expectStatus().isEqualTo(HttpStatus.OK)
			.expectBody(new ParameterizedTypeReference<Response<Boolean>>() {
			})
			.consumeWith(response -> {
				successDataValidate(response);
				assertThat(Objects.requireNonNull(response.getResponseBody()).getData())
					.isTrue();
			});

		Optional<Account> target = accountRepository.findByEmail(email);
		assertThat(target.isPresent()).isTrue();
		assertThat(passwordEncoder.matches(password, target.get().getPassword())).isTrue();
	}

	private void saveInRedis(MailCertification verification) {
		String key = verification.createKey();
		long milliSec = verification.calDurationMillis();
		redisRepository.setWithExpired(key, verification, milliSec);
	}

	private ResponseSpec sendGetRequest(String path, String email) {
		return webTestClient.get()
			.uri(uriBuilder -> uriBuilder
				.path(path)
				.queryParam("email", email)
				.build()
			)
			.exchange();
	}

	private ResponseSpec sendPostRequest(String path, Object input) {
		return webTestClient.post()
			.uri(uriBuilder -> uriBuilder
				.path(path)
				.build()
			)
			.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
			.bodyValue(input)
			.exchange(); // 요청 실행
	}

	private static <T> void successDataValidate(EntityExchangeResult<Response<T>> response) {
		Response<T> body = response.getResponseBody();
		assert body != null;
		assertThat(body).isNotNull();
		assertThat(body.getErrors()).isNull();
		assertThat(body.getData()).isNotNull();
	}

	private static <T> void inputErrorDataValidate(EntityExchangeResult<Response<T>> response,
		String... contains) {
		ExceptionMessage expectedError = ExceptionMessage.INPUT_VALUE_INVALID;
		Response<T> body = response.getResponseBody();
		assert body != null;
		assertThat(body).isNotNull();
		assertThat(body.getData()).isNull();
		assertThat(body.getErrors()).isNotNull();
		assertThat(body.getErrors().getErrorCode()).isEqualTo(expectedError.getErrorCode());
		assertThat(body.getErrors().getErrorDescription()).contains(contains);
	}

	private static <T> void errorDataValidate(EntityExchangeResult<Response<T>> response,
		ExceptionMessage expectedError) {
		Response<T> body = response.getResponseBody();
		assert body != null;
		assertThat(body).isNotNull();
		assertThat(body.getData()).isNull();
		assertThat(body.getErrors()).isNotNull();
		assertThat(body.getErrors().getErrorCode()).isEqualTo(expectedError.getErrorCode());
		assertThat(body.getErrors().getErrorDescription()).isEqualTo(
			expectedError.getDescription());
	}

	private MimeMessage findReceivedMail(String subject) {
		greenMail.waitForIncomingEmail(1);

		return Arrays.stream(greenMail.getReceivedMessages())
			.filter(msg -> {
				try {
					return msg.getSubject().equals(subject);
				} catch (MessagingException e) {
					throw new RuntimeException(e);
				}
			})
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("메일을 찾을 수 없습니다: " + subject));
	}

}
