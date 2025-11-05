<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="entity.Movie" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Resultados de Búsqueda - Fat Movies</title>
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
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 40px 20px;
        }
        
        .page-title {
            font-size: 2.5rem;
            font-weight: 700;
            color: #333;
            margin-bottom: 30px;
            text-align: center;
        }
        
        .results-info {
            text-align: center;
            color: #666;
            margin-bottom: 40px;
            font-size: 1.1rem;
        }
        
        .movies-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 30px;
            margin-bottom: 40px;
        }
        
        .movie-card {
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
        
        .no-results {
            text-align: center;
            padding: 60px 20px;
            color: #666;
        }
        
        .no-results h3 {
            font-size: 1.5rem;
            margin-bottom: 15px;
        }
        
        .back-btn {
            display: inline-block;
            background: #333;
            color: #FAF8F3;
            padding: 12px 24px;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 600;
            margin-top: 20px;
            transition: background 0.3s;
        }
        
        .back-btn:hover {
            background: #555;
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/components/navbar-new.jsp" %>
    
    <div class="container">
        <h1 class="page-title">Resultados de Búsqueda</h1>
        
        <%
            @SuppressWarnings("unchecked")
            List<Movie> movies = (List<Movie>) request.getAttribute("movies");
            
            if (movies != null && !movies.isEmpty()) {
        %>
            <div class="results-info">
                Se encontraron <%= movies.size() %> película<%= movies.size() != 1 ? "s" : "" %>
            </div>
            
            <div class="movies-grid">
                <%
                    for (Movie movie : movies) {
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
                %>
            </div>
        <%
            } else {
        %>
            <div class="no-results">
                <h3>No se encontraron películas</h3>
                <p>Intenta ajustar los filtros de búsqueda o busca con otros términos.</p>
                <a href="${pageContext.request.contextPath}/movies" class="back-btn">Volver a Películas</a>
            </div>
        <%
            }
        %>
        
        <div style="text-align: center; margin-top: 40px;">
            <a href="${pageContext.request.contextPath}/movies" class="back-btn">Volver a Películas</a>
        </div>
    </div>
</body>
</html>