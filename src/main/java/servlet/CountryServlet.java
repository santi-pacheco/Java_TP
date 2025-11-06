package servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CountryService;
import repository.CountryRepository;
import controller.CountryController;
import entity.Country;

@WebServlet("/countries")
public class CountryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CountryController countryController;
	
	@Override
	public void init() throws ServletException {
		super.init();
		CountryRepository countryRepository = new CountryRepository();
		CountryService countryService = new CountryService(countryRepository);
		this.countryController = new CountryController(countryService);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String accion = request.getParameter("accion");
		if (accion == null) accion = "listar";
		
		switch (accion) {
			case "listar":
				List<Country> countries = countryController.getCountries();
				request.setAttribute("countries", countries);
				request.getRequestDispatcher("/countryCrud.jsp").forward(request, response);
				break;
			case "mostrarFormEditar":
				int idEditar = Integer.parseInt(request.getParameter("id"));
				Country country = countryController.getCountryById(idEditar);
				request.setAttribute("country", country);
				request.getRequestDispatcher("/countryForm.jsp").forward(request, response);
				break;
			case "mostrarFormCrear":
				request.getRequestDispatcher("/countryForm.jsp").forward(request, response);
				break;
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String accion = request.getParameter("accion");
		
		switch (accion) {
			case "crear":
				Country newCountry = new Country();
				newCountry.setIso_3166_1(request.getParameter("iso"));
				newCountry.setEnglish_name(request.getParameter("name"));
				countryController.addCountry(newCountry);
				break;
			case "actualizar":
				Country updateCountry = new Country();
				updateCountry.setId(Integer.parseInt(request.getParameter("id")));
				updateCountry.setEnglish_name(request.getParameter("name"));
				countryController.updateCountry(updateCountry);
				break;
			case "eliminar":
				int idEliminar = Integer.parseInt(request.getParameter("id"));
				Country deleteCountry = new Country();
				deleteCountry.setId(idEliminar);
				countryController.deleteCountry(deleteCountry);
				break;
		}
		response.sendRedirect(request.getContextPath() + "/countries?accion=listar&exito=true");
	}
}
