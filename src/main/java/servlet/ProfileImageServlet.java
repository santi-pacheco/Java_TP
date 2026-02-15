package servlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import controller.UserController;
import entity.User;
import exception.AppException;
import exception.ErrorFactory;
import repository.FollowRepository;
import repository.UserRepository;
import service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@WebServlet("/profile-image")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, 
    maxFileSize = 1024 * 1024 * 10,      
    maxRequestSize = 1024 * 1024 * 15    
)
public class ProfileImageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserController userController;

    @Override
    public void init() throws ServletException {
        super.init();
        UserRepository userRepository = new UserRepository();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        FollowRepository followRepository = new FollowRepository();
        UserService userService = new UserService(userRepository, passwordEncoder, followRepository);
        this.userController = new UserController(userService);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        
        if (accion == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        User user = (User) request.getSession().getAttribute("usuarioLogueado");
        String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
        try {
            switch (accion) {
                case "subir":
                    Part filePart = request.getPart("photo");
                    if (filePart == null || filePart.getSize() == 0) {
                        throw ErrorFactory.validation("Debes seleccionar una imagen.");
                    }
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdir();

                    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String extension = "";
                    int i = fileName.lastIndexOf('.');
                    if (i > 0) extension = fileName.substring(i);
                    
                    String uniqueFileName = "avatar_" + user.getId() + "_" + UUID.randomUUID().toString() + extension;

                    filePart.write(uploadPath + File.separator + uniqueFileName);
                    userController.updateProfileImage(user.getId(), uniqueFileName, uploadPath);
                    user.setProfileImage(uniqueFileName);
                    request.getSession().setAttribute("flashMessage", "¡Foto actualizada con éxito!");
                    request.getSession().setAttribute("flashType", "success");
                    break;

                case "eliminar":
                    userController.removeProfileImage(user.getId(), uploadPath);
                    user.setProfileImage(null);
                    request.getSession().setAttribute("flashMessage", "Foto de perfil eliminada.");
                    request.getSession().setAttribute("flashType", "info");
                    break;
                    
                default:
                    throw ErrorFactory.badRequest("Acción no reconocida: " + accion);
            }
            request.getSession().setAttribute("usuarioLogueado", user);
            response.sendRedirect(request.getContextPath() + "/profile?id=" + user.getId());

        } catch (AppException e) {
            e.printStackTrace();
            request.getSession().setAttribute("flashMessage", e.getMessage());
            request.getSession().setAttribute("flashType", "danger");
            response.sendRedirect(request.getContextPath() + "/profile?id=" + user.getId());
        } catch (Exception e) {
            e.printStackTrace();
            String msg = "Ocurrió un error inesperado.";
            if (e instanceof IllegalStateException) {
                msg = "El archivo es demasiado grande (Máx 10MB).";
            }
            request.getSession().setAttribute("flashMessage", msg);
            request.getSession().setAttribute("flashType", "danger");
            response.sendRedirect(request.getContextPath() + "/profile?id=" + user.getId());
        }
    }
}