package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import controller.DatosApiController;
import service.DatosApiService;
import exception.AppException;

@WebServlet("/data-load")
public class DatosApiServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private DatosApiController datosApiController;

    @Override
    public void init() throws ServletException {
        super.init();
        DatosApiService datosApiService = new DatosApiService();
        this.datosApiController = new DatosApiController(datosApiService);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/cargaDatos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        String mensaje = "";
        String tipoMensaje = "success";

        if (accion == null) {
            response.sendRedirect(request.getContextPath() + "/data-load?error=AccionNoEspecificada");
            return;
        }

        try {
            switch (accion) {
                case "loadGenres":
                    System.out.println("Iniciando carga de géneros...");
                    datosApiController.loadGenres();
                    mensaje = "Géneros cargados correctamente.";
                    break;
                case "loadMovies":
                    System.out.println("Iniciando carga de películas...");
                    datosApiController.loadMovies();
                    mensaje = "Películas actualizadas correctamente.";
                    break;
                case "loadDetails":
                    System.out.println("Iniciando carga de detalles (Actores/Directores/Paises)...");
                    datosApiController.loadActorsDirectorsAndRuntime();
                    mensaje = "Detalles de películas (Cast, Crew, Runtime, Paises) cargados correctamente.";
                    break;
                case "loadPersons":
                    System.out.println("Iniciando carga profunda de personas...");
                    datosApiController.loadPersons();
                    mensaje = "Datos detallados de Personas cargados correctamente.";
                    break;
                default:
                    mensaje = "Acción no reconocida.";
                    tipoMensaje = "danger";
                    break;
            }
            request.getSession().setAttribute("flashMessage", mensaje);
            request.getSession().setAttribute("flashType", tipoMensaje);
            response.sendRedirect(request.getContextPath() + "/data-load");
        } catch (AppException e) {
        	System.err.println("Error operativo controlado: " + e.getMessage());
            request.getSession().setAttribute("flashMessage", "⚠️ No se pudo completar: " + e.getMessage());
            request.getSession().setAttribute("flashType", "danger");
            response.sendRedirect(request.getContextPath() + "/data-load");
        } catch (Exception e) {
        	throw new ServletException("Error crítico en carga de datos", e);
        }
    }
}