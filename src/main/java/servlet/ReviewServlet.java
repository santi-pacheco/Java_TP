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
import controller.ReviewController;
import entity.Review;
import repository.ReviewRepository;
import repository.UserRepository;
import repository.MovieRepository;
import service.ReviewService;
import service.UserService;
import service.MovieService;
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
        
        // Inicializar servicios
        UserService userService = new UserService(userRepository);
        MovieService movieService = new MovieService(movieRepository);
        ReviewService reviewService = new ReviewService(reviewRepository, userService, movieService);
        
        // Inicializar controller
        this.reviewController = new ReviewController(reviewService);
        
        // Inicializar validador
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        
        // Inicializar Gson con formato de fecha
        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
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
        
        // Crear nueva reseña
        Review newReview = gson.fromJson(request.getReader(), Review.class);
        
        // Validar con Jakarta Bean Validation
        Set<ConstraintViolation<Review>> violations = validator.validate(newReview);
        if (!violations.isEmpty()) {
            String errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(" "));
            throw ErrorFactory.validation(errorMessages);
        }
        
        // Crear o actualizar reseña (lógica principal del caso de uso)
        Review createdReview = reviewController.createOrUpdateReview(newReview);
        
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(gson.toJson(createdReview));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Actualizar reseña existente
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            throw ErrorFactory.badRequest("El ID de la reseña es requerido para actualizar");
        }
        
        int id = Integer.parseInt(idParam);
        Review reviewToUpdate = gson.fromJson(request.getReader(), Review.class);
        reviewToUpdate.setId(id);
        
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
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            throw ErrorFactory.badRequest("El ID de la reseña es requerido para eliminar");
        }
        
        int id = Integer.parseInt(idParam);
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
