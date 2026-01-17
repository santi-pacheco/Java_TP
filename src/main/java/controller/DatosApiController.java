package controller;
import service.DatosApiService;

public class DatosApiController {

	private DatosApiService datosApiService;
	
	public DatosApiController(DatosApiService datosApiService) {
			this.datosApiService = datosApiService;
	}

	public void loadGenres() {
		datosApiService.loadGenres();
	}
	
	public void loadMovies() {
		datosApiService.loadMovies();
	}
	
	public void loadActorsDirectorsAndRuntime() {
		datosApiService.loadActorsDirectorsAndRuntime();
	}
	
	public void loadPersons() {
		datosApiService.loadPersons();
	}
}
