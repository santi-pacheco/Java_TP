package servlet;

import java.io.IOException;
import java.util.List;
import service.ExternalApiService;
import repository.CountryRepository;
import controller.CountryController;
import jakarta.servlet.ServletException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import controller.GenreController;
import entity.Country;
import info.movito.themoviedbapi.tools.TmdbException;

@WebServlet("/countries")
public class CountryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private CountryController countryController;
	private Gson gson;
	
	@Override
	public void init() throws ServletException {
		super.init();
		this.countryController = new CountryController();
		
		this.countryController = new CountryController();
		
	}
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// Determinar si es una solicitud AJAX o una solicitud normal
		String acceptHeader = request.getHeader("Accept");
		String xRequestedWith = request.getHeader("X-Requested-With");
		boolean isAjaxRequest = (xRequestedWith != null && xRequestedWith.equals("XMLHttpRequest")) || 
							(acceptHeader != null && acceptHeader.contains("application/json"));
		
		
		try {

			CountryRepository countryRepository = new CountryRepository();
			List<Country> countries = countryRepository.findAll();
			// Si es una solicitud AJAX, devuelve JSON
			if (isAjaxRequest) {
				response.setContentType("application/json;charset=UTF-8");
				String json = gson.toJson(countries);
				response.getWriter().write(json);
			} 
			// Si es una solicitud normal, redirige a JSP
			else {
				// Establecer atributos para la vista JSP
				request.setAttribute("countries", countries);
				
				// Establecer el tipo de contenido para HTML
				response.setContentType("text/html;charset=UTF-8");
				
				// Reenviar al JSP desde el context root
				request.getRequestDispatcher("/countryCrud.jsp").forward(request, response);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Obtener la lista de países
		
		
		
	}
}	
