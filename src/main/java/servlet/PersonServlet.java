package servlet;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.sql.Date;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;

import controller.PersonController;
import entity.Person;
import repository.PersonRepository;
import service.PersonService;
import exception.ErrorFactory;
import exception.AppException;

@WebServlet("/persons")
public class PersonServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PersonController personController;
    private Validator validator;
    
    @Override
    public void init() throws ServletException {
        super.init();
        PersonRepository personRepository = new PersonRepository();
        PersonService personService = new PersonService(personRepository);
        this.personController = new PersonController(personService);
        
        ServletContext context = getServletContext();
        this.validator = (Validator) context.getAttribute("miValidador");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";
        
        switch (accion) {
            case "listar":
                List<Person> persons = personController.getPersons();
                request.setAttribute("persons", persons);
                request.getRequestDispatcher("/personCrud.jsp").forward(request, response);
                break;
            case "mostrarFormEditar":
                int idEditar = parseIntParam(request.getParameter("id"), "ID");
                Person person = personController.getPersonById(idEditar);
                request.setAttribute("person", person);
                request.getRequestDispatcher("/personForm.jsp").forward(request, response);
                break;
            case "mostrarFormCrear":
                request.getRequestDispatcher("/personForm.jsp").forward(request, response);
                break;
            default:
                throw ErrorFactory.badRequest("Acción inválida");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) throw ErrorFactory.badRequest("Acción requerida");

        if ("eliminar".equals(accion)) {
            int idEliminar = parseIntParam(request.getParameter("id"), "ID");
            Person deletePerson = new Person();
            deletePerson.setPersonId(idEliminar);
            personController.removePerson(deletePerson);
            
            response.sendRedirect(request.getContextPath() + "/persons?accion=listar&exito=true");
            return;
        }
        
        String jspTarget = "/personForm.jsp";
        Person personFromForm = new Person();
        
        try {
            if ("crear".equals(accion)) {
                populatePersonFromRequest(personFromForm, request);

                Set<ConstraintViolation<Person>> violationsCreate = validator.validate(personFromForm);
                if (!violationsCreate.isEmpty()) {
                    request.setAttribute("errors", getErrorMessages(violationsCreate));
                    request.setAttribute("person", personFromForm);
                    request.getRequestDispatcher(jspTarget).forward(request, response);
                    return;
                }  
                personController.createPerson(personFromForm);
                
            } else if ("actualizar".equals(accion)) {
                personFromForm.setPersonId(parseIntParam(request.getParameter("id"), "ID"));
                populatePersonFromRequest(personFromForm, request);
                
                Set<ConstraintViolation<Person>> violationsUpdate = validator.validate(personFromForm);
                if (!violationsUpdate.isEmpty()) {
                    request.setAttribute("errors", getErrorMessages(violationsUpdate));
                    request.setAttribute("person", personFromForm);
                    request.getRequestDispatcher(jspTarget).forward(request, response);
                    return;
                }
                personController.modifyPerson(personFromForm); 
                
            } else {
                throw ErrorFactory.badRequest("Acción desconocida");
            }  
            response.sendRedirect(request.getContextPath() + "/persons?accion=listar&exito=true");
            
        } catch (AppException e) {
            if (e.getErrorType().equals("DUPLICATE_ERROR") || e.getErrorType().equals("VALIDATION_ERROR")) {
                request.setAttribute("appError", e.getMessage());
                request.setAttribute("person", personFromForm);
                request.getRequestDispatcher(jspTarget).forward(request, response);
            } else {
                throw e;
            }
        }
    }
    

    private void populatePersonFromRequest(Person person, HttpServletRequest request) {
        person.setName(request.getParameter("name"));
        person.setAlsoKnownAs(request.getParameter("alsoKnownAs"));
        person.setPlaceOfBirth(request.getParameter("placeOfBirth"));
        person.setProfilePath(request.getParameter("profilePath"));
        
        String apiIdParam = request.getParameter("apiId");
        if (apiIdParam != null && !apiIdParam.trim().isEmpty()) {
            try {
                person.setApiId(Integer.parseInt(apiIdParam.trim()));
            } catch (NumberFormatException e) {
                person.setApiId(0);
            }
        }
        
        String birthdateParam = request.getParameter("birthdate");
        if (birthdateParam != null && !birthdateParam.trim().isEmpty()) {
            try {
                person.setBirthdate(Date.valueOf(birthdateParam));
            } catch (IllegalArgumentException e) {
                person.setBirthdate(null);
            }
        } else {
            person.setBirthdate(null);
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
    
    private Set<String> getErrorMessages(Set<ConstraintViolation<Person>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}