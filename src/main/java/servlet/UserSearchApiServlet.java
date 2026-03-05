package servlet;

import java.io.IOException;
import repository.BlockRepository;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import controller.UserController;
import entity.User;
import repository.FollowRepository;
import repository.UserRepository;
import service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@WebServlet("/api/search-users")
public class UserSearchApiServlet extends HttpServlet {

    private UserController userController;

    @Override
    public void init() throws ServletException {
        super.init();
        UserRepository userRepository = new UserRepository();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        FollowRepository followRepository = new FollowRepository();
        BlockRepository blockRepository = new BlockRepository();
        UserService userService = new UserService(userRepository, passwordEncoder, followRepository, blockRepository);
        this.userController = new UserController(userService);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String query = request.getParameter("q");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("usuarioLogueado") : null;
        int loggedUserId = (loggedUser != null) ? loggedUser.getUserId() : -1;
        
        if (query == null || query.trim().isEmpty()) {
            out.print("[]");
            out.flush();
            return;
        }
        List<User> foundUsers = userController.searchUsers(query.trim(), loggedUserId);

        JsonArray jsonArray = new JsonArray();
        for (User u : foundUsers) {
            JsonObject userJson = new JsonObject();
            userJson.addProperty("id", u.getUserId());
            userJson.addProperty("username", u.getUsername());
            userJson.addProperty("profileImage", u.getProfileImage() != null ? u.getProfileImage() : "");
            userJson.addProperty("userLevel", u.getUserLevel()); 
            jsonArray.add(userJson);
        }
        out.print(new Gson().toJson(jsonArray));
        out.flush();
    }
}