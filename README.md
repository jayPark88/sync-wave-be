# 🚀 Sync Wave

> 일정과 할 일을 효율적으로 관리하고 실시간 알림을 제공하는 통합 일정 관리 시스템

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-green.svg)](https://gradle.org/)

## 📋 목차
- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [프로젝트 구조](#-프로젝트-구조)
- [빠른 시작](#-빠른-시작)
- [API 문서](#-api-문서)
- [테스트 가이드](#-테스트-가이드)
- [설정](#-설정)

## ✨ 주요 기능

- 🔐 **JWT 기반 인증/인가**
- 👥 **사용자 관리** (회원가입, 로그인, 프로필 관리)
- 📝 **할 일 관리** (CRUD, 상태 관리)
- 📅 **일정 관리** (CRUD, 알림 설정)
- 📊 **대시보드** (통합 현황 조회)
- 🔔 **실시간 Slack 알림**
- 📁 **파일 업로드/관리**

## 🛠 기술 스택

### Backend
- **Java 21** - 최신 LTS 버전
- **Spring Boot 3.2.3** - 웹 애플리케이션 프레임워크
- **Spring Data JPA** - 데이터 접근 계층
- **MySQL 8.0** - 관계형 데이터베이스

### Infrastructure
- **Gradle** - 빌드 도구
- **JWT** - 토큰 기반 인증
- **Retrofit2** - HTTP 클라이언트
- **Slack API** - 알림 연동

### Development
- **Lombok** - 코드 간소화
- **Swagger** - API 문서화
- **JUnit5** - 단위 테스트
- **Mockito** - Mock 테스트
- **AssertJ** - 테스트 어설션

## 📁 프로젝트 구조

```
sync-wave/
├── sync-wave-common/          # 공통 모듈
│   ├── config/               # 공통 설정
│   ├── jpa/                 # 엔티티 및 레포지토리
│   ├── jwt/                 # JWT 인증
│   ├── exception/           # 예외 처리
│   └── util/                # 유틸리티
├── sync-wave-service/        # API 서버
│   └── api/v1/
│       ├── auth/            # 인증 (로그인/로그아웃)
│       ├── user/            # 사용자 관리
│       ├── todos/           # 할 일 관리
│       ├── schedules/       # 일정 관리
│       ├── dashboard/       # 대시보드
│       └── file/            # 파일 관리
└── sync-wave-batch/          # 배치 서버
    ├── common/              # 배치 공통
    ├── schedules/            # 일정 배치
    └── todos/                # 할 일 배치
```

## 🚀 빠른 시작

### 1. 사전 요구사항
```bash
# 필수
- JDK 21
- MySQL 8.0
- Gradle 8.x

# 선택사항
- Docker & Docker Compose
```

### 2. 프로젝트 실행
```bash
# 저장소 클론
git clone https://github.com/username/sync-wave.git
cd sync-wave

# 빌드
./gradlew clean build

# API 서버 실행
./gradlew :sync-wave-service:bootRun

# 배치 서버 실행 (별도 터미널)
./gradlew :sync-wave-batch:bootRun
```

### 3. 접속 확인
- **API 서버**: http://localhost:8080
- **API 문서**: http://localhost:8080/service/swagger-ui.html

## 📚 API 문서

### 🔐 인증
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/service/v1/auth/login` | 로그인 |

### 👥 사용자 관리
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/service/v1/user/signup` | 회원가입 |
| GET | `/service/v1/user/list` | 사용자 목록 |
| GET | `/service/v1/user/{userId}` | 사용자 상세 |
| PATCH | `/service/v1/user/{userId}` | 사용자 수정 |
| DELETE | `/service/v1/user/{userId}` | 회원 탈퇴 |

### 📝 할 일 관리
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/service/v1/todos` | 할 일 생성 |
| GET | `/service/v1/todos` | 할 일 목록 |
| GET | `/service/v1/todos/{id}` | 할 일 상세 |
| PATCH | `/service/v1/todos` | 할 일 수정 |
| DELETE | `/service/v1/todos/{id}` | 할 일 삭제 |

### 📅 일정 관리
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/service/v1/schedules` | 일정 생성 |
| GET | `/service/v1/schedules` | 일정 목록 |
| GET | `/service/v1/schedules/{id}` | 일정 상세 |
| PATCH | `/service/v1/schedules/{id}` | 일정 수정 |
| DELETE | `/service/v1/schedules/{id}` | 일정 삭제 |

### 🛠 유틸리티
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/service/v1/dashboard` | 대시보드 |
| POST | `/service/v1/file/upload` | 파일 업로드 |
| DELETE | `/service/v1/file/{id}` | 파일 삭제 |

## 🧪 테스트 가이드

### 전체 테스트 실행
```bash
# 모든 모듈 테스트
./gradlew test

# 특정 모듈 테스트
./gradlew :sync-wave-service:test
./gradlew :sync-wave-common:test
./gradlew :sync-wave-batch:test
```

### 특정 테스트 실행
```bash
# 특정 클래스 테스트
./gradlew :sync-wave-service:test --tests "com.parker.service.api.v1.user.controller.UserControllerTest"

# 패턴 매칭 테스트
./gradlew :sync-wave-service:test --tests "*UserControllerTest*"
./gradlew :sync-wave-service:test --tests "com.parker.service.api.v1.notice.*"

# 특정 메서드 테스트
./gradlew :sync-wave-service:test --tests "com.parker.service.api.v1.user.controller.UserControllerTest.signup_유효한사용자정보_성공응답반환"
```

### 테스트 옵션
```bash
# 상세 정보 출력
./gradlew :sync-wave-service:test --info

# 실패해도 계속 실행
./gradlew :sync-wave-service:test --continue

# 병렬 실행
./gradlew :sync-wave-service:test --parallel
```

### 📊 테스트 리포트
테스트 실행 후 생성되는 리포트: `sync-wave-service/build/reports/tests/test/index.html`

### 🔄 TDD (Test Driven Development)
이 프로젝트는 TDD 방법론을 적용하여 개발되었습니다.

**테스트 구조:**
- **Controller 테스트**: HTTP 요청/응답 처리 검증
- **Service 테스트**: 비즈니스 로직 검증
- **Mock 객체**: 의존성 격리를 위한 Mockito 활용

**TDD 사이클:**
```bash
# 🔴 Red: 실패하는 테스트 작성
./gradlew :sync-wave-service:test --tests "*UserServiceTest.signUp*"

# 🟢 Green: 테스트 통과하는 최소 코드 작성
./gradlew :sync-wave-service:test --tests "*UserControllerTest*"

# 🔵 Refactor: 코드 개선 및 안전성 확인
./gradlew :sync-wave-service:test
```

### 🎯 Mockito ArgumentMatcher 사용법

**핵심 원칙**: `any()` 같은 Matcher를 사용하는 순간, **나머지 모든 파라미터도 Matcher가 강제**됩니다!

#### ✅ 올바른 사용법

**1. 모든 파라미터에 리터럴 사용**
```java
when(mock.method("value1", "value2", 123))
    .thenReturn(result);
```

**2. 모든 파라미터에 Matcher 사용**
```java
when(mock.method(anyString(), anyString(), anyInt()))
    .thenReturn(result);
```

**3. 혼용 시 모든 리터럴에 eq() 사용**
```java
when(mock.method(eq("value1"), anyString(), eq(123)))
    .thenReturn(result);
```

#### ❌ 잘못된 사용법
```java
// 절대 안 됨! 에러 발생!
when(mock.method("value1", anyString(), 123))
//                    ^^^^^^^^  ^^^^^^^^^^  ^^^
//                    리터럴     Matcher     리터럴
//                    → Mockito 에러!
```

#### 💡 실무 패턴 예시
```java
// Repository 메서드 Mock
when(noticeRepository.findByIsActive(eq(true), any(Pageable.class)))
    .thenReturn(mockPage);

// Service 메서드 Mock  
when(userService.createUser(eq("홍길동"), anyString(), anyInt()))
    .thenReturn(mockUser);

// 검증 시에도 동일한 패턴 사용
verify(noticeRepository).findByIsActive(eq(true), any(Pageable.class));
```

**기억하기**: Mockito는 **"All or Nothing"** 원칙!

## 📖 추가 문서

- **API 문서**: http://localhost:8080/service/swagger-ui.html
- **상세 설계**: [🔗 프로젝트 노션](https://closed-roar-8b6.notion.site/SyncWave-10d08810873880bbba55ef782f590edb?pvs=4)
- **TDD 문서**: [🔗 TDD 노션 문서](https://www.notion.so/TDD-26d088108738801ca19fe57ec6938702?source=copy_link)

---

<div align="center">

**© 2024 Parker. All rights reserved.**

Made with ❤️ by Parker

</div>