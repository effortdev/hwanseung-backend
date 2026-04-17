# 🛒 환승마켓 (HwanSeung Market) — Backend

> 상품 거래와 실시간 소통 흐름을 결합한 중고거래 플랫폼 백엔드

<br>

## 📌 프로젝트 소개

환승마켓은 사용자 간 거래를 지원하는 중고거래 플랫폼입니다.  
실시간 채팅, 인증, 결제 등 다양한 기능을 포함하고 있으며,  
상품 도메인(등록, 거래 상태 관리, 이미지, 신고)을 담당하여 설계 및 구현했습니다.

<br>

## 🔗 관련 링크

| 구분 | 링크 |
|---|---|
| 배포 서버 | [https://hsmarket.duckdns.org](https://hsmarket.duckdns.org) |
| 프론트엔드 저장소 | [Frontend Repository](https://github.com/effortdev/hwanseung-frontend) |

<br>

## 🛠 기술 스택

### Backend
![Java](https://img.shields.io/badge/Java_17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.2-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![Spring Batch](https://img.shields.io/badge/Spring_Batch-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat-square&logo=spring&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket_STOMP-010101?style=flat-square)

### Database & Cache
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)

### 인증 & 외부 연동
![JWT](https://img.shields.io/badge/JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white)
![Google OAuth2](https://img.shields.io/badge/Google_OAuth2-4285F4?style=flat-square&logo=google&logoColor=white)
![아임포트](https://img.shields.io/badge/Iamport-FF6B00?style=flat-square)
![CoolSMS](https://img.shields.io/badge/CoolSMS-00AAFF?style=flat-square)
![Gmail SMTP](https://img.shields.io/badge/Gmail_SMTP-EA4335?style=flat-square&logo=gmail&logoColor=white)

### Infra & DevOps
![GCP](https://img.shields.io/badge/GCP-4285F4?style=flat-square&logo=googlecloud&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white)

<br>

## ✨ 주요 기능

### 👤 회원 인증
- JWT Access Token / Refresh Token 발급 및 갱신
- Google ID Token 검증을 통한 소셜 로그인
- 이메일 인증 코드 발송 (Gmail SMTP)
- SMS 인증 코드 발송 (CoolSMS)
- 아이디 / 닉네임 / 이메일 / 연락처 중복 검사

### 💬 실시간 채팅
- **WebSocket(STOMP)** 기반 1:1 실시간 채팅
- **Redis Pub/Sub**을 메시지 브로커로 활용
- 채팅 수신 시 실시간 알림(`/sub/user/{receiverId}/notification`) 발송
- **Spring Batch**로 Redis 버퍼의 채팅 메시지를 주기적으로 MySQL에 일괄 저장 (쓰기 지연)
  - chunk size: 100건 단위 처리
  - `ChatBatchScheduler`로 자동 스케줄링

### 🛍 상품 거래
- 상품 등록 / 수정 / 삭제 (다중 이미지 업로드 지원)
- 거래 상태 관리: 판매중 → 예약중 → 판매완료
- 인기 상품 조회 (메인 페이지 노출)
- 찜 목록 (위시리스트) 관리
- 내 판매 목록 조회
- 키워드 검색 및 카테고리 필터

### 💳 환승 페이 (자체 포인트 결제)
- 아임포트(Iamport) API를 통한 결제 금액 검증 후 포인트 충전
- 충전된 포인트로 상품 구매
- 잔액 조회 및 결제 이력 관리

### 🔔 알림
- 채팅 수신 시 실시간 알림
- 거래 관련 알림

### 🛡 관리자 (Admin)
- 회원 목록 조회 / 상태 변경 (정지 / 활성화) / 역할 변경
- 상품 관리 (목록 조회, 강제 삭제, 신고 처리)
- 신고 관리 (신고 목록, 상세, 처리 내역, 메모)
- 카테고리 관리 (등록 / 수정 / 삭제 / 순서 변경)
- 공지사항 / 문의 관리
- 대시보드: 전체 요약, 주간 트렌드, 최근 상품/거래 현황
- 통계: 회원 / 상품 / 거래 / 검색 키워드 / 신고 통계

<br>

## 🏗 시스템 아키텍처

```
[Browser / Mobile Client]
         │  HTTP REST / WebSocket(STOMP)
         ▼
[GCP VM — Docker Compose]
   ├── Backend (Spring Boot :8080)
   │     ├── Spring Security + JWT 인증
   │     ├── WebSocket Controller (STOMP)
   │     ├── Redis Pub/Sub (채팅 메시지 브로커)
   │     ├── Spring Batch (Redis → MySQL 채팅 로그 저장)
   │     └── REST API (상품 / 유저 / 결제 / 관리자)
   ├── MySQL (상품, 유저, 채팅 로그, 거래 내역)
   └── Redis (채팅 메시지 버퍼, Pub/Sub)
```

<br>

## 📁 프로젝트 구조

```
src/main/java/com/hwanseung/backend/
├── domain/
│   ├── admin/          # 관리자 (회원관리, 상품관리, 신고, 통계, 대시보드)
│   ├── chat/           # 실시간 채팅 (WebSocket, Redis Pub/Sub, Spring Batch)
│   ├── inquiry/        # 문의 관리
│   ├── notice/         # 공지사항 관리
│   ├── notification/   # 알림
│   ├── product/        # 상품 CRUD, 찜, 이미지
│   ├── report/         # 신고 기능
│   ├── search/         # 키워드 검색
│   └── user/           # 회원 인증 (JWT, OAuth2, 이메일/SMS, 페이)
└── config/
    └── WebConfig.java  # CORS 설정
```

<br>

## ⚙️ 환경 변수 설정

`.env` 또는 GitHub Actions Secrets에 아래 값을 설정하세요.

| 변수명 | 설명 |
|---|---|
| `JWT_SECRET` | JWT 서명 키 |
| `IAMPORT_API_KEY` | 아임포트 API 키 |
| `IAMPORT_API_SECRET` | 아임포트 API 시크릿 |
| `MAIL_PASSWORD` | Gmail SMTP 앱 비밀번호 |
| `COOLSMS_API_KEY` | CoolSMS API 키 |
| `COOLSMS_API_SECRET` | CoolSMS API 시크릿 |
| `GOOGLE_CLIENT_ID` | Google OAuth2 클라이언트 ID |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 클라이언트 시크릿 |

<br>

## 🚀 실행 방법

### 사전 요건
- Java 17
- MySQL
- Redis

### 로컬 실행

```bash
git clone https://github.com/{your-username}/hsmarket-backend.git
cd hsmarket-backend

# 환경 변수 설정 후 실행
./gradlew bootRun
```

### Docker로 실행

```bash
# 이미지 빌드
docker build -t hsmarket-backend .

# 컨테이너 실행
docker run -p 8080:8080 \
  -e JWT_SECRET=your_secret \
  -e IAMPORT_API_KEY=your_key \
  -e IAMPORT_API_SECRET=your_secret \
  -e MAIL_PASSWORD=your_password \
  -e COOLSMS_API_KEY=your_key \
  -e COOLSMS_API_SECRET=your_secret \
  -e GOOGLE_CLIENT_ID=your_id \
  -e GOOGLE_CLIENT_SECRET=your_secret \
  hsmarket-backend
```

### Docker Compose (권장)

```bash
# docker-compose.yml에 환경 변수 설정 후
docker-compose up -d --build backend
```

<br>

## 🔄 CI/CD

`main` 브랜치 push 시 GitHub Actions가 자동으로 배포합니다.

```
1. 소스 코드 체크아웃
2. SCP로 GCP 서버에 코드 전송
3. SSH로 GCP 서버 접속 → Docker 컨테이너 재빌드 및 재시작
```

<br>

## 📝 API 명세

### 인증 (`/api/auth`)

| Method | Endpoint | 설명 | 인증 필요 |
|---|---|---|---|
| POST | `/api/auth/login` | 로그인 | ❌ |
| POST | `/api/auth/signup` | 회원가입 | ❌ |
| GET | `/api/auth/refresh` | Access Token 갱신 | ❌ |
| POST | `/api/auth/google` | 구글 소셜 로그인 | ❌ |
| GET | `/api/auth/check-username` | 아이디 중복 확인 | ❌ |
| GET | `/api/auth/check-nickname` | 닉네임 중복 확인 | ❌ |
| GET | `/api/auth/check-email` | 이메일 중복 확인 | ❌ |
| GET | `/api/auth/check-contact` | 연락처 중복 확인 | ❌ |
| POST | `/api/auth/email/send-code` | 이메일 인증번호 발송 | ❌ |
| POST | `/api/auth/sms/send-code` | SMS 인증번호 발송 | ❌ |
| POST | `/api/auth/verify-code` | 인증번호 검증 | ❌ |

### 상품 (`/api/products`)

| Method | Endpoint | 설명 | 인증 필요 |
|---|---|---|---|
| GET | `/api/products` | 상품 목록 조회 | 선택 |
| POST | `/api/products` | 상품 등록 (multipart) | ✅ |
| GET | `/api/products/{productId}` | 상품 상세 조회 | ❌ |
| PUT | `/api/products/{productId}` | 상품 수정 | ✅ |
| DELETE | `/api/products/{productId}` | 상품 삭제 | ✅ |
| PATCH | `/api/products/{productId}/sold-out` | 판매완료 처리 | ✅ |
| PATCH | `/api/products/{productId}/reserved` | 예약중 처리 | ✅ |
| PATCH | `/api/products/{productId}/sale` | 판매중으로 변경 | ✅ |
| GET | `/api/products/popular` | 인기 상품 조회 | 선택 |
| GET | `/api/products/wishlist` | 찜 목록 조회 | ✅ |
| GET | `/api/products/my-sales` | 내 판매 목록 조회 | ✅ |

### 채팅 (WebSocket)

| 구분 | 경로 | 설명 |
|---|---|---|
| WebSocket 연결 | `/ws` | STOMP 연결 엔드포인트 |
| 메시지 발행 | `/pub/chat/message` | 메시지 전송 |
| 메시지 구독 | `/sub/chatroom/{roomId}` | 채팅방 구독 |
| 알림 구독 | `/sub/user/{userId}/notification` | 실시간 알림 구독 |

### 환승 페이 (`/api/v1/pay`)

| Method | Endpoint | 설명 | 인증 필요 |
|---|---|---|---|
| POST | `/api/v1/pay/verify` | 아임포트 결제 검증 및 포인트 충전 | ✅ |
| GET | `/api/v1/pay/balance` | 잔액 조회 | ✅ |
| POST | `/api/v1/pay/use` | 포인트 사용 | ✅ |

### 관리자 (`/api/admin`)

| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/admin/users` | 회원 목록 조회 (페이징, 검색) |
| PATCH | `/api/admin/users/{id}/status` | 회원 상태 변경 |
| GET | `/api/admin/dashboard` | 대시보드 요약 |
| GET | `/api/admin/statistics` | 통계 조회 |
| GET | `/api/admin/reports` | 신고 목록 |
| GET | `/api/admin/products` | 상품 관리 목록 |
| GET | `/api/admin/categories` | 카테고리 목록 |
| POST | `/api/admin/categories` | 카테고리 등록 |

<br>

## 👥 팀원 소개

> 전원 풀스택으로 프론트엔드와 백엔드를 함께 담당했습니다.

| 이름 | 담당 기능 |
|------|-----------|
| **강태준** | chat·notification 테이블 설계·구현, 채팅 API, 알림 API, CI/CD |
| **송은설** | user·auth 테이블 설계·구현, 로그인/회원가입 API, 소셜 로그인 |
| **김민석** | Pay 관련 테이블 설계·구현, 결제 API |
| **강석영** | Product 관련 테이블 설계·구현, 상품 API |
| **김태헌** | categories·reports·reports_history·search_keywords 테이블 설계·구현, 관리자 API |
| **김덕식** | 공지사항·자주묻는질문 API |

<br>

## 📄 라이선스

본 프로젝트는 팀 프로젝트 학습 목적으로 제작되었습니다.
