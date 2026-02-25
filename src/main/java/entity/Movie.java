package entity;

import jakarta.validation.constraints.*;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Movie {

    private int id;
    private int id_api; //
    
    @NotNull(message = "El año de estreno es obligatorio")
    @Min(value = 1888, message = "El año de estreno debe ser 1888 o posterior")
    @Max(value = 2030, message = "El año de estreno no puede ser tan a futuro")
    private int estrenoYear; //
    
    private LocalDate FechaEstreno; 
    
    @NotNull(message = "La duración no puede ser nula")
    private Time duracion;
    
    @NotNull(message = "Debe especificar si la película es para adultos")
    private Boolean adulto; //
    
    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 255, message = "El título no debe exceder los 255 caracteres")
    private String titulo; //
    
    @NotNull(message = "La popularidad no puede ser nula")
    @PositiveOrZero(message = "La popularidad debe ser 0 o un número positivo")
    private Double popularidad; //
    
    private String id_imdb; //
    
    //@Column(name = "votos_api")
    @NotNull(message = "El conteo de votos no puede ser nulo")
    @PositiveOrZero(message = "El número de votos debe ser 0 o más")
    private int votosApi; //
    
    //@Column(name = "titulo_original")
    @NotBlank(message = "El título original no puede estar vacío")
    @Size(max = 255, message = "El título original no debe exceder los 255 caracteres")
    private String tituloOriginal; //
    
    //@Column(columnDefinition = "TEXT")
    @NotBlank(message = "La sinopsis no puede estar vacía")
    private String sinopsis; //
    
    //@Column(name = "puntuacion_api")
    @NotNull(message = "La puntuación no puede ser nula")
    @DecimalMin(value = "0.0", message = "La puntuación debe ser como mínimo 0.0")
    @DecimalMax(value = "10.0", message = "La puntuación debe ser como máximo 10.0")
    private Double puntuacionApi; //
    
    //@Column(name = "idioma_original")
    @NotBlank(message = "El idioma original no puede estar vacío")
    @Size(min = 2, max = 2, message = "El idioma original debe ser un código ISO de 2 letras")
    private String idiomaOriginal; //
    
    //@Column(name = "poster_path")
    @NotBlank(message = "La ruta del póster (poster_path) es obligatoria")
    private String posterPath; //
    
    
    private Float rating;
    
    private Double promedioResenasLocal = 0.0;
    private Integer cantidadResenasLocal = 0;
    
    //Lista temporal para generar la relación muchos a muchos con géneros
    private List<Integer> generosTemporales;
    
	public void setRating(Float rating) {
		this.rating = rating;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId_api() {
		return id_api;
	}
	public void setId_api(int id_api) {
		this.id_api = id_api;
	} 
	public int getEstrenoYear() {
		return estrenoYear;
	}
	public void setEstrenoYear(int estrenoYear) {
		this.estrenoYear = estrenoYear;
	}
	public Time getDuracion() {
		return duracion;
	}
	public void setDuracion(Time duracion) {
		this.duracion = duracion;
	}
	public Boolean getAdulto() {
		return adulto;
	}
	public void setAdulto(Boolean adulto) {
		this.adulto = adulto;
	}
	public Double getPopularidad() {
		return popularidad;
	}
	public void setPopularidad(Double popularidad) {
		this.popularidad = popularidad;
	}
	public int getVotosApi() {
		return votosApi;
	}
	public void setVotosApi(int votosApi) {
		this.votosApi = votosApi;
	}
	public String getTituloOriginal() {
		return tituloOriginal;
	}
	public void setTituloOriginal(String tituloOriginal) {
		this.tituloOriginal = tituloOriginal;
	}
	public String getSinopsis() {
		return sinopsis;
	}
	public void setSinopsis(String sinopsis) {
		this.sinopsis = sinopsis;
	}
	public Double getPuntuacionApi() {
		return puntuacionApi;
	}
	public void setPuntuacionApi(Double puntuacionApi) {
		this.puntuacionApi = puntuacionApi;
	}
	public String getIdiomaOriginal() {
		return idiomaOriginal;
	}
	public void setIdiomaOriginal(String idiomaOriginal) {
		this.idiomaOriginal = idiomaOriginal;
	}
	public String getPosterPath() {
		return posterPath;
	}
	public void setPosterPath(String posterPath) {
		this.posterPath = posterPath;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getId_imdb() {
		return id_imdb;
	}
	public void setId_imdb(String id_imdb) {
		this.id_imdb = id_imdb;
	}
	
	public float getRating() {
		return rating;
	}
	
	public Double getPromedioResenasLocal() {
		return promedioResenasLocal;
	}
	
	public void setPromedioResenasLocal(Double promedioResenasLocal) {
		this.promedioResenasLocal = promedioResenasLocal;
	}
	
	public Integer getCantidadResenasLocal() {
		return cantidadResenasLocal;
	}
	
	public void setCantidadResenasLocal(Integer cantidadResenasLocal) {
		this.cantidadResenasLocal = cantidadResenasLocal;
	}
	
	public List<Integer> getGenerosTemporales() {
		return generosTemporales;
	}
	public void setGenerosTemporales(List<Integer> generosTemporales) {
		this.generosTemporales = generosTemporales;
	}
	
	public LocalDate getFechaEstreno() {
		return FechaEstreno;
	}
	
	public void setFechaEstreno(LocalDate fechaEstreno) {
		FechaEstreno = fechaEstreno;
	}
	
	@Override
	public String toString() {
		return "Movie [id=" + id + ", id_api=" + id_api + ", estrenoYear=" + estrenoYear + ", duracion=" + duracion
				+ ", adulto=" + adulto + ", titulo=" + titulo + ", popularidad=" + popularidad + ", votosApi="
				+ votosApi + ", tituloOriginal=" + tituloOriginal + ", sinopsis=" + sinopsis + ", puntuacionApi="
				+ puntuacionApi + ", idiomaOriginal=" + idiomaOriginal + ", posterPath=" + posterPath + "]";
	}
}