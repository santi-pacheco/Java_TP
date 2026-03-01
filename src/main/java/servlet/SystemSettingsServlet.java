package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import controller.SystemSettingsController;
import entity.SystemSettings;
import repository.SystemSettingsRepository;
import service.SystemSettingsService;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.servlet.ServletContext;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import exception.AppException;
import exception.ErrorFactory;
import entity.User;

@WebServlet("/system-settings")
public class SystemSettingsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SystemSettingsController controller;
    private Validator validator;

    @Override
    public void init() throws ServletException {
        super.init();
        SystemSettingsRepository repository = new SystemSettingsRepository();
        SystemSettingsService service = new SystemSettingsService(repository);
        this.controller = new SystemSettingsController(service);

        ServletContext context = getServletContext();
        this.validator = (Validator) context.getAttribute("miValidador");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<SystemSettings> configuraciones = controller.getAllSystemSettings();
        request.setAttribute("configuraciones", configuraciones);
        request.getRequestDispatcher("/WEB-INF/views/system-settings/SystemSettingsCRUD.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String jspTarget = "/WEB-INF/views/system-settings/SystemSettingsCRUD.jsp";
        SystemSettings configFromForm = null;

        try {
            configFromForm = new SystemSettings();
            populateConfigFromRequest(configFromForm, request);
            Set<ConstraintViolation<SystemSettings>> violations = validator.validate(configFromForm);

            if (!violations.isEmpty()) {
                request.setAttribute("errors", getErrorMessages(violations));
                request.setAttribute("configForm", configFromForm);
                request.setAttribute("configuraciones", controller.getAllSystemSettings());
                request.getRequestDispatcher(jspTarget).forward(request, response);
                return;
            }
            controller.addSystemSettings(configFromForm);
            response.sendRedirect(request.getContextPath() + "/system-settings?exito=true");

        } catch (AppException e) {
            Set<String> errors = Set.of(e.getMessage());
            request.setAttribute("errors", errors);
            request.setAttribute("configForm", configFromForm);
            request.setAttribute("configuraciones", controller.getAllSystemSettings());
            request.getRequestDispatcher(jspTarget).forward(request, response);

        } catch (Exception e) {
            System.err.println("Error no esperado en SystemSettingsServlet: " + e.getMessage());
            throw e;
        }
    }

    private void populateConfigFromRequest(SystemSettings config, HttpServletRequest request) {

        config.setKcalsToLevel2(parseIntParam(request.getParameter("kcalsToLevel2"), "Umbral Nivel 2"));
        config.setKcalsToLevel3(parseIntParam(request.getParameter("kcalsToLevel3"), "Umbral Nivel 3"));
        config.setKcalsToLevel4(parseIntParam(request.getParameter("kcalsToLevel4"), "Umbral Nivel 4"));
        config.setNormalWatchlistLimit(parseIntParam(request.getParameter("normalWatchlistLimit"), "Límite Watchlist Normal"));
        config.setActiveWatchlistLimit(parseIntParam(request.getParameter("activeWatchlistLimit"), "Límite Watchlist Activo"));

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("usuarioLogueado") != null) {
            User user = (User) session.getAttribute("usuarioLogueado");
            config.setAdminUserId(user.getUserId());
        } else {
            config.setAdminUserId(null);
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

    private Set<String> getErrorMessages(Set<ConstraintViolation<SystemSettings>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}
