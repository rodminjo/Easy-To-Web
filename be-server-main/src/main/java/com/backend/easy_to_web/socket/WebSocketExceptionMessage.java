package com.backend.easy_to_web.socket;

import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.web.socket.CloseStatus;

@Getter
public enum WebSocketExceptionMessage {

	RESOURCE_NOT_FOUND(new CloseStatus(1002, ExceptionMessage.RESOURCE_NOT_FOUND.getDescription()), ExceptionMessage.RESOURCE_NOT_FOUND), // 1002: No closure code

	INPUT_VALUE_INVALID(new CloseStatus(1003, ExceptionMessage.INPUT_VALUE_INVALID.getDescription()), ExceptionMessage.INPUT_VALUE_INVALID), // 1003: Invalid data format

	PROJECT_ACCESS_DENIED(new CloseStatus(1008, ExceptionMessage.PROJECT_ACCESS_DENIED.getDescription()), ExceptionMessage.PROJECT_ACCESS_DENIED), // 1008: Policy violation
	PROJECT_NOT_FOUND(new CloseStatus(1008, ExceptionMessage.PROJECT_NOT_FOUND.getDescription()), ExceptionMessage.PROJECT_NOT_FOUND), // 1008: Policy violation
	USER_NOT_LOGIN(new CloseStatus(1008, ExceptionMessage.USER_NOT_LOGIN.getDescription()), ExceptionMessage.USER_NOT_LOGIN), // 1008: Policy violation (Authentication failure)
	ACCESS_DENIED(new CloseStatus(1008, ExceptionMessage.ACCESS_DENIED.getDescription()), ExceptionMessage.ACCESS_DENIED), // 1008: Policy violation (Authorization failure)

	UNEXPECTED_INTERNAL_SERVER_ERROR(new CloseStatus(1011, ExceptionMessage.UNEXPECTED_INTERNAL_SERVER_ERROR.getDescription()), ExceptionMessage.UNEXPECTED_INTERNAL_SERVER_ERROR), // 1011: Server error

	ACCESS_TOKEN_EXPIRED(new CloseStatus(4401, ExceptionMessage.ACCESS_TOKEN_EXPIRED.getDescription()), ExceptionMessage.ACCESS_TOKEN_EXPIRED); // Custom code (4401)

	private final CloseStatus closeStatus;
	private final ExceptionMessage exceptionMessage;

	WebSocketExceptionMessage(CloseStatus closeStatus, ExceptionMessage exceptionMessage) {
		this.closeStatus = closeStatus;
		this.exceptionMessage = exceptionMessage;
	}

	private static final Map<ExceptionMessage, WebSocketExceptionMessage> EXCEPTION_MAP =
		Arrays.stream(values()).collect(Collectors.toMap(WebSocketExceptionMessage::getExceptionMessage, Function.identity()));

	public static WebSocketExceptionMessage fromExceptionMessage(ExceptionMessage exceptionMessage) {
		return EXCEPTION_MAP.getOrDefault(exceptionMessage, WebSocketExceptionMessage.UNEXPECTED_INTERNAL_SERVER_ERROR);
	}
}
