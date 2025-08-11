# Java Web History 빌드 가이드

## 📋 개요

이 가이드는 Java Web History 프로젝트를 컴파일하고 WAR 파일을 생성한 후 Tomcat에 배포하는 과정을 단계별로 안내합니다.

## 🚀 단계별 빌드 과정

### 1단계: 환경 확인

#### Java 설치 확인

```bash
java -version
javac -version
```

#### Tomcat 실행 상태 확인

- 브라우저에서 `http://localhost:8080` 접속 테스트

---

### 2단계: 컴파일

#### WEB-INF/lib 폴더 생성

```bash
mkdir WEB-INF/lib
```

#### H2 JDBC 드라이버 복사

- `h2-2.1.214.jar` 파일을 `WEB-INF/lib/` 폴더에 복사

**참고**: H2 JDBC 드라이버는 [H2 Database 공식 사이트](http://www.h2database.com/)에서 다운로드 가능합니다.

#### Java 소스 컴파일

```bash
javac -cp "WEB-INF/lib/*" -d "WEB-INF/classes" "src/statement/BoardServlet.java"
javac -cp "WEB-INF/lib/*" -d "WEB-INF/classes" "src/prepared/BoardServlet.java"
javac -cp "WEB-INF/lib/*" -d "WEB-INF/classes" "src/WelcomeServlet.java"
```

**확인사항**: `WEB-INF/classes` 폴더에 `.class` 파일들이 생성되었는지 확인

---

### 3단계: WAR 파일 생성

#### 임시 빌드 디렉토리 생성

```bash
mkdir -p temp_build/WEB-INF/classes temp_build/WEB-INF/lib
```

#### 필요한 파일 복사

- `WEB-INF/classes/` 폴더의 모든 내용을 `temp_build/WEB-INF/classes/`에 복사
- `WEB-INF/lib/` 폴더의 모든 내용을 `temp_build/WEB-INF/lib/`에 복사
- `WEB-INF/web.xml` 파일을 `temp_build/WEB-INF/`에 복사

#### WAR 파일 생성

```bash
jar -cvf java-web-history.war -C temp_build .
```

#### 임시 디렉토리 정리

```bash
# Windows
rmdir /s /q temp_build

# Mac/Linux
rm -rf temp_build
```

**확인사항**: `java-web-history.war` 파일이 생성되었는지 확인

---

### 4단계: Tomcat 배포

#### 방법 1: WAR 파일을 ROOT에 직접 복사

- `java-web-history.war` 파일을 Tomcat의 `webapps/ROOT/` 폴더에 복사

#### 방법 2: WAR 파일 압축 해제 후 내용 복사

- `java-web-history.war` 파일을 압축 해제
- 압축 해제된 내용을 Tomcat의 `webapps/ROOT/` 폴더에 복사

**주의사항**: Tomcat 경로는 실제 설치 경로로 수정해야 합니다.

---

### 5단계: 접속 테스트

#### 접속 URL (ROOT 배포)

- **환영 페이지**: `http://localhost:8080/welcome`
- **게시판 목록**: `http://localhost:8080/board?action/list`
- **글쓰기**: `http://localhost:8080/board?action=write`
- **글 상세보기**: `http://localhost:8080/board?action=view&id=1`

---

## ⚠️ 주의사항

1. **Java 소스 컴파일이 완료되어야 합니다**

   - `WEB-INF/classes` 폴더에 `.class` 파일들이 있어야 함

2. **H2 JDBC 드라이버가 필요한 경우**

   - `WEB-INF/lib` 폴더에 JAR 파일을 복사해야 함

3. **Tomcat 경로 수정**

   - Windows: `C:\apache-tomcat-9.0.xx\` → 실제 설치 경로로 수정
   - Mac/Linux: `/path/to/apache-tomcat-9.0.xx/` → 실제 설치 경로로 수정

4. **ROOT 배포의 장점**
   - 컨텍스트 경로 없이 바로 접근 가능
   - `http://localhost:8080/welcome` 형태로 간단한 URL 사용

---

## 🔧 문제 해결

### 컴파일 오류가 발생하는 경우

- Java 버전 확인 (Java 8 이상 필요)
- `WEB-INF/lib` 폴더에 필요한 JAR 파일이 있는지 확인
- 소스 파일 경로가 올바른지 확인

### 배포 후 접속이 안 되는 경우

- Tomcat이 실행 중인지 확인
- WAR 파일이 올바른 위치에 복사되었는지 확인
- Tomcat 로그에서 오류 메시지 확인

### 데이터베이스 연결 오류

- H2 데이터베이스가 실행 중인지 확인
- `jdbc:h2:./goorm_db` 경로에 데이터베이스 파일이 있는지 확인
