package entity;

import java.sql.Date;

public class Person {
	
	private int id;
	private int id_api;
	private String name;
	private String also_known_as;
	private String place_of_birth;
	private Date BirthDate;
	private String profile_path;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId_api() {
		return id_api;
	}

	public void setId_api(int id_api) {
		this.id_api = id_api;
	}

	public String getAlso_known_as() {
		return also_known_as;
	}

	public void setAlso_known_as(String also_known_as) {
		this.also_known_as = also_known_as;
	}

	public String getPlace_of_birth() {
		return place_of_birth;
	}

	public void setPlace_of_birth(String place_of_birth) {
		this.place_of_birth = place_of_birth;
	}

	public Date getBirthDate() {
		return BirthDate;
	}

	public void setBirthDate(Date BirthDate) {
		this.BirthDate = BirthDate;
	}
	
	public String getProfile_path() {
		return profile_path;
	}

	public void setProfile_path(String profile_path) {
		this.profile_path = profile_path;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", id_api=" + id_api + ", name=" + name + ", also_known_as=" + also_known_as
				+ ", place_of_birth=" + place_of_birth + ", BirthDate=" + BirthDate + "]";
	}
}
