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
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.servlet.ServletContext;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import exception.AppException;
import exception.ErrorFactory;


@WebServlet("/countries")
public class CountryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CountryController countryController;
	private Validator validator;
	
	@Override
	public void init() throws ServletException {
		super.init();
		CountryRepository countryRepository = new CountryRepository();
		CountryService countryService = new CountryService(countryRepository);
		this.countryController = new CountryController(countryService);
		
		ServletContext context = getServletContext();
        this.validator = (Validator) context.getAttribute("miValidador");
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String accion = request.getParameter("accion");
		if (accion == null) accion = "listar";
		
		try {
			switch (accion) {
				case "listar":
					List<Country> countries = countryController.getCountries();
					request.setAttribute("countries", countries);
					request.getRequestDispatcher("/countryCrud.jsp").forward(request, response);
					break;
				case "mostrarFormEditar":
					int idEditar = parseIntParam(request.getParameter("id"), "ID"); // NUEVO
					Country country = countryController.getCountryById(idEditar);
					request.setAttribute("country", country);
					request.getRequestDispatcher("/countryForm.jsp").forward(request, response);
					break;
				case "mostrarFormCrear":
					request.getRequestDispatcher("/countryForm.jsp").forward(request, response);
					break;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String accion = request.getParameter("accion");
		
		String jspTarget = "/countryForm.jsp";
        Country countryFromForm = null;
        try {
			switch (accion) {
				case "crear":
					countryFromForm = new Country();
					populateCountryFromRequest(countryFromForm, request);

                    Set<ConstraintViolation<Country>> violationsCreate = validator.validate(countryFromForm);
                    if (!violationsCreate.isEmpty()) {
                        request.setAttribute("errors", getErrorMessages(violationsCreate));
                        request.setAttribute("country", countryFromForm);
                        request.getRequestDispatcher(jspTarget).forward(request, response);
                        return;
                    }
					countryController.createCountry(countryFromForm);
					break;

				case "actualizar":
					countryFromForm = new Country();
					countryFromForm.setId(parseIntParam(request.getParameter("id"), "ID"));
					populateCountryFromRequest(countryFromForm, request);
					
                    Set<ConstraintViolation<Country>> violationsUpdate = validator.validate(countryFromForm);
                    if (!violationsUpdate.isEmpty()) {
                        request.setAttribute("errors", getErrorMessages(violationsUpdate));
                        request.setAttribute("country", countryFromForm);
                        request.getRequestDispatcher(jspTarget).forward(request, response);
                        return;
                    }

					countryController.modifyCountry(countryFromForm);
					break;

				case "eliminar":
					int idEliminar = parseIntParam(request.getParameter("id"), "ID");
					Country deleteCountry = new Country();
					deleteCountry.setId(idEliminar);
					countryController.removeCountry(deleteCountry);
					break;
			}
			
			response.sendRedirect(request.getContextPath() + "/countries?accion=listar&exito=true");

        } catch (AppException e) {
            
            if (e.getErrorType().equals("DUPLICATE_ERROR")) {
                request.setAttribute("appError", e.getMessage());
                request.setAttribute("country", countryFromForm);
                request.getRequestDispatcher(jspTarget).forward(request, response);
            
            } else if (e.getErrorType().equals("VALIDATION_ERROR")) {
                request.setAttribute("errors", Set.of(e.getMessage()));
                request.setAttribute("country", countryFromForm);
                request.getRequestDispatcher(jspTarget).forward(request, response);
            } else {
                throw e;
            }
        } catch (Exception e) {
            throw e;
        }
      }
	
	private void populateCountryFromRequest(Country country, HttpServletRequest request) {
        country.setIso_3166_1(request.getParameter("iso"));
        country.setEnglish_name(request.getParameter("name"));
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
	
	private Set<String> getErrorMessages(Set<ConstraintViolation<Country>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}
