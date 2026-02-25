package servlet;

import java.io.IOException;
import java.util.List;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import entity.Notification;
import entity.User;
import repository.NotificationRepository;
import repository.UserRepository;

@WebServlet("/notifications-api")
public class NotificationApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private NotificationRepository notificationRepository;
    private UserRepository userRepository;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        this.notificationRepository = new NotificationRepository();
        this.userRepository = new UserRepository();
        
       
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                }
            })
            .create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("[]");
            return;
        }

        User sessionUser = (User) session.getAttribute("usuarioLogueado");
        
        try {
            
            User freshUser = userRepository.findOne(sessionUser.getId());
            
            List<Notification> notifications = notificationRepository.getNotificationsForUser(
                freshUser.getId(), 
                freshUser.getUltimaRevisionNotificaciones()
            );
            
            response.getWriter().write(gson.toJson(notifications));
        } catch (Exception e) {
            System.err.println("Error obteniendo notificaciones: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("[]");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false}");
            return;
        }

        User sessionUser = (User) session.getAttribute("usuarioLogueado");
        
        try {
            userRepository.updateNotificacionesLeidas(sessionUser.getId());
            
            User updatedUser = userRepository.findOne(sessionUser.getId());
            session.setAttribute("usuarioLogueado", updatedUser);
            
            response.getWriter().write("{\"success\": true}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false}");
        }
    }
}
