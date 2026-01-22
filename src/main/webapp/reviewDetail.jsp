<%@ page import="entity.Review"%>
<%@ page import="entity.ModerationStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<title>Detalle de Reseña</title>
<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<style>
body { background: #FAF8F3; font-family: 'Poppins', sans-serif; }
.detail-wrapper { background: #fff; padding: 30px; margin: 50px auto; max-width: 800px; border-radius: 5px; box-shadow: 0 1px 1px rgba(0,0,0,.05); }
.review-text { background: #f9f9f9; padding: 20px; border-radius: 5px; margin: 20px 0; line-height: 1.6; }
.info-row { margin: 10px 0; }
.info-label { font-weight: 600; display: inline-block; width: 150px; }
.status-badge { padding: 5px 10px; border-radius: 3px; font-weight: 600; display: inline-block; }
.status-pending { background-color: #fff3cd; color: #856404; }
.status-approved { background-color: #d4edda; color: #155724; }
.status-rejected { background-color: #f8d7da; color: #721c24; }
</style>
</head>
<body>
<div class="container">
    <div class="detail-wrapper">
        <h2>Detalle de Reseña</h2>
        <% Review review = (Review) request.getAttribute("review"); %>
        <% if (review != null) { %>
        
        <div class="info-row">
            <span class="info-label">ID:</span>
            <span><%= review.getId() %></span>
        </div>
        <div class="info-row">
            <span class="info-label">Usuario:</span>
            <span><%= review.getUsername() != null ? review.getUsername() : "Usuario " + review.getId_user() %></span>
        </div>
        <div class="info-row">
            <span class="info-label">Película:</span>
            <span><%= review.getMovieTitle() != null ? review.getMovieTitle() : "Película #" + review.getId_movie() %></span>
        </div>
        <div class="info-row">
            <span class="info-label">Rating:</span>
            <span><%= String.format("%.1f", review.getRating()) %> / 5.0</span>
        </div>
        <div class="info-row">
            <span class="info-label">Fecha de creación:</span>
            <span><%= review.getCreated_at() %></span>
        </div>
        <div class="info-row">
            <span class="info-label">Visto el:</span>
            <span><%= review.getWatched_on() %></span>
        </div>
        <div class="info-row">
            <span class="info-label">Estado de Moderación:</span>
            <% 
                String statusClass = "";
                String statusText = "";
                if (review.getModerationStatus() == ModerationStatus.PENDING_MODERATION) {
                    statusClass = "status-pending";
                    statusText = "Pendiente de Moderación";
                } else if (review.getModerationStatus() == ModerationStatus.APPROVED) {
                    statusClass = "status-approved";
                    statusText = "Aprobada";
                } else if (review.getModerationStatus() == ModerationStatus.REJECTED) {
                    statusClass = "status-rejected";
                    statusText = "Rechazada";
                }
            %>
            <span class="status-badge <%= statusClass %>"><%= statusText %></span>
        </div>
        <% if (review.getModerationStatus() == ModerationStatus.REJECTED && review.getModerationReason() != null) { %>
        <div class="info-row">
            <span class="info-label">Razón del rechazo:</span>
            <span class="text-danger"><%= review.getModerationReason() %></span>
        </div>
        <% } %>
        
        <h4 style="margin-top: 30px;">Texto de la Reseña:</h4>
        <div class="review-text">
            <%= review.getReview_text() %>
        </div>
        
        <h4>Actualizar Estado de Moderación:</h4>
        <form action="<%= request.getContextPath() %>/reviews-admin" method="POST">
            <input type="hidden" name="accion" value="actualizarModeracion">
            <input type="hidden" name="id" value="<%= review.getId() %>">
            
            <div class="form-group">
                <label>
                    <input type="radio" name="status" value="PENDING_MODERATION" <%= review.getModerationStatus() == ModerationStatus.PENDING_MODERATION ? "checked" : "" %>>
                    Pendiente de Moderación
                </label>
                <br>
                <label>
                    <input type="radio" name="status" value="APPROVED" <%= review.getModerationStatus() == ModerationStatus.APPROVED ? "checked" : "" %>>
                    Aprobar
                </label>
                <br>
                <label>
                    <input type="radio" name="status" value="REJECTED" <%= review.getModerationStatus() == ModerationStatus.REJECTED ? "checked" : "" %>>
                    Rechazar
                </label>
            </div>
            
            <div class="form-group">
                <label for="reason">Razón (opcional para aprobar, requerido para rechazar):</label>
                <textarea class="form-control" name="reason" id="reason" rows="3"><%= review.getModerationReason() != null ? review.getModerationReason() : "" %></textarea>
            </div>
            
            <button type="submit" class="btn btn-primary">Guardar Estado</button>
            <a href="<%= request.getContextPath() %>/reviews-admin" class="btn btn-default">Volver al Listado</a>
        </form>
        
        <% } else { %>
        <div class="alert alert-warning">Reseña no encontrada</div>
        <a href="<%= request.getContextPath() %>/reviews-admin" class="btn btn-default">Volver al Listado</a>
        <% } %>
    </div>
</div>
</body>
</html>
