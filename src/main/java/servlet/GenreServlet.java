package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
        //String apiKey = "a47ba0b127499b0e1b28ceb0a183ec57";
        this.genreController = new GenreController();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Determinar si es una solicitud AJAX o una solicitud normal
        String acceptHeader = request.getHeader("Accept");
        String xRequestedWith = request.getHeader("X-Requested-With");
        boolean isAjaxRequest = (xRequestedWith != null && xRequestedWith.equals("XMLHttpRequest")) || 
                            (acceptHeader != null && acceptHeader.contains("application/json"));
        
        // Obtener la lista de géneros
        List<Genre> genres = genreController.getGenres();
        
        // Si es una solicitud AJAX, devuelve JSON
        if (isAjaxRequest) {
            response.setContentType("application/json;charset=UTF-8");
            String json = gson.toJson(genres);
            response.getWriter().write(json);
        } 
        // Si es una solicitud normal, redirige a JSP
        else {
            // Establecer atributos para la vista JSP
            request.setAttribute("genres", genres);
            
            // Establecer el tipo de contenido para HTML
            response.setContentType("text/html;charset=UTF-8");
            
            // Reenviar al JSP desde el context root
            request.getRequestDispatcher("/genreCrud.jsp").forward(request, response);
        }
    }
    
    
    
    
    
}