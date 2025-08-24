package com.backend.easy_to_web.socket.crdt;

import com.backend.easy_to_web.common.domain.exception.CustomIllegalStateException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.config.properties.ProjectSocketProperties;
import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import com.backend.easy_to_web.security.authentication.manager.JwtManager.JwtStatus;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Slf4j
@RequiredArgsConstructor
@Component
public class CRDTSyncSocketInterceptor implements HandshakeInterceptor {

	private final ProjectSocketProperties ProjectSocketProperties;
	private final JwtManager jwtManager;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
		WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		try {
			String accessToken = extractAccessToken(request, response);
			validToken(accessToken);

			// 토큰이 유효하면 SecurityContext에 저장
			Authentication authentication = jwtManager.getAuthentication(accessToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);

		} catch (CustomIllegalStateException e){
			sendWebSocketErrorResponse(response, e.getExceptionMessage());
			return false;

		} catch (Exception e){
			sendWebSocketErrorResponse(response, ExceptionMessage.UNEXPECTED_INTERNAL_SERVER_ERROR);
			return false;
		}

		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
		WebSocketHandler wsHandler, Exception exception) {
		if (exception != null) {
			log.error("WebSocket 핸드셰이크 실패: {}", exception.getMessage());
			sendWebSocketErrorResponse(response, ExceptionMessage.UNEXPECTED_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * WebSocket 에러 정보를 HTTP 헤더를 통해 클라이언트에 전달
	 */
	private void sendWebSocketErrorResponse(ServerHttpResponse response, ExceptionMessage errorMessage) {
		log.info("WebSocket 핸드셰이크 오류: {}", errorMessage.getDescription());
		response.setStatusCode(errorMessage.getHttpStatus());
	}


	/**
	 * Sec-WebSocket-Protocol에서 액세스 토큰 추출
	 */
	private String extractAccessToken(ServerHttpRequest request, ServerHttpResponse response) {
		List<String> protocols = request.getHeaders().get("Sec-WebSocket-Protocol");

		if (!ObjectUtils.isEmpty(protocols)) {
			for (String protocol : protocols) {
				if (protocol.startsWith(ProjectSocketProperties.getTokenPrefix())) {
					String token = protocol.replace(ProjectSocketProperties.getTokenPrefix() + "_", "");
					response.getHeaders().set("Sec-WebSocket-Protocol", protocol);
					return token;
				}
			}
		}

		throw new CustomIllegalStateException(ExceptionMessage.USER_NOT_LOGIN);
	}

	/**
	 * 토큰이 유효하지 않을 때 예외 처리
	 */
	private void validToken(String accessToken) {
		if (jwtManager.validateToken(accessToken)){
			return;
		}

		JwtStatus status = jwtManager.statusToken(accessToken);
		if (status.equals(JwtStatus.EXPIRED)) {
			throw new CustomIllegalStateException(ExceptionMessage.ACCESS_TOKEN_EXPIRED);
		} else {
			throw new CustomIllegalStateException(ExceptionMessage.USER_NOT_LOGIN);
		}
	}
}
