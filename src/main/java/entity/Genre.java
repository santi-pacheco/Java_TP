package entity;

import jakarta.validation.constraints.*;

public class Genre {
	
	private int id;
	
	@NotBlank(message = "El nombre del género no puede estar vacío")
	private String name;
	
	private int id_api;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId_api() {
		return id_api;
	}
	public void setId_api(int id_api) {
		this.id_api = id_api;
	}
		
}
