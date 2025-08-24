package com.backend.easy_to_web.socket.crdt;

import com.backend.easy_to_web.common.domain.exception.BaseException;
import com.backend.easy_to_web.socket.WebSocketExceptionMessage;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.io.IOException;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

@Slf4j
public class CRDTSyncSocketErrorHandleDecorator extends WebSocketHandlerDecorator {

  public CRDTSyncSocketErrorHandleDecorator(WebSocketHandler delegate) {
    super(delegate);
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    injectAuthAndHandleWithExceptionHandling(() -> super.afterConnectionEstablished(session), session);
  }

  @Override
  public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    injectAuthAndHandleWithExceptionHandling(() -> super.handleMessage(session, message), session);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    injectAuthAndHandleWithExceptionHandling(() -> super.afterConnectionClosed(session, closeStatus), session);
  }

  /**
   * 공통 예외 처리 메서드
   */
  private void injectAuthAndHandleWithExceptionHandling(WebSocketAction action, WebSocketSession session) {
    try {
      Principal principal = session.getPrincipal();
      Authentication authentication = ((UsernamePasswordAuthenticationToken) principal);

      if (authentication != null) {
        SecurityScopeUtils.setAuthentication(authentication);
      }

      action.execute();
    } catch (BaseException e) {
      log.info("Expected Error Occurred during socket link : {}", e.getMessage());
      WebSocketExceptionMessage socketException = WebSocketExceptionMessage.fromExceptionMessage(e.getExceptionMessage());
      closeSession(session, socketException.getCloseStatus());

    } catch (IllegalArgumentException e) {
      log.info("Server Argument Error Occurred during socket link : {}", e.getMessage());
      closeSession(session, new CloseStatus(1007, e.getMessage()));

    } catch (Exception e) {
      log.error("Unexpected Error Occurred during socket link : {}", e.getMessage());
      closeSession(session, CloseStatus.SERVER_ERROR);

    } finally {
      SecurityScopeUtils.removeAuthentication();

    }
  }

  /**
   * WebSocket 세션 종료 메서드
   */
  private void closeSession(WebSocketSession session, CloseStatus status) {
    try {
      session.close(status);
    } catch (IOException e) {
      log.error("Failed to close WebSocket session: {}", e.getMessage());
    }
  }

  @FunctionalInterface
  private interface WebSocketAction {
    void execute() throws Exception;
  }
}
