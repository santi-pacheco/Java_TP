package entity;

import java.sql.Date;
import jakarta.validation.constraints.*;

public class Person {

private int personId;
    
    private int apiId;
   
    @NotBlank(message = "El nombre de la persona no puede estar vacío")
    @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres")
    private String name;
    
    private String alsoKnownAs;
    
    private String placeOfBirth;
    
    @PastOrPresent(message = "La fecha de nacimiento no puede ser una fecha futura")
    private Date birthdate;
    
    private String profilePath;
	public int getPersonId() {
		return personId;
	}

	public void setPersonId(int personId) {
		this.personId = personId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getApiId() {
		return apiId;
	}

	public void setApiId(int apiId) {
		this.apiId = apiId;
	}

	public String getAlsoKnownAs() {
		return alsoKnownAs;
	}

	public void setAlsoKnownAs(String alsoKnownAs) {
		this.alsoKnownAs = alsoKnownAs;
	}

	public String getPlaceOfBirth() {
		return placeOfBirth;
	}

	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public String getProfilePath() {
		return profilePath;
	}

	public void setProfilePath(String profilePath) {
		this.profilePath = profilePath;
	}

	@Override
	public String toString() {
		return "Person [personId=" + personId + ", apiId=" + apiId + ", name=" + name + ", alsoKnownAs=" + alsoKnownAs
				+ ", placeOfBirth=" + placeOfBirth + ", birthdate=" + birthdate + "]";
	}
}
