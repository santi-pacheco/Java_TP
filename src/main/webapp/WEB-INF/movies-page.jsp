<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="entity.Movie" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Películas - Fat Movies</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            background-color: #FAF8F3;
            font-family: 'Poppins', sans-serif;
            margin: 0;
            padding: 0;
        }
        
        .page-container {
            padding: 40px 0;
        }
        
        .section {
            margin-bottom: 60px;
        }
        
        .section-title {
            font-size: 2.5rem;
            font-weight: 700;
            color: #333;
            margin-bottom: 30px;
            padding: 0 40px;
        }
        
        .carousel-container {
            position: relative;
            overflow: hidden;
            padding: 0 40px;
        }
        
        .carousel {
            display: flex;
            gap: 20px;
            overflow-x: auto;
            scroll-behavior: smooth;
            padding-bottom: 10px;
        }
        
        .carousel::-webkit-scrollbar {
            height: 8px;
        }
        
        .carousel::-webkit-scrollbar-track {
            background: #E0E0E0;
            border-radius: 4px;
        }
        
        .carousel::-webkit-scrollbar-thumb {
            background: #999;
            border-radius: 4px;
        }
        
        .movie-card {
            flex: 0 0 200px;
            background: white;
            border-radius: 15px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            overflow: hidden;
            transition: transform 0.3s, box-shadow 0.3s;
            cursor: pointer;
        }
        
        .movie-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 24px rgba(0,0,0,0.15);
        }
        
        .movie-poster {
            width: 100%;
            height: 300px;
            object-fit: cover;
        }
        
        .movie-info {
            padding: 15px;
        }
        
        .movie-title {
            font-weight: 600;
            font-size: 1rem;
            color: #333;
            margin-bottom: 8px;
            line-height: 1.3;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        .movie-year {
            font-size: 0.9rem;
            color: #666;
            margin-bottom: 8px;
        }
        
        .movie-rating {
            display: inline-block;
            background: #333;
            color: #FAF8F3;
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 0.8rem;
            font-weight: 500;
        }
        
        .scroll-button {
            position: absolute;
            top: 50%;
            transform: translateY(-50%);
            background: rgba(255,255,255,0.9);
            border: none;
            width: 50px;
            height: 50px;
            border-radius: 50%;
            cursor: pointer;
            font-size: 1.5rem;
            color: #333;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            z-index: 10;
            transition: background 0.3s;
        }
        
        .scroll-button:hover {
            background: white;
        }
        
        .scroll-left {
            left: 50px;
        }
        
        .scroll-right {
            right: 50px;
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/components/navbar-new.jsp" %>
    
    <div class="page-container">
        <%
            @SuppressWarnings("unchecked")
            List<Movie> popularMovies = (List<Movie>) request.getAttribute("popularMovies");
            @SuppressWarnings("unchecked")
            List<Movie> topRatedMovies = (List<Movie>) request.getAttribute("topRatedMovies");
            @SuppressWarnings("unchecked")
            List<Movie> recentMovies = (List<Movie>) request.getAttribute("recentMovies");
        %>
        
        <!-- Películas Más Populares -->
        <div class="section">
            <h2 class="section-title">Más Populares</h2>
            <div class="carousel-container">
                <button class="scroll-button scroll-left" onclick="scrollCarousel('popular', -220)">‹</button>
                <div class="carousel" id="popular-carousel">
                    <%
                        if (popularMovies != null) {
                            for (Movie movie : popularMovies) {
                    %>
                    <div class="movie-card" onclick="window.location.href='${pageContext.request.contextPath}/movie/<%= movie.getId() %>'">
                        <img src="https://image.tmdb.org/t/p/w300<%= movie.getPosterPath() != null ? movie.getPosterPath() : "" %>" 
                             alt="<%= movie.getTitulo() %>" 
                             class="movie-poster"
                             onerror="this.src='https://via.placeholder.com/200x300?text=Sin+Imagen'">
                        <div class="movie-info">
                            <div class="movie-title"><%= movie.getTitulo() %></div>
                            <div class="movie-year"><%= movie.getEstrenoYear() %></div>
                            <% if (movie.getPuntuacionApi() != null && movie.getPuntuacionApi() > 0) { %>
                                <div class="movie-rating">⭐ <%= String.format("%.1f", movie.getPuntuacionApi()) %></div>
                            <% } %>
                        </div>
                    </div>
                    <%
                            }
                        }
                    %>
                </div>
                <button class="scroll-button scroll-right" onclick="scrollCarousel('popular', 220)">›</button>
            </div>
        </div>
        
        <!-- Mejor Valoradas -->
        <div class="section">
            <h2 class="section-title">Mejor Valoradas</h2>
            <div class="carousel-container">
                <button class="scroll-button scroll-left" onclick="scrollCarousel('toprated', -220)">‹</button>
                <div class="carousel" id="toprated-carousel">
                    <%
                        if (topRatedMovies != null) {
                            for (Movie movie : topRatedMovies) {
                    %>
                    <div class="movie-card" onclick="window.location.href='${pageContext.request.contextPath}/movie/<%= movie.getId() %>'">
                        <img src="https://image.tmdb.org/t/p/w300<%= movie.getPosterPath() != null ? movie.getPosterPath() : "" %>" 
                             alt="<%= movie.getTitulo() %>" 
                             class="movie-poster"
                             onerror="this.src='https://via.placeholder.com/200x300?text=Sin+Imagen'">
                        <div class="movie-info">
                            <div class="movie-title"><%= movie.getTitulo() %></div>
                            <div class="movie-year"><%= movie.getEstrenoYear() %></div>
                            <% if (movie.getPuntuacionApi() != null && movie.getPuntuacionApi() > 0) { %>
                                <div class="movie-rating">⭐ <%= String.format("%.1f", movie.getPuntuacionApi()) %></div>
                            <% } %>
                        </div>
                    </div>
                    <%
                            }
                        }
                    %>
                </div>
                <button class="scroll-button scroll-right" onclick="scrollCarousel('toprated', 220)">›</button>
            </div>
        </div>
        
        <!-- Películas Recientes -->
        <div class="section">
            <h2 class="section-title">Estrenos Recientes</h2>
            <div class="carousel-container">
                <button class="scroll-button scroll-left" onclick="scrollCarousel('recent', -220)">‹</button>
                <div class="carousel" id="recent-carousel">
                    <%
                        if (recentMovies != null) {
                            for (Movie movie : recentMovies) {
                    %>
                    <div class="movie-card" onclick="window.location.href='${pageContext.request.contextPath}/movie/<%= movie.getId() %>'">
                        <img src="https://image.tmdb.org/t/p/w300<%= movie.getPosterPath() != null ? movie.getPosterPath() : "" %>" 
                             alt="<%= movie.getTitulo() %>" 
                             class="movie-poster"
                             onerror="this.src='https://via.placeholder.com/200x300?text=Sin+Imagen'">
                        <div class="movie-info">
                            <div class="movie-title"><%= movie.getTitulo() %></div>
                            <div class="movie-year"><%= movie.getEstrenoYear() %></div>
                            <% if (movie.getPuntuacionApi() != null && movie.getPuntuacionApi() > 0) { %>
                                <div class="movie-rating">⭐ <%= String.format("%.1f", movie.getPuntuacionApi()) %></div>
                            <% } %>
                        </div>
                    </div>
                    <%
                            }
                        }
                    %>
                </div>
                <button class="scroll-button scroll-right" onclick="scrollCarousel('recent', 220)">›</button>
            </div>
        </div>
    </div>
    
    <script>
        function scrollCarousel(carouselId, scrollAmount) {
            const carousel = document.getElementById(carouselId + '-carousel');
            carousel.scrollBy({
                left: scrollAmount,
                behavior: 'smooth'
            });
        }
    </script>
</body>
</html>