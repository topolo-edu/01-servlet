package io.goorm.backend;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * 공통 환영 페이지 서블릿
 * 2000년대 초반 스타일의 간단한 환영 페이지
 */
public class WelcomeServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    response.setContentType("text/html;charset=UTF-8");
    PrintWriter out = response.getWriter();

    // 현재 시간 포맷팅
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss");
    String currentTime = sdf.format(new Date());

    out.println("<!DOCTYPE html>");
    out.println("<html>");
    out.println("<head>");
    out.println("<meta charset='UTF-8'>");
    out.println("<title>Java Web History - Statement vs PreparedStatement</title>");
    out.println("<style>");
    out.println("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }");
    out.println(
        ".container { max-width: 800px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
    out.println("h1 { color: #333; text-align: center; border-bottom: 3px solid #0066cc; padding-bottom: 10px; }");
    out.println(".time { text-align: center; color: #666; font-size: 18px; margin: 20px 0; }");
    out.println(".menu { margin: 30px 0; }");
    out.println(".menu h2 { color: #0066cc; border-left: 5px solid #0066cc; padding-left: 15px; }");
    out.println(
        ".menu-item { margin: 15px 0; padding: 15px; border: 1px solid #ddd; border-radius: 5px; background-color: #f9f9f9; }");
    out.println(".menu-item h3 { margin: 0 0 10px 0; color: #333; }");
    out.println(".menu-item p { margin: 5px 0; color: #666; }");
    out.println(
        ".menu-item a { display: inline-block; padding: 8px 16px; background-color: #0066cc; color: white; text-decoration: none; border-radius: 4px; margin-top: 10px; }");
    out.println(".menu-item a:hover { background-color: #0052a3; }");
    out.println(
        ".warning { background-color: #fff3cd; border: 1px solid #ffeaa7; color: #856404; padding: 15px; border-radius: 5px; margin: 20px 0; }");
    out.println(
        ".info { background-color: #d1ecf1; border: 1px solid #bee5eb; color: #0c5460; padding: 15px; border-radius: 5px; margin: 20px 0; }");
    out.println("</style>");
    out.println("</head>");
    out.println("<body>");
    out.println("<div class='container'>");
    out.println("<h1>🌐 Java Web History</h1>");
    out.println("<h2 style='text-align: center; color: #666;'>Statement vs PreparedStatement 보안 비교</h2>");
    out.println("<div class='time'>현재 시간: " + currentTime + "</div>");

    out.println("<div class='warning'>");
    out.println("<strong>⚠️ 주의사항:</strong> 이 예제는 교육 목적으로 보안 취약점을 의도적으로 포함하고 있습니다.");
    out.println("</div>");

    out.println("<div class='menu'>");
    out.println("<h2>📋 메뉴</h2>");

    out.println("<div class='menu-item'>");
    out.println("<h3>🔴 Statement 버전 (보안 취약)</h3>");
    out.println("<p>• SQL 인젝션에 취약한 코드</p>");
    out.println("<p>• XSS 공격에 취약한 코드</p>");
    out.println("<p>• 2000년대 초반 스타일의 취약한 코드</p>");
    out.println("<a href='board?action=list'>게시판 보기</a>");
    out.println("</div>");

    out.println("<div class='menu-item'>");
    out.println("<h3>🔵 PreparedStatement 버전 (SQL 인젝션 방어)</h3>");
    out.println("<p>• SQL 인젝션을 방어하는 코드</p>");
    out.println("<p>• XSS는 여전히 취약 (비교를 위해)</p>");
    out.println("<p>• 2000년대 초반 스타일의 보안 강화 코드</p>");
    out.println("<a href='board?action=list'>게시판 보기</a>");
    out.println("</div>");
    out.println("</div>");

    out.println("<div class='info'>");
    out.println("<strong>💡 사용법:</strong>");
    out.println("<ul>");
    out.println("<li>위의 두 버전 중 하나를 선택하여 게시판을 사용해보세요</li>");
    out.println("<li>글쓰기에서 악성 스크립트나 SQL 인젝션 코드를 입력해보세요</li>");
    out.println("<li>두 버전의 차이점을 비교해보세요</li>");
    out.println("</ul>");
    out.println("</div>");

    out.println("<div class='info'>");
    out.println("<strong>🔧 설정 변경:</strong>");
    out.println("<p>web.xml에서 &lt;servlet-class&gt;를 변경하여 Statement/PreparedStatement 버전을 전환할 수 있습니다.</p>");
    out.println("</div>");

    out.println("</div>");
    out.println("</body>");
    out.println("</html>");
  }
}
