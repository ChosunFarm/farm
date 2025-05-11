# 농산물 경매 시스템

농산물 판매자와 구매자를 연결하는 실시간 경매 플랫폼입니다. 판매자는 자신의 농산물을 등록하고 검수를 받은 후 경매에 참여할 수 있으며, 구매자는 경매에 참여하여 가격을 제안하고 거래할 수 있습니다.

## 기능 목록

### 사용자 관리
- 회원가입 및 로그인
- 사용자 유형: 일반 사용자, 판매자, 관리자

### 상품 관리
- 상품 등록: 과일, 채소, 곡물 카테고리 지원
- 상품 검수: 관리자 검수 승인/거부 기능
- 검수 상태 확인: 검수 신청 전, 검수 대기중, 경매 진행중, 판매 완료

### 경매 시스템
- 실시간 경매 현황 조회
- 카테고리별 상품 필터링
- 가격 제안(입찰) 기능
- 최고가 입찰 수락 시스템
- 입찰 내역 조회

### 마이페이지
- 내 상품 관리
- 입찰 현황 확인
- 받은 입찰 관리

## 개발 환경

- Java 17+
- Spring Boot
- Spring Security
- Spring Data JPA
- Thymeleaf
- H2 Database(개발용) / MySQL(배포용)
- Tailwind CSS

## 프로젝트 설정

### 데이터베이스 설정

#### 개발 환경 (H2)
```yaml
spring:
  datasource:
    url: jdbc:h2:tcp://localhost/~/farmshop
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
        format_sql: true
```

#### 배포 환경 (MySQL)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/farmshop?serverTimezone=UTC&characterEncoding=UTF-8
    username: [사용자명]
    password: [비밀번호]
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
```

### 파일 업로드 설정
```yaml
spring:
  file:
    upload:
      directory: ./uploads/images  # 업로드 루트
  web:
    resources:
      static-locations:
        - classpath:/static/       # 기존 스태틱
        - file:./uploads/
      cache:
        period: 0
```

## 프로젝트 구조

### 엔티티 구조
- `Member`: 사용자 정보
- `Product`: 상품 정보(추상 클래스)
    - `Fruit`: 과일 상품
    - `Vegetable`: 채소 상품
    - `Grain`: 곡물 상품
- `Bid`: 입찰 정보
- `ProductImage`: 상품 이미지 정보

### 패키지 구조
```
src/main/java/farm/farmshop/
├── config/             # 설정 파일
├── controller/         # 컨트롤러
├── dto/                # DTO 클래스
├── entity/             # 엔티티 클래스
│   └── product/        # 상품 관련 엔티티
├── repository/         # 리포지토리
└── service/            # 서비스 클래스
```

## 설치 및 실행 방법

1. 저장소 클론
```bash
git clone https://github.com/yourusername/farmshop.git
cd farmshop
```

2. 데이터베이스 설정
- H2 데이터베이스 실행 또는 MySQL 데이터베이스 설정
- `application.yml` 파일에서 데이터베이스 연결 정보 설정

3. 프로젝트 빌드 및 실행
```bash
./gradlew build
./gradlew bootRun
```

4. 웹 브라우저에서 접속
```
http://localhost:8080
```

## 시스템 워크플로우

### 상품 등록 및 경매 프로세스

1. 판매자: 상품 등록 페이지에서 상품 정보 입력 및 등록
2. 판매자: 마이페이지 또는 경매 예정 페이지에서 검수 신청
3. 관리자: 검수 요청된 상품 확인 후 승인/거부
4. 승인된 상품은 실시간 경매 페이지에 등록됨
5. 구매자: 실시간 경매 페이지에서 상품 목록 확인
6. 구매자: 관심 상품의 상세 페이지로 이동하여 상품 정보 확인
7. 구매자: 입찰가 제안 입력 후 "입찰하기" 버튼 클릭
8. 판매자: 마이페이지에서 받은 입찰 내역 확인
9. 판매자: 마음에 드는 입찰을 선택하여 "입찰 수락" 버튼 클릭
10. 거래 성립: 낙찰된 상품은 "판매 완료" 상태로 변경되며, 같은 상품의 다른 입찰은 자동으로 거부됨

## 주요 페이지

- `/`: 메인 페이지
- `/login`: 로그인 페이지
- `/auction`: 상품 등록 페이지
- `/live_auction`: 실시간 경매 페이지
- `/auction/detail/{productId}`: 상품 상세 및 입찰 페이지
- `/mypage`: 마이페이지
- `/admin/*`: 관리자 페이지
    - `/admin/pendinglist`: 검수 대기 목록
    - `/admin/products`: 상품 관리
    - `/admin/members`: 회원 관리

## 참고사항

- 최고가 입찰만 수락 가능하도록 구현됨
- 입찰은 1,000원 단위로만 가능
- 상품 검수는 관리자만 가능
- 이미지 업로드는 상품당 여러 개 가능

## 향후 계획

- MySQL 데이터베이스로 마이그레이션
- 실시간 알림 기능 추가
- 결제 시스템 연동
- 모바일 최적화 UI 개선