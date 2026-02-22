package servlet;

import java.io.File;
import okhttp3.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.util.Set;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

@WebServlet("/profile-image")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, 
    maxFileSize = 1024 * 1024 * 10,      
    maxRequestSize = 1024 * 1024 * 15    
)
public class ProfileImageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserController userController;
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of("image/jpeg", "image/png", "image/gif");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif");
    private OkHttpClient httpClient;
    private static final String apiUser;
    private static final String apiSecret;
    static {
        ResourceBundle config = ResourceBundle.getBundle("config");
        apiUser = config.getString("sightengine.api.user").trim();
        apiSecret = config.getString("sightengine.api.secret").trim();
    }
    
    @Override
    public void init() throws ServletException {
        super.init();
        UserRepository userRepository = new UserRepository();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        FollowRepository followRepository = new FollowRepository();
        UserService userService = new UserService(userRepository, passwordEncoder, followRepository);
        this.userController = new UserController(userService);
        this.httpClient = new OkHttpClient();
        
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
        String uploadPath = "C:" + File.separator + "fatmovies_uploads"; 
	    java.io.File uploadDir = new java.io.File(uploadPath);
	    if (!uploadDir.exists()) {
	    	uploadDir.mkdirs();
	    }

        try {
            switch (accion) {
	            case "subir":
	                Part filePart = request.getPart("photo");
	                if (filePart == null || filePart.getSize() == 0) {
	                    throw ErrorFactory.validation("Debes seleccionar una imagen.");
	                }
	                String contentType = filePart.getContentType();
	                if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
	                    throw ErrorFactory.validation("El tipo de archivo no es válido. Solo se permiten imágenes (JPG, PNG, GIF).");
	                }

	                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
	                int i = fileName.lastIndexOf('.');
	                
	                if (i <= 0) {
	                    throw ErrorFactory.validation("El archivo debe tener una extensión válida.");
	                }
	                
	                String extension = fileName.substring(i).toLowerCase();
	                if (!ALLOWED_EXTENSIONS.contains(extension)) {
	                    throw ErrorFactory.validation("La extensión " + extension + " no está permitida.");
	                }
	                String uniqueFileName = "avatar_" + user.getId() + "_" + UUID.randomUUID().toString() + extension;
	                String rutaAbsolutaArchivo = uploadPath + File.separator + uniqueFileName;
                    File archivoFisico = new File(rutaAbsolutaArchivo);
                    filePart.write(rutaAbsolutaArchivo);
                    
                    try {
                        RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("models", "nudity-2.0,weapon,gore")
                            .addFormDataPart("api_user", apiUser)
                            .addFormDataPart("api_secret", apiSecret)
                            .addFormDataPart("media", archivoFisico.getName(),
                                RequestBody.create(MediaType.parse("application/octet-stream"), archivoFisico))
                            .build();

                        Request sightengineRequest = new Request.Builder()
                            .url("https://api.sightengine.com/1.0/check.json")
                            .post(requestBody)
                            .build();

                        try (Response apiResponse = httpClient.newCall(sightengineRequest).execute()) {
                            
                            String responseData = apiResponse.body().string();
                            JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();

                            double nudityScore = jsonObject.getAsJsonObject("nudity").get("none").getAsDouble();
                            JsonObject weaponClasses = jsonObject.getAsJsonObject("weapon").getAsJsonObject("classes");
                            double firearmScore = weaponClasses.get("firearm").getAsDouble();
                            double knifeScore = weaponClasses.get("knife").getAsDouble();
                            double goreScore = jsonObject.getAsJsonObject("gore").get("prob").getAsDouble();
                            
                            if (nudityScore < 0.80 || firearmScore > 0.20 || knifeScore > 0.20 || goreScore > 0.15) {
                                archivoFisico.delete();
                                throw ErrorFactory.validation("La imagen no cumple con nuestras normas de comunidad (contiene desnudez, armas o violencia extrema).");
                            }
                        }
                    } catch (AppException e) {
                        throw e; 
                    } catch (Exception e) {
                        archivoFisico.delete();
                        throw ErrorFactory.internal("No pudimos verificar la seguridad de tu foto en este momento. Inténtalo más tarde.");
                    }

	                userController.updateProfileImage(user.getId(), uniqueFileName, uploadPath);
	                user.setProfileImage(uniqueFileName);
	                System.out.println(uploadPath);
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
    
    @Override
    public void destroy() {
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
        }
        super.destroy();
    }
}