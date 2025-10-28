package entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class Review {
	private int id;
	private int id_user;
	private int id_movie;
	private String review_text;
	private Double rating;
	private LocalDate review_date;
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId_user() {
		return id_user;
	}

	public void setId_user(int id_user) {
		this.id_user = id_user;
	}

	public int getId_movie() {
		return id_movie;
	}

	public void setId_movie(int id_movie) {
		this.id_movie = id_movie;
	}

	public String getReview_text() {
		return review_text;
	}

	public void setReview_text(String review_text) {
		this.review_text = review_text;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public LocalDate getReview_date() {
		return review_date;
	}

	public void setReview_date(LocalDate localTime) {
		this.review_date = localTime;
	}


}
