package entity;

public class Country {
	private int id;
	String iso_3166_1;
	String english_name;
	String native_name;
	
	
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
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
