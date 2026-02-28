package entity;

public class FeedReviewDTO {
	private int movieId;
    private int reviewId;
    private int userId;
    private String posterPath;
    private String username;
    private String userAvatar;
    private String dateFormatted;
    private double rating;
    private boolean isFollowing;
    private String movieTitle;
    private String text;
    private String moderation_status;
    private int userLevel;

    
    public int getReviewId() {
    	return reviewId;
    }
    public void setReviewId(int reviewId) {
    	this.reviewId = reviewId;
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
    
    public String getUserAvatar() {
    	return userAvatar;
    }
    
    public void setUserAvatar(String userAvatar) {
    	this.userAvatar = userAvatar;
    }
    
    public String getDateFormatted() {
    	return dateFormatted;
    }
    public void setDateFormatted(String dateFormatted) {
    	this.dateFormatted = dateFormatted;
    }
    
    public double getRating() {
    	return rating;
    }
    public void setRating(double rating) {
    	this.rating = rating;
    }
    
    public String getMovieTitle() {
    	return movieTitle;
    }
    public void setMovieTitle(String movieTitle) {
    	this.movieTitle = movieTitle;
    }
    
    public String getText() {
    	return text;
    }
    
    public void setText(String text) {
    	this.text = text;
    }
    
    public int getMovieId() {
    	return movieId;
    }
    
    public void setMovieId(int movieId) {
    	this.movieId = movieId;
    }
    
    public String getPosterPath() {
    	return posterPath;
    }
    
    public void setPosterPath(String posterPath) {
    	this.posterPath = posterPath;
    }
    
	public boolean isFollowing() {
		return isFollowing;
	}
	public void setFollowing(boolean isFollowing) {
		this.isFollowing = isFollowing;
	}
	
	public String getModeration_status() {
		return moderation_status;
	}
	
	public void setModeration_status(String moderation_status) {
		this.moderation_status = moderation_status;
	}
	
	public int getUserLevel() {
		return userLevel;
	}
	public void setUserLevel(int userLevel) {
		this.userLevel = userLevel;
	}
	
    
}