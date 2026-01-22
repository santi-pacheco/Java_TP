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
import service.ReviewService;

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
		
		org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
		service.UserService userService = new service.UserService(userRepository, passwordEncoder);
		service.MovieService movieService = new service.MovieService(movieRepository);
		service.ConfiguracionReglasService configService = new service.ConfiguracionReglasService(configRepository);
		
		ReviewService reviewService = new ReviewService(reviewRepository, userService, movieService, configService);
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
						ModerationStatus status = ModerationStatus.fromString(statusParam);
						reviews = reviewRepository.getReviewsByModerationStatus(status);
					} else {
						reviews = reviewController.getAllReviews();
					}
					
					request.setAttribute("reviews", reviews);
					request.getRequestDispatcher("/reviewAdminCrud.jsp").forward(request, response);
					break;
				case "detalle":
					int idDetalle = Integer.parseInt(request.getParameter("id"));
					Review review = reviewController.getReviewById(idDetalle);
					request.setAttribute("review", review);
					request.getRequestDispatcher("/reviewDetail.jsp").forward(request, response);
					break;
			}
		} catch (SQLException e) {
			throw new ServletException("Error al procesar la solicitud", e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String accion = request.getParameter("accion");
	    
	    if ("actualizarModeracion".equals(accion)) {
	        int reviewId = Integer.parseInt(request.getParameter("id"));
	        String statusStr = request.getParameter("status");
	        String reason = request.getParameter("reason");
	        
	        ModerationStatus status = ModerationStatus.fromString(statusStr);
	        reviewController.updateModerationStatus(reviewId, status, reason);
	        
	        response.sendRedirect(request.getContextPath() + "/reviews-admin?accion=listar&exito=true");
	    }
	}
}
