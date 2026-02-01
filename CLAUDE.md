# Musinsa Wagon Backend

## Project Overview

**Musinsa Wagon**은 한국 주요 패션몰(무신사, 에이블리, 지그재그)의 가격 투명성을 제공하는 가격 추적 서비스입니다.

### 핵심 가치
- 20~30대 패션 소비자들이 명절 시즌마다 겪는 "가짜 할인" 속임수 탐지
- 브랜드별 상품 가격 히스토리 추적
- "지금 사도 되는 가격인지" 즉시 판단 지원

## Tech Stack

| Layer | Technology | Version |
|-------|------------|---------|
| Language | Java | 17 |
| Framework | Spring Boot | 3.x |
| Build Tool | Gradle | 8.x |
| Database | MySQL | 8.x |
| ORM | JPA/Hibernate | - |

## Project Structure (Multi-Module)

```
musinsa-wagon-back/
├── core/           # 핵심 도메인 엔티티, 리포지토리
├── fo/             # Front Office API (사용자향)
├── batch/          # 배치 크롤링 작업
└── docs/           # 프로젝트 문서
```

## Coding Conventions

### Package Structure
```
com.musinsa.wagon.{module}.domain.{feature}
  ├── entity/
  ├── repository/
  ├── service/
  ├── dto/
  └── api/
```

### Code Style
- **Format**: Google Java Format (Spotless 적용)
- **Price Fields**: 반드시 `BigDecimal` 사용 (금융 정확도)
- **Nullable**: `Optional<T>` 반환 또는 `@Nullable` 명시
- **Entity Auditing**: `BaseEntity` 상속 (createdAt, updatedAt 자동 관리)

### Naming Conventions
- Entity: PascalCase (예: `Product`, `PriceHistory`)
- Repository: `{Entity}Repository`
- Service: `{Feature}Service`
- Controller: `{Feature}Controller`
- DTO: `{Action}{Entity}Request/Response`

## Build Commands

```bash
# 전체 빌드
./gradlew build

# 특정 모듈 빌드
./gradlew :core:build
./gradlew :fo:build
./gradlew :batch:build

# 테스트 실행
./gradlew test

# 포맷 검사
./gradlew spotlessCheck

# 포맷 적용
./gradlew spotlessApply
```

## Key Documentation

| Document | Path | Description |
|----------|------|-------------|
| PRD | `docs/prd.md` | 제품 요구사항 정의서 |
| Architecture | `docs/architecture.md` | 프론트엔드 아키텍처 (참고용) |
| Development Guide | `docs/development-guide.md` | 개발 가이드 |
| Multi-Module Setup | `docs/MULTI_MODULE_SETUP_GUIDE.md` | 멀티모듈 구조 가이드 |

## Domain Knowledge

### 가격 평가 시스템 (PriceLabel)
| Label | Meaning | Criteria |
|-------|---------|----------|
| GREEN_LOWEST | 역대 최저가 | 90일 내 최저가 |
| GREEN_GOOD | 좋은 딜 | 평균 대비 10% 이상 저렴 |
| YELLOW_NORMAL | 보통 | 평균 가격대 |
| RED_EXPENSIVE | 비싼 편 | 평균 대비 10% 이상 비쌈 |

### 명절 속임수 탐지
- **탐지 기간**: 명절 D-14일 ~ D+7일
- **패턴**: 원가 급등 후 할인 표시
- **알림**: 가짜 할인 의심 경고

### 지원 쇼핑몰 (ShopType)
- `MUSINSA` - 무신사
- `ABLY` - 에이블리
- `ZIGZAG` - 지그재그

## Custom Agents

프로젝트 전용 서브에이전트가 `.claude/agents/`에 정의되어 있습니다:

- **prd-validator**: 코드가 PRD 요구사항과 일치하는지 검토
- **code-quality**: 클린 아키텍처 및 코드 품질 검토
