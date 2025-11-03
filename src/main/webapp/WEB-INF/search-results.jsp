<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="entity.Movie" %>
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
            padding: 20px;
        }
        .search-header {
            margin-bottom: 30px;
        }
        .search-header h1 {
            color: #333;
            font-size: 2rem;
            margin-bottom: 10px;
        }
        .movies-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 20px;
        }
        .movie-card {
            background: white;
            border-radius: 10px;
            padding: 15px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            text-align: center;
        }
        .movie-poster {
            width: 100%;
            height: 280px;
            object-fit: cover;
            border-radius: 8px;
            margin-bottom: 10px;
        }
        .movie-title {
            font-weight: 600;
            color: #333;
            margin-bottom: 5px;
        }
        .movie-year {
            color: #666;
            font-size: 14px;
        }
        .no-results {
            text-align: center;
            color: #666;
            font-size: 18px;
            margin-top: 50px;
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/components/navbar.jsp" %>
    
    <div class="container">
        <div class="search-header">
            <h1>Resultados para: "<%= request.getParameter("q") %>"</h1>
        </div>
        
        <%
            List<Movie> movies = (List<Movie>) request.getAttribute("movies");
            if (movies != null && !movies.isEmpty()) {
        %>
            <div class="movies-grid">
                <%
                    for (Movie movie : movies) {
                %>
                    <div class="movie-card">
                        <img src="https://image.tmdb.org/t/p/w500<%= movie.getPosterPath() != null ? movie.getPosterPath() : "" %>" 
                             alt="<%= movie.getTitulo() %>" 
                             class="movie-poster"
                             onerror="this.src='https://via.placeholder.com/200x280?text=Sin+Imagen'">
                        <div class="movie-title"><%= movie.getTitulo() %></div>
                        <div class="movie-year"><%= movie.getEstrenoYear() %></div>
                    </div>
                <%
                    }
                %>
            </div>
        <%
            } else {
        %>
            <div class="no-results">
                No se encontraron películas para tu búsqueda.
            </div>
        <%
            }
        %>
    </div>
</body>
</html>