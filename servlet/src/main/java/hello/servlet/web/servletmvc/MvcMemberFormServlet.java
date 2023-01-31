package hello.servlet.web.servletmvc;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "mvcMemberFormServlet", urlPatterns = "/servlet-mvc/members/new-form")
public class MvcMemberFormServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // WEB-INF/** 의 리소스 들은 반드시 컨트롤러를 통해 요청해야한다(urlPattern).
        String viewPath = "/WEB-INF/views/new-form.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);

        // 다른 서블릿 또는 jsp로 이동할 수 있는 기능
        // 서버 내부에서 다시 호출이 발생한다. 클라이언트가 요청하는 것이 아니다.
        // 클라이언트가 다시 요청하는 것은 redirect!
        dispatcher.forward(request, response);
    }
}
