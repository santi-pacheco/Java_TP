package servlet;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.time.LocalDate;

import controller.ReviewController;
import entity.Review;
import entity.User;
import repository.ReviewRepository;
import repository.UserRepository;
import repository.MovieRepository;
import service.ReviewService;
import service.UserService;
import service.MovieService;
import service.ConfiguracionReglasService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import exception.ErrorFactory;

@WebServlet("/reviews")
public class ReviewServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ReviewController reviewController;
    private Validator validator;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        
        // Inicializar repositorios
        ReviewRepository reviewRepository = new ReviewRepository();
        UserRepository userRepository = new UserRepository();
        MovieRepository movieRepository = new MovieRepository();
        repository.ConfiguracionReglasRepository configuracionRepository = new repository.ConfiguracionReglasRepository();
        
        // Inicializar servicios
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        UserService userService = new UserService(userRepository, passwordEncoder);
        MovieService movieService = new MovieService(movieRepository);
        ConfiguracionReglasService configuracionService = new ConfiguracionReglasService(configuracionRepository);
        ReviewService reviewService = new ReviewService(reviewRepository, userService, movieService, configuracionService);
        
        // Inicializar controller
        this.reviewController = new ReviewController(reviewService);
        
        // Inicializar validador
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        
        // Inicializar Gson con formato de fecha
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                    @Override
                    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.toString()); // "2024-01-15"
                    }
                })
                .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                    @Override
                    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
                            throws JsonParseException {
                        return LocalDate.parse(json.getAsString());
                    }
                })
                .create();
    }
    
 // Método helper para obtener usuario logueado
    private User getLoggedUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            throw ErrorFactory.unauthorized("Debes estar logueado para realizar esta acción");
        }
        return (User) session.getAttribute("usuarioLogueado");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        
        String idParam = request.getParameter("id");
        String userIdParam = request.getParameter("userId");
        String movieIdParam = request.getParameter("movieId");
        
        if (idParam != null) {
            // GET /reviews?id=123 - Obtener reseña por ID
            int id = Integer.parseInt(idParam);
            Review review = reviewController.getReviewById(id);
            response.getWriter().write(gson.toJson(review));
            
        } else if (userIdParam != null && movieIdParam != null) {
            // GET /reviews?userId=123&movieId=456 - Obtener reseña específica de usuario para película
            int userId = Integer.parseInt(userIdParam);
            int movieId = Integer.parseInt(movieIdParam);
            Review review = reviewController.getReviewByUserAndMovie(userId, movieId);
            
            if (review != null) {
                response.getWriter().write(gson.toJson(review));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"success\":false,\"message\":\"No se encontró reseña para este usuario y película\"}");
            }
            
        } else if (movieIdParam != null) {
            // GET /reviews?movieId=456 - Obtener todas las reseñas de una película
            int movieId = Integer.parseInt(movieIdParam);
            List<Review> reviews = reviewController.getReviewsByMovie(movieId);
            response.getWriter().write(gson.toJson(reviews));
            
        } else {
            // GET /reviews - Para administradores: obtener reseñas pendientes de revisión de spoilers
            List<Review> pendingReviews = reviewController.getPendingSpoilerReviews();
            response.getWriter().write(gson.toJson(pendingReviews));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        User loggedUser = getLoggedUser(request);
        String contentType = request.getContentType();
        
        Review newReview;
        int movieId = 0;
        
        try {
            if (contentType != null && contentType.contains("application/json")) {
                String jsonBody = request.getReader().lines().collect(Collectors.joining());
                newReview = gson.fromJson(jsonBody, Review.class);
            } else {
                // Formulario HTML
                String movieIdStr = request.getParameter("movieId");
                String reviewText = request.getParameter("reviewText");
                String ratingStr = request.getParameter("rating");
                String watchedOnStr = request.getParameter("watchedOn");
                
                movieId = Integer.parseInt(movieIdStr);
                newReview = new Review();
                newReview.setId_movie(movieId);
                newReview.setReview_text(reviewText);
                newReview.setRating(Double.parseDouble(ratingStr));
                newReview.setWatched_on(LocalDate.parse(watchedOnStr));
            }
            
            newReview.setId_user(loggedUser.getId());
            
            Set<ConstraintViolation<Review>> violations = validator.validate(newReview);
            if (!violations.isEmpty()) {
                String errorMessages = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(" "));
                throw ErrorFactory.validation(errorMessages);
            }
            
            Review createdReview = reviewController.createOrUpdateReview(newReview);
            
            if (contentType != null && contentType.contains("application/json")) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(gson.toJson(createdReview));
            } else {
                response.sendRedirect(request.getContextPath() + "/movie/" + newReview.getId_movie());
            }
        } catch (Exception e) {
            if (contentType != null && contentType.contains("application/json")) {
                throw e;
            } else {
                HttpSession session = request.getSession();
                session.setAttribute("reviewError", e.getMessage());
                session.setAttribute("reviewData", request.getParameterMap());
                response.sendRedirect(request.getContextPath() + "/movie/" + movieId + "#review-form");
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	
    	//Verificar que el usuario esté logueado
        User loggedUser = getLoggedUser(request);
        
        
        // Actualizar reseña existente
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            throw ErrorFactory.badRequest("El ID de la reseña es requerido para actualizar");
        }
        
        int id = Integer.parseInt(idParam);
        Review reviewToUpdate = gson.fromJson(request.getReader(), Review.class);
        reviewToUpdate.setId(id);
        
        //Verificar que la reseña pertenece al usuario logueado
        Review existingReview = reviewController.getReviewById(id);
        if (existingReview.getId_user() != loggedUser.getId()) {
            throw ErrorFactory.forbidden("Solo puedes editar tus propias reseñas");
        }
        
        // Validar
        Set<ConstraintViolation<Review>> violations = validator.validate(reviewToUpdate);
        if (!violations.isEmpty()) {
            String errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(" "));
            throw ErrorFactory.validation(errorMessages);
        }
        
        Review updatedReview = reviewController.updateReview(reviewToUpdate);
        
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(gson.toJson(updatedReview));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
    	User loggedUser = getLoggedUser(request);
    	
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            throw ErrorFactory.badRequest("El ID de la reseña es requerido para eliminar");
        }
        
        int id = Integer.parseInt(idParam);
        
     //Verificar que la reseña pertenece al usuario logueado (o es admin)
        Review existingReview = reviewController.getReviewById(id);
        if (existingReview.getId_user() != loggedUser.getId() && !"admin".equals(loggedUser.getRole())) {
            throw ErrorFactory.forbidden("Solo puedes eliminar tus propias reseñas");
        }
        
        reviewController.deleteReview(id);
        
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (request.getMethod().equalsIgnoreCase("PATCH")) {
            this.doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	
    	//Solo administradores pueden actualizar estado de spoiler
        User loggedUser = getLoggedUser(request);
        if (!"admin".equals(loggedUser.getRole())) {
            throw ErrorFactory.forbidden("Solo los administradores pueden actualizar el estado de spoiler");
        }
        
        // PATCH para actualizar estado de spoiler (solo administradores)
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            throw ErrorFactory.badRequest("El ID de la reseña es requerido");
        }
        
        int id = Integer.parseInt(idParam);
        
        // Leer el JSON con el estado de spoiler
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<java.util.Map<String, Object>>(){}.getType();
        java.util.Map<String, Object> changes = gson.fromJson(request.getReader(), type);
        
        if (changes.containsKey("contieneSpoiler")) {
            boolean containsSpoiler = (Boolean) changes.get("contieneSpoiler");
            reviewController.updateSpoilerStatus(id, containsSpoiler);
            
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":true,\"message\":\"Estado de spoiler actualizado\"}");
        } else {
            throw ErrorFactory.badRequest("Se requiere el campo 'contieneSpoiler'");
        }
    }
}
