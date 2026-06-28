# restaurant-reservation-ai

Spring AI를 활용해 **레스토랑 예약 업무를 처리하는 AI 에이전트 백엔드**를 구현한 프로젝트입니다.  
사용자의 자연어 요청을 단순 응답으로 끝내지 않고, 실제 예약 조회, 예약 가능 테이블 검색, 예약 생성, 예약 취소까지 이어지도록 설계했습니다.

백엔드 개발자 관점에서 이 프로젝트의 핵심은 **LLM을 비즈니스 로직의 주체로 두지 않고**, `Spring AI Tool Calling`을 통해 **도메인 로직은 Spring 서비스 계층이 책임지고 AI는 대화 흐름과 의사결정만 담당하도록 분리한 구조**에 있습니다.

또한 외부 상용 API가 아닌 **Ollama 기반 로컬 LLM 환경**으로 개발하여, AI 기능을 백엔드 애플리케이션 안에서 직접 통합하고 검증하는 경험에 초점을 두었습니다.

## 프로젝트 소개

예약 업무는 단순 CRUD보다도 사용자 입력의 모호함을 어떻게 해석하고, 어떤 순서로 정보를 수집하며, 실서비스 규칙에 맞게 상태를 변경할지가 중요합니다.

이 프로젝트에서는 그 문제를 다음과 같이 풀었습니다.

- 사용자는 채팅으로 예약을 요청합니다.
- 백엔드는 `ChatClient`를 통해 LLM과 대화합니다.
- 시스템 프롬프트로 예약 업무 규칙을 정의합니다.
- LLM은 필요한 순간에만 Tool을 호출합니다.
- 실제 데이터 조회와 상태 변경은 Spring 서비스 계층이 수행합니다.
- 결과는 PostgreSQL에 반영되고, 대화 문맥은 Chat Memory에 저장됩니다.

즉, 이 프로젝트는 단순한 챗봇이 아니라 **AI를 프론트에 붙인 예약 서비스가 아니라, 예약 도메인을 안전하게 수행하도록 설계한 백엔드 중심 AI 서비스**라고 볼 수 있습니다.

## 내가 구현한 백엔드 포인트

### 1. HTTP 진입점은 단순하게, AI orchestration은 설정으로 분리

`/api/v1/chat` 엔드포인트는 사용자의 메시지와 대화 ID를 받아 `ChatClient`에 전달하는 역할만 합니다.

- 위치: `backend/ai/src/main/java/koboolean/ai/controller/AiController.java`
- `Conversation-Id` 헤더를 기준으로 대화를 구분합니다.
- 헤더가 없으면 UUID를 새로 발급해 대화를 시작합니다.

이렇게 Controller를 얇게 유지하고, AI 관련 조립은 별도 설정으로 분리해 웹 계층과 AI 계층의 책임이 섞이지 않도록 구성했습니다.

### 2. Spring AI 설정에서 프롬프트, 메모리, Tool을 조립

`AiConfig`에서 `ChatClient`를 구성합니다.

- 위치: `backend/ai/src/main/java/koboolean/ai/config/AiConfig.java`
- `prompt/system.st` 파일을 시스템 프롬프트로 등록합니다.
- 오늘 날짜를 주입해 상대 날짜 표현(예: 내일, 이번 주 금요일)을 해석할 수 있게 합니다.
- `GourmetBotTools`를 `defaultTools`로 등록합니다.
- `MessageChatMemoryAdvisor`를 통해 대화 문맥을 유지합니다.
- `SimpleLoggerAdvisor`로 요청/응답 로그를 남깁니다.

이 구조를 통해 프롬프트 처리, 메모리 연결, Tool 등록을 모두 한 곳에서 관리할 수 있게 했고, Controller나 Service에 AI 설정 코드가 퍼지지 않도록 했습니다.

### 3. Tool Calling으로 LLM과 예약 도메인을 연결

실제 예약 기능은 `GourmetBotTools`에 정의되어 있습니다.

- 위치: `backend/ai/src/main/java/koboolean/ai/tools/GourmetBotTools.java`
- `@Tool` 기반으로 기능을 노출합니다.
- 현재 제공하는 도구는 다음과 같습니다.

| Tool | 역할 |
| --- | --- |
| `checkCustomerHistory` | 연락처 기반 고객 식별 및 기존 방문 이력 조회 |
| `searchTables` | 날짜, 시간, 인원, 선호 좌석 타입 기준 예약 가능한 테이블 조회 |
| `bookTable` | 예약 생성 |
| `cancelReservation` | 예약 취소 |
| `checkMyBooking` | 고객의 예약 내역 조회 |

여기서 의도한 설계는 명확합니다.

- LLM은 예약 도메인 객체를 직접 조작하지 않습니다.
- LLM은 필요한 Tool을 선택합니다.
- 실제 조회, 검증, 저장, 상태 변경은 서비스 계층이 수행합니다.

이 방식으로 AI의 유연함은 가져가되, 데이터 정합성과 도메인 규칙은 기존 백엔드 방식으로 통제할 수 있게 했습니다.

### 4. 예약 도메인 로직은 Service 계층에 집중

핵심 비즈니스 로직은 `ReservationService`에 들어 있습니다.

- 위치: `backend/ai/src/main/java/koboolean/ai/service/ReservationService.java`

주요 처리 방식은 다음과 같습니다.

- 예약 가능 테이블 조회
  - 요청 시간 기준 앞뒤 2시간 범위를 잡아 이미 예약된 테이블을 제외합니다.
  - 인원 수와 좌석 타입 조건으로 후보 테이블을 먼저 조회한 뒤 필터링합니다.
- 예약 생성
  - 테이블 존재 여부를 검증합니다.
  - 연락처 기준으로 고객을 조회하고, 없으면 신규 고객을 생성합니다.
  - 알레르기 정보가 있으면 고객 메모에 반영합니다.
  - 예약 생성 후 상태를 `CONFIRMED`로 저장합니다.
  - 고객 방문 횟수를 증가시킵니다.
- 예약 취소
  - 예약 ID 기준으로 조회 후 상태를 `CANCELLED`로 변경합니다.
- 예약 조회
  - 현재 시점 이후의 `CONFIRMED` 예약만 조회합니다.

AI가 직접 예약을 확정하는 것이 아니라, **서비스 계층이 예약 가능 여부와 상태 변경을 책임지도록 설계한 점**이 이 프로젝트의 백엔드 핵심입니다.

### 5. JPA 기반 도메인 모델

주요 엔티티는 아래와 같습니다.

| 엔티티 | 설명 |
| --- | --- |
| `Customer` | 예약자 정보, 연락처, 방문 횟수, 메모 관리 |
| `Reservation` | 예약 시간, 인원 수, 알레르기 정보, 예약 상태 관리 |
| `RestaurantTable` | 테이블 수용 인원과 좌석 타입 관리 |

관련 파일 위치:

- `backend/ai/src/main/java/koboolean/ai/entity/Customer.java`
- `backend/ai/src/main/java/koboolean/ai/entity/Reservation.java`
- `backend/ai/src/main/java/koboolean/ai/entity/RestaurantTable.java`

Repository는 Spring Data JPA로 구성했고, 예약 충돌 확인과 예정 예약 조회는 JPQL 쿼리로 처리했습니다. 단순 저장소 수준을 넘어서, 실제 예약 도메인에 필요한 조회 패턴을 반영한 구조입니다.

### 6. 시스템 프롬프트로 업무 규칙을 통제

프롬프트 파일은 다음 위치에 있습니다.

- `backend/ai/src/main/resources/prompt/system.st`

이 프롬프트에는 아래 규칙들이 들어 있습니다.

- 연락처 없이 바로 예약 진행하지 않기
- 동일 연락처로 고객 조회 Tool을 반복 호출하지 않기
- 시간이 모호하면 다시 질문하기
- 예약 확정 전 알레르기 정보 반드시 수집하기
- 예약 취소 전 재확인하기
- 한국어로 짧고 자연스럽게 응답하기

단순히 모델을 연결하는 수준이 아니라, **AI가 어떤 순서로 정보를 수집하고 어떤 시점에 Tool을 호출해야 하는지 업무 규칙을 프롬프트로 설계했다는 점**이 이 프로젝트의 중요한 구현 포인트입니다.

## 아키텍처 요약

```text
User Request
  -> AiController
  -> ChatClient
  -> System Prompt + Chat Memory + Tools
  -> GourmetBotTools
  -> ReservationService
  -> JPA Repository
  -> PostgreSQL
```

## 사용 기술

| 구분 | 기술 |
| --- | --- |
| Language | Java |
| Framework | Spring Boot 4.0.3 |
| AI Framework | Spring AI 2.0.0 |
| AI Model Runtime | Ollama |
| LLM Model | `gemma4:31b-cloud` |
| Web | Spring Web MVC |
| ORM | Spring Data JPA |
| Database | PostgreSQL |
| Chat Memory | JDBC Chat Memory Repository |
| Build Tool | Gradle Multi Module |
| Frontend | React + Vite |
| Dev Environment | Docker Compose, DevTools |

## Ollama 기반 개발

이 프로젝트는 외부 상용 API 호출 중심이 아니라, **Ollama를 이용한 로컬 LLM 개발 환경**을 기준으로 구성했습니다.

- 설정 위치: `backend/ai/src/main/resources/application-local.yml`
- Ollama Base URL: `http://localhost:11434`
- 모델 설정: `gemma4:31b-cloud`
- `pull-model-strategy: when_missing` 설정으로 모델이 없으면 자동으로 가져오도록 구성했습니다.

백엔드 개발 관점에서 이 방식의 장점은 다음과 같습니다.

- 애플리케이션 로컬 환경에서 바로 AI 기능을 검증할 수 있습니다.
- 프롬프트 수정, Tool 동작, 메모리 저장 흐름을 빠르게 반복 테스트할 수 있습니다.
- 외부 API 의존도를 줄인 상태에서 Spring AI 통합 구조를 실험할 수 있습니다.

개발 흐름은 아래와 같습니다.

1. Ollama를 로컬에서 실행합니다.
2. PostgreSQL을 Docker로 띄웁니다.
3. Spring Boot 애플리케이션을 실행합니다.
4. 프론트 또는 API 호출로 채팅을 보내면, Spring AI가 Ollama 모델과 연동해 Tool Calling을 수행합니다.

## 기술적으로 집중한 부분

- AI 응답 생성보다 **도메인 실행의 안전한 연결 구조**를 만드는 데 집중했습니다.
- LLM이 임의로 비즈니스 로직을 수행하지 않도록 Tool 경계를 분명히 했습니다.
- Chat Memory를 통해 멀티턴 대화가 이어져도 사용자 정보를 반복 수집하지 않도록 구성했습니다.
- 프롬프트와 서비스 로직을 분리해, 대화 규칙과 도메인 규칙을 각각 관리할 수 있게 했습니다.
- 예약 도메인에서 실제 필요한 조회 조건과 상태 변경 흐름을 JPA 기반으로 구현했습니다.

## 로컬 실행

### 1. DB 실행

```bash
docker compose -f restaurant-db/docker-compose.yml up -d
```

PostgreSQL은 기본적으로 `localhost:54323`에서 실행됩니다.

### 2. Ollama 실행

로컬에 Ollama가 실행 중이어야 하며, `application-local.yml`에 지정된 모델을 사용할 수 있어야 합니다.

예시:

```bash
ollama serve
ollama pull gemma4:31b-cloud
```

### 3. 백엔드 실행

```bash
./gradlew :ai:bootRun
```

실행 위치는 `backend` 디렉터리입니다.

백엔드는 `8090` 포트를 사용합니다.

### 4. API 호출

엔드포인트:

```text
POST /api/v1/chat
```

예시 요청:

```http
POST /api/v1/chat
Conversation-Id: test-conversation-1
Content-Type: application/json

{
  "message": "내일 저녁 7시에 2명 예약하고 싶어요"
}
```

## 프로젝트 구조

```text
.
├─ backend
│  ├─ ai
│  │  ├─ controller      # 채팅 API 진입점
│  │  ├─ config          # ChatClient, Memory, 초기 데이터 설정
│  │  ├─ tools           # Spring AI Tool Calling 정의
│  │  ├─ service         # 예약 도메인 로직
│  │  ├─ repository      # JPA Repository
│  │  ├─ entity          # Customer, Reservation, RestaurantTable
│  │  ├─ dto             # Tool 입출력 DTO
│  │  └─ resources
│  │     ├─ application.yml
│  │     ├─ application-local.yml
│  │     └─ prompt/system.st
├─ gourmetbot-frontend   # React 기반 채팅 UI
└─ restaurant-db         # PostgreSQL Docker Compose
```

## 구현 포인트 정리

- Spring AI `ChatClient`를 중심으로 예약형 AI 백엔드를 구성했습니다.
- Tool Calling을 통해 LLM과 도메인 로직의 책임을 분리했습니다.
- 서비스 계층에서 예약 정합성과 상태 변경을 관리하도록 설계했습니다.
- JDBC Chat Memory로 대화 문맥을 유지해 멀티턴 예약 흐름을 지원했습니다.
- 시스템 프롬프트로 예약 업무 규칙을 통제해 대화 흐름을 안정화했습니다.
- Ollama 기반 로컬 개발 환경에서 AI 통합 백엔드를 직접 검증할 수 있도록 구성했습니다.
