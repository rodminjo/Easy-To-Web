package com.backend.easy_to_web.account.infrastructure.controller;

import com.backend.easy_to_web.account.domain.result.GetProfileResult;
import com.backend.easy_to_web.account.domain.result.JoinAccountResult;
import com.backend.easy_to_web.account.domain.result.SendJoinMailResult;
import com.backend.easy_to_web.account.domain.result.SendPasswordChangeMailResult;
import com.backend.easy_to_web.account.domain.result.VerifyMailCertificationResult;
import com.backend.easy_to_web.account.domain.usecase.JoinAccountUseCase;
import com.backend.easy_to_web.account.domain.usecase.PasswordChangeUseCase;
import com.backend.easy_to_web.account.domain.usecase.SendJoinMailUseCase;
import com.backend.easy_to_web.account.domain.usecase.SendPasswordChangeMailUseCase;
import com.backend.easy_to_web.account.domain.usecase.VerifyMailCertificationUseCase;
import com.backend.easy_to_web.account.infrastructure.controller.doc.AccountRestControllerDoc;
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
import com.backend.easy_to_web.common.application.handler.UseCasePublisher;
import com.backend.easy_to_web.common.infrastructure.controller.BaseController;
import com.backend.easy_to_web.common.infrastructure.controller.rest.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account")
public class AccountRestController extends BaseController implements AccountRestControllerDoc {

  private final UseCasePublisher publisher;

  @Override
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/mail/join")
  public Response<JoinMailSendOutput> sendCertificationMailForJoin(@Valid JoinMailSendInput input) {
    SendJoinMailUseCase command = input.toCommand();
    SendJoinMailResult result = publisher.publish(SendJoinMailResult.class, command);
    return respond(new JoinMailSendOutput(result.getType()));
  }

  @Override
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/mail/password")
  public Response<PasswordMailSendOutput> sendCertificationMailForPassword(
      @Valid PasswordMailSendInput input) {
    SendPasswordChangeMailUseCase command = input.toCommand();
    SendPasswordChangeMailResult result = publisher.publish(SendPasswordChangeMailResult.class, command);
    return respond(new PasswordMailSendOutput(result.getType()));
  }

  @Override
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/mail/certification")
  public Response<MailCertificationOutput> verifyCertificationMail(@Valid @RequestBody MailCertificationInput input) {
    VerifyMailCertificationUseCase command = input.toCommand();
    VerifyMailCertificationResult result = publisher.publish(VerifyMailCertificationResult.class, command);
    return respond(new MailCertificationOutput(result.isValid()));
  }


  @Override
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/join")
  public Response<AccountJoinOutput> join(@Valid @RequestBody AccountJoinInput input) {
    JoinAccountUseCase command = input.toCommand();
    JoinAccountResult result = publisher.publish(JoinAccountResult.class, command);
    return respond(new AccountJoinOutput(result.getEmail()));
  }


  @Override
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/password")
  public Response<Boolean> passwordChange(@Valid @RequestBody AccountPasswordChangeInput input) {
    PasswordChangeUseCase command = input.toCommand();
    publisher.publish(command);
    return respond(true);
  }

  @Override
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/me")
  public Response<GetProfileOutput> profile() {
    GetProfileResult result = publisher.publish(GetProfileResult.class);
    return respond(new GetProfileOutput(result.getId(), result.getEmail(), result.getNickname(), result.getProfileUrl()));
  }

  @Override
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/me")
  public Response<Boolean> updateProfile(@Valid @RequestBody UpdateProfileInput input) {
    publisher.publish(input.toCommand());
    return respond(true);
  }
}
