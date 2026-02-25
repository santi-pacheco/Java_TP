<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="entity.Movie" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Watchlist - Fat Movies</title>
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
        
        h1 {
            font-size: 2.5rem;
            font-weight: 700;
            color: #333;
            margin-bottom: 30px;
        }
        
        .movies-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 30px;
        }
        
        .movie-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            overflow: hidden;
            transition: transform 0.3s;
            cursor: pointer;
            position: relative;
        }
        
        .movie-card:hover {
            transform: translateY(-5px);
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
        }
        
        .movie-year {
            font-size: 0.9rem;
            color: #666;
        }
        
        .btn-remove {
            position: absolute;
            top: 10px;
            right: 10px;
            background: rgba(255,255,255,0.9);
            border: none;
            width: 35px;
            height: 35px;
            border-radius: 50%;
            cursor: pointer;
            font-size: 20px;
            color: #a80000;
            transition: background 0.3s;
        }
        
        .btn-remove:hover {
            background: white;
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #666;
        }
        
        .empty-state h2 {
            font-size: 1.5rem;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/components/navbar-new.jsp" %>
    
    <div class="container">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px;">
            <h1 style="margin: 0;">Mi Watchlist</h1>
            <%
                @SuppressWarnings("unchecked")
                List<Movie> moviesCheck = (List<Movie>) request.getAttribute("movies");
                if (moviesCheck != null && !moviesCheck.isEmpty()) {
            %>
            <form method="get" action="${pageContext.request.contextPath}/roulette" style="margin: 0;">
                <button type="submit" style="padding: 12px 30px; background-color: #8B7355; color: white; border: none; border-radius: 25px; font-family: 'Poppins', sans-serif; font-weight: 500; font-size: 16px; cursor: pointer; transition: background-color 0.3s;"> Ruleta de Películas</button>
            </form>
            <%
                }
            %>
        </div>
        
        <%
            @SuppressWarnings("unchecked")
            List<Movie> movies = (List<Movie>) request.getAttribute("movies");
            
            if (movies == null || movies.isEmpty()) {
        %>
            <div class="empty-state">
                <h2>Tu watchlist está vacía</h2>
                <p>Agrega películas desde la página de detalles</p>
            </div>
        <%
            } else {
        %>
            <div class="movies-grid">
                <%
                    for (Movie movie : movies) {
                %>
                <div class="movie-card" onclick="window.location.href='${pageContext.request.contextPath}/movie/<%= movie.getId() %>'">
                    <form method="post" action="${pageContext.request.contextPath}/watchlist" style="display:inline;">
                        <input type="hidden" name="action" value="remove">
                        <input type="hidden" name="movieId" value="<%= movie.getId() %>">
                        <button type="submit" class="btn-remove" title="Eliminar" onclick="event.stopPropagation();">×</button>
                    </form>
                    <img src="https://image.tmdb.org/t/p/w300<%= movie.getPosterPath() %>" 
                         alt="<%= movie.getTitulo() %>" 
                         class="movie-poster"
                         onerror="this.src='https://via.placeholder.com/200x300?text=Sin+Imagen'">
                    <div class="movie-info">
                        <div class="movie-title"><%= movie.getTitulo() %></div>
                        <div class="movie-year"><%= movie.getEstrenoYear() %></div>
                    </div>
                </div>
                <%
                    }
                %>
            </div>
        <%
            }
        %>
    </div>
</body>
</html>
