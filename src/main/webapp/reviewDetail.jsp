<%@ page import="entity.Review"%>
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
            <span class="info-label">Película ID:</span>
            <span><%= review.getId_movie() %></span>
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
        
        <h4 style="margin-top: 30px;">Texto de la Reseña:</h4>
        <div class="review-text">
            <%= review.getReview_text() %>
        </div>
        
        <h4>Estado de Spoiler:</h4>
        <form action="<%= request.getContextPath() %>/reviews-admin" method="POST">
            <input type="hidden" name="accion" value="actualizarSpoiler">
            <input type="hidden" name="id" value="<%= review.getId() %>">
            
            <div class="form-group">
                <label>
                    <input type="radio" name="contieneSpoiler" value="false" <%= review.getContieneSpoiler() != null && !review.getContieneSpoiler() ? "checked" : "" %>>
                    No contiene spoiler
                </label>
                <br>
                <label>
                    <input type="radio" name="contieneSpoiler" value="true" <%= review.getContieneSpoiler() != null && review.getContieneSpoiler() ? "checked" : "" %>>
                    Contiene spoiler
                </label>
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
