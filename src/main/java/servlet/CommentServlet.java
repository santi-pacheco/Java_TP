package servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

// Importamos lo necesario para validar a quién seguimos
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; 
import service.UserService;
import repository.UserRepository;
import repository.FollowRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import entity.ReviewComment;
import entity.User;
import exception.BanException;
import service.CommentService;

@WebServlet("/review-comments")
public class CommentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            String reviewIdParam = req.getParameter("reviewId");
            if (reviewIdParam == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "reviewId is required"); return;
            }

            int reviewId = Integer.parseInt(reviewIdParam);
            CommentService commentService = new CommentService();
            List<ReviewComment> comments = commentService.getCommentsByReview(reviewId);

            StringBuilder json = new StringBuilder("[");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
           
            HttpSession session = req.getSession(false);
            User loggedUser = (session != null) ? (User) session.getAttribute("usuarioLogueado") : null;
            
          
            UserService userService = new UserService(new UserRepository(), new BCryptPasswordEncoder(), new FollowRepository());
            
            for (int i = 0; i < comments.size(); i++) {
                ReviewComment c = comments.get(i);
                String dateStr = c.getCreatedAt() != null ? sdf.format(c.getCreatedAt()) : "";
                String safeText = c.getCommentText() != null ? c.getCommentText().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "") : "";
                String safeUser = c.getUsername() != null ? c.getUsername().replace("\"", "\\\"") : "Usuario";
                String status = c.getModerationStatus() != null ? c.getModerationStatus().getValue() : "APPROVED";
                
                String avatarName = "";
                try { avatarName = c.getProfileImage(); } catch (Exception e) {}
                
              
                String profilePicture = (avatarName != null && !avatarName.trim().isEmpty()) 
                        ? "/fatmovies_uploads/" + avatarName 
                        : req.getContextPath() + "/utils/default_profile.png";

            
                boolean isFollowing = false;
                if (loggedUser != null && loggedUser.getId() != c.getIdUsuario()) {
                    isFollowing = userService.isFollowing(loggedUser.getId(), c.getIdUsuario());
                }
                
                json.append("{")
                    .append("\"idComment\":").append(c.getIdComment()).append(",")
                    .append("\"idUsuario\":").append(c.getIdUsuario()).append(",") 
                    .append("\"username\":\"").append(safeUser).append("\",")
                    .append("\"createdAt\":\"").append(dateStr).append("\",")
                    .append("\"status\":\"").append(status).append("\",")
                    .append("\"commentText\":\"").append(safeText).append("\",")
                    .append("\"profilePicture\":\"").append(profilePicture).append("\",")
                    .append("\"isFollowing\":").append(isFollowing)
                    .append("}");
                if (i < comments.size() - 1) json.append(",");
            }
            json.append("]");
            resp.getWriter().write(json.toString());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                resp.getWriter().write("{\"success\":false,\"error\":\"User not logged in\"}"); return;
            }

            User user = (User) session.getAttribute("usuarioLogueado");
            String action = req.getParameter("action");
            if(action == null) action = "create";

            CommentService commentService = new CommentService();

            if ("delete".equals(action)) {
                int commentId = Integer.parseInt(req.getParameter("commentId"));
                commentService.deleteOwnComment(user.getId(), commentId);
                resp.getWriter().write("{\"success\":true}");
                
            } else if ("edit".equals(action)) {
                int commentId = Integer.parseInt(req.getParameter("commentId"));
                String newText = req.getParameter("commentText");
                ReviewComment comment = commentService.editComment(user.getId(), commentId, newText);
                String status = comment.getModerationStatus() != null ? comment.getModerationStatus().getValue() : "APPROVED";
                
                resp.getWriter().write("{\"success\":true,\"newText\":\"" + comment.getCommentText().replace("\"", "\\\"") + "\",\"status\":\"" + status + "\"}");
                
            } else {
                // CREATE (default)
                int reviewId = Integer.parseInt(req.getParameter("reviewId"));
                String commentText = req.getParameter("commentText");
                if (commentText == null || commentText.trim().isEmpty()) {
                    resp.getWriter().write("{\"success\":false,\"error\":\"commentText required\"}"); return;
                }
                ReviewComment comment = commentService.createComment(user.getId(), reviewId, commentText);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String createdAt = comment.getCreatedAt() != null ? sdf.format(comment.getCreatedAt()) : sdf.format(new java.util.Date());
                String status = comment.getModerationStatus() != null ? comment.getModerationStatus().getValue() : "APPROVED";
                
                resp.getWriter().write("{\"success\":true,\"idComment\":" + comment.getIdComment() + ",\"idUsuario\":" + user.getId() + ",\"username\":\"" + user.getUsername() + "\",\"createdAt\":\"" + createdAt + "\",\"status\":\"" + status + "\"}");
            }

        } catch (BanException e) {
            resp.getWriter().write("{\"success\":false,\"error\":\"You are banned\",\"bannedUntil\":\"" + e.getBannedUntil() + "\"}");
        } catch (Exception e) {
            resp.getWriter().write("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        String safeMessage = message != null ? message.replace("\"", "'") : "Unknown error";
        resp.getWriter().write("{\"error\":\"" + safeMessage + "\"}");
    }
}