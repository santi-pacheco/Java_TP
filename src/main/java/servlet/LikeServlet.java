package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import entity.User;
import service.LikeService;

@WebServlet("/review-likes")
public class LikeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"success\":false,\"error\":\"User not logged in\"}");
                return;
            }

            User user = (User) session.getAttribute("usuarioLogueado");
            String action = req.getParameter("action");
            String reviewIdParam = req.getParameter("reviewId");

            if (reviewIdParam == null || action == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false,\"error\":\"reviewId and action are required\"}");
                return;
            }

            int reviewId = Integer.parseInt(reviewIdParam);
            LikeService likeService = new LikeService();

            LikeService.LikeResponse response = likeService.toggleLike(user.getId(), reviewId);
            
            resp.getWriter().write("{\"success\":true,\"likesCount\":" + response.getLikesCount() + ",\"liked\":" + response.isLiked() + "}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
