package io.goorm.backend.prepared;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * PreparedStatement를 사용한 게시판 서블릿 (SQL 인젝션 방어)
 * 2000년대 초반 스타일의 코드로 보안 강화 버전
 */
public class BoardServlet extends HttpServlet {

  // DB 연결 정보
  private static final String DB_URL = "jdbc:h2:file:D:/devEnv/h2/data/goorm_db;AUTO_SERVER=TRUE";
  private static final String DB_USER = "sa";
  private static final String DB_PASSWORD = "";

  @Override
  public void init() throws ServletException {
    try {
      Class.forName("org.h2.Driver"); // H2 드라이버 등록
    } catch (ClassNotFoundException e) {
      throw new UnavailableException("H2 Driver not found in WEB-INF/lib");
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    response.setContentType("text/html;charset=UTF-8");
    PrintWriter out = response.getWriter();

    String action = request.getParameter("action");
    if (action == null) {
      action = "list";
    }

    // HTML 헤더 출력
    printHeader(out);

    try {
      switch (action) {
        case "list":
          showBoardList(out);
          break;
        case "write":
          showWriteForm(out);
          break;
        case "view":
          showBoardDetail(out, request);
          break;
        default:
          showBoardList(out);
      }
    } catch (Exception e) {
      out.println("<h2>오류 발생</h2>");
      out.println("<p style='color: red;'>" + e.getMessage() + "</p>");
      e.printStackTrace(out);
    }

    // HTML 푸터 출력
    printFooter(out);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    response.setContentType("text/html;charset=UTF-8");
    PrintWriter out = response.getWriter();

    // HTML 헤더 출력
    printHeader(out);

    try {
      // 게시글 저장
      saveBoard(request);
      out.println("<h2>게시글이 저장되었습니다!</h2>");
      out.println("<p><a href='board?action=list'>목록으로 돌아가기</a></p>");
    } catch (Exception e) {
      out.println("<h2>저장 중 오류 발생</h2>");
      out.println("<p style='color: red;'>" + e.getMessage() + "</p>");
    }

    // HTML 푸터 출력
    printFooter(out);
  }

  /**
   * 게시글 목록 출력
   */
  private void showBoardList(PrintWriter out) throws Exception {
    out.println("<h2>게시판 목록</h2>");
    out.println("<p><a href='board?action=write'>글쓰기</a></p>");

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

      // PreparedStatement 사용 - SQL 인젝션 방어
      String sql = "SELECT id, title, author, created_at FROM board ORDER BY created_at DESC";
      pstmt = conn.prepareStatement(sql);
      rs = pstmt.executeQuery();

      out.println("<table border='1' style='width: 100%; border-collapse: collapse;'>");
      out.println("<tr style='background-color: #f0f0f0;'>");
      out.println("<th>번호</th><th>제목</th><th>작성자</th><th>작성일</th>");
      out.println("</tr>");

      while (rs.next()) {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String author = rs.getString("author");
        String createdAt = rs.getString("created_at");

        out.println("<tr>");
        out.println("<td>" + id + "</td>");
        // XSS 취약 - 사용자 입력을 그대로 출력 (Statement 버전과 동일하게 유지)
        out.println("<td><a href='board?action=view&id=" + id + "'>" + title + "</a></td>");
        out.println("<td>" + author + "</td>");
        out.println("<td>" + createdAt + "</td>");
        out.println("</tr>");
      }

      out.println("</table>");

    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (pstmt != null)
        try {
          pstmt.close();
        } catch (SQLException e) {
        }
      if (conn != null)
        try {
          conn.close();
        } catch (SQLException e) {
        }
    }
  }

  /**
   * 글쓰기 폼 출력
   */
  private void showWriteForm(PrintWriter out) {
    out.println("<h2>글쓰기</h2>");
    out.println("<form method='post' action='board'>");
    out.println("<table style='width: 100%;'>");
    out.println("<tr><td>제목:</td><td><input type='text' name='title' size='50' required></td></tr>");
    out.println("<tr><td>작성자:</td><td><input type='text' name='author' size='20' required></td></tr>");
    out.println("<tr><td>내용:</td><td><textarea name='content' rows='10' cols='50' required></textarea></td></tr>");
    out.println("<tr><td colspan='2'><input type='submit' value='저장' style='margin-top: 10px;'></td></tr>");
    out.println("</table>");
    out.println("</form>");
    out.println("<p><a href='board?action=list'>목록으로 돌아가기</a></p>");
  }

  /**
   * 게시글 상세보기
   */
  private void showBoardDetail(PrintWriter out, HttpServletRequest request) throws Exception {
    String id = request.getParameter("id");

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

      // PreparedStatement 사용 - SQL 인젝션 방어
      String sql = "SELECT * FROM board WHERE id = ?";
      pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, id);
      rs = pstmt.executeQuery();

      if (rs.next()) {
        String title = rs.getString("title");
        String content = rs.getString("content");
        String author = rs.getString("author");
        String createdAt = rs.getString("created_at");

        out.println("<h2>" + title + "</h2>");
        out.println("<table style='width: 100%;'>");
        out.println("<tr><td><strong>작성자:</strong></td><td>" + author + "</td></tr>");
        out.println("<tr><td><strong>작성일:</strong></td><td>" + createdAt + "</td></tr>");
        out.println("<tr><td><strong>내용:</strong></td><td>" + content + "</td></tr>");
        out.println("</table>");
      } else {
        out.println("<h2>게시글을 찾을 수 없습니다.</h2>");
      }

    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (pstmt != null)
        try {
          pstmt.close();
        } catch (SQLException e) {
        }
      if (conn != null)
        try {
          conn.close();
        } catch (SQLException e) {
        }
    }

    out.println("<p><a href='board?action=list'>목록으로 돌아가기</a></p>");
  }

  /**
   * 게시글 저장
   */
  private void saveBoard(HttpServletRequest request) throws Exception {
    String title = request.getParameter("title");
    String content = request.getParameter("content");
    String author = request.getParameter("author");

    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

      // PreparedStatement 사용 - SQL 인젝션 방어
      String sql = "INSERT INTO board (title, content, author, created_at) VALUES (?, ?, ?, NOW())";
      pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, title);
      pstmt.setString(2, content);
      pstmt.setString(3, author);
      pstmt.executeUpdate();

    } finally {
      if (pstmt != null)
        try {
          pstmt.close();
        } catch (SQLException e) {
        }
      if (conn != null)
        try {
          conn.close();
        } catch (SQLException e) {
        }
    }
  }

  /**
   * HTML 헤더 출력
   */
  private void printHeader(PrintWriter out) {
    out.println("<!DOCTYPE html>");
    out.println("<html>");
    out.println("<head>");
    out.println("<meta charset='UTF-8'>");
    out.println("<title>게시판 - PreparedStatement 버전</title>");
    out.println("<style>");
    out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
    out.println("table { border-collapse: collapse; width: 100%; }");
    out.println("th, td { padding: 8px; text-align: left; border: 1px solid #ddd; }");
    out.println("th { background-color: #f2f2f2; }");
    out.println("input[type='text'], textarea { width: 100%; padding: 5px; }");
    out.println(
        "input[type='submit'] { padding: 8px 16px; background-color: #4CAF50; color: white; border: none; cursor: pointer; }");
    out.println("input[type='submit']:hover { background-color: #45a049; }");
    out.println("a { color: #0066cc; text-decoration: none; }");
    out.println("a:hover { text-decoration: underline; }");
    out.println("</style>");
    out.println("</head>");
    out.println("<body>");
    out.println("<h1 style='color: #0066cc;'>🔒 PreparedStatement 버전 (SQL 인젝션 방어)</h1>");
    out.println("<p style='color: #666;'>이 버전은 SQL 인젝션을 방어합니다. (XSS는 여전히 취약)</p>");
  }

  /**
   * HTML 푸터 출력
   */
  private void printFooter(PrintWriter out) {
    out.println("<hr style='margin: 20px 0;'>");
    out.println("<p><a href='welcome'>홈으로</a> | <a href='board?action=list'>게시판</a></p>");
    out.println("</body>");
    out.println("</html>");
  }
}
