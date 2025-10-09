package controller;

import service.EstrenoService;
import entity.Estreno;

public class EstrenoController {
	
	private EstrenoService estrenoService;
	
	public EstrenoController(EstrenoService estrenoService) {
		this.estrenoService = estrenoService;
	}
	
	public boolean addEstreno(Estreno e) {
		try {
			Estreno createdEstreno = estrenoService.createEstreno(e);
			if (createdEstreno != null) {
	            return true;
	        } else {
	            return false;
	        }
		} catch (Exception ex) {
			throw new RuntimeException("Error adding estreno", ex);
		}
	}
	
	public Estreno getEstrenoByYear(int year) {
		try {
			Estreno estrenoByYear = estrenoService.getEstrenoByYear(year);
			return estrenoByYear;
		} catch (Exception e) {
			throw new RuntimeException("Error getting estreno by year", e);
		}	
	}
}
