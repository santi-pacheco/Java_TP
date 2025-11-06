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
        
        .filter-section {
            background: white;
            margin: 0 40px 40px 40px;
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        
        .filter-form {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }
        
        .filter-row {
            display: flex;
            gap: 20px;
            align-items: end;
        }
        
        .filter-group {
            display: flex;
            flex-direction: column;
            flex: 1;
        }
        
        .filter-group label {
            font-weight: 600;
            color: #333;
            margin-bottom: 8px;
            font-size: 0.9rem;
        }
        
        .filter-group input,
        .filter-group select {
            padding: 12px;
            border: 2px solid #E0E0E0;
            border-radius: 8px;
            font-size: 1rem;
            transition: border-color 0.3s;
        }
        
        .filter-group input:focus,
        .filter-group select:focus {
            outline: none;
            border-color: #333;
        }
        
        .filter-btn {
            background: #333;
            color: #FAF8F3;
            border: none;
            padding: 12px 24px;
            border-radius: 8px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.3s;
        }
        
        .filter-btn:hover {
            background: #555;
        }
        
        .clear-btn {
            background: #999;
            color: #FAF8F3;
            border: none;
            padding: 8px 16px;
            border-radius: 6px;
            font-size: 0.9rem;
            font-weight: 500;
            text-decoration: none;
            display: inline-block;
            transition: background 0.3s;
        }
        
        .clear-btn:hover {
            background: #777;
        }
        
        .movies-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 20px;
            padding: 0 40px;
        }
        
        @media (max-width: 768px) {
            .filter-row {
                flex-direction: column;
                align-items: stretch;
            }
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/components/navbar-new.jsp" %>
    
    <div class="page-container">
        <!-- Filter Section -->
        <div class="filter-section">
            <h2 class="section-title">Buscar y Filtrar Películas</h2>
            <form action="${pageContext.request.contextPath}/movies-page" method="get" class="filter-form">
                <div class="filter-row">
                    <div class="filter-group">
                        <label for="name">Nombre:</label>
                        <input type="text" id="name" name="name" placeholder="Buscar por nombre..." value="${currentName != null ? currentName : ''}" onchange="this.form.submit()">
                    </div>
                    <div class="filter-group">
                        <label for="genre">Género:</label>
                        <select id="genre" name="genre" onchange="this.form.submit()">
                            <option value="" ${currentGenre == null || currentGenre == '' ? 'selected' : ''}>Todos los géneros</option>
                            <option value="Acción" ${currentGenre == 'Acción' ? 'selected' : ''}>Acción</option>
                            <option value="Aventura" ${currentGenre == 'Aventura' ? 'selected' : ''}>Aventura</option>
                            <option value="Animación" ${currentGenre == 'Animación' ? 'selected' : ''}>Animación</option>
                            <option value="Comedia" ${currentGenre == 'Comedia' ? 'selected' : ''}>Comedia</option>
                            <option value="Crimen" ${currentGenre == 'Crimen' ? 'selected' : ''}>Crimen</option>
                            <option value="Documental" ${currentGenre == 'Documental' ? 'selected' : ''}>Documental</option>
                            <option value="Drama" ${currentGenre == 'Drama' ? 'selected' : ''}>Drama</option>
                            <option value="Familia" ${currentGenre == 'Familia' ? 'selected' : ''}>Familia</option>
                            <option value="Fantasía" ${currentGenre == 'Fantasía' ? 'selected' : ''}>Fantasía</option>
                            <option value="Historia" ${currentGenre == 'Historia' ? 'selected' : ''}>Historia</option>
                            <option value="Terror" ${currentGenre == 'Terror' ? 'selected' : ''}>Terror</option>
                            <option value="Música" ${currentGenre == 'Música' ? 'selected' : ''}>Música</option>
                            <option value="Misterio" ${currentGenre == 'Misterio' ? 'selected' : ''}>Misterio</option>
                            <option value="Romance" ${currentGenre == 'Romance' ? 'selected' : ''}>Romance</option>
                            <option value="Ciencia ficción" ${currentGenre == 'Ciencia ficción' ? 'selected' : ''}>Ciencia ficción</option>
                            <option value="Película de TV" ${currentGenre == 'Película de TV' ? 'selected' : ''}>Película de TV</option>
                            <option value="Suspense" ${currentGenre == 'Suspense' ? 'selected' : ''}>Suspense</option>
                            <option value="Bélica" ${currentGenre == 'Bélica' ? 'selected' : ''}>Bélica</option>
                            <option value="Western" ${currentGenre == 'Western' ? 'selected' : ''}>Western</option>
                        </select>
                    </div>
                </div>
                <div class="filter-row">
                    <div class="filter-group">
                        <label for="since">Desde año:</label>
                        <input type="number" id="since" name="since" min="1900" max="<%= java.time.Year.now().getValue() %>" placeholder="1990" value="${currentSince != null ? currentSince : ''}" onchange="validateYears(); this.form.submit();">
                    </div>
                    <div class="filter-group">
                        <label for="until">Hasta año:</label>
                        <input type="number" id="until" name="until" min="1900" max="<%= java.time.Year.now().getValue() %>" placeholder="<%= java.time.Year.now().getValue() %>" value="${currentUntil != null ? currentUntil : ''}" onchange="validateYears(); this.form.submit();">
                    </div>
                    <div class="filter-group">
                        <button type="submit" class="filter-btn">Buscar</button>
                    </div>
                    <% if (request.getAttribute("filteredMovies") != null) { %>
                    <div class="filter-group">
                        <a href="${pageContext.request.contextPath}/movies-page" class="clear-btn">Limpiar</a>
                    </div>
                    <% } %>
                </div>
            </form>
        </div>
        
        <%
            @SuppressWarnings("unchecked")
            List<Movie> filteredMovies = (List<Movie>) request.getAttribute("filteredMovies");
            @SuppressWarnings("unchecked")
            List<Movie> popularMovies = (List<Movie>) request.getAttribute("popularMovies");
            @SuppressWarnings("unchecked")
            List<Movie> topRatedMovies = (List<Movie>) request.getAttribute("topRatedMovies");
            @SuppressWarnings("unchecked")
            List<Movie> recentMovies = (List<Movie>) request.getAttribute("recentMovies");
        %>
        
        <% if (filteredMovies != null) { %>
        <!-- Resultados Filtrados -->
        <div class="section">
            <h2 class="section-title">Resultados de Búsqueda (<%= filteredMovies.size() %> películas)</h2>
            <div class="movies-grid">
                <%
                    for (Movie movie : filteredMovies) {
                %>
                <div class="movie-card" onclick="window.location.href='${pageContext.request.contextPath}/movie/<%= movie.getId() %>'">
                    <img src="https://image.tmdb.org/t/p/w300<%= movie.getPosterPath() != null ? movie.getPosterPath() : "" %>" 
                         alt="<%= movie.getTitulo() %>" 
                         class="movie-poster"
                         onerror="this.src='https://via.placeholder.com/200x300?text=Sin+Imagen'">
                    <div class="movie-info">
                        <div class="movie-title"><%= movie.getTitulo() %></div>
                        <div class="movie-year"><%= movie.getEstrenoYear() %></div>
                        <div style="display: flex; gap: 5px; flex-wrap: wrap;">
                            <% if (movie.getPuntuacionApi() != null && movie.getPuntuacionApi() > 0) { %>
                                <div class="movie-rating">⭐ <%= String.format("%.1f", movie.getPuntuacionApi()) %></div>
                            <% } %>
                            <% if (movie.getPromedioResenasLocal() != null && movie.getPromedioResenasLocal() > 0) { %>
                                <div class="movie-rating" style="background: #8B7355;">🍿 <%= String.format("%.1f", movie.getPromedioResenasLocal()) %></div>
                            <% } %>
                        </div>
                    </div>
                </div>
                <%
                    }
                %>
            </div>
        </div>
        <% } else { %>
        
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
        <% } %>
    </div>
    
    <script>
        function scrollCarousel(carouselId, scrollAmount) {
            const carousel = document.getElementById(carouselId + '-carousel');
            carousel.scrollBy({
                left: scrollAmount,
                behavior: 'smooth'
            });
        }
        
        function validateYears() {
            const sinceInput = document.getElementById('since');
            const untilInput = document.getElementById('until');
            const currentYear = new Date().getFullYear();
            
            const since = parseInt(sinceInput.value) || 0;
            const until = parseInt(untilInput.value) || 0;
            
            if (until > 0 && until > currentYear) {
                alert('El año hasta no puede ser mayor al año actual (' + currentYear + ')');
                untilInput.value = currentYear;
                return false;
            }
            
            if (since > 0 && until > 0 && since > until) {
                alert('El año desde no puede ser mayor al año hasta');
                sinceInput.value = until;
                return false;
            }
            
            return true;
        }
    </script>
</body>
</html>