# 프로젝트 개요: Musinsa Wagon - Next.js React TypeScript v2

## 프로젝트 정보

- **프로젝트명**: musinsa-pricewagon-nextjs-react-ts-v2
- **버전**: 0.1.0
- **설명**: 무신사 프라이스 왜건 Next.js 기반 리액트 애플리케이션
- **저장소 타입**: Monolith (단일 프로젝트)

## 기술 스택 요약

| 카테고리            | 기술           | 버전    |
| ------------------- | -------------- | ------- |
| **프레임워크**      | Next.js        | 15.3.3  |
| **라이브러리**      | React          | 19.0.0  |
| **언어**            | TypeScript     | 5.x     |
| **스타일링**        | Tailwind CSS   | 4.1.10  |
| **UI 컴포넌트**     | shadcn/ui      | Latest  |
| **상태 관리**       | Zustand        | 5.0.5   |
| **데이터 페칭**     | TanStack Query | 5.80.7  |
| **HTTP 클라이언트** | Axios          | 1.10.0  |
| **UI 프리미티브**   | Radix UI       | Latest  |
| **아이콘**          | Lucide React   | 0.523.0 |
| **알림/토스트**     | Sonner         | 2.0.5   |
| **캐러셀**          | Embla Carousel | 8.6.0   |
| **슬라이더**        | Swiper         | 11.2.10 |
| **패키지 매니저**   | pnpm           | 10.8.0  |

## 주요 기술 특성

- **아키텍처 패턴**: Component-based architecture with Colocation pattern (Next.js 권장)
- **API 통합**: OpenAPI Generator를 사용한 자동 TypeScript API 클라이언트 생성 (필요 시)
- **상태 관리**: 전역 상태는 Zustand, 서버 상태는 TanStack Query
- **스타일링**: Tailwind CSS v4 (유틸리티 퍼스트) + shadcn/ui 컴포넌트
- **테스팅**: Vitest + Storybook (컴포넌트 개발 및 테스팅)
- **코드 품질**: ESLint, Prettier, Husky (pre-commit 훅)
- **타입 안전성**: 엄격한 TypeScript 설정

## 프로젝트 구조

```
src/
├── app/                 # Next.js App Router (페이지 및 페이지 전용 컴포넌트)
├── components/          # 공유 컴포넌트
│   ├── layout/         # Header, Footer 등 레이아웃
│   ├── ui/             # shadcn/ui 컴포넌트
│   └── provider/        # 컨텍스트 프로바이더
├── hooks/              # 전역 커스텀 훅
├── lib/                # 유틸리티 함수 및 설정 (cn 등)
├── queries/            # TanStack Query 훅
├── stores/             # Zustand 전역 스토어
├── types/              # 전역 타입 정의
├── constants/          # 상수 정의 (브레이크포인트 등)
└── stories/            # Storybook 스토리
```

## 주요 서비스 영역

### 인증 (Auth)

- 세션 관리 및 로그인/로그아웃 기능

### 상품 (Products)

- 가격 추적 및 히스토리 시각화
- 브랜드별 상품 목록 및 필터링

### 알림 (Notifications)

- 역대 최저가 및 목표가 달성 푸시 알림
- 명절 가짜 할인 경고 시스템

### 카테고리 (Category)

- 쇼핑몰별 카테고리 네비게이션 및 필터링

## 환경 설정

### 지원하는 환경

- **development** (.env.development)
- **production** (.env.production)
- **local** (.env.local)

### API 엔드포인트

- **개발**: https://devapi.ilsang.co.kr/api-docs
- **프로덕션**: (미설정 - .env.production 참조)

## 개발 워크플로우

1. **API 스키마 변경 시**

   ```bash
   pnpm codegen:dev    # OpenAPI 스펙에서 API 클라이언트 생성
   ```

2. **개발 서버 실행**

   ```bash
   pnpm dev            # 코드 생성 + 개발 서버 실행
   pnpm dev-without-codegen  # 빠른 시작 (API 변경 없을 때)
   ```

3. **코드 품질 관리**

   ```bash
   pnpm lint          # ESLint 검사
   pnpm fix:lint      # ESLint 자동 수정
   pnpm prettier      # Prettier 포맷팅 검사
   pnpm fix:prettier  # Prettier 자동 수정
   pnpm type-check    # TypeScript 타입 검사
   ```

4. **테스팅 및 문서화**
   ```bash
   pnpm storybook     # Storybook 개발 서버
   pnpm build-storybook  # Storybook 빌드
   ```

## 코드 컨벤션

### 파일 요약 주석

모든 파일은 목적을 명확히 하는 요약 주석으로 시작합니다:

```typescript
/**
 * 파일 요약: [파일 목적]
 * 주요 내보내기: [내보내는 항목들]
 * 의존성: [중요 외부 의존성]
 */
```

### React 컴포넌트

- `export function`을 선호 (Arrow function ❌)
- Props와 타입 명시
- JSX는 React 19 new JSX transform 사용

### 폴더 구조

- Colocation pattern in `src/app/` (페이지와 컴포넌트 함께 배치)
- Shared components in `src/components/`
- Utility functions in `src/lib/`
- Constants in `src/constants/` (반응형 설정 등)

## 글로벌 에러 처리

- QueryClient 프로바이더가 API 에러를 전역 처리
- 401 응답 시 자동으로 세션 정리 및 로그인 페이지 리다이렉트
- Sonner를 사용한 토스트 알림

## 다음 단계

1. [아키텍처 문서](architecture.md) 검토
2. [소스 트리 분석](./source-tree-analysis.md) 참조
3. [개발 가이드](development-guide.md) 숙지
4. [API 계약](./api-contracts.md) 확인

---

**생성 일시**: 2025-12-06  
**스캔 레벨**: Quick Scan (패턴 기반 분석)
