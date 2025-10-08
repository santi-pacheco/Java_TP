package entity;

public class Movie {

    private int id;
    private int id_api;
    private Double puntuacionProm;
    private Estreno estreno;
    private Integer duracion;
    private Boolean adulto;
    //@Column(name = "titulo_original")
    private String tituloOriginal;
    //@Column(columnDefinition = "TEXT")
    private String sinopsis;
    //@Column(name = "puntuacion_api")
    private Double puntuacionApi;
    //@Column(name = "idioma_original")
    private String idiomaOriginal;
    //@Column(name = "poster_path")
    private String posterPath;
    
    
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

	public Estreno getEstreno() {
		return estreno;
	}

	public void setEstreno(Estreno estreno) {
		this.estreno = estreno;
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

	public Double getPuntuacionProm() {
		return puntuacionProm;
	}

	public void setPuntuacionProm(Double puntuacionProm) {
		this.puntuacionProm = puntuacionProm;
	}

	public String getIdiomaOriginal() {
		return idiomaOriginal;
	}

	public void setIdiomaOriginal(String idiomaOriginal) {
		this.idiomaOriginal = idiomaOriginal;
	}

	public Integer getDuracion() {
		return duracion;
	}

	public void setDuracion(Integer duracion) {
		this.duracion = duracion;
	}

	public Boolean getAdulto() {
		return adulto;
	}

	public void setAdulto(Boolean adulto) {
		this.adulto = adulto;
	}

	public String getPosterPath() {
		return posterPath;
	}

	public void setPosterPath(String posterPath) {
		this.posterPath = posterPath;
	}
}