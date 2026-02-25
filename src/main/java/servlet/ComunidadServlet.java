package servlet;

import entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/comunidad")
public class ComunidadServlet extends HttpServlet {
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User usuarioActual = (User) request.getSession().getAttribute("usuarioLogueado");
        request.getRequestDispatcher("/WEB-INF/community.jsp").forward(request, response);
    }
}