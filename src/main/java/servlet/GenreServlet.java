package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import controller.GenreController;
import entity.Genre;

@WebServlet("/genres")
public class GenreServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private GenreController genreController;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        super.init();
        // Inicializar el controlador con la API key
        String apiKey = "a47ba0b127499b0e1b28ceb0a183ec57";
        this.genreController = new GenreController(apiKey);
        this.gson = new Gson();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null || action.equals("list")) {
            handleListGenres(request, response);
        } else if (action.equals("json")) {
            handleJsonGenres(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }
    
    private void handleListGenres(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            List<Genre> genres = genreController.getGenres();
            
            // Establecer el tipo de contenido para HTML
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            // Reenviar al JSP desde el context root
            request.getRequestDispatcher("/genreCrud.jsp").forward(request, response);
        }
    }
}