package entity;

import jakarta.validation.constraints.*;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Movie {

    private int movieId;
    private Integer apiId;

    @NotNull(message = "El año de estreno es obligatorio")
    @Min(value = 1888, message = "El año de estreno debe ser 1888 o posterior")
    @Max(value = 2030, message = "El año de estreno no puede ser tan a futuro")
    private int releaseYear;

    private LocalDate releaseDate;

    @NotNull(message = "La duración no puede ser nula")
    private Time duration;

    @NotNull(message = "Debe especificar si la película es para adultos")
    private Boolean isAdult;

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 255, message = "El título no debe exceder los 255 caracteres")
    private String title;

    @NotNull(message = "La popularidad no puede ser nula")
    @PositiveOrZero(message = "La popularidad debe ser 0 o un número positivo")
    private Double popularity;

    private String imdbId;

    @NotNull(message = "El conteo de votos no puede ser nulo")
    @PositiveOrZero(message = "El número de votos debe ser 0 o más")
    private int apiVotes;

    @NotBlank(message = "El título original no puede estar vacío")
    @Size(max = 255, message = "El título original no debe exceder los 255 caracteres")
    private String originalTitle;

    @NotBlank(message = "La sinopsis no puede estar vacía")
    private String synopsis;

    @NotNull(message = "La puntuación no puede ser nula")
    @DecimalMin(value = "0.0", message = "La puntuación debe ser como mínimo 0.0")
    @DecimalMax(value = "10.0", message = "La puntuación debe ser como máximo 10.0")
    private Double apiRating;

    @NotBlank(message = "El idioma original no puede estar vacío")
    @Size(min = 2, max = 2, message = "El idioma original debe ser un código ISO de 2 letras")
    private String originalLanguage;

    @NotBlank(message = "La ruta del póster (poster_path) es obligatoria")
    private String posterPath;

    private Float rating;

    private Double localRatingAvg = 0.0;
    private Integer localReviewsCount = 0;

    private List<Integer> temporaryGenres;

    public int getMovieId() {
        return movieId;
    }
    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
    public Integer getApiId() {
        return apiId;
    }
    public void setApiId(Integer apiId) {
        this.apiId = apiId;
    }
    public int getReleaseYear() {
        return releaseYear;
    }
    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }
    public LocalDate getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
    public Time getDuration() {
        return duration;
    }
    public void setDuration(Time duration) {
        this.duration = duration;
    }
    public Boolean getIsAdult() {
        return isAdult;
    }
    public void setIsAdult(Boolean isAdult) {
        this.isAdult = isAdult;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Double getPopularity() {
        return popularity;
    }
    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }
    public String getImdbId() {
        return imdbId;
    }
    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }
    public int getApiVotes() {
        return apiVotes;
    }
    public void setApiVotes(int apiVotes) {
        this.apiVotes = apiVotes;
    }
    public String getOriginalTitle() {
        return originalTitle;
    }
    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }
    public String getSynopsis() {
        return synopsis;
    }
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
    public Double getApiRating() {
        return apiRating;
    }
    public void setApiRating(Double apiRating) {
        this.apiRating = apiRating;
    }
    public String getOriginalLanguage() {
        return originalLanguage;
    }
    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }
    public String getPosterPath() {
        return posterPath;
    }
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
    public Float getRating() {
        return rating;
    }
    public void setRating(Float rating) {
        this.rating = rating;
    }
    public Double getLocalRatingAvg() {
        return localRatingAvg;
    }
    public void setLocalRatingAvg(Double localRatingAvg) {
        this.localRatingAvg = localRatingAvg;
    }
    public Integer getLocalReviewsCount() {
        return localReviewsCount;
    }
    public void setLocalReviewsCount(Integer localReviewsCount) {
        this.localReviewsCount = localReviewsCount;
    }
    public List<Integer> getTemporaryGenres() {
        return temporaryGenres;
    }
    public void setTemporaryGenres(List<Integer> temporaryGenres) {
        this.temporaryGenres = temporaryGenres;
    }

    @Override
    public String toString() {
        return "Movie [movieId=" + movieId + ", apiId=" + apiId + ", releaseYear=" + releaseYear + ", duration=" + duration
                + ", isAdult=" + isAdult + ", title=" + title + ", popularity=" + popularity + ", apiVotes="
                + apiVotes + ", originalTitle=" + originalTitle + ", synopsis=" + synopsis + ", apiRating="
                + apiRating + ", originalLanguage=" + originalLanguage + ", posterPath=" + posterPath + "]";
    }
}
