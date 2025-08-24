package com.backend.easy_to_web.project.domain.model;

import com.backend.easy_to_web.project.infrastructure.external.yjs.Decoder;
import com.backend.easy_to_web.project.infrastructure.external.yjs.Encoder;
import com.backend.easy_to_web.project.infrastructure.external.yjs.TransactionalYDoc;
import com.backend.easy_to_web.socket.crdt.port.CRDTSyncProvider;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

@Slf4j
public class Room {

	private static final int MESSAGE_SYNC = 0;
	private static final int MESSAGE_AWARENESS = 1;
	private static final int MESSAGE_AUTH = 2;
	private static final int MESSAGE_CAPTURE = 3;

	private static final int MESSAGE_YJS_SYNC_STEP_1 = 0;
	private static final int MESSAGE_YJS_SYNC_STEP_2 = 1;
	private static final int MESSAGE_YJS_UPDATE = 2;

	private boolean isCaptured = false;
	private final TransactionalYDoc yDoc;
	private final Set<UUID> editors = new CopyOnWriteArraySet<>();

	private final ExecutorService sendExecutor;

	// 세션 ID → (WebSocketSession, Lock)
	private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

	// 세션 ID → 권한
	private final Map<String, ProjectPermission> permissions = new ConcurrentHashMap<>();

	public Room(String roomName) {
		this.yDoc = new TransactionalYDoc();
		sendExecutor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual()
				.name("VirtualExecutorForSocket-" + roomName + "-", 0)
				.factory()
		);
	}

	public boolean isExpired(int minutes) {
		return this.sessions.isEmpty() && this.yDoc.isTimeExceeded(minutes);
	}

	public void addSession(WebSocketSession session, ProjectPermission permission) {
		String sessionId = session.getId();
		// 세션 래핑 for 멀티쓰레드 전송 (send bufferSize: 1024KB, send timeout: 10초)
		WebSocketSession safeSession = new ConcurrentWebSocketSessionDecorator(session, 10_000, 1024 * 1024);
		this.sessions.put(sessionId, safeSession);
		this.permissions.put(sessionId, permission);
	}

	public void removeSession(WebSocketSession session) {
		String sessionId = session.getId();
		this.sessions.remove(sessionId);
		this.permissions.remove(sessionId);
	}

	public void destroy(String projectId, CRDTSyncProvider provider) {
		saveHistory(projectId, provider);
		this.yDoc.destroy();
	}

	public void saveHistory(String projectId, CRDTSyncProvider provider) {
		if (!editors.isEmpty()) {
			byte[] backup = yDoc.encodeStateAsUpdate();
			provider.saveHistory(projectId, backup, new HashSet<>(editors));
			editors.clear();
		}
	}

	public boolean updateFromHistory(byte[] backup) {
		return this.yDoc.applyUpdate(backup);
	}

	public void handle(WebSocketSession session, BinaryMessage message, UUID accountId) throws Exception {
		Decoder decoder = new Decoder(message.getPayload().array());
		Encoder encoder = new Encoder();

		if (!checkAuth(session, message)) {
			return;
		}

		handleByMessageType(decoder, encoder, accountId);
		send(session, message);

		if (!encoder.isEmpty()) {
			sendAll(encoder.toBinaryMessage());
		}
	}

	public boolean checkAuth(WebSocketSession session, BinaryMessage message) {
		ProjectPermission permission = this.permissions.get(session.getId());
		Decoder decoder = new Decoder(message.getPayload().array());
		int messageType = decoder.readVarUint();
		int messageProcess = decoder.readVarUint();

		if (messageType == MESSAGE_SYNC && messageProcess > MESSAGE_YJS_SYNC_STEP_1) {
			return permission != null && permission.isValidPermission(ProjectPermission.EDIT);
		}
		return true;
	}

	private void sendMessageCapture() {
		if (this.sessions.size() == 1 && !isCaptured) {
			Encoder encoder = new Encoder();
			encoder.writeVarUint(MESSAGE_CAPTURE);
			sendAll(encoder.toBinaryMessage());
			isCaptured = true;
		}
	}

	// 본인을 제외한 모든 세션에 브로드캐스트
	private void send(WebSocketSession sender, BinaryMessage message){
		String senderId = sender.getId();
		for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
			String id = entry.getKey();
			WebSocketSession s = entry.getValue();

			if (!id.equals(senderId) && s.isOpen()) {
				publishMessageToSocket(message, s);
			}
		}
	}


	// 전체 세션에 브로드캐스트
	private void sendAll(BinaryMessage message) {
		for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
			WebSocketSession s = entry.getValue();
			if (s.isOpen()) {
				publishMessageToSocket(message, s);
			}
		}
	}

	private void publishMessageToSocket(BinaryMessage message, WebSocketSession s) {
		sendExecutor.submit(() -> {
			try {
				s.sendMessage(message);
			} catch (IOException e) {
				log.warn("sendAll error: {} / sessionId: {}", e.getMessage(), s.getId());
				try {
					s.close();
				} catch (IOException closeEx) {
					log.warn("session.close() failed: {}", closeEx.getMessage());
				}
			}
		});
	}


	private void handleByMessageType(Decoder decoder, Encoder encoder, UUID accountId) {
		log.debug("YjsWebSocketHandler.handleByMessageType - accountId: {}", accountId);

		switch (decoder.readVarUint()) {
			case MESSAGE_SYNC -> handleMessageSync(decoder, encoder, accountId);
//      case MESSAGE_SAVE -> handleMessageSave(decoder); break;
			default -> {}
		}
	}

	private void handleMessageSync(Decoder decoder, Encoder encoder, UUID accountId) {
		log.debug("YjsWebSocketHandler.handleMessageSync - accountId: {}", accountId);
		switch (decoder.readVarUint()) {
			case MESSAGE_YJS_SYNC_STEP_1 -> handleMessageYjsSyncStep1(decoder, encoder);
			case MESSAGE_YJS_SYNC_STEP_2 -> handleMessageYjsSyncStep2(decoder, accountId);
			case MESSAGE_YJS_UPDATE -> handleMessageYjsUpdate(decoder, accountId);
		}
	}

	private void handleMessageYjsUpdate(Decoder decoder, UUID accountId) {
		log.debug("YjsWebSocketHandler.handleMessageYjsUpdate - accountId: {}", accountId);
		handleMessageYjsSyncStep2(decoder, accountId);
	}

	private void handleMessageYjsSyncStep2(Decoder decoder, UUID accountId) {
		log.debug("YjsWebSocketHandler.handleMessageYjsSyncStep2 - accountId: {}", accountId);
		byte[] updateDiff = decoder.readVarUint8Array();
		this.yDoc.applyUpdate(updateDiff);
		this.editors.add(accountId);
		sendMessageCapture();
	}

	// 서버의 yDoc도 싱크를 맞추기
	private void handleMessageYjsSyncStep1(Decoder decoder, Encoder encoder) {
		log.debug("YjsWebSocketHandler.handleMessageYjsSyncStep1");
		byte[] diff = decoder.readVarUint8Array();
		byte[] update = this.yDoc.encodeStateAsUpdate(diff);
		encoder.writeVarUint(MESSAGE_SYNC);
		encoder.writeVarUint(MESSAGE_YJS_SYNC_STEP_2);
		encoder.writeVarUint8Array(update);
	}
}
