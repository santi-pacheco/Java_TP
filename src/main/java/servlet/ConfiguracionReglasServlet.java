package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import controller.ConfiguracionReglasController;
import entity.ConfiguracionReglas;
import repository.ConfiguracionReglasRepository;
import service.ConfiguracionReglasService;

@WebServlet("/configuracion-reglas")
public class ConfiguracionReglasServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ConfiguracionReglasController controller;

	@Override
	public void init() throws ServletException {
		super.init();
		ConfiguracionReglasRepository repository = new ConfiguracionReglasRepository();
		ConfiguracionReglasService service = new ConfiguracionReglasService(repository);
		this.controller = new ConfiguracionReglasController(service);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<ConfiguracionReglas> configuraciones = controller.getAllConfiguraciones();
		request.setAttribute("configuraciones", configuraciones);
		request.getRequestDispatcher("/WEB-INF/VistaConfiguracionReglasCRUD/ConfiguracionReglasCRUD.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ConfiguracionReglas config = new ConfiguracionReglas();
		config.setUmbralResenasActivo(Integer.parseInt(request.getParameter("umbralResenasActivo")));
		config.setLimiteWatchlistNormal(Integer.parseInt(request.getParameter("limiteWatchlistNormal")));
		config.setLimiteWatchlistActivo(Integer.parseInt(request.getParameter("limiteWatchlistActivo")));
		
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute("usuarioLogueado") != null) {
			entity.User user = (entity.User) session.getAttribute("usuarioLogueado");
			config.setUsuarioAdminID(user.getId());
		}
		
		controller.addConfiguracionReglas(config);
		response.sendRedirect(request.getContextPath() + "/configuracion-reglas?exito=true");
	}
}
