package com.backend.easy_to_web.socket.crdt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

/**
 * handler Exception 처리, Authentication 처리 로직
 */

@Slf4j
@Component
public class CRDTSyncSocketDecoratorFactory implements WebSocketHandlerDecoratorFactory {

	@Override
	public WebSocketHandler decorate(WebSocketHandler handler) {
		return new CRDTSyncSocketErrorHandleDecorator(handler);
	}
}
