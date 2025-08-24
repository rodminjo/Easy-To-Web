# Easy to Web (BE)

> 동시편집 Web Builder의 **백엔드**만을 위한 요약 문서. 아키텍처, 기술스택, 기능, 품질 중심 정리.  

---

## 1) Architecture

- **Hexagonal Architecture (DDD 지향)**  
  도메인 로직을 순수 Java로 유지하고 어댑터로 인프라 의존성을 분리하여 유지보수성과 확장성 확보. Mock 치환이 용이해 테스트 친화적 구조 보장.  
- **동시편집 서버 설계 (CRDT, Server-merge)**  
  WebSocket + Yjs 기반 CRDT로 클라이언트 변경을 수집·병합하는 **서버 중심 동기화** 채택. Rust y-crdt를 **JNI로 Java에 연동**하여 문서 정합성과 일관성 보장.  
- **권한·동시성 제어**  
  편집 권한을 확인한 후에만 브로드캐스트하며, 문서 접근은 ReentrantLock으로 Thread-Safe 제어 보장.  
- **인증/인가**  
  자체 로그인(이메일 인증)과 Google OAuth 지원. **JWT(Access/Refresh)** 기반 세션 유지 및 토큰 재발급 흐름 제공.  
- **데이터베이스 설계 원칙**  
  외부 노출 ID는 **UUID**를 기본키로 사용, Audit 필드 표준화, N:M은 중간 테이블 관리.  

---

## 2) Tech Stack

- **Language/Runtime**: Java 21  
- **Framework**: Spring Boot 3.4.1, Spring Security 
- **Collab/Conflict-free Editing**: Yjs(y-crdt) + JNI(Custom Library)  
- **DB/Cache**: PostgreSQL, Redis  
- **Infra**: Docker, Nginx, Cloudflare Tunnel, Oracle Cloud (DB/모니터링)  
- **CI/CD**: Jenkins(Webhook 트리거, buildpack 이미지, docker-compose 재배포, Actuator 헬스체크 후 라우팅 반영)  
- **Observability**: Prometheus, Grafana, Loki, Alloy, Spring Actuator  

---

## 3) Features

- **실시간 동시편집**  
  Rust 기반 CRDT(y-crdt)를 JNI로 연동, 문서 정합성과 충돌 해결 보장.  
- **인증·인가**  
  이메일 인증 기반 자체 로그인, Google 소셜 로그인, JWT 토큰 재발급 로직 제공.  
- **프로젝트 관리**  
  프로젝트 생성, 편집, 게시 및 게시 이력 관리 기능.  
- **파일 업로드**  
  이미지 업로드 및 WebP 인코딩 처리, Strategy 패턴 기반 확장 구조.  
- **API 문서화**  
  Swagger + Custom Annotation으로 자동화된 API 문서 및 에러 코드 관리.  
- **모니터링·로깅**  
  Prometheus + Grafana + Loki + Alloy 기반 통합 모니터링과 Slack 알림 제공.  

---

## 4) Quality (Performance, Reliability, Observability)

- **성능 목표 및 측정(동시편집 시나리오)**  
  목표: Client 30, RPS 2/Client 환경.  
  결과: 평균 TPS 44.12, 평균 응답 0.24s, CPU 9.5%, 메모리 153MB 달성.  
- **일관성·정합성**  
  서버 중심 병합과 문서 타입 기반 동기화 프로토콜(encoder/decoder)을 Java로 구현하여 충돌 해결 보장.  
- **보안/안정성**  
  이메일 인증 + JWT 재발급 흐름, 비동기 환경에서의 인증정보 안전 활용(Static Map + Thread Context)으로 안정적 인증 제공.  
- **가시성/알림**  
  Actuator 메트릭 → Prometheus 수집 → Grafana 대시보드 시각화. Slack 알림으로 장애 감지/해제 통지.  

---
