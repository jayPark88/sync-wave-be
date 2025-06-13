# Sync Wave

## Overview
일정과 할 일을 효율적으로 관리하고 실시간 알림을 제공하는 통합 일정 관리 시스템입니다.

## Key Features
- 사용자 기반 일정/할 일 관리
- 실시간 Slack 알림 연동
- 대시보드 기반 Overview 제공
- JWT 기반 인증/인가
- RESTful API 제공

## Technical Stack
### Backend
- Java 21
- Spring Boot 3.2.3
- Spring Data JPA / Hibernate
- MySQL 8.0

### Infrastructure
- Gradle (Build Tool)
- JWT (Authentication)
- Retrofit2 2.7.2 (HTTP Client)
- Slack API (Notification)

### Development
- Lombok
- Swagger (API Documentation)
- JUnit5 (Testing)

## Project Structure
```
sync-wave
├── sync-wave-common/
│   ├── config/          # 공통 설정
│   ├── jpa/            # 엔티티 및 레포지토리
│   ├── jwt/            # 인증 관련
│   ├── exception/      # 예외 처리
│   └── util/           # 유틸리티
├── sync-wave-service/
│   └── api/v1/
│       ├── auth/       # 인증
│       ├── user/       # 사용자 관리
│       ├── todos/      # 할 일 관리
│       ├── schedules/  # 일정 관리
│       └── dashboard/  # 대시보드
└── sync-wave-batch/
    ├── common/         # 배치 공통
    ├── schedules/      # 일정 배치
    └── todos/          # 할 일 배치
```

## API Specification

### Authentication
```http
POST   /service/v1/auth/login          # 로그인
```

### User Management
```http
POST   /service/v1/user/signup         # 회원가입
GET    /service/v1/user/list           # 사용자 목록
GET    /service/v1/user/{userId}       # 사용자 상세
PATCH  /service/v1/user/{userId}       # 사용자 수정
DELETE /service/v1/user/{userId}       # 회원 탈퇴
```

### Todo Management
```http
POST   /service/v1/todos               # 할 일 생성
GET    /service/v1/todos               # 할 일 목록
GET    /service/v1/todos/{id}          # 할 일 상세
PATCH  /service/v1/todos               # 할 일 수정
DELETE /service/v1/todos/{id}          # 할 일 삭제
```

### Schedule Management
```http
POST   /service/v1/schedules           # 일정 생성
GET    /service/v1/schedules           # 일정 목록
GET    /service/v1/schedules/{id}      # 일정 상세
PATCH  /service/v1/schedules/{id}      # 일정 수정
DELETE /service/v1/schedules/{id}      # 일정 삭제
```

### Utilities
```http
GET    /service/v1/dashboard           # 대시보드
POST   /service/v1/file/upload         # 파일 업로드
DELETE /service/v1/file/{id}           # 파일 삭제
```

## Development Guide

### Prerequisites
```bash
# Required
- JDK 21
- MySQL 8.0
- Gradle 8.x

# Optional
- Docker
- Docker Compose
```

### Quick Start
```bash
# Clone the repository
git clone https://github.com/username/sync-wave.git

# Build
./gradlew clean build

# Run API Server
./gradlew :sync-wave-service:bootRun

# Run Batch Server
./gradlew :sync-wave-batch:bootRun
```

### Configuration
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/syncwave
    username: ${MYSQL_USERNAME}
    password: ${MYSQL_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000

slack:
  token: ${SLACK_TOKEN}
  channel: ${SLACK_CHANNEL}
```

## Documentation
- API 문서: http://localhost:8080/service/swagger-ui.html
- 상세 설계: [🔗 프로젝트 노션](https://closed-roar-8b6.notion.site/SyncWave-10d08810873880bbba55ef782f590edb?pvs=4)

---
© 2024 Parker. All rights reserved.
