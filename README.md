# AI 책장 관리 앱 (AI-Librarian for Android)

AI 기반 Flask 서버와 연동하여, 사용자가 책장 사진을 통해 자신의 서재를 디지털로 관리할 수 있도록 제작된 안드로이드 애플리케이션입니다.

## 1. 프로젝트 개요

본 애플리케이션은 **AI Librarian** 프로젝트의 클라이언트(Client) 파트로, 사용자가 모바일 환경에서 모든 핵심 기능을 편리하게 사용할 수 있도록 UI/UX를 제공합니다. 사용자는 이 앱을 통해 책장 사진을 서버로 전송하고, AI가 분석한 도서 정보를 확인, 수정, 검색하며, 최종적으로 정리된 도서 목록을 파일로 내보낼 수 있습니다.

- **프로젝트 기간:** 2025.03.01 ~ 2025.06.26
- **관련 레포지토리:** [AI_Librarian_Server (Backend)](https://github.com/JungYoonWu/AI_Librarian)

## 2. 주요 기능 및 화면 흐름

### 주요 기능
- **책장 관리:** 신규 책장 등록, 기존 책장 목록 조회 및 상세 내용 확인
- **도서 정보 관리:** AI 서버가 분석한 책 제목 확인 및 직접 수정/확정
- **보유 도서 검색:** 사용자가 등록한 모든 책을 대상으로 키워드 검색
- **데이터 내보내기:** 책장별 도서 목록을 CSV 파일로 생성 및 서버에 저장

### 화면 흐름 (User Flow)
1.  **메인 화면 (`MainActivity`)**
    -   **기존 사용자:** 앱 실행 시, DB에 저장된 '내 책장 목록'이 자동으로 표시됩니다.
    -   **신규 사용자:** `[새 책장 등록]` 버튼을 통해 책장 생성 프로세스를 시작합니다.
    -   `[보유 도서 검색]` 기능을 통해 등록된 모든 책을 검색할 수 있습니다.

2.  **책장 등록 화면 (`ShelfRegistrationActivity`)**
    -   새 책장의 이름과 물리적인 크기(행/열)를 설정합니다.

3.  **책장 내용 설정 (`MainActivity`)**
    -   설정한 크기에 맞춰 생성된 그리드(Grid)의 각 칸을 클릭하여, 해당 칸의 사진을 갤러리에서 선택하고 업로드합니다.
    -   `[책장 업로드]` 버튼을 누르면 모든 정보가 서버로 전송되어 AI 분석 및 DB 저장이 수행됩니다.

4.  **책 제목 수정 화면 (`CorrectionActivity`)**
    -   서버가 반환한 OCR 결과를 바탕으로, 각 책의 제목을 최종적으로 확인하고 수정/저장할 수 있습니다.
    -   수정 완료 후, 변경사항은 DB에 즉시 업데이트됩니다.

## 3. 시스템 연동 방식 (How it Works)

본 앱은 **Retrofit2** 라이브러리를 사용하여 백엔드 Flask 서버와 RESTful API 방식으로 통신합니다.

- **데이터 요청:** 사용자의 모든 요청(책장 목록 조회, 검색 등)은 HTTP `GET` 방식으로 서버에 전달됩니다.
- **데이터 전송:** 책장 등록과 같이 이미지 파일과 텍스트 데이터가 함께 전송되어야 하는 경우, `Multipart` 방식을 사용하여 HTTP `POST`로 서버에 안전하게 전달합니다.
- **데이터 처리:** 서버로부터 받은 JSON 형식의 응답은 **GSON** 라이브러리를 통해 Java 객체(DTO)로 자동 변환되어 앱의 각 화면에 표시됩니다.
- **이미지 로딩:** 서버 DB에 저장된 이미지 URL은 **Glide** 라이브러리를 통해 효율적으로 불러와 사용자에게 보여줍니다.

## 4. 사용된 기술 스택 (Tech Stack)

- **Language:** Java
- **Platform:** Android SDK
- **Architecture:** 3-Tier Client (연동)
- **Networking:**
    - `Retrofit2`: REST API 통신
    - `OkHttp3`: HTTP Client
- **JSON Parsing:**
    - `GSON`: JSON <-> Java Object 직렬화/역직렬화
- **Image Loading:**
    - `Glide`: 이미지 비동기 로딩 및 캐싱
- **UI Components:**
    - `GridLayout`, `LinearLayout`, `ListView (Custom Adapter)` 등

## 5. 빌드 및 실행 방법 (Build & Run)

1.  본 프로젝트를 Android Studio로 엽니다.
2.  `app/src/main/java/com/example/ai_librarian/ApiClient.java` 파일의 `BASE_URL`이 실행 중인 Flask 서버의 주소와 일치하는지 확인합니다.
    -   **Android Emulator 사용 시:** `private static final String BASE_URL = "http://10.0.2.2:8000/";`
3.  Gradle 빌드가 자동으로 완료되면, 연결된 가상 디바이스(AVD) 또는 실제 기기에서 앱을 실행합니다.

## 6. 주요 화면 스크린샷

- **메인 화면 (책장 목록)**
  
  `[스크린샷: 내 책장 목록이 표시된 기본 화면]`

- **책장 상세 보기**
  
  `[스크린샷: 책장 선택 후, 실제 사진들이 그리드에 표시된 화면]`

- **책 제목 수정**

  `[스크린샷: CorrectionActivity에서 책 제목을 수정하는 화면]`
