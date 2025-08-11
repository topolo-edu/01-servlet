# Java Web History - Statement vs PreparedStatement 보안 비교

## 📋 프로젝트 개요

2000년대 초반 스타일의 Java Servlet 코드를 구현하여 `Statement`와 `PreparedStatement`의 보안 차이를 비교할 수 있는 예제입니다.

## 🏗️ 폴더 구조

```
01-servlet/
├── src/                        # Java 소스 코드
│   ├── statement/              # Statement 사용 (XSS·SQL 인젝션 취약)
│   │   └── BoardServlet.java
│   ├── prepared/               # PreparedStatement 사용 (SQL 인젝션 방어)
│   │   └── BoardServlet.java
│   └── WelcomeServlet.java     # 공통 환영 페이지
├── WEB-INF/
│   └── web.xml                # 서블릿 설정
├── build.md                    # 통합 빌드 가이드
└── README.md                   # 프로젝트 설명서
```

### 📦 배포 후 디렉토리 구조 (Tomcat ROOT)

```
C:\apache-tomcat-9.0.xx\webapps\ROOT\
├── WEB-INF/
│   ├── classes/
│   │   ├── io/
│   │   │   └── goorm/
│   │   │       └── backend/
│   │   │           ├── statement/
│   │   │           │   └── BoardServlet.class
│   │   │           ├── prepared/
│   │   │           │   └── BoardServlet.class
│   │   │           └── WelcomeServlet.class
│   │   ├── lib/
│   │   │   └── (필요한 경우에만 추가 JAR 파일)
│   │   └── web.xml
└── (기타 ROOT 파일들)
```

**참고**:

- `WEB-INF/classes/`에는 컴파일된 `.class` 파일들이 패키지 구조대로 배치됩니다
- `servlet-api.jar`는 Tomcat lib 폴더에 이미 존재하므로 별도로 복사할 필요가 없습니다
- `WEB-INF/lib/`에는 H2 JDBC 드라이버 등 추가로 필요한 JAR 파일만 위치합니다
- Tomcat ROOT에 배포하면 컨텍스트 경로 없이 바로 접근 가능합니다

## 🔒 보안 취약점 비교

### Statement 버전 (보안 취약)

- **SQL 인젝션**: 사용자 입력을 문자열 결합으로 SQL에 삽입
- **XSS**: 사용자 입력을 HTML에 그대로 출력
- **취약 코드 예시**:
  ```java
  String sql = "SELECT * FROM board WHERE id = " + id;
  String sql = "INSERT INTO board VALUES ('" + title + "', '" + content + "')";
  ```

### PreparedStatement 버전 (SQL 인젝션 방어)

- **SQL 인젝션 방어**: `?` 바인딩으로 사용자 입력 분리
- **XSS**: 여전히 취약 (비교를 위해 Statement 버전과 동일하게 유지)
- **보안 코드 예시**:
  ```java
  String sql = "SELECT * FROM board WHERE id = ?";
  pstmt.setString(1, id);
  ```

## 🚀 실행 방법

### 1. 환경 설정

- Java 8 이상 설치
- Apache Tomcat 9.0 이상 설치
- H2 JDBC 드라이버 JAR 파일 준비 (필요한 경우)

### 2. 컴파일 및 배포

```bash
# 1. 가이드 스크립트 실행하여 단계별 안내 확인
# 2. 가이드에 따라 수동으로 명령어 실행
# 3. WAR 파일 생성 후 Tomcat webapps 폴더에 배포
```

### 3. 통합 빌드 가이드

```bash
# build.md 파일을 참고하여 단계별로 진행
# Windows와 Mac/Linux 명령어가 모두 포함되어 있습니다
```

**참고**: `build.md`는 단계별 가이드 문서이며, 사용자가 직접 명령어를 입력해야 합니다.

### 4. 접속 URL

- **환영 페이지**: `http://localhost:8080/welcome`
- **게시판 목록**: `http://localhost:8080/board?action=list`
- **글쓰기**: `http://localhost:8080/board?action=write`
- **글 상세보기**: `http://localhost:8080/board?action=view&id=1`

## 🔧 버전 전환 방법

### Statement 버전 사용

```xml
<servlet>
    <servlet-name>BoardServlet</servlet-name>
    <servlet-class>io.goorm.backend.statement.BoardServlet</servlet-class>
</servlet>
```

### PreparedStatement 버전 사용

```xml
<servlet>
    <servlet-name>BoardServlet</servlet-name>
    <servlet-class>io.goorm.backend.prepared.BoardServlet</servlet-class>
</servlet>
```

## 🧪 보안 테스트 예시

### SQL 인젝션 테스트

**Statement 버전에서 작동하는 악성 입력**:

```
제목: '; DROP TABLE board; --
내용: 아무 내용
작성자: 해커
```

**PreparedStatement 버전에서는 방어됨**:

- `?` 바인딩으로 SQL 구조와 데이터가 분리됨
- 악성 코드가 데이터로만 처리됨

### XSS 테스트

**두 버전 모두에서 작동하는 악성 입력**:

```
제목: <script>alert('XSS!')</script>
내용: <img src="x" onerror="alert('XSS!')">
작성자: <script>alert('XSS!')</script>
```

## 📚 학습 포인트

1. **SQL 인젝션 방어**: PreparedStatement의 `?` 바인딩 사용
2. **XSS 방어**: HTML 이스케이핑 필요 (이 예제에서는 구현하지 않음)
3. **보안 코딩**: 사용자 입력 검증 및 이스케이핑의 중요성
4. **레거시 코드**: 2000년대 초반 스타일의 취약한 코드 패턴

## 🔒 보안 방어 방법 비교

### SQL 인젝션 방어

| 방법                  | 보안 수준 | 설명                              |
| --------------------- | --------- | --------------------------------- |
| **Statement**         | ❌ 취약   | 문자열 결합으로 SQL 생성          |
| **PreparedStatement** | ✅ 안전   | `?` 바인딩으로 데이터 분리        |
| **JdbcTemplate**      | ✅ 안전   | 내부적으로 PreparedStatement 사용 |

### XSS 방어

| 방법                   | 보안 수준            | 설명                         |
| ---------------------- | -------------------- | ---------------------------- |
| **입력 시 이스케이핑** | ✅ 권장              | DB 저장 전 HTML 이스케이핑   |
| **출력 시 이스케이핑** | ⚠️ 가능하지만 비효율 | 매번 출력할 때마다 처리 필요 |
| **이스케이핑 없음**    | ❌ 취약              | 악성 스크립트 실행 가능      |

### 보안 코딩 패턴

```java
// ✅ 권장하는 안전한 방법
// 1. 입력 시 HTML 이스케이핑
String title = HtmlUtils.htmlEscape(request.getParameter("title"));

// 2. PreparedStatement로 SQL 인젝션 방어
jdbcTemplate.update("INSERT INTO board (title) VALUES (?)", title);

// 3. 출력 시 추가 처리 불필요 (이미 안전함)
out.println("<h2>" + board.getTitle() + "</h2>");
```

**핵심 원칙**: "입력은 검증하고 이스케이핑하고, 출력은 안전하게"

## ⚠️ 주의사항

- 이 예제는 **교육 목적**으로만 사용하세요
- 실제 프로덕션 환경에서는 절대 사용하지 마세요
- 보안 취약점이 의도적으로 포함되어 있습니다

## 🔗 관련 링크

- [H2 Database](http://www.h2database.com/)
- [Apache Tomcat](https://tomcat.apache.org/)
- [Java Servlet API](https://docs.oracle.com/javaee/7/api/javax/servlet/package-summary.html)
- [OWASP SQL Injection](https://owasp.org/www-community/attacks/SQL_Injection)
- [OWASP XSS](https://owasp.org/www-community/attacks/xss/)
