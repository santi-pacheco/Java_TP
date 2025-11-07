<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="entity.Movie" %>
<%@ page import="entity.Genre" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Filtrar Películas - Fat Movies</title>
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
        
        .filter-section {
            background: white;
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 40px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        
        .filter-title {
            font-size: 2rem;
            font-weight: 700;
            color: #333;
            margin-bottom: 30px;
            text-align: center;
        }
        
        .filter-form {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 20px;
        }
        
        .form-group {
            display: flex;
            flex-direction: column;
        }
        
        .form-label {
            font-weight: 600;
            color: #333;
            margin-bottom: 8px;
            font-size: 0.9rem;
        }
        
        .form-input, .form-select {
            padding: 12px;
            border: 2px solid #E0E0E0;
            border-radius: 8px;
            font-size: 1rem;
            transition: border-color 0.3s;
        }
        
        .form-input:focus, .form-select:focus {
            outline: none;
            border-color: #333;
        }
        
        .date-group {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 10px;
        }
        
        .filter-buttons {
            display: flex;
            gap: 15px;
            justify-content: center;
            margin-top: 20px;
        }
        
        .btn {
            padding: 12px 30px;
            border: none;
            border-radius: 8px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .btn-primary {
            background: #333;
            color: white;
        }
        
        .btn-primary:hover {
            background: #555;
        }
        
        .btn-secondary {
            background: #E0E0E0;
            color: #333;
        }
        
        .btn-secondary:hover {
            background: #D0D0D0;
        }
        
        .results-section {
            background: white;
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        
        .results-title {
            font-size: 1.5rem;
            font-weight: 600;
            color: #333;
            margin-bottom: 20px;
        }
        
        .movies-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 20px;
        }
        
        .movie-card {
            background: white;
            border-radius: 12px;
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
            color: #666;
            font-size: 1.1rem;
            padding: 40px;
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/components/navbar-new.jsp" %>
    
    <div class="container">
        <!-- Sección de Filtros -->
        <div class="filter-section">
            <h1 class="filter-title">Filtrar Películas</h1>
            
            <form action="${pageContext.request.contextPath}/movie/filter" method="GET" class="filter-form">
                <!-- Género -->
                <div class="form-group">
                    <label class="form-label" for="genre">Género</label>
                    <select name="genre" id="genre" class="form-select">
                        <option value="">Todos los géneros</option>
                        <%
                            @SuppressWarnings("unchecked")
                            List<Genre> genres = (List<Genre>) request.getAttribute("genres");
                            String selectedGenre = (String) request.getAttribute("selectedGenre");
                            
                            if (genres != null) {
                                for (Genre genre : genres) {
                                    boolean isSelected = selectedGenre != null && selectedGenre.equals(genre.getName());
                        %>
                        <option value="<%= genre.getName() %>" <%= isSelected ? "selected" : "" %>>
                            <%= genre.getName() %>
                        </option>
                        <%
                                }
                            }
                        %>
                    </select>
                </div>
                
                <!-- Año de Estreno -->
                <div class="form-group">
                    <label class="form-label">Año de Estreno</label>
                    <div class="date-group">
                        <input type="number" 
                               name="since" 
                               placeholder="Desde" 
                               min="1900" 
                               max="2030" 
                               class="form-input"
                               value="<%= request.getAttribute("selectedSince") != null ? request.getAttribute("selectedSince") : "" %>">
                        <input type="number" 
                               name="until" 
                               placeholder="Hasta" 
                               min="1900" 
                               max="2030" 
                               class="form-input"
                               value="<%= request.getAttribute("selectedUntil") != null ? request.getAttribute("selectedUntil") : "" %>">
                    </div>
                </div>
                
                <!-- Rating IMDB -->
                <div class="form-group">
                    <label class="form-label" for="rating">Rating Mínimo (IMDB)</label>
                    <input type="number" 
                           name="rating" 
                           id="rating" 
                           step="0.1" 
                           min="0" 
                           max="10" 
                           placeholder="Ej: 7.5" 
                           class="form-input"
                           value="<%= request.getAttribute("selectedRating") != null ? request.getAttribute("selectedRating") : "" %>">
                </div>
                
                <div class="filter-buttons">
                    <button type="submit" class="btn btn-primary">Buscar</button>
                    <button type="button" class="btn btn-secondary" onclick="clearFilters()">Limpiar</button>
                </div>
            </form>
        </div>
        
        <!-- Sección de Resultados -->
        <div class="results-section">
            <%
                @SuppressWarnings("unchecked")
                List<Movie> movies = (List<Movie>) request.getAttribute("movies");
                
                if (movies != null && !movies.isEmpty()) {
            %>
            <h2 class="results-title">Resultados (<%= movies.size() %> películas encontradas)</h2>
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
                <p>No se encontraron peliculas con los filtros seleccionados.</p>
                <p>Intenta ajustar los criterios de búsqueda.</p>
            </div>
            <%
                }
            %>
        </div>
    </div>
    
    <script>
        function clearFilters() {
            document.getElementById('genre').value = '';
            document.querySelector('input[name="since"]').value = '';
            document.querySelector('input[name="until"]').value = '';
            document.querySelector('input[name="rating"]').value = '';
        }
    </script>
</body>
</html>