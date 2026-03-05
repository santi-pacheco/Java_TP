package entity;

import jakarta.validation.constraints.*;

public class Genre {
	
	private int genreId;
	
	@NotBlank(message = "El nombre del género no puede estar vacío")
	private String name;
	
	private Integer apiId;
	
	public int getGenreId() {
		return genreId;
	}
	public void setGenreId(int genreId) {
		this.genreId = genreId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getApiId() {
		return apiId;
	}
	public void setApiId(Integer apiId) {
		this.apiId = apiId;
	}
		
}
