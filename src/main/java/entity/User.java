package entity;

import java.sql.Date;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import java.util.LinkedList;
import validations.OnCreate;

public class User {
    private int userId;

    @NotBlank(message = "El nombre de usuario no puede estar vacío.")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres.")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía.", groups = OnCreate.class)
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

    private int totalKcals = 0;
    private int userLevel = 1;
    private int notifiedLevel = 1;
    private Integer mainDishMovieId;

    private String profileImage;
    private java.sql.Timestamp bannedUntil;
    private LocalDateTime lastNotificationCheck;

    private LinkedList<String> watchlist;

    public LocalDateTime getLastNotificationCheck() {
        return lastNotificationCheck;
    }

    public void setLastNotificationCheck(LocalDateTime lastNotificationCheck) {
        this.lastNotificationCheck = lastNotificationCheck;
    }

    public int getTotalKcals() {
        return totalKcals;
    }

    public void setTotalKcals(int totalKcals) {
        this.totalKcals = totalKcals;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public LinkedList<String> getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(LinkedList<String> watchlist) {
        this.watchlist = watchlist;
    }

    public void addToWatchlist(String movie) {
        if (this.watchlist == null) {
            this.watchlist = new LinkedList<>();
        }
        this.watchlist.add(movie);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public int getNotifiedLevel() {
        return notifiedLevel;
    }

    public void setNotifiedLevel(int notifiedLevel) {
        this.notifiedLevel = notifiedLevel;
    }

    public Integer getMainDishMovieId() {
        return mainDishMovieId;
    }

    public void setMainDishMovieId(Integer mainDishMovieId) {
        this.mainDishMovieId = mainDishMovieId;
    }

    public java.sql.Timestamp getBannedUntil() {
        return bannedUntil;
    }

    public void setBannedUntil(java.sql.Timestamp bannedUntil) {
        this.bannedUntil = bannedUntil;
    }

    public boolean isBanned() {
        if (bannedUntil == null) return false;
        return bannedUntil.after(new java.sql.Timestamp(System.currentTimeMillis()));
    }
}
