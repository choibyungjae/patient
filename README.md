# Heuron Backend Assignment

본 프로젝트는 병원에서 근무하는 의사가 환자의 병변 분석을 위해  
환자의 기본 정보와 이미지 파일을 업로드하고 저장, 조회, 삭제할 수 있도록 하는  
REST API 서버를 구현하는 과제입니다.

과제 요구사항에 따라 환자 데이터 저장을 기본 정보 저장과 이미지 업로드의  
**2단계 구조**로 설계하였으며,  
이미지 업로드가 완료된 경우에만 환자 정보 조회가 가능하도록 구현하였습니다.

---

## 사용 기술

- Java 17
- Spring Boot
- Spring Data JPA
- H2 Database
- Gradle
- Lombok

---

## 프로젝트 구조

```text
com.heuron.patient
 ├─ global
 │   └─ config
 │       └─ WebConfig.java
 └─ patient
     ├─ controller
     │   └─ PatientController.java
     ├─ service
     │   └─ PatientService.java
     ├─ repository
     │   └─ PatientRepository.java
     ├─ domain
     │   ├─ Patient.java
     │   └─ PatientStatus.java
     └─ dto
         ├─ request
         │   └─ PatientCreateRequest.java
         └─ response
             └─ PatientResponse.java
```
---

## 구현 내용

### 환자 데이터 저장 구조

환자 데이터 저장은 다음과 같은 2단계로 진행됩니다.

- 환자 기본 정보 저장
- 환자 이미지 업로드

환자 상태를 관리하기 위해 `PatientStatus`를 사용하였습니다.

```text
PENDING   : 환자 기본 정보만 저장된 상태 (이미지 업로드 전)
COMPLETED : 환자 이미지 업로드 완료 상태 (조회 가능)
```
---
### 환자 정보 조회 제한
 - 환자 정보 조회 시 상태가 `COMPLETED`인 경우에만 조회 가능
 - 이미지 업로드 전(`PENDING`) 상태에서 조회 요청 시 409(CONFLICT) 응답 반환

이를 통해
“저장 2단계(이미지 업로드)가 완료된 후에만 조회 가능”
요구사항을 충족하였습니다.
---
### 이미지 저장 및 조회 방식
 - 이미지 파일은 프로젝트 내부 로컬 경로에 저장
 - 데이터베이스에는 이미지 파일명이 저장됨
 - `/images/{filename}` 형태의 URL을 통해 브라우저에서 직접 이미지 조회 가능
```text
storage/images/
 └─ {UUID}.jpg
```
Spring Web MVC의 ResourceHandler를 사용하여
이미지 URL 요청을 로컬 파일 경로와 매핑하였습니다.
---

### 환자 데이터 삭제
 - 환자 데이터 삭제 시 데이터베이스 정보 삭제
 - 연관된 이미지 파일도 함께 삭제
 - 파일 삭제 실패 시 예외 처리하여 데이터 정합성 유지
```text
POST /patients
{
  "name": "홍길동",
  "age": 30,
  "gender": "M",
  "hasDisease": false
}
```
응답으로 생성된 환자 ID를 반환합니다.

---
### 환자 이미지 업로드
```text
POST /patients/{patientId}/image
```
 - Content-Type: multipart/form-data
 - Key: image
 - Value: jpg 또는 png 파일

---
### 환자 기본 정보 조회
```text
GET /patients/{patientId}
```
이미지 업로드가 완료된 환자만 조회 가능합니다.
```text
{
  "name": "홍길동",
  "age": 30,
  "gender": "M",
  "hasDisease": false,
  "imageUrl": "/images/xxxxxxxx.jpg"
}
```
---
### 환자 이미지 조회
```text
GET /images/{filename}
```
브라우저에서 URL 직접 접근 시 이미지가 표시됩니다.

---
### 환자 데이터 삭제
```text
DELETE /patients/{patientId}
```
환자 데이터와 연관된 이미지 파일이 함께 삭제됩니다.

---
### 실행 방법
1. Java 17 설치
2. 프로젝트 실행
```text
./gradlew bootRun
```
3. Postman을 이용하여 API 테스트
---

### 테스트 흐름
1. 환자 기본 정보 저장
2. 환자 이미지 업로드
3, 환자 정보 조회
4. 이미지 URL 브라우저 접근
5. 환자 데이터 삭제
6. 이미지 파일 삭제 확인
---