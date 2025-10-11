package entity;

import java.sql.Date;
import jakarta.validation.constraints.*;

public class User {
    private int id; // El ID no se valida porque lo genera la base de datos.
    
    @NotBlank(message = "El nombre de usuario no puede estar vacío.")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres.")
    private String username;
    
    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).*$", message = "La contraseña debe contener mayúscula, minúscula, número y un carácter especial.")
    private String password;
    
    @NotBlank(message = "El rol no puede estar vacío.")
    @Pattern(regexp = "^(admin|user)$", message = "El rol debe ser 'admin' o 'user'.")
    private String role;
    
    @NotBlank(message = "El email no puede estar vacío.")
    @Email(message = "El formato del email es inválido.")
    private String email;
    
    @NotNull(message = "La fecha de nacimiento es obligatoria.")
    @Past(message = "La fecha de nacimiento debe ser una fecha en el pasado.")
    private Date birthDate;
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date b) {
		birthDate = b;
	}
}