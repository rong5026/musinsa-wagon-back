# 개발 가이드: Musinsa Wagon

## 목차

1. [사전 요구사항](#사전-요구사항)
2. [환경 설정](#환경-설정)
3. [로컬 개발](#로컬-개발)
4. [빌드 프로세스](#빌드-프로세스)
5. [테스팅](#테스팅)
6. [배포](#배포)
7. [코드 컨벤션](#코드-컨벤션)
8. [일반적인 작업](#일반적인-작업)
9. [문제 해결](#문제-해결)

---

## 사전 요구사항

### 필수 요구사항

- **Node.js**: v18.17 이상
- **pnpm**: v10.8.0 이상
- **Git**: 최신 버전

### 선택 요구사항

- **Docker**: 컨테이너 기반 배포 (선택)
- **Vercel CLI**: Vercel 배포 (선택)

### 필요한 계정

- **GitHub**: 코드 저장소 접근
- **API 서버**: 개발/프로덕션 API 엔드포인트

---

## 환경 설정

### 1. 저장소 클론

```bash
git clone https://github.com/your-org/musinsa-wagon.git
cd musinsa-wagon-nextjs-react-ts-v2
```

### 2. 의존성 설치

```bash
# pnpm 설치 (Node.js에 포함되지 않은 경우)
npm install -g pnpm@10.8.0

# 프로젝트 의존성 설치
pnpm install
```

### 3. 환경 변수 설정

#### 개발 환경 (`.env.development`)

```
NEXT_PUBLIC_API_URL=https://devapi.ilsang.co.kr
OPENAPI_SPEC_URL=https://devapi.ilsang.co.kr/api-docs
```

#### 프로덕션 환경 (`.env.production`)

```
NEXT_PUBLIC_API_URL=https://api.ilsang.co.kr
OPENAPI_SPEC_URL=https://api.ilsang.co.kr/api-docs
```

#### 로컬 개발 (`.env.local`)

```
NEXT_PUBLIC_API_URL=http://localhost:8000
NEXT_PUBLIC_DEBUG=true
```

### 4. Git 훅 설정

```bash
pnpm prepare  # Husky 설정 (필요시)
```

---

## 로컬 개발

### 개발 서버 시작

#### 방법 1: API 코드 생성 포함 (권장)

```bash
pnpm dev                # .env.development
pnpm prod               # .env.production
pnpm local              # .env.local
```

#### 방법 2: 빠른 시작 (API 변경 없을 때)

```bash
pnpm dev-without-codegen
pnpm prod-without-codegen
pnpm local-without-codegen
```

### 개발 서버 접근

```
http://localhost:3000    # 애플리케이션
http://localhost:6006    # Storybook (별도 터미널)
```

---

## 빌드 프로세스

```bash
pnpm build:dev      # 개발 환경 빌드
pnpm build:prod     # 프로덕션 빌드
pnpm build:local    # 로컬 빌드
```

---

## 테스팅

### 유닛 테스트 (Vitest)

```bash
pnpm test                    # 전체 테스트
pnpm test --watch           # Watch 모드
pnpm test --coverage        # 커버리지
```

### 컴포넌트 개발 (Storybook)

```bash
pnpm storybook              # Storybook 시작
pnpm build-storybook        # Storybook 빌드
```

### E2E 테스트 (Playwright - 구현 필요)

```bash
pnpm playwright test         # E2E 테스트 실행
pnpm playwright test --ui    # UI 모드
```

---

## 코드 품질

### 린팅

```bash
pnpm lint           # ESLint 검사
pnpm fix:lint       # 자동 수정
```

### 포맷팅

```bash
pnpm prettier       # 검사
pnpm fix:prettier   # 자동 수정
```

### 타입 검사

```bash
pnpm type-check     # TypeScript 검사
```

---

## 배포

### Vercel 배포

```bash
npm install -g vercel
vercel login
vercel link                              # 프로젝트 연결
vercel --env-file .env.production        # 프로덕션 배포
vercel --prod --env-file .env.production # 프로덕션 환경으로 배포
```

### Docker 배포

```bash
docker build -t musinsa-wagon:latest .
docker run -p 3000:3000 \
  -e NEXT_PUBLIC_API_URL=https://api.ilsang.co.kr \
  musinsa-wagon:latest
```

---

## 코드 컨벤션

### 파일 구조

```typescript
/**
 * 파일 요약: [파일의 목적]
 * 주요 내보내기: [내보내는 항목들]
 * 의존성: [외부 라이브러리]
 */
import { ReactNode } from 'react'

import { useRouter } from 'next/navigation'

import { useMediaQuery } from '@/hooks'

import { Button } from '@/components/ui'
```

### 컴포넌트 작성

```typescript
/**
 * 컴포넌트 요약: 상품 카드 표시
 * Props: title, price, image, onClick
 * 사용법: <ProductCard title="..." price={...} />
 */

export function ProductCard({ title, price, image, onClick }: ProductCardProps) {
  return (
    <div className="p-4 border rounded-lg" onClick={onClick}>
      <img src={image} alt={title} />
      <h3 className="font-bold">{title}</h3>
      <p className="text-gray-600">{price.toLocaleString()}원</p>
    </div>
  );
}
```

### 훅 작성

```typescript
/**
 * 훅 요약: 상품 데이터 페칭
 * 반환값: { products, isLoading, error }
 * 사용법: const { products } = useProducts();
 */

export function useProducts() {
  const { data, isLoading, error } = useProductsQuery()
  return { products: data || [], isLoading, error }
}
```

### 네이밍 규칙

| 항목         | 규칙              | 예시              |
| ------------ | ----------------- | ----------------- |
| **컴포넌트** | PascalCase        | ProductCard.tsx   |
| **훅**       | camelCase + use   | useProducts.ts    |
| **스토어**   | camelCase + Store | useAuthStore.ts   |
| **유틸**     | camelCase         | formatCurrency.ts |
| **상수**     | UPPER_SNAKE_CASE  | API_ENDPOINTS.ts  |
| **타입**     | PascalCase        | Product.ts        |

---

## 일반적인 작업

### 새로운 페이지 및 기능 추가 (Colocation 패턴)

Next.js App Router의 Colocation 패턴을 사용하여 관련 코드가 한곳에 위치하도록 합니다.

```bash
mkdir -p src/app/products/_components
# src/app/products/page.tsx (메인 페이지)
# src/app/products/_components/ProductList.tsx (해당 페이지 전용 컴포넌트)
```

### 새로운 공유 컴포넌트 추가

```bash
# src/components/ui/ (shadcn/ui 기반)
# src/components/layout/ (Header, Footer 등)
```

---

## 문제 해결

### 포트 이미 사용 중

```bash
lsof -i :3000          # 프로세스 확인
kill -9 <PID>          # 프로세스 종료
pnpm dev -- -p 3001    # 다른 포트 사용
```

### API 연결 오류

```bash
echo $NEXT_PUBLIC_API_URL    # 환경 변수 확인
curl https://devapi.ilsang.co.kr/api-docs  # API 상태 확인
```

### 모듈 resolve 오류

```bash
rm -rf node_modules pnpm-lock.yaml
pnpm install
rm -rf .next
pnpm dev
```

### TypeScript 에러

```bash
pnpm type-check         # 타입 검사
pnpm type-check 2>&1    # 에러 상세 확인
```

---

## 성능 팁

### 개발 속도 향상

```bash
# API 변경 없을 때 빠른 시작
pnpm dev-without-codegen

# 필요한 것만 lint
pnpm lint -- --fix src/components/Button.tsx
```

### 번들 크기 최적화

```bash
# 동적 import 사용
const MyComponent = dynamic(() => import('@/components/MyComponent'));
```

---

## 리소스

- [Next.js 문서](https://nextjs.org/docs)
- [React 문서](https://react.dev)
- [TypeScript 문서](https://www.typescriptlang.org)
- [Tailwind CSS 문서](https://tailwindcss.com)
- [shadcn/ui 문서](https://ui.shadcn.com)
- [TanStack Query 문서](https://tanstack.com/query/latest)
- [Zustand 문서](https://github.com/pmndrs/zustand)

---

**마지막 업데이트**: 2025-12-06
**개발 환경**: macOS/Linux/Windows
**Node 버전**: 18.17+
**pnpm 버전**: 10.8.0+
