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
import exception.ErrorFactory;

@WebServlet("/review-likes")
public class LikeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LikeService likeService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.likeService = new LikeService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("usuarioLogueado");
        
        String action = req.getParameter("action");
        String reviewIdParam = req.getParameter("reviewId");

        if (reviewIdParam == null || reviewIdParam.isEmpty() || action == null || action.isEmpty()) {
            throw ErrorFactory.badRequest("El ID de la reseña y la acción son requeridos.");
        }

        try {
            int reviewId = Integer.parseInt(reviewIdParam);
            LikeService.LikeResponse response = likeService.toggleLike(user.getUserId(), reviewId);
            resp.getWriter().write("{\"success\":true,\"likesCount\":" + response.getLikesCount() + ",\"liked\":" + response.isLiked() + "}");
        } catch (NumberFormatException e) {
            throw ErrorFactory.badRequest("El ID de la reseña debe ser numérico.");
        }
    }
}