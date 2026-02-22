package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import controller.ReviewController;
import entity.ModerationStatus;
import entity.Review;
import repository.ReviewRepository;
import repository.WatchlistRepository;
import service.ReviewService;
import service.WatchlistService;
import repository.FollowRepository;

@WebServlet("/reviews-admin")
public class ReviewAdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ReviewController reviewController;
	private ReviewRepository reviewRepository;

	@Override
	public void init() throws ServletException {
		super.init();
		this.reviewRepository = new ReviewRepository();
		repository.UserRepository userRepository = new repository.UserRepository();
		repository.MovieRepository movieRepository = new repository.MovieRepository();
		repository.ConfiguracionReglasRepository configRepository = new repository.ConfiguracionReglasRepository();
		FollowRepository followRepository = new FollowRepository();
		org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
		service.UserService userService = new service.UserService(userRepository, passwordEncoder, followRepository);
		service.MovieService movieService = new service.MovieService(movieRepository);
		service.ConfiguracionReglasService configService = new service.ConfiguracionReglasService(configRepository);
		WatchlistRepository watchlistRepository = new WatchlistRepository(movieRepository);
        WatchlistService watchlistService = new WatchlistService(watchlistRepository, userService, movieService);
		ReviewService reviewService = new ReviewService(reviewRepository, userService, movieService, configService, watchlistService);
		this.reviewController = new ReviewController(reviewService);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String accion = request.getParameter("accion");
	    if (accion == null) accion = "listar";
	    
	    try {
	        switch (accion) {
	            case "listar":
	                String statusParam = request.getParameter("status");
	                List<Review> reviews;
	                
	                if (statusParam != null && !statusParam.isEmpty()) {
	                    try {
	                        // Validar que el status exista
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
	                if (idStr != null && !idStr.isEmpty()) {
	                    int idDetalle = Integer.parseInt(idStr);
	                    Review review = reviewController.getReviewById(idDetalle);
	                    request.setAttribute("review", review);
	                    request.getRequestDispatcher("/reviewDetail.jsp").forward(request, response);
	                } else {
	                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de reseña faltante");
	                }
	                break;
	            default:
	                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción desconocida");
	                break;
	        }
	    } catch (Exception e) {
	        throw new ServletException("Error al procesar la solicitud: " + e.getMessage(), e);
	    }
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String accion = request.getParameter("accion");
	    
	    if ("actualizarModeracion".equals(accion)) {
	    	try {
	        int reviewId = Integer.parseInt(request.getParameter("id"));
	        String statusStr = request.getParameter("status");
	        String reason = request.getParameter("reason");
	       
	        if (statusStr == null || statusStr.isBlank()) {
	            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro status");
	            return;
	        }
	        
	        ModerationStatus status = ModerationStatus.fromString(statusStr);
	        
	        if (status == ModerationStatus.REJECTED && (reason == null || reason.trim().isEmpty())) {
	            request.setAttribute("error", "Debes escribir una razón para rechazar la reseña.");
	            request.getRequestDispatcher("/reviewAdminCrud.jsp").forward(request, response);
	            return; 
	        }
	        reviewController.updateModerationStatus(reviewId, status, reason);
	        response.sendRedirect(request.getContextPath() + "/reviews-admin?accion=listar&exito=true");
	       } catch (IllegalArgumentException e) {
	           response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Status inválido: " + e.getMessage());
	       } catch (Exception e) {
	           response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error procesando solicitud");
	       }
	    	   	        
	       }
	    }
	}

