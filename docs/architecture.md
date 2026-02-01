# 아키텍처 문서: Musinsa Wagon

## 1. Executive Summary

**Musinsa Wagon**은 Next.js 15.3.3 기반의 현대적인 웹 애플리케이션입니다.
모던 React 19의 함수형 컴포넌트와 강타입 TypeScript를 활용하여 확장 가능하고 유지보수하기 쉬운 구조로 설계되었습니다.

### 핵심 특징

- **Component-based Architecture**: React 컴포넌트의 재사용성과 모듈화
- **Colocation Pattern**: 페이지와 관련 컴포넌트를 함께 배치 (Next.js 권장)
- **Type-safe Development**: 엄격한 TypeScript 설정으로 런타임 에러 최소화
- **Server-side Rendering**: Next.js App Router를 통한 SSR/SSG 지원
- **Modern State Management**: Zustand (전역) + TanStack Query (서버 상태)

---

## 2. 기술 스택

### 2.1 핵심 프레임워크 및 라이브러리

| 계층              | 기술         | 용도                    | 버전   |
| ----------------- | ------------ | ----------------------- | ------ |
| **프레임워크**    | Next.js      | SSR/SSG, 라우팅, 번들링 | 15.3.3 |
| **UI 라이브러리** | React        | UI 구성 및 상태 관리    | 19.0.0 |
| **언어**          | TypeScript   | 타입 안전성             | 5.x    |
| **스타일링**      | Tailwind CSS | 유틸리티 우선 CSS       | 4.1.10 |
| **UI 컴포넌트**   | shadcn/ui    | 재사용 가능한 컴포넌트  | Latest |

### 2.2 상태 및 데이터 관리

| 역할               | 라이브러리     | 사용 케이스            | 버전   |
| ------------------ | -------------- | ---------------------- | ------ |
| **전역 상태**      | Zustand        | 인증 정보, 사용자 설정 | 5.0.5  |
| **서버 상태**      | TanStack Query | API 데이터, 캐싱       | 5.80.7 |
| **API 클라이언트** | Axios          | HTTP 요청              | 1.10.0 |

### 2.3 UI/UX 라이브러리

| 기능              | 라이브러리     | 버전    |
| ----------------- | -------------- | ------- |
| **UI 프리미티브** | Radix UI       | Latest  |
| **아이콘**        | Lucide React   | 0.523.0 |
| **토스트/알림**   | Sonner         | 2.0.5   |
| **캐러셀**        | Embla Carousel | 8.6.0   |
| **슬라이더**      | Swiper         | 11.2.10 |

### 2.4 개발 및 품질 도구

| 용도                | 도구       | 버전   |
| ------------------- | ---------- | ------ |
| **테스팅**          | Vitest     | 3.2.3  |
| **컴포넌트 문서화** | Storybook  | 9.0.9  |
| **E2E 테스팅**      | Playwright | 1.53.0 |
| **린팅**            | ESLint     | 9.29.0 |
| **포맷팅**          | Prettier   | 3.5.3  |
| **Git 훅**          | Husky      | 8.0.0  |
| **패키지 매니저**   | pnpm       | 10.8.0 |

---

## 3. 아키텍처 패턴

### 3.1 계층 구조 (Layered Architecture)

```
┌─────────────────────────────────────────────────────┐
│                 Presentation Layer                   │
│  (Pages, Layouts, Shared Components, UI Primitives)  │
└─────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────┐
│              Business Logic Layer (Colocation)      │
│  (Page-specific Components, Hooks within routes)    │
└─────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────┐
│            Data Access & State Management Layer      │
│  (Queries, Stores, API Client)                      │
└─────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────┐
│              External Services / APIs                │
│  (OpenAPI, Authentication, etc.)                    │
└─────────────────────────────────────────────────────┘
```

### 3.2 Component-based Architecture

모든 UI는 작은 재사용 가능한 컴포넌트로 구성:

- **Base Components** (`src/components/base/`)

  - Badge, Button, Text 등 기본 빌딩 블록
  - 단일 책임 원칙 준수
  - Props를 통한 커스터마이제이션

- **Layout Components** (`src/components/layout/`)

  - Header, Footer, Navigation
  - 페이지 구조 정의
  - 공통 UI 레이아웃 재사용

- **UI Components** (`src/components/ui/`)
  - shadcn/ui 기반 사전 정의된 컴포넌트
  - Radix UI 프리미티브 활용
  - 접근성 고려

### 3.3 Colocation Pattern (Next.js 권장)

페이지와 관련 컴포넌트를 함께 배치하여 관련 코드가 가까이 위치:

```
src/app/
├── (auth)/
│   ├── login/
│   │   ├── _components/      # 이 페이지 전용 컴포넌트
│   │   │   └── LoginForm.tsx
│   │   └── page.tsx
│   └── layout.tsx
├── products/
│   ├── [id]/
│   │   ├── _components/      # 상품 상세 전용 컴포넌트
│   │   └── page.tsx
│   └── page.tsx
├── layout.tsx
└── page.tsx
```

**이점:**

- 관련 코드가 가까이 위치하여 탐색 용이
- Next.js 공식 권장 패턴
- 페이지별 코드 스플리팅 자연스럽게 적용
- `_components/` 폴더는 라우트로 노출되지 않음

---

## 4. 데이터 흐름

### 4.1 API 통합 흐름

```
User Interaction
       ↓
React Component
       ↓
TanStack Query Hook (src/queries/)
       ↓
API Client (src/apis/main/ - OpenAPI 생성)
       ↓
Zustand Store (필요시 전역 상태 업데이트)
       ↓
Component Re-render
```

### 4.2 상태 관리 전략

#### 전역 상태 (Zustand)

- 인증 정보 (사용자, 토큰, 세션)
- 애플리케이션 설정 (테마, 언어)
- 사용자 선호도

#### 서버 상태 (TanStack Query)

- API 데이터 (상품, 카테고리, 주문 등)
- 캐싱 및 동기화 관리
- 로딩/에러 상태 자동 처리

#### 로컬 상태 (React useState)

- 폼 입력값
- UI 토글 상태 (모달 열기/닫기 등)
- 일시적인 데이터

### 4.3 에러 처리 흐름

```
API Request
    ↓
Error Occurs
    ↓
QueryClient Global Error Handler
    ↓
401 Unauthorized?
├─ Yes → Clear Session + Redirect to Login
└─ No → Show Toast Notification (Sonner)
    ↓
Error Component 렌더링
```

---

## 5. 디렉토리 구조

```
musinsa-wagon/
├── src/
│   ├── app/                  # Next.js App Router (Colocation 패턴)
│   │   ├── globals.css       # 글로벌 스타일
│   │   ├── layout.tsx        # 루트 레이아웃
│   │   ├── loading.tsx       # 로딩 UI
│   │   ├── not-found.tsx     # 404 페이지
│   │   ├── page.tsx          # 홈 페이지
│   │   ├── (auth)/           # 인증 관련 라우트 그룹
│   │   │   ├── login/
│   │   │   │   ├── _components/  # 페이지 전용 컴포넌트
│   │   │   │   └── page.tsx
│   │   │   └── layout.tsx
│   │   └── products/         # 상품 라우트
│   │       ├── [id]/
│   │       │   ├── _components/
│   │       │   └── page.tsx
│   │       └── page.tsx
│   │
│   ├── components/           # 공유 컴포넌트
│   │   ├── layout/           # 레이아웃 (Header, Footer)
│   │   ├── ui/               # shadcn/ui 컴포넌트
│   │   └── provider/         # 컨텍스트 프로바이더
│   │
│   ├── hooks/                # 전역 커스텀 훅
│   │   └── useMediaQuery.ts  # 반응형 미디어쿼리
│   ├── lib/                  # 유틸리티 및 설정
│   │   └── utils.ts          # cn() 등 유틸리티
│   ├── queries/              # TanStack Query 훅
│   ├── stores/               # 전역 Zustand 스토어
│   ├── types/                # 전역 타입 정의
│   ├── constants/            # 상수 정의
│   │   └── breakpoints.ts    # 반응형 브레이크포인트
│   └── stories/              # Storybook 스토리
│
├── public/                   # 정적 파일
│
├── .env.example              # 환경변수 템플릿
├── .env.development          # 개발 환경변수
├── .env.production           # 프로덕션 환경변수
├── .env.local                # 로컬 환경변수
│
├── next.config.ts            # Next.js 설정
├── tsconfig.json             # TypeScript 설정 (엄격)
├── components.json           # shadcn/ui 설정
├── postcss.config.js         # PostCSS 설정 (Tailwind v4)
├── vitest.config.ts          # Vitest 설정
│
├── .storybook/               # Storybook 설정
├── .husky/                   # Git 훅 설정
│
└── package.json
```

---

## 6. 핵심 아키텍처 결정사항

### 6.1 왜 Next.js?

- 서버 사이드 렌더링 (SEO 최적화)
- 자동 코드 분할 및 최적화
- API 라우트 지원
- 내장 이미지 최적화
- App Router를 통한 직관적 라우팅

### 6.2 왜 TypeScript (엄격 설정)?

- 개발 시 에러 조기 감지
- IDE 자동완성 향상
- 코드 가독성 및 유지보수성 향상
- 런타임 에러 감소
- 팀 협업 시 명확한 인터페이스

### 6.3 왜 Zustand 대신 Context API?

- 번들 크기 감소
- 보일러플레이트 코드 최소화
- 성능 최적화 (불필요한 리렌더링 방지)
- 비동기 상태 관리 용이
- DevTools 지원

### 6.4 왜 TanStack Query?

- API 캐싱 자동 관리
- 백그라운드 데이터 동기화
- 로딩/에러 상태 자동 처리
- DevTools 지원
- 복잡한 서버 상태 관리 간소화

---

## 7. 보안 고려사항

### 7.1 인증

- JWT 토큰 기반 인증
- 401 응답 시 자동 세션 정리
- HttpOnly 쿠키 지원 (추후 구현)

### 7.2 API 호출

- Axios 인터셉터를 통한 요청/응답 처리
- CORS 정책 준수
- 에러 핸들링

### 7.3 코드 품질

- ESLint를 통한 코드 검사
- TypeScript 엄격 모드
- Pre-commit 훅으로 커밋 전 검사

---

## 8. 성능 최적화

### 8.1 번들 최적화

- 자동 코드 분할 (Next.js)
- Tree shaking (TypeScript + Webpack)
- 동적 import 사용

### 8.2 렌더링 최적화

- React 메모이제이션 (React.memo, useMemo)
- 이미지 최적화 (next/image)
- 폰트 최적화 (next/font)

### 8.3 데이터 페칭 최적화

- TanStack Query 캐싱
- 요청 스테일타임 최적화
- 백그라운드 리페칭

---

## 9. 개발 경험 (DX)

### 9.1 개발 도구

- **Storybook**: 컴포넌트 개발 및 문서화
- **Vitest**: 빠른 유닛 테스트
- **Prettier**: 자동 코드 포맷팅
- **ESLint**: 코드 품질 관리

### 9.2 개발 워크플로우

1. Feature 브랜치 생성
2. 컴포넌트 개발 (Storybook에서 테스트)
3. 유닛 테스트 작성
4. PR 생성 (pre-commit 훅으로 자동 검사)
5. 코드 리뷰 및 병합

---

## 10. 확장성 및 유지보수성

### 10.1 새로운 기능 추가

1. `src/app/` 내 해당 경로에 폴더 생성
2. 페이지 전용 컴포넌트는 `_components/` 폴더 내에 작성
3. 전역 공유가 필요한 경우 `src/components/`, `src/hooks/` 등을 활용
4. 필요시 전역 Zustand 스토어 생성

### 10.2 컴포넌트 재사용

- **Shared Components**: `src/components/` 내에 위치하며 프로젝트 전반에서 재사용
- **Page-specific Components**: `_components/` 내에 위치하며 해당 경로의 페이지에서만 사용
- **UI Primitives**: shadcn/ui 기반의 기본 컴포넌트 (`src/components/ui/`)

### 10.3 타입 정의

- API 타입은 OpenAPI Generator에서 자동 생성 (필요 시)
- 비즈니스 로직 타입은 각 페이지 폴더 또는 `src/types/`에서 정의
- 전역 타입은 `src/types/`에서 중앙 관리

---

## 11. 배포 및 운영

### 11.1 빌드 최적화

```bash
pnpm build:prod  # 프로덕션 빌드
```

### 11.2 환경 설정

- `.env.production`: 프로덕션 API 엔드포인트
- `.env.development`: 개발 API 엔드포인트
- `.env.local`: 로컬 개발 설정

### 11.3 모니터링

- 에러 추적 (Error boundary 구현 권고)
- 성능 모니터링 (Web Vitals)
- 사용자 분석

---

## 12. 알려진 제약사항 및 개선점

### 현재 상태

- OpenAPI 코드 생성 자동화
- TanStack Query 캐싱 활성화
- Zustand 전역 상태 관리

### 개선 가능 영역

- [ ] Error Boundary 컴포넌트 구현
- [ ] 오프라인 상태 관리 (서비스 워커)
- [ ] 다국어 지원 (i18n)
- [ ] 다크 테마 지원
- [ ] E2E 테스트 자동화 (Playwright)
- [ ] 성능 모니터링 도구 통합

---

## 13. 참고 자료

- [Next.js 문서](https://nextjs.org/docs)
- [React 문서](https://react.dev)
- [TypeScript 문서](https://www.typescriptlang.org)
- [Tailwind CSS 문서](https://tailwindcss.com)
- [shadcn/ui 문서](https://ui.shadcn.com)
- [TanStack Query 문서](https://tanstack.com/query/latest)
- [Zustand 문서](https://github.com/pmndrs/zustand)

---

**마지막 업데이트**: 2025-12-06  
**아키텍처 버전**: 1.0  
**스캔 레벨**: Quick Scan (패턴 기반)
