# FarmShop

FarmShop은 농산물 직거래 온라인 쇼핑몰 플랫폼입니다. 소비자와 판매자를 직접 연결하여 신선한 농산물을 편리하게 구매할 수 있는 서비스를 제공합니다.

## 기술 스택

### 백엔드
- Java 17
- Spring Boot 3.4.4
- Spring Data JPA
- Hibernate
- MySQL
- H2 Database (개발 및 테스트용)
- Lombok
- JUnit

### 프론트엔드 (개발 예정)
- React

## 프로젝트 구조

현재 구현된 주요 엔티티 관계(구현중):

- **Member**: 사용자 정보 관리
- **Order**: 주문 정보 관리
- **Delivery**: 배송 정보 관리

엔티티 간 관계:
- Member와 Order: 일대다(1:N) 관계
- Order와 Delivery: 일대일(1:1) 관계

## 주요 기능 (개발 중)

- 회원 가입 및 로그인
- 상품 목록 조회 및 상세 정보 확인
- 장바구니 기능
- 주문 및 결제 프로세스
- 배송 상태 추적
- 리뷰 작성 및 조회

## 개발 로드맵

1. 백엔드 API 개발 (현재 진행 중)
2. 프론트엔드 React 통합 개발 (예정)
3. 배포 및 테스트

## 설치 및 실행 방법

### 요구사항
- Java 17 이상
- Gradle

### 설치
```bash
git clone https://github.com/username/farmshop.git
cd farmshop
./gradlew build
```

### 실행
```bash
./gradlew bootRun
```

## 참고사항

이 프로젝트는 현재 개발 중이며, 기능과 구조가 변경될 수 있습니다. React 프론트엔드와의 통합 작업이 추후 진행될 예정입니다.