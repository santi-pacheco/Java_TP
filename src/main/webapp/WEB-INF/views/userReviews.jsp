<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Map, entity.Review, entity.Movie, entity.User" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mis Reseñas - FatMovies</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        
        body {
            font-family: 'Poppins', sans-serif;
            background-color: #FAF8F3;
            color: #333;
            margin: 0;
        }
        .container {
            max-width: 1200px;
            margin: 40px auto;
            padding: 0 20px;
        }
        .header {
            margin-bottom: 40px;
        }
        .header h1 {
            font-size: 36px;
            font-weight: 700;
            color: #333;
            margin-bottom: 10px;
        }
        .header p {
            font-size: 16px;
            color: #666;
        }
        .reviews-grid {
            display: grid;
            gap: 24px;
        }
        .review-card {
            background: white;
            border-radius: 12px;
            padding: 24px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            display: flex;
            gap: 20px;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .review-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.12);
        }
        .movie-poster {
            width: 120px;
            height: 180px;
            border-radius: 8px;
            object-fit: cover;
            flex-shrink: 0;
            cursor: pointer;
        }
        .review-content {
            flex: 1;
            display: flex;
            flex-direction: column;
            gap: 12px;
        }
        .movie-title {
            font-size: 22px;
            font-weight: 600;
            color: #333;
            cursor: pointer;
            text-decoration: none;
        }
        .movie-title:hover {
            color: #666;
        }
        .rating-date {
            display: flex;
            align-items: center;
            gap: 16px;
            flex-wrap: wrap;
        }
        .rating {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 18px;
            font-weight: 600;
            color: #8B7355;
        }
        .date {
            font-size: 14px;
            color: #999;
        }
        .spoiler-badge {
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
        }
        .spoiler-yes {
            background: #FFE5E5;
            color: #D32F2F;
        }
        .spoiler-pending {
            background: #FFF3E0;
            color: #F57C00;
        }
        .review-text {
            font-size: 15px;
            line-height: 1.6;
            color: #555;
        }
        .empty-state {
            text-align: center;
            padding: 80px 20px;
        }
        .empty-state h2 {
            font-size: 24px;
            color: #666;
            margin-bottom: 12px;
        }
        .empty-state p {
            font-size: 16px;
            color: #999;
            margin-bottom: 24px;
        }
        .btn-primary {
            display: inline-block;
            padding: 12px 32px;
            background: #333;
            color: white;
            text-decoration: none;
            border-radius: 24px;
            font-weight: 500;
            transition: background 0.3s;
        }
        .btn-primary:hover {
            background: #555;
        }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/components/navbar-new.jsp" />
    
    <div class="container">
        <div class="header">
            <%
                User user = (User) session.getAttribute("usuarioLogueado");
            %>
            <h1>Mis Reseñas</h1>
            <p>Todas tus reseñas en un solo lugar</p>
        </div>
        
        <%
            List<Review> reviews = (List<Review>) request.getAttribute("reviews");
            Map<Integer, Movie> moviesMap = (Map<Integer, Movie>) request.getAttribute("moviesMap");
            
            if (reviews == null || reviews.isEmpty()) {
        %>
            <div class="empty-state">
                <h2>Aún no has escrito ninguna reseña</h2>
                <p>Explora películas y comparte tu opinión con la comunidad</p>
                <a href="${pageContext.request.contextPath}/movies-page" class="btn-primary">Explorar Películas</a>
            </div>
        <%
            } else {
        %>
            <div class="reviews-grid">
                <%
                    for (Review review : reviews) {
                        Movie movie = moviesMap.get(review.getId_movie());
                        if (movie != null) {
                            String posterUrl = movie.getPosterPath() != null 
                                ? "https://image.tmdb.org/t/p/w500" + movie.getPosterPath()
                                : request.getContextPath() + "/utils/no-poster.png";
                %>
                    <div class="review-card">
                        <img src="<%= posterUrl %>" 
                             alt="<%= movie.getTitulo() %>" 
                             class="movie-poster"
                             onclick="window.location.href='${pageContext.request.contextPath}/movie/<%= movie.getId() %>'"
                             onerror="this.src='${pageContext.request.contextPath}/utils/no-poster.png'">
                        
                        <div class="review-content">
                            <a href="${pageContext.request.contextPath}/movie/<%= movie.getId() %>" class="movie-title">
                                <%= movie.getTitulo() %>
                            </a>
                            
                            <div class="rating-date">
                                <div class="rating">
                                    🍿 <%= String.format("%.1f", review.getRating()) %>/5.0 Kcals
                                </div>
                                <div class="date">
                                    <%= review.getCreated_at() != null ? review.getCreated_at().toString() : "" %>
                                </div>
                                
                                <%
                                    String status = (review.getModerationStatus() != null) ? review.getModerationStatus().toString() : "PENDING_MODERATION";

                                    if ("PENDING_MODERATION".equals(status)) {
                                %>
                                    <span class="spoiler-badge spoiler-pending">Pendiente de revisión</span>
                                <%
                                    } else if ("SPOILER".equals(status)) {
                                %>
                                    <span class="spoiler-badge spoiler-yes" style="background:#ffcccc; color:#cc0000;">Contiene Spoiler</span>
                                <%
                                    }
                                %>
                            </div>
                            
                            <div class="review-text">
                                <%= review.getReview_text() %>
                            </div>
                        </div>
                    </div>
                <%
                        }
                    }
                %>
            </div>
        <%
            }
        %>
    </div>
</body>
</html>