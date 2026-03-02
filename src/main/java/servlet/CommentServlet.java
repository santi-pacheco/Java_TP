package servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.google.gson.Gson;

import service.UserService;
import repository.UserRepository;
import repository.FollowRepository;
import repository.BlockRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import entity.ReviewComment;
import entity.User;
import exception.BanException;
import exception.ErrorFactory;
import service.CommentService;

@WebServlet("/review-comments")
public class CommentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private CommentService commentService;
    private UserService userService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
    	CommentService commentService = new CommentService();
    	this.commentService = commentService;
    	
    	UserRepository userRepository = new UserRepository();
    	BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
    	FollowRepository followRepository = new FollowRepository();
    	BlockRepository blockRepository = new BlockRepository();
    	UserService userService = new UserService(userRepository, bcryptPasswordEncoder, followRepository, blockRepository);
        this.userService = userService;
        
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String reviewIdParam = request.getParameter("reviewId");
        if (reviewIdParam == null || reviewIdParam.trim().isEmpty()) {
            throw ErrorFactory.badRequest("El ID de la reseña es requerido");
        }
        
        int reviewId;
        try {
            reviewId = Integer.parseInt(reviewIdParam);
        } catch (NumberFormatException e) {
            throw ErrorFactory.badRequest("El ID de la reseña debe ser un número válido");
        }

        HttpSession session = request.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("usuarioLogueado") : null;
        int loggedUserId = (loggedUser != null) ? loggedUser.getUserId() : -1;
        
        List<ReviewComment> comments = commentService.getCommentsByReview(reviewId, loggedUserId);
        List<Map<String, Object>> responseList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (ReviewComment c : comments) {
            Map<String, Object> map = new HashMap<>();
            map.put("commentId", c.getCommentId());
            map.put("userId", c.getUserId());
            map.put("username", c.getUsername() != null ? c.getUsername() : "Usuario");
            map.put("createdAt", c.getCreatedAt() != null ? sdf.format(c.getCreatedAt()) : "");
            map.put("status", c.getModerationStatus() != null ? c.getModerationStatus().getValue() : "APPROVED");
            map.put("commentText", c.getCommentText() != null ? c.getCommentText() : "");
            
            String avatarName = c.getProfileImage();
            String profilePicture = (avatarName != null && !avatarName.trim().isEmpty()) 
                    ? request.getContextPath() + "/uploads/" + avatarName 
                    : request.getContextPath() + "/utils/default_profile.png";
            map.put("profilePicture", profilePicture);

            boolean isFollowing = false;
            if (loggedUser != null && loggedUser.getUserId() != c.getUserId()) {
                isFollowing = userService.isFollowing(loggedUser.getUserId(), c.getUserId());
            }
            map.put("isFollowing", isFollowing);
            responseList.add(map);
        }
        response.getWriter().write(gson.toJson(responseList));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("usuarioLogueado");
        String action = req.getParameter("action");
        if (action == null) action = "create";

        Map<String, Object> jsonResponse = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            if ("delete".equals(action)) {
                String commentIdParam = req.getParameter("commentId");
                if (commentIdParam == null) throw ErrorFactory.badRequest("commentId requerido");
                
                int commentId = Integer.parseInt(commentIdParam);
                commentService.deleteOwnComment(user.getUserId(), commentId);
                jsonResponse.put("success", true);
                
            } else if ("edit".equals(action)) {
                String commentIdParam = req.getParameter("commentId");
                String newText = req.getParameter("commentText");
                
                if (commentIdParam == null) throw ErrorFactory.badRequest("commentId requerido");
                if (newText == null || newText.trim().isEmpty()) throw ErrorFactory.badRequest("El texto del comentario es requerido");

                int commentId = Integer.parseInt(commentIdParam);
                ReviewComment comment = commentService.editComment(user.getUserId(), commentId, newText);
                
                jsonResponse.put("success", true);
                jsonResponse.put("newText", comment.getCommentText());
                jsonResponse.put("status", comment.getModerationStatus() != null ? comment.getModerationStatus().getValue() : "APPROVED");
                
            } else {
                String reviewIdParam = req.getParameter("reviewId");
                String commentText = req.getParameter("commentText");
                
                if (reviewIdParam == null) throw ErrorFactory.badRequest("reviewId requerido");
                if (commentText == null || commentText.trim().isEmpty()) throw ErrorFactory.badRequest("El texto del comentario es requerido");
                int reviewId = Integer.parseInt(reviewIdParam);
                ReviewComment comment = commentService.createComment(user.getUserId(), reviewId, commentText);
                
                jsonResponse.put("success", true);
                jsonResponse.put("commentId", comment.getCommentId());
                jsonResponse.put("userId", user.getUserId());
                jsonResponse.put("username", user.getUsername());
                jsonResponse.put("createdAt", comment.getCreatedAt() != null ? sdf.format(comment.getCreatedAt()) : sdf.format(new java.util.Date()));
                jsonResponse.put("status", comment.getModerationStatus() != null ? comment.getModerationStatus().getValue() : "APPROVED");
            }
            
            resp.getWriter().write(gson.toJson(jsonResponse));

        } catch (BanException e) {
            jsonResponse.put("success", false);
            jsonResponse.put("error", "Fuiste baneado");
            jsonResponse.put("bannedUntil", e.getBannedUntil());
            resp.getWriter().write(gson.toJson(jsonResponse));
        } 
    }
}