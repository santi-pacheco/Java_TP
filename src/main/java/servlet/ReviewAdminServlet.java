package servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import controller.ReviewController;
import entity.Review;
import repository.ReviewRepository;
import service.ReviewService;

@WebServlet("/reviews-admin")
public class ReviewAdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ReviewController reviewController;

	@Override
	public void init() throws ServletException {
		super.init();
		ReviewRepository reviewRepository = new ReviewRepository();
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
		
		switch (accion) {
			case "listar":
				List<Review> reviews = reviewController.getAllReviews();
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
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String accion = request.getParameter("accion");
		
		if ("actualizarSpoiler".equals(accion)) {
			int reviewId = Integer.parseInt(request.getParameter("id"));
			boolean contieneSpoiler = Boolean.parseBoolean(request.getParameter("contieneSpoiler"));
			reviewController.updateSpoilerStatus(reviewId, contieneSpoiler);
			response.sendRedirect(request.getContextPath() + "/reviews-admin?accion=listar&exito=true");
		}
	}
}
