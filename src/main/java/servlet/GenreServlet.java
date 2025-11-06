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

@WebServlet("/genres")
public class GenreServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private GenreController genreController;
    
    @Override
    public void init() throws ServletException {
        super.init();
        GenreRepository genreRepository = new GenreRepository();
        GenreService genreService = new GenreService(genreRepository);
        this.genreController = new GenreController(genreService);
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
                int idEditar = Integer.parseInt(request.getParameter("id"));
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
        
        switch (accion) {
            case "crear":
                Genre newGenre = new Genre();
                newGenre.setName(request.getParameter("name"));
                newGenre.setId_api(Integer.parseInt(request.getParameter("id_api")));
                genreController.createGenre(newGenre);
                break;
            case "actualizar":
                Genre updateGenre = new Genre();
                updateGenre.setId(Integer.parseInt(request.getParameter("id")));
                updateGenre.setName(request.getParameter("name"));
                updateGenre.setId_api(Integer.parseInt(request.getParameter("id_api")));
                genreController.modifyGenre(updateGenre);
                break;
            case "eliminar":
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                Genre deleteGenre = new Genre();
                deleteGenre.setId(idEliminar);
                genreController.removeGenre(deleteGenre);
                break;
        }
        response.sendRedirect(request.getContextPath() + "/genres?accion=listar&exito=true");
    }
}