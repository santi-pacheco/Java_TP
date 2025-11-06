<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Perfil - FatMovies</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style>
        body { background: #FAF8F3; font-family: 'Poppins', sans-serif; }
        .profile-container { max-width: 1000px; margin: 30px auto; }
        .profile-header { background: #fff; padding: 30px; border-radius: 5px; box-shadow: 0 1px 3px rgba(0,0,0,.1); margin-bottom: 20px; }
        .profile-header h1 { margin: 0 0 10px 0; color: #333; }
        .stats-box { background: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 1px 3px rgba(0,0,0,.1); margin-bottom: 20px; }
        .chart-container { background: #fff; padding: 20px 20px 50px 20px; border-radius: 5px; box-shadow: 0 1px 3px rgba(0,0,0,.1); margin-bottom: 20px; }
        .bar-chart { display: flex; align-items: flex-end; height: 250px; gap: 10px; margin-top: 40px; padding: 0 10px; position: relative; }
        .bar { flex: 1; background: linear-gradient(180deg, #a08570 0%, #8B7355 100%); border-radius: 8px 8px 0 0; position: relative; transition: all 0.4s ease; box-shadow: 0 2px 4px rgba(0,0,0,0.1); min-height: 5px; }
        .bar:hover { background: linear-gradient(180deg, #8B7355 0%, #6d5a43 100%); transform: translateY(-3px); box-shadow: 0 4px 8px rgba(0,0,0,0.15); }
        .bar-label { position: absolute; bottom: -30px; width: 100%; text-align: center; font-weight: 500; font-size: 13px; color: #666; }
        .bar-value { position: absolute; top: -30px; width: 100%; text-align: center; font-weight: 600; color: #8B7355; font-size: 14px; }
        .reviews-list { background: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 1px 3px rgba(0,0,0,.1); }
        .review-item { border-bottom: 1px solid #eee; padding: 15px 0; }
        .review-item:last-child { border-bottom: none; }
        .rating-stars { color: #f39c12; }
    </style>
</head>
<body>
<div class="container profile-container">
    <a href="<%= request.getContextPath() %>/home" class="btn btn-primary" style="margin-bottom: 20px; background-color: #8B7355; border-color: #8B7355;">
        <i class="glyphicon glyphicon-arrow-left"></i> Volver
    </a>
    
    <div class="profile-header">
        <h1><i class="glyphicon glyphicon-user"></i> ${user.username}</h1>
        <p style="color: #666; margin: 0;">${user.email}</p>
    </div>
    
    <div class="stats-box">
        <h3>Estadísticas</h3>
        <p style="font-size: 18px; margin: 10px 0;">
            <strong>Películas reseñadas:</strong> ${totalReviews}
        </p>
    </div>
    
    <div class="chart-container">
        <h3>Distribución de Calificaciones</h3>
        <div class="bar-chart">
            <c:forEach var="rating" items="${['0.5', '1.0', '1.5', '2.0', '2.5', '3.0', '3.5', '4.0', '4.5', '5.0']}">
                <c:set var="count" value="${ratingDistribution[rating]}" />
                <c:set var="maxHeight" value="220" />
                <c:set var="height" value="${totalReviews > 0 ? (count * maxHeight / totalReviews) : 5}" />
                <div class="bar" style="height: ${height}px;">
                    <c:if test="${count > 0}">
                        <div class="bar-value">${count}</div>
                    </c:if>
                    <div class="bar-label">${rating} ★</div>
                </div>
            </c:forEach>
        </div>
    </div>
    
    <div class="reviews-list">
        <h3>Últimas Reseñas</h3>
        <c:choose>
            <c:when test="${empty recentReviews}">
                <p style="color: #999; text-align: center; padding: 20px;">No has escrito reseñas aún.</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="review" items="${recentReviews}">
                    <div class="review-item">
                        <div style="display: flex; justify-content: space-between; align-items: center;">
                            <div>
                                <a href="<%= request.getContextPath() %>/movie/${review.id_movie}" style="text-decoration: none; color: #8B7355; font-weight: 600; font-size: 16px;">
                                    ${review.movieTitle != null ? review.movieTitle : 'Película ID: '.concat(review.id_movie)}
                                </a>
                                <div class="rating-stars" style="margin-top: 5px;">
                                    <c:forEach begin="1" end="${review.rating}">★</c:forEach>
                                    <c:forEach begin="${review.rating + 1}" end="5">☆</c:forEach>
                                </div>
                            </div>
                            <small style="color: #999;">${review.created_at}</small>
                        </div>
                        <p style="margin: 10px 0 0 0; color: #666;">${review.review_text}</p>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
