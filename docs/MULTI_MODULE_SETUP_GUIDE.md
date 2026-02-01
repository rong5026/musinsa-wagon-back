# Spring Boot Multi-Module Project Setup Guide

이 문서는 Spring Boot 멀티모듈 프로젝트의 초기 세팅 순서와 구조를 설명합니다.

---

## 📋 목차

1. [프로젝트 구조 개요](#1-프로젝트-구조-개요)
2. [세팅 순서](#2-세팅-순서)
3. [Gradle 설정 상세](#3-gradle-설정-상세)
4. [모듈별 역할 및 의존성](#4-모듈별-역할-및-의존성)
5. [Application 클래스 설정](#5-application-클래스-설정)
6. [Profile 및 설정 파일 구성](#6-profile-및-설정-파일-구성)
7. [공통 의존성 관리](#7-공통-의존성-관리)
8. [체크리스트](#8-체크리스트)

---

## 1. 프로젝트 구조 개요

```
project-root/
├── build.gradle              # 루트 빌드 설정 (공통 의존성, 플러그인)
├── settings.gradle           # 모듈 포함 설정
├── core/                     # 공통 모듈 (Entity, Repository, Infra)
│   ├── build.gradle
│   └── src/main/java/
├── fo/                       # Front Office (고객용 API)
│   ├── build.gradle
│   └── src/main/java/
└── batch/                    # 배치 작업 모듈
    ├── build.gradle
    └── src/main/java/
```

### 모듈 의존성 다이어그램

```
                    ┌─────────┐
                    │  core   │  ← 공통 Entity, Repository, Infra
                    └────┬────┘
                         │
              ┌──────────┴──────────┐
              │                     │
         ┌────┴────┐          ┌────┴────┐
         │   fo    │          │  batch  │
         │(고객API)│          │(배치)   │
         └─────────┘          └─────────┘
```

---

## 2. 세팅 순서

### Step 1: 루트 프로젝트 생성

```bash
mkdir my-multimodule-project
cd my-multimodule-project
gradle init --type basic
```

### Step 2: settings.gradle 설정

```groovy
rootProject.name = 'my-multimodule-project'

include 'core'
include 'fo'
include 'batch'
```

### Step 3: 루트 build.gradle 작성

버전 관리 및 공통 설정을 정의합니다.

### Step 4: 각 모듈 디렉토리 생성

```bash
mkdir -p core/src/main/java
mkdir -p core/src/main/resources
mkdir -p fo/src/main/java
mkdir -p fo/src/main/resources
mkdir -p batch/src/main/java
mkdir -p batch/src/main/resources
```

### Step 5: 각 모듈의 build.gradle 작성

### Step 6: Application 클래스 작성

### Step 7: application.yml 설정

---

## 3. Gradle 설정 상세

### 3.1 루트 build.gradle (전체 예시)

```groovy
buildscript {
    ext {
        springBootVersion = '3.0.6'
        querydslPluginVersion = '1.0.10'
        querydslVersion = '5.0.0'
        lombokVersion = '1.18.24'
        querydslSrcDir = 'src/main/querydsl'
        swaggerVersion = '2.0.2'
    }
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
        classpath "io.spring.gradle:dependency-management-plugin:1.0.11.RELEASE"
    }
}

plugins {
    id "com.ewerk.gradle.plugins.querydsl" version "1.0.10"
    id 'com.epages.restdocs-api-spec' version '0.16.0'
    id 'com.diffplug.spotless' version '6.25.0'
}

ext {
    projectGroup = 'com.example.myproject'
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
    querydsl.extendsFrom compileClasspath
}

// =============================================
// 모든 서브프로젝트에 적용되는 공통 설정
// =============================================
subprojects {
    apply plugin: 'java-library'
    apply plugin: 'java'
    apply plugin: 'idea'
    apply plugin: 'org.springframework.boot'
    apply plugin: 'io.spring.dependency-management'
    apply plugin: 'com.diffplug.spotless'

    group = 'com.example.myproject'
    sourceCompatibility = 17
    targetCompatibility = 17

    compileJava.options.encoding = 'UTF-8'

    repositories {
        mavenCentral()
    }

    // 모든 모듈에서 사용하는 공통 의존성
    dependencies {
        implementation 'org.jetbrains:annotations:23.0.0'

        // Spring Boot Starter
        testImplementation('org.springframework.boot:spring-boot-starter-test')

        // Lombok
        implementation('org.projectlombok:lombok')
        compileOnly('org.projectlombok:lombok:1.18.24')
        annotationProcessor('org.projectlombok:lombok:1.18.24')
        annotationProcessor "org.springframework.boot:spring-boot-configuration-processor"

        // DB
        runtimeOnly('com.mysql:mysql-connector-j')
        implementation('org.springframework.boot:spring-boot-starter-jdbc')
        implementation('org.springframework.boot:spring-boot-starter-data-jpa')

        // Web
        implementation('org.springframework.boot:spring-boot-starter-web')
        implementation('org.springframework.boot:spring-boot-starter')

        // Validation
        implementation('org.springframework.boot:spring-boot-starter-validation')

        // JWT
        implementation(group: 'io.jsonwebtoken', name: 'jjwt', version: '0.11.5')
        implementation('io.jsonwebtoken:jjwt:0.9.1')

        // Swagger
        implementation('org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2')

        // dotenv
        implementation "me.paulschwarz:spring-dotenv:3.0.0"

        // Flyway
        implementation 'org.flywaydb:flyway-mysql'
        implementation 'org.flywaydb:flyway-core'

        // JUnit 5
        testImplementation 'org.junit.jupiter:junit-jupiter:5.8.1'
    }

    // Spotless 코드 포맷팅 설정
    spotless {
        java {
            target("**/*.java")
            targetExclude("**/generated/**/*.java")
            googleJavaFormat().aosp().skipJavadocFormatting()
            importOrder()
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
        }
    }

    test {
        useJUnitPlatform()
        testLogging {
            events "passed", "skipped", "failed"
            showCauses = true
            showExceptions = true
            showStackTraces = true
            exceptionFormat = 'full'
        }
    }
}

// =============================================
// 모듈별 개별 설정
// =============================================

// CORE 모듈 - 라이브러리 모듈 (bootJar 비활성화)
project(':core') {
    bootJar.enabled = false
    jar.enabled = true

    // QueryDSL 설정
    def generated = 'src/main/generated'

    tasks.withType(JavaCompile) {
        options.getGeneratedSourceOutputDirectory().set(file(generated))
    }

    sourceSets {
        main.java.srcDirs += [generated]
    }

    clean {
        delete file(generated)
    }
}

// FO 모듈 - 실행 가능한 애플리케이션
project(':fo') {
    dependencies {
        api project(':core')
        api group: 'org.springframework.retry', name: 'spring-retry', version: '1.2.5.RELEASE'
    }

    bootJar {
        manifest {
            attributes 'Start-Class': 'com.example.myproject.fo.FOApplication'
        }
    }
}

// BATCH 모듈 - 실행 가능한 애플리케이션
project(':batch') {
    dependencies {
        api project(':core')
        api group: 'org.springframework.retry', name: 'spring-retry', version: '1.2.5.RELEASE'
    }

    bootJar {
        manifest {
            attributes 'Start-Class': 'com.example.myproject.batch.BatchApplication'
        }
    }
}
```

### 3.2 핵심 설정 포인트

| 설정 | 설명 |
|------|------|
| `bootJar.enabled = false` | 라이브러리 모듈(core)은 실행 JAR가 필요 없음 |
| `jar.enabled = true` | 일반 JAR로 패키징 |
| `api project(':core')` | 다른 모듈에서 core의 의존성을 전이적으로 사용 |

---

## 4. 모듈별 역할 및 의존성

### 4.1 Core 모듈

**역할**: 공통 Entity, Repository, 설정, 인프라 코드

```groovy
// core/build.gradle
dependencies {
    // Lombok
    implementation('org.projectlombok:lombok')
    compileOnly('org.projectlombok:lombok:1.18.24')
    annotationProcessor('org.projectlombok:lombok:1.18.24')

    // JPA & Envers (히스토리 관리)
    implementation('org.springframework.boot:spring-boot-starter-data-jpa')
    implementation 'org.springframework.data:spring-data-envers'

    // QueryDSL
    implementation "com.querydsl:querydsl-jpa:5.0.0:jakarta"
    implementation "com.querydsl:querydsl-sql:5.0.0"
    implementation "com.querydsl:querydsl-sql-spring:5.0.0"
    annotationProcessor "com.querydsl:querydsl-apt:5.0.0:jakarta"
    annotationProcessor "jakarta.annotation:jakarta.annotation-api"
    annotationProcessor "jakarta.persistence:jakarta.persistence-api"
}
```

**디렉토리 구조**:
```
core/src/main/java/com/example/myproject/core/
├── aop/               # AOP 관련
├── config/            # 설정 클래스 (JPA, QueryDSL, DataSource 등)
├── db/                # DB 라우팅 (Read Replica 등)
├── entity/            # JPA Entity
│   ├── user/
│   ├── product/
│   ├── order/
│   └── ...
├── repository/        # JPA Repository
├── infra/             # 외부 서비스 연동 (S3, FCM, Slack 등)
└── utils/             # 유틸리티 클래스
```

### 4.2 FO 모듈 (Front Office)

**역할**: 고객용 API 제공

```groovy
// fo/build.gradle
dependencies {
    // Spring Boot Starter
    testImplementation('org.springframework.boot:spring-boot-starter-test')

    // Lombok
    implementation('org.projectlombok:lombok')
    annotationProcessor('org.projectlombok:lombok:1.18.24')

    // Web
    implementation('org.springframework.boot:spring-boot-starter-web')
    implementation('org.springframework.boot:spring-boot-starter')

    // Validation
    implementation('org.springframework.boot:spring-boot-starter-validation')

    // JWT (추가 라이브러리)
    implementation(group: 'com.nimbusds', name: 'nimbus-jose-jwt', version: '7.8.1')

    // Swagger
    implementation('org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2')

    // Sentry
    implementation 'io.sentry:sentry-spring-boot-starter-jakarta:6.25.0'

    // Test
    testImplementation 'com.github.javafaker:javafaker:1.0.2'
}
```

**디렉토리 구조**:
```
fo/src/main/java/com/example/myproject/fo/
├── FOApplication.java          # 메인 Application 클래스
├── common/
│   ├── config/                 # FO 전용 설정
│   ├── exceptions/             # 예외 처리
│   ├── response/               # 응답 DTO
│   └── Constant.java
├── converter/                  # 타입 컨버터
└── domains/
    ├── user/
    │   ├── UserController.java
    │   ├── UserService.java
    │   └── model/
    │       ├── request/
    │       └── response/
    ├── product/
    ├── order/
    └── ...
```

### 4.3 Batch 모듈

**역할**: 스케줄링 및 배치 작업

```groovy
// batch/build.gradle
dependencies {
    // Batch
    implementation('org.springframework.boot:spring-boot-starter-batch')
    testImplementation('org.springframework.batch:spring-batch-test')

    // QueryDSL
    implementation "com.querydsl:querydsl-jpa:5.0.0:jakarta"
    annotationProcessor "com.querydsl:querydsl-apt:5.0.0:jakarta"
    annotationProcessor "jakarta.annotation:jakarta.annotation-api"
    annotationProcessor "jakarta.persistence:jakarta.persistence-api"

    // Quartz (스케줄러)
    implementation 'org.springframework.boot:spring-boot-starter-quartz'

    // SQL 로그 (개발용)
    implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0'
}
```

---

## 5. Application 클래스 설정

### 5.1 FO Application

```java
package com.example.myproject.fo;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example.myproject")
@EnableJpaRepositories(basePackages = "com.example.myproject.core.repository")
@EntityScan(basePackages = "com.example.myproject.core")
public class FOApplication {

    public static void main(String[] args) {
        SpringApplication.run(FOApplication.class, args);
    }

    @PostConstruct
    void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
```

### 5.2 핵심 어노테이션 설명

| 어노테이션 | 설명 |
|-----------|------|
| `@SpringBootApplication(scanBasePackages = "...")` | 컴포넌트 스캔 범위를 전체 프로젝트로 확장 |
| `@EnableJpaRepositories(basePackages = "...")` | JPA Repository 위치 명시 (core 모듈) |
| `@EntityScan(basePackages = "...")` | Entity 위치 명시 (core 모듈) |

---

## 6. Profile 및 설정 파일 구성

### 6.1 설정 파일 구조

```
core/src/main/resources/
├── application-core.yml      # Core 모듈 전용 설정 (DB, 외부 서비스)
└── application-test.yml      # 테스트 환경

fo/src/main/resources/
└── application.yml           # FO 전용 설정

batch/src/main/resources/
└── application.yml           # Batch 전용 설정
```

### 6.2 Profile 그룹 설정

```yaml
# fo/src/main/resources/application.yml
spring:
  profiles:
    group:
      dev: core, common, dev
      prod: core, common, prod
      test: test
```

### 6.3 Profile별 설정 분리

```yaml
# application.yml
---
server:
  port: 8080

spring:
  config:
    activate:
      on-profile: "common"
  application:
    name: my-application

# 공통 설정들...

---
spring:
  config:
    activate:
      on-profile: "prod"

# 운영 환경 설정...

---
spring:
  config:
    activate:
      on-profile: "dev"

# 개발 환경 설정...
```

### 6.4 환경 변수 사용

```yaml
spring:
  datasource:
    hikari:
      jdbc-url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:test}
      username: ${DB_USERNAME:test}
      password: ${DB_PASSWORD:example}

s3:
  secret-key: ${S3_SECRET_KEY:}
  access-key: ${S3_ACCESS_KEY:}
  bucket: ${S3_BUCKET:dev-bucket}
```

---

## 7. 공통 의존성 관리

### 7.1 버전 관리 전략

루트 `build.gradle`의 `ext` 블록에서 버전을 중앙 관리:

```groovy
buildscript {
    ext {
        springBootVersion = '3.0.6'
        querydslVersion = '5.0.0'
        lombokVersion = '1.18.24'
        swaggerVersion = '2.0.2'
    }
}
```

### 7.2 의존성 분류

| 위치 | 의존성 종류 |
|------|------------|
| 루트 `subprojects` | 모든 모듈이 사용하는 공통 의존성 |
| `project(':core')` | 공통 인프라 의존성 (S3, FCM, QueryDSL 등) |
| `project(':fo')` | FO 전용 의존성 |
| `project(':batch')` | Batch 전용 의존성 |
| 각 모듈 `build.gradle` | 해당 모듈에서만 사용하는 의존성 |

---

## 8. 체크리스트

### 초기 세팅 체크리스트

- [ ] **settings.gradle**
  - [ ] rootProject.name 설정
  - [ ] 모든 모듈 include

- [ ] **루트 build.gradle**
  - [ ] 버전 변수 정의 (ext 블록)
  - [ ] 플러그인 설정
  - [ ] subprojects 공통 설정
  - [ ] 각 프로젝트별 의존성 및 bootJar 설정

- [ ] **Core 모듈**
  - [ ] bootJar.enabled = false
  - [ ] jar.enabled = true
  - [ ] QueryDSL 설정 (generated 디렉토리)
  - [ ] Entity, Repository 패키지 구조

- [ ] **FO/Batch 모듈**
  - [ ] api project(':core') 의존성
  - [ ] bootJar manifest 설정 (Start-Class)
  - [ ] Application 클래스 작성
    - [ ] @SpringBootApplication(scanBasePackages)
    - [ ] @EnableJpaRepositories(basePackages)
    - [ ] @EntityScan(basePackages)

- [ ] **설정 파일**
  - [ ] application.yml 작성
  - [ ] Profile 그룹 설정
  - [ ] 환경 변수 플레이스홀더

### 빌드 검증

```bash
# 전체 빌드
./gradlew clean build -x test

# 특정 모듈 빌드
./gradlew :fo:build -x test

# 실행
./gradlew :fo:bootRun
./gradlew :batch:bootRun
```

---

## 부록: 자주 발생하는 문제

### A. Entity/Repository를 찾지 못하는 경우

**원인**: `@EntityScan`, `@EnableJpaRepositories` 누락

**해결**:
```java
@SpringBootApplication(scanBasePackages = "com.example.myproject")
@EnableJpaRepositories(basePackages = "com.example.myproject.core.repository")
@EntityScan(basePackages = "com.example.myproject.core")
public class FOApplication { }
```

### B. 순환 의존성 오류

**원인**: 모듈 간 양방향 의존성

**해결**: 의존성 방향을 단방향으로 유지
- core ← fo/bo/batch (O)
- core ↔ fo (X)

### C. QueryDSL Q클래스 생성 안됨

**원인**: generated 디렉토리 설정 누락

**해결**: core 모듈의 build.gradle에 QueryDSL 설정 추가
```groovy
def generated = 'src/main/generated'

tasks.withType(JavaCompile) {
  options.getGeneratedSourceOutputDirectory().set(file(generated))
}

sourceSets {
  main.java.srcDirs += [generated]
}

clean {
  delete file(generated)
}
```

---

## 변경 이력

| 버전 | 날짜 | 내용 |
|-----|------|------|
| 1.0 | 2026-01-22 | 최초 작성 |
| 1.1 | 2026-01-22 | PG 모듈 제외 |