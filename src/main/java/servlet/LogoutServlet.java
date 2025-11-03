package servlet; // O el paquete donde tengas tus servlets

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Obtener la sesión actual.
        //    Usamos 'false' porque si no hay sesión, NO queremos crear una nueva.
        HttpSession session = request.getSession(false);

        // 2. Verificar si el usuario realmente tenía una sesión
        if (session != null) {
            // 3. Invalidar la sesión.
            //    Esto le dice a Tomcat que borre este objeto de sesión de su memoria
            //    y cualquier cookie asociada.
            session.invalidate();
        }

        // 4. Redirigir al usuario a la página de login.
        //    Esto se hace siempre, incluso si no había sesión (por si acaso).
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
