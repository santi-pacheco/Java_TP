package entity;

import jakarta.validation.constraints.*;

public class Country {
	private int id;
	
	@NotNull(message = "El código ISO no puede ser nulo")
    @Size(min = 2, max = 2, message = "El código ISO debe tener exactamente 2 caracteres")
    @Pattern(regexp = "[A-Z]{2}", message = "El código ISO debe consistir en 2 letras mayúsculas")
	private String iso_3166_1;
	
	
	@NotBlank(message = "El nombre en inglés no puede estar vacío ni ser nulo")
    @Size(max = 100, message = "El nombre en inglés no debe exceder los 100 caracteres")
	private String english_name;
	
	private String native_name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIso_3166_1() {
		return iso_3166_1;
	}

	public void setIso_3166_1(String iso_3166_1) {
		this.iso_3166_1 = iso_3166_1;
	}

	public String getEnglish_name() {
		return english_name;
	}

	public void setEnglish_name(String english_name) {
		this.english_name = english_name;
	}

	public String getNative_name() {
		return native_name;
	}

	public void setNative_name(String native_name) {
		this.native_name = native_name;
	}

}
