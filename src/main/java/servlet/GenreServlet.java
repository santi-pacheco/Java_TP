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
            
            // Configurar respuesta HTML
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Géneros de Películas</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
            out.println("h1 { color: #333; }");
            out.println("table { border-collapse: collapse; width: 100%; margin-top: 20px; }");
            out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            out.println("th { background-color: #f2f2f2; }");
            out.println("tr:nth-child(even) { background-color: #f9f9f9; }");
            out.println(".btn { background-color: #4CAF50; color: white; padding: 10px 20px; ");
            out.println("text-decoration: none; border-radius: 4px; display: inline-block; margin: 10px 0; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            
            out.println("<h1>Géneros de Películas</h1>");
            out.println("<p>Total de géneros: " + genres.size() + "</p>");
            
            // Botones de acción
            out.println("<a href='genres?action=json' class='btn'>Ver como JSON</a>");
            out.println("<a href='genreCrud.html' class='btn'>Volver al CRUD</a>");
            
            // Tabla de géneros
            out.println("<table>");
            out.println("<tr><th>ID</th><th>Nombre</th></tr>");
            
            for (Genre genre : genres) {
                out.println("<tr>");
                out.println("<td>" + genre.getId() + "</td>");
                out.println("<td>" + genre.getName() + "</td>");
                out.println("</tr>");
            }
            
            out.println("</table>");
            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception e) {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h1>Error</h1>");
            out.println("<p>Error al obtener los géneros: " + e.getMessage() + "</p>");
            out.println("<a href='genreCrud.html'>Volver</a>");
            out.println("</body></html>");
            e.printStackTrace();
        }
    }
    
    private void handleJsonGenres(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            List<Genre> genres = genreController.getGenres();
            
            // Configurar respuesta JSON
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            String jsonResponse = gson.toJson(genres);
            out.print(jsonResponse);
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            String errorJson = gson.toJson(new ErrorResponse("Error al obtener los géneros: " + e.getMessage()));
            out.print(errorJson);
            e.printStackTrace();
        }
    }
    
    // Clase auxiliar para respuestas de error en JSON
    private static class ErrorResponse {
        private String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() {
            return error;
        }
    }
}