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
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.servlet.ServletContext;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import exception.AppException;
import exception.ErrorFactory;
import entity.User;

@WebServlet("/configuracion-reglas")
public class ConfiguracionReglasServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ConfiguracionReglasController controller;
    private Validator validator;

    @Override
    public void init() throws ServletException {
        super.init();
        ConfiguracionReglasRepository repository = new ConfiguracionReglasRepository();
        ConfiguracionReglasService service = new ConfiguracionReglasService(repository);
        this.controller = new ConfiguracionReglasController(service);
        
        ServletContext context = getServletContext();
        this.validator = (Validator) context.getAttribute("miValidador");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<ConfiguracionReglas> configuraciones = controller.getAllConfiguraciones();
        request.setAttribute("configuraciones", configuraciones);
        request.getRequestDispatcher("/WEB-INF/VistaConfiguracionReglasCRUD/ConfiguracionReglasCRUD.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String jspTarget = "/WEB-INF/VistaConfiguracionReglasCRUD/ConfiguracionReglasCRUD.jsp";
        ConfiguracionReglas configFromForm = null;
        
        try {
            configFromForm = new ConfiguracionReglas();
            populateConfigFromRequest(configFromForm, request);
            Set<ConstraintViolation<ConfiguracionReglas>> violations = validator.validate(configFromForm);
            
            if (!violations.isEmpty()) {
                request.setAttribute("errors", getErrorMessages(violations));
                request.setAttribute("configForm", configFromForm);
                request.setAttribute("configuraciones", controller.getAllConfiguraciones()); 
                request.getRequestDispatcher(jspTarget).forward(request, response);
                return;
            }
            controller.addConfiguracionReglas(configFromForm);
            response.sendRedirect(request.getContextPath() + "/configuracion-reglas?exito=true");

        } catch (AppException e) {
            Set<String> errors = Set.of(e.getMessage());
            request.setAttribute("errors", errors);
            request.setAttribute("configForm", configFromForm);
            request.setAttribute("configuraciones", controller.getAllConfiguraciones()); 
            request.getRequestDispatcher(jspTarget).forward(request, response);
        
        } catch (Exception e) {
            System.err.println("Error no esperado en ConfiguracionReglasServlet: " + e.getMessage());
            throw e;
        }
    }
    private void populateConfigFromRequest(ConfiguracionReglas config, HttpServletRequest request) {
        
        config.setUmbralKcalsNivel2(parseIntParam(request.getParameter("umbralKcalsNivel2"), "Umbral Nivel 2"));
        config.setUmbralKcalsNivel3(parseIntParam(request.getParameter("umbralKcalsNivel3"), "Umbral Nivel 3"));
        config.setUmbralKcalsNivel4(parseIntParam(request.getParameter("umbralKcalsNivel4"), "Umbral Nivel 4"));
        config.setLimiteWatchlistNormal(parseIntParam(request.getParameter("limiteWatchlistNormal"), "Límite Watchlist Normal"));
        config.setLimiteWatchlistActivo(parseIntParam(request.getParameter("limiteWatchlistActivo"), "Límite Watchlist Activo"));
        
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("usuarioLogueado") != null) { 
            User user = (User) session.getAttribute("usuarioLogueado");
            config.setUsuarioAdminID(user.getId());
        } else {
            config.setUsuarioAdminID(null); 
        }
    }

    private int parseIntParam(String param, String fieldName) {
        if (param == null || param.isEmpty()) {
             throw ErrorFactory.validation("El campo '" + fieldName + "' no puede estar vacío.");
        }
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException e) {
            throw ErrorFactory.validation("El campo '" + fieldName + "' debe ser un número entero.");
        }
    }

    private Set<String> getErrorMessages(Set<ConstraintViolation<ConfiguracionReglas>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
    
}