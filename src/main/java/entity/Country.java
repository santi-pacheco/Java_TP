package entity;

import jakarta.validation.constraints.*;

public class Country {
	private int countryId;

	@NotNull(message = "El código ISO no puede ser nulo")
	@Size(min = 2, max = 2, message = "El código ISO debe tener exactamente 2 caracteres")
	@Pattern(regexp = "[A-Z]{2}", message = "El código ISO debe consistir en 2 letras mayúsculas")
	private String isoCode;

	@NotBlank(message = "El nombre no puede estar vacío ni ser nulo")
	@Size(max = 100, message = "El nombre no debe exceder los 100 caracteres")
	private String name;

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
