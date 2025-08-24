package com.backend.easy_to_web.socket.crdt;

import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.project.domain.model.Room;
import com.backend.easy_to_web.socket.crdt.port.CRDTSyncProvider;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
@Component
public class CRDTSyncSocketHandler extends BinaryWebSocketHandler {

  private static final String PARAMETER_ROOM_NAME = "roomName";

  private final CRDTSyncProvider crdtSyncProvider;

  // 방(room) 별로 WebSocket 세션 관리
  private static final Map<String, Room> rooms = new ConcurrentHashMap<>();


  @Override
  public void afterConnectionEstablished(WebSocketSession session){
    String projectId = getQueryParamFromSession(session);
    ProjectPermission permission = crdtSyncProvider.hasAccessToProject(SecurityScopeUtils.getAccountId().toString(), projectId);
    init(session, projectId, permission);
    log.info("New connection in room: {}", projectId);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
    String roomName = getQueryParamFromSession(session);
    exit(session, roomName);
    log.info("Connection closed in room: {}", roomName);
  }

  @Override
  protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
    String roomName = getQueryParamFromSession(session);
    Room room = rooms.computeIfAbsent(roomName, k -> new Room(roomName));
    UUID accountId = SecurityScopeUtils.getAccountId();
    room.handle(session, message, accountId);
  }


  private String getQueryParamFromSession(WebSocketSession session) {
    if (session == null || session.getUri() == null) {
      return null;
    }

    String query = session.getUri().getQuery();
    if (query == null || query.isBlank()) {
      return null;
    }

    for (String param : query.split("&")) {
      String[] keyValue = param.split("=", 2);
      if (keyValue.length == 2 && PARAMETER_ROOM_NAME.equals(keyValue[0]) && !keyValue[1].isBlank()) {
        return keyValue[1];
      }
    }

    return null;
  }

  private void init(WebSocketSession session, String roomName, ProjectPermission permission){
    Room savedRoom = rooms.computeIfAbsent(roomName, k -> {
      Room room = new Room(roomName);
      byte[] backup = crdtSyncProvider.getRecentHistory(roomName);
      room.updateFromHistory(backup);
      return room;
    });

    savedRoom.addSession(session, permission);
  }


  private void exit(WebSocketSession session, String roomName){
    Room room = rooms.get(roomName);
    if (!ObjectUtils.isEmpty(room)){
      room.removeSession(session);
    }
  }


  @Scheduled(fixedRate = 60 * 1000)
  private void historyScheduler() {
    log.debug("schedule running");
    Iterator<Entry<String, Room>> iterator = rooms.entrySet().iterator();

    while (iterator.hasNext()) {
      Map.Entry<String, Room> entry = iterator.next();
      String roomName = entry.getKey();
      Room room = entry.getValue();

      if (room.isExpired(10)) {
        iterator.remove();
        room.destroy(roomName, crdtSyncProvider);
        log.info("Room destroyed: {}", roomName);

      } else {
        room.saveHistory(roomName, crdtSyncProvider);

      }
    }
  }
}