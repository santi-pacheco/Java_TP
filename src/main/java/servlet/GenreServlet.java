package servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import controller.GenreController;
import entity.Genre;
import repository.GenreRepository;
import service.GenreService;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.servlet.ServletContext;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import exception.ErrorFactory;
import exception.AppException;

@WebServlet("/genres")
public class GenreServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private GenreController genreController;
    private Validator validator;
    
    @Override
    public void init() throws ServletException {
        super.init();
        GenreRepository genreRepository = new GenreRepository();
        GenreService genreService = new GenreService(genreRepository);
        this.genreController = new GenreController(genreService);
        
        ServletContext context = getServletContext();
        this.validator = (Validator) context.getAttribute("miValidador");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";
        
        switch (accion) {
            case "listar":
                List<Genre> genres = genreController.getGenres();
                request.setAttribute("genres", genres);
                request.getRequestDispatcher("/genreCrud.jsp").forward(request, response);
                break;
            case "mostrarFormEditar":
            	int idEditar = parseIntParam(request.getParameter("id"), "ID");
                Genre genre = genreController.getGenreById(idEditar);
                request.setAttribute("genre", genre);
                request.getRequestDispatcher("/genreForm.jsp").forward(request, response);
                break;
            case "mostrarFormCrear":
                request.getRequestDispatcher("/genreForm.jsp").forward(request, response);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        
        String jspTarget = "/genreForm.jsp";
        Genre genreFromForm = null;
        
        try {
            switch (accion) {
                case "crear":
                    genreFromForm = new Genre();
                    populateGenreFromRequest(genreFromForm, request);

                    // NUEVO: Validar
                    Set<ConstraintViolation<Genre>> violationsCreate = validator.validate(genreFromForm);
                    if (!violationsCreate.isEmpty()) {
                        request.setAttribute("errors", getErrorMessages(violationsCreate));
                        request.setAttribute("genre", genreFromForm);
                        request.getRequestDispatcher(jspTarget).forward(request, response);
                        return;
                    }
                    
                    genreController.createGenre(genreFromForm);
                    break;
                    
                case "actualizar":
                    genreFromForm = new Genre();
                    genreFromForm.setId(parseIntParam(request.getParameter("id"), "ID"));
                    populateGenreFromRequest(genreFromForm, request);

                    Set<ConstraintViolation<Genre>> violationsUpdate = validator.validate(genreFromForm);
                    if (!violationsUpdate.isEmpty()) {
                        request.setAttribute("errors", getErrorMessages(violationsUpdate));
                        request.setAttribute("genre", genreFromForm);
                        request.getRequestDispatcher(jspTarget).forward(request, response);
                        return;
                    }
                    genreController.modifyGenre(genreFromForm);
                    break;
                    
                case "eliminar":
                    int idEliminar = parseIntParam(request.getParameter("id"), "ID");
                    Genre deleteGenre = new Genre();
                    deleteGenre.setId(idEliminar);
                    genreController.removeGenre(deleteGenre);
                    break;
            }
            
            response.sendRedirect(request.getContextPath() + "/genres?accion=listar&exito=true");

        } catch (AppException e) {
            if (e.getErrorType().equals("DUPLICATE_ERROR")) {
                request.setAttribute("appError", e.getMessage());
                request.setAttribute("genre", genreFromForm);
                request.getRequestDispatcher(jspTarget).forward(request, response);
            } else {
                throw e;
            }
        
        } catch (Exception e) {
            Set<String> errors = Set.of(e.getMessage());
            request.setAttribute("errors", errors);
            request.setAttribute("genre", genreFromForm); 
            request.getRequestDispatcher(jspTarget).forward(request, response);
        }
    }
    
    private void populateGenreFromRequest(Genre genre, HttpServletRequest request) {
        genre.setName(request.getParameter("name"));
        genre.setId_api(parseIntParam(request.getParameter("id_api"), "ID API"));
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
    
    private Set<String> getErrorMessages(Set<ConstraintViolation<Genre>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}