package servlet;

import java.io.IOException;
import repository.BlockRepository;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import controller.ReviewController;
import entity.ModerationStatus;
import entity.Review;
import repository.ReviewRepository;
import repository.SystemSettingsRepository;
import repository.UserRepository;
import repository.WatchlistRepository;
import service.MovieService;
import service.ReviewService;
import service.SystemSettingsService;
import service.UserService;
import service.WatchlistService;
import repository.FollowRepository;
import repository.MovieRepository;
import exception.ErrorFactory;

@WebServlet("/reviews-admin")
public class ReviewAdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ReviewController reviewController;
	private ReviewRepository reviewRepository;

	@Override
	public void init() throws ServletException {
		super.init();
		this.reviewRepository = new ReviewRepository();
		UserRepository userRepository = new UserRepository();
		MovieRepository movieRepository = new MovieRepository();
		SystemSettingsRepository configRepository = new SystemSettingsRepository();
		FollowRepository followRepository = new FollowRepository();
		BlockRepository blockRepository = new BlockRepository();
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		UserService userService = new UserService(userRepository, passwordEncoder, followRepository, blockRepository);
		MovieService movieService = new MovieService(movieRepository);
		SystemSettingsService configService = new SystemSettingsService(configRepository);
		WatchlistRepository watchlistRepository = new WatchlistRepository(movieRepository);
        WatchlistService watchlistService = new WatchlistService(watchlistRepository, userService, movieService);
		ReviewService reviewService = new ReviewService(reviewRepository, userService, movieService, configService, watchlistService);
		this.reviewController = new ReviewController(reviewService);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String accion = request.getParameter("accion");
	    if (accion == null) accion = "listar";  
        switch (accion) {
            case "listar":
                String statusParam = request.getParameter("status");
                List<Review> reviews;  
                if (statusParam != null && !statusParam.isEmpty()) {
                    try {
                        ModerationStatus status = ModerationStatus.fromString(statusParam);
                        reviews = reviewRepository.getReviewsByModerationStatus(status);
                    } catch (IllegalArgumentException e) {
                        reviews = reviewController.getAllReviews();
                        request.setAttribute("error", "Estado de filtro inválido: " + statusParam);
                    }
                } else {
                    reviews = reviewController.getAllReviews();
                }         
                request.setAttribute("reviews", reviews);
                request.getRequestDispatcher("/reviewAdminCrud.jsp").forward(request, response);
                break;
            case "detalle":
                String idStr = request.getParameter("id");
                if (idStr == null || idStr.isEmpty()) {
                    throw ErrorFactory.badRequest("ID de reseña faltante");
                }
                try {
                    int idDetalle = Integer.parseInt(idStr);
                    Review review = reviewController.getReviewById(idDetalle);
                    if (review == null) throw ErrorFactory.notFound("La reseña solicitada no existe.");
                    
                    request.setAttribute("review", review);
                    request.getRequestDispatcher("/reviewDetail.jsp").forward(request, response);
                } catch (NumberFormatException e) {
                    throw ErrorFactory.badRequest("El ID de la reseña debe ser un número.");
                }
                break;  
            default:
                throw ErrorFactory.badRequest("Acción desconocida");
        }
	}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String accion = request.getParameter("accion");
        if (accion == null) throw ErrorFactory.badRequest("Acción requerida");
	    
	    if ("actualizarModeracion".equals(accion)) {
	    	try {
                int reviewId = Integer.parseInt(request.getParameter("id"));
                String statusStr = request.getParameter("status");
                String reason = request.getParameter("reason");
                if (statusStr == null || statusStr.isBlank()) {
                    throw ErrorFactory.badRequest("Falta el parámetro status");
                }     
                ModerationStatus status = ModerationStatus.fromString(statusStr);
                if (status == ModerationStatus.REJECTED && (reason == null || reason.trim().isEmpty())) {
                    request.setAttribute("error", "Debes escribir una razón para rechazar la reseña.");
                    request.setAttribute("reviews", reviewController.getAllReviews()); 
                    request.getRequestDispatcher("/reviewAdminCrud.jsp").forward(request, response);
                    return; 
                }
                reviewController.updateModerationStatus(reviewId, status, reason);
                response.sendRedirect(request.getContextPath() + "/reviews-admin?accion=listar&exito=true");

            } catch (NumberFormatException e) {
                throw ErrorFactory.badRequest("El ID proporcionado es inválido.");
            } catch (IllegalArgumentException e) {
                throw ErrorFactory.badRequest("Status de moderación inválido.");
            }
	    } else {
            throw ErrorFactory.badRequest("Acción desconocida");
        }
	}
}