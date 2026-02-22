package entity;

import java.sql.Timestamp;

public class ReviewLike {
    private int idUsuario;
    private int idReview;
    private Timestamp createdAt;

    public ReviewLike() {}

    public ReviewLike(int idUsuario, int idReview) {
        this.idUsuario = idUsuario;
        this.idReview = idReview;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdReview() { return idReview; }
    public void setIdReview(int idReview) { this.idReview = idReview; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
