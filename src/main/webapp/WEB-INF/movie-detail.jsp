<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="entity.Movie" %>
<%@ page import="entity.Person" %>
<%@ page import="entity.ActorWithCharacter" %>
<%@ page import="entity.Review" %>
<%@ page import="entity.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="entity.Country" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= ((Movie)request.getAttribute("movie")).getTitulo() %> - Fat Movies</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            background-color: #FAF8F3;
            font-family: 'Poppins', sans-serif;
            margin: 0;
            padding: 0;
            overflow-x: hidden;
        }
        
        .movie-hero {
            position: relative;
            height: 70vh;
            background: linear-gradient(135deg, #FAF8F3 0%, #F0EDE6 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
        }
        
        .movie-bg {
            position: fixed;
            width: 550px;
            height: 100vh;
            background-color: #FAF8F3;
            top: 0;
            left: 50%;
            transform: translateX(-50%);
            filter: blur(15px);
            box-shadow: 0 0 60px 30px rgba(250,248,243,0.8);
            z-index: 0;
        }
        
        .movie-content {
            display: flex;
            gap: 40px;
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 40px;
            position: relative;
            z-index: 2;
        }
        
        .movie-poster {
            width: 300px;
            height: 450px;
            border-radius: 15px;
            box-shadow: 0 8px 24px rgba(0,0,0,0.2);
            object-fit: cover;
        }
        
        .movie-info {
            flex: 1;
            color: #333;
            display: flex;
            flex-direction: column;
            max-height: 450px;
        }
        
        .movie-title {
            font-size: 3rem;
            font-weight: 700;
            margin-bottom: 10px;
            letter-spacing: -1px;
        }
        
        .movie-original-title {
            font-size: 1.2rem;
            color: #666;
            margin-bottom: 20px;
            font-style: italic;
        }
        
        .movie-meta {
            display: flex;
            gap: 30px;
            margin-bottom: 30px;
            flex-wrap: wrap;
        }
        
        .meta-item {
            display: flex;
            flex-direction: column;
        }
        
        .meta-label {
            font-size: 0.9rem;
            color: #666;
            font-weight: 500;
            margin-bottom: 5px;
        }
        
        .meta-value {
            font-size: 1.1rem;
            font-weight: 600;
            color: #333;
        }
        
        .movie-synopsis {
            font-size: 1.1rem;
            line-height: 1.6;
            color: #444;
            margin-bottom: 20px;
            max-height: 120px;
            overflow-y: auto;
            flex-shrink: 1;
        }
        
        .rating-badge {
            display: inline-block;
            background: #333;
            color: #FAF8F3;
            padding: 8px 16px;
            border-radius: 20px;
            font-weight: 600;
            font-size: 1rem;
        }
        
        .btn-watchlist {
            background: #333;
            color: white;
            border: none;
            padding: 12px 24px;
            border-radius: 8px;
            font-family: 'Poppins', sans-serif;
            font-weight: 600;
            font-size: 1rem;
            cursor: pointer;
            margin-top: 20px;
            transition: background 0.3s;
        }
        
        .btn-watchlist:hover {
            background: #555;
        }
        
        .details-section {
            max-width: 1200px;
            margin: 60px auto;
            padding: 0 40px;
            position: relative;
            z-index: 2;
        }
        
        .section-title {
            font-size: 2rem;
            font-weight: 700;
            color: #333;
            margin-bottom: 30px;
            text-align: center;
        }
        
        .details-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 30px;
            background: white;
            padding: 40px;
            border-radius: 20px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        
        .detail-item {
            display: flex;
            justify-content: space-between;
            padding: 15px 0;
            border-bottom: 1px solid #eee;
        }
        
        .detail-label {
            font-weight: 600;
            color: #666;
        }
        
        .detail-value {
            color: #333;
            text-align: right;
        }
        
        .actors-section {
            max-width: 1200px;
            margin: 60px auto;
            padding: 0 40px;
            position: relative;
            z-index: 2;
        }
        
        .actors-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 20px;
            background: white;
            padding: 40px;
            border-radius: 20px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        
        .actor-card {
            text-align: center;
            padding: 20px;
            border-radius: 10px;
            background: #FAF8F3;
            transition: transform 0.3s;
        }
        
        .actor-card:hover {
            transform: translateY(-5px);
        }
        
        .actor-name {
            font-weight: 600;
            color: #333;
            margin-bottom: 5px;
        }
        
        .actor-birth {
            font-size: 0.9rem;
            color: #666;
        }
        
        .actor-character {
            font-size: 0.85rem;
            color: #888;
            font-style: italic;
            margin-top: 3px;
        }
        
        .directors-section {
            max-width: 1200px;
            margin: 60px auto;
            padding: 0 40px;
            position: relative;
            z-index: 2;
        }
        
        .directors-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 20px;
            background: white;
            padding: 40px;
            border-radius: 20px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        
        .director-card {
            text-align: center;
            padding: 20px;
            border-radius: 10px;
            background: #F0EDE6;
            transition: transform 0.3s;
        }
        
        .director-card:hover {
            transform: translateY(-5px);
        }
        
        .director-name {
            font-weight: 600;
            color: #333;
            margin-bottom: 5px;
        }
        
        .reviews-section {
            max-width: 1200px;
            margin: 60px auto;
            padding: 0 40px;
            position: relative;
            z-index: 2;
        }
        
        .review-form {
            background: white;
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        
        .review-form textarea {
            width: calc(100% - 30px);
            min-height: 120px;
            padding: 15px;
            border: 2px solid #eee;
            border-radius: 8px;
            font-family: 'Poppins', sans-serif;
            font-size: 1rem;
            resize: vertical;
            margin-bottom: 15px;
            box-sizing: border-box;
        }
        
        .review-form input[type="number"],
        .review-form input[type="date"] {
            padding: 10px 15px;
            border: 2px solid #eee;
            border-radius: 8px;
            font-family: 'Poppins', sans-serif;
            font-size: 1rem;
            margin-right: 15px;
        }
        
        .review-form button {
            background: #333;
            color: white;
            border: none;
            padding: 12px 30px;
            border-radius: 8px;
            font-family: 'Poppins', sans-serif;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.3s;
        }
        
        .review-form button:hover {
            background: #555;
        }
        
        .reviews-list {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }
        
        .review-card {
            background: white;
            padding: 25px;
            border-radius: 15px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        
        .review-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }
        
        .review-rating {
            background: #333;
            color: white;
            padding: 5px 12px;
            border-radius: 15px;
            font-weight: 600;
        }
        
        .review-text {
            color: #444;
            line-height: 1.6;
            margin-bottom: 10px;
        }
        
        .review-meta {
            font-size: 0.9rem;
            color: #888;
        }
        
        .review-spoiler {
            position: relative;
        }
        
        .spoiler-checkbox {
            display: none;
        }
        
        .review-spoiler .review-text {
            filter: blur(8px);
            user-select: none;
            transition: filter 0.3s;
        }
        
        .spoiler-checkbox:checked ~ .review-text {
            filter: none;
            user-select: text;
        }
        
        .spoiler-overlay {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: rgba(255, 255, 255, 0.95);
            padding: 15px 25px;
            border-radius: 10px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.2);
            cursor: pointer;
            transition: all 0.3s;
            z-index: 10;
        }
        
        .spoiler-checkbox:checked ~ .spoiler-overlay {
            display: none;
        }
        
        .spoiler-overlay:hover {
            background: rgba(255, 255, 255, 1);
            transform: translate(-50%, -50%) scale(1.05);
        }
        
        .spoiler-icon {
            font-size: 2rem;
            margin-bottom: 5px;
        }
        
        .spoiler-text {
            font-weight: 600;
            color: #333;
        }
        
        .spoiler-badge {
            background: #ff6b6b;
            color: white;
            padding: 3px 8px;
            border-radius: 5px;
            font-size: 0.8rem;
            font-weight: 600;
            margin-left: 10px;
        }
        
        .star-rating {
            display: inline-flex;
            cursor: pointer;
            gap: 8px;
            margin: 15px 0;
        }
        
        .star-container {
            position: relative;
            display: inline-block;
            width: 60px;
            height: 60px;
        }
        
        .popcorn-full {
            width: 60px;
            height: 60px;
            opacity: 0.3;
            transition: opacity 0.2s;
            display: block;
            object-fit: contain;
        }
        
        .popcorn-full.active {
            opacity: 1;
        }
        
        .popcorn-half {
            position: absolute;
            left: 0;
            top: 0;
            width: 60px;
            height: 60px;
            clip-path: inset(0 50% 0 0);
            opacity: 0.3;
            transition: opacity 0.2s;
            display: none;
            object-fit: contain;
        }
        
        .popcorn-half.active {
            opacity: 1;
            display: block;
        }
        
        .popcorn-full.half-hidden {
            display: none;
        }
        
        .popcorn-full[src*="great_movie"],
        .popcorn-half[src*="great_movie"] {
            transform: scale(1.25);
        }
        
        .rating-display {
            margin-left: 15px;
            font-weight: 600;
            color: #333;
            font-size: 1.2rem;
        }
        
        .review-stars {
            color: #FFD700;
            font-size: 1.2rem;
        }
        
        .btn-write-review {
            position: fixed;
            bottom: 30px;
            right: 30px;
            background: #333;
            color: white;
            border: none;
            padding: 15px 25px;
            border-radius: 50px;
            font-family: 'Poppins', sans-serif;
            font-weight: 600;
            font-size: 1rem;
            cursor: pointer;
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
            transition: all 0.3s;
            z-index: 1000;
            text-decoration: none;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .btn-write-review:hover {
            background: #555;
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(0,0,0,0.4);
        }
        
        .review-form-container {
            max-width: 1200px;
            margin: 40px auto;
            padding: 0 40px;
            position: relative;
            z-index: 2;
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/components/navbar-new.jsp" %>
    
    <%
        Movie movie = (Movie) request.getAttribute("movie");
        if (movie != null) {
    %>
    
    <div class="movie-bg"></div>
    
    <div class="movie-hero">
        <div class="movie-content">
            <img src="https://image.tmdb.org/t/p/w500<%= movie.getPosterPath() != null ? movie.getPosterPath() : "" %>" 
                 alt="<%= movie.getTitulo() %>" 
                 class="movie-poster"
                 onerror="this.src='https://via.placeholder.com/300x450?text=Sin+Imagen'">
            
            <div class="movie-info">
                <h1 class="movie-title"><%= movie.getTitulo() %></h1>
                
                <% if (movie.getTituloOriginal() != null && !movie.getTituloOriginal().equals(movie.getTitulo())) { %>
                    <div class="movie-original-title"><%= movie.getTituloOriginal() %></div>
                <% } %>
                
                <div class="movie-meta">
                    <div class="meta-item">
                        <span class="meta-label">Año</span>
                        <span class="meta-value"><%= movie.getEstrenoYear() %></span>
                    </div>
                    
                    <% if (movie.getDuracion() != null) { %>
                    <div class="meta-item">
                        <span class="meta-label">Duración</span>
                        <span class="meta-value"><%= movie.getDuracion() %></span>
                    </div>
                    <% } %>
                    
                    <% if (movie.getIdiomaOriginal() != null) { %>
                    <div class="meta-item">
                        <span class="meta-label">Idioma</span>
                        <span class="meta-value"><%= movie.getIdiomaOriginal().toUpperCase() %></span>
                    </div>
                    <% } %>
					<% 
					    @SuppressWarnings("unchecked")
					    List<entity.Country> countries = (List<entity.Country>) request.getAttribute("countries");
					    if (countries != null && !countries.isEmpty()) { 
					%>
					<div class="meta-item">
					    <span class="meta-label">Países</span>
					    <span class="meta-value">
					        <% 
					            for (int i = 0; i < countries.size(); i++) {
					                if (i > 0) out.print(", ");
					                out.print(countries.get(i).getIso_3166_1());
					            }
					        %>
					    </span>
					</div>
					<% } %>
                </div>
                
                <% if (movie.getSinopsis() != null && !movie.getSinopsis().trim().isEmpty()) { %>
                    <div class="movie-synopsis">
                        <%= movie.getSinopsis() %>
                    </div>
                <% } %>
                
                <div style="display: flex; gap: 15px; align-items: center;">
                    <% if (movie.getPuntuacionApi() != null && movie.getPuntuacionApi() > 0) { %>
                        <div class="rating-badge">
                            ⭐ TMDB: <%= String.format("%.1f", movie.getPuntuacionApi()) %>/10
                        </div>
                    <% } %>
                    <% if (movie.getPromedioResenasLocal() != null && movie.getPromedioResenasLocal() > 0) { %>
                        <div class="rating-badge" style="background: #8B7355;">
                            🍿 FatMovies: <%= String.format("%.1f", movie.getPromedioResenasLocal()) %>/5.0 (<%= movie.getCantidadResenasLocal() %> reseñas)
                        </div>
                    <% } %>
                </div>
                
                <div style="margin-top: auto; padding-top: auto;">
                    <% 
                        if (session.getAttribute("usuarioLogueado") != null) {
                            Boolean isInWatchlist = (Boolean) request.getAttribute("isInWatchlist");
                            Boolean canAddToWatchlist = (Boolean) request.getAttribute("canAddToWatchlist");
                            if (isInWatchlist != null && isInWatchlist) {
                    %>
                        <button class="btn-watchlist" disabled style="background: #999; cursor: not-allowed;">✓ En Watchlist</button>
                    <% } else if (canAddToWatchlist != null && !canAddToWatchlist) { %>
                        <button class="btn-watchlist" disabled style="background: #999; cursor: not-allowed; font-size: 0.9rem;" onclick="alert('Has alcanzado el límite de películas en tu watchlist')">⚠️ Límite Alcanzado</button>
                    <% } else { %>
                        <form method="post" action="${pageContext.request.contextPath}/watchlist" style="display:inline;">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="movieId" value="<%= movie.getId() %>">
                            <button type="submit" class="btn-watchlist">+ Agregar a Watchlist</button>
                        </form>
                    <% 
                            }
                        } 
                    %>
                </div>

            </div>
        </div>
    </div>
    
    <% if (session.getAttribute("usuarioLogueado") != null) { %>
        <a href="#review-form" class="btn-write-review">
            Escribir Reseña
        </a>
    <% } %>
    
    <!-- Formulario de Reseña -->
    <% if (session.getAttribute("usuarioLogueado") != null) {
        Review userReview = (Review) request.getAttribute("userReview");
    %>
    <div class="review-form-container" id="review-form">
        <div class="review-form">
            <h3 style="margin-bottom: 20px;"><%= userReview != null ? "Editar tu reseña" : "Escribe tu reseña" %></h3>
            <% 
                String reviewError = (String) session.getAttribute("reviewError");
                if (reviewError != null) {
                    session.removeAttribute("reviewError");
            %>
                <div style="background:#FFE5E5; color:#D32F2F; padding:12px 20px; border-radius:8px; margin-bottom:15px; font-weight:600; border-left:4px solid #D32F2F;">
                    ⚠️ <%= reviewError %>
                </div>
            <% } %>
            <form method="post" action="${pageContext.request.contextPath}/reviews">
                <input type="hidden" name="action" value="create">
                <input type="hidden" name="movieId" value="<%= movie.getId() %>">
                
                <% 
                    java.util.Map<String, String[]> reviewData = (java.util.Map<String, String[]>) session.getAttribute("reviewData");
                    String savedReviewText = "";
                    String savedRating = "0";
                    String savedWatchedOn = "";
                    if (reviewData != null) {
                        savedReviewText = reviewData.get("reviewText") != null ? reviewData.get("reviewText")[0] : "";
                        savedRating = reviewData.get("rating") != null ? reviewData.get("rating")[0] : "0";
                        savedWatchedOn = reviewData.get("watchedOn") != null ? reviewData.get("watchedOn")[0] : "";
                        session.removeAttribute("reviewData");
                    }
                    if (userReview != null && savedReviewText.isEmpty()) {
                        savedReviewText = userReview.getReview_text();
                        savedRating = String.valueOf(userReview.getRating());
                        savedWatchedOn = userReview.getWatched_on().toString();
                    }
                %>
                
                <textarea name="reviewText" placeholder="Escribe tu reseña aquí..." required><%= savedReviewText %></textarea>
                
                <div style="display: flex; align-items: center; gap: 20px; flex-wrap: wrap;">
                    <div style="display: flex; align-items: center; gap: 10px;">
                        <label>Rating:</label>
                        <div class="star-rating" id="starRating">
                            <div class="star-container" data-index="1">
                                <img src="${pageContext.request.contextPath}/utils/good_movie.svg" class="popcorn-full" alt="popcorn">
                                <img src="${pageContext.request.contextPath}/utils/good_movie.svg" class="popcorn-half" alt="popcorn">
                            </div>
                            <div class="star-container" data-index="2">
                                <img src="${pageContext.request.contextPath}/utils/good_movie.svg" class="popcorn-full" alt="popcorn">
                                <img src="${pageContext.request.contextPath}/utils/good_movie.svg" class="popcorn-half" alt="popcorn">
                            </div>
                            <div class="star-container" data-index="3">
                                <img src="${pageContext.request.contextPath}/utils/good_movie.svg" class="popcorn-full" alt="popcorn">
                                <img src="${pageContext.request.contextPath}/utils/good_movie.svg" class="popcorn-half" alt="popcorn">
                            </div>
                            <div class="star-container" data-index="4">
                                <img src="${pageContext.request.contextPath}/utils/good_movie.svg" class="popcorn-full" alt="popcorn">
                                <img src="${pageContext.request.contextPath}/utils/good_movie.svg" class="popcorn-half" alt="popcorn">
                            </div>
                            <div class="star-container" data-index="5">
                                <img src="${pageContext.request.contextPath}/utils/good_movie.svg" class="popcorn-full" alt="popcorn">
                                <img src="${pageContext.request.contextPath}/utils/good_movie.svg" class="popcorn-half" alt="popcorn">
                            </div>
                        </div>
                        <input type="hidden" name="rating" id="ratingInput" value="<%= savedRating %>" required>
                        <span class="rating-display" id="ratingDisplay"><%= Double.parseDouble(savedRating) > 0 ? savedRating + " kCal" : "0 kCal" %></span>
                    </div>
                    
                    <div style="display: flex; align-items: center; gap: 10px;">
                        <label>Visto el:</label>
                        <input type="date" name="watchedOn" value="<%= savedWatchedOn %>" max="<%= java.time.LocalDate.now() %>" required>
                    </div>
                    
                    <button type="submit"><%= userReview != null ? "Actualizar Reseña" : "Publicar Reseña" %></button>
                </div>
            </form>
        </div>
    </div>
    <% } %>
    
    <div class="details-section">
        <h2 class="section-title">Información Adicional</h2>
        <div class="details-grid">
            <div class="detail-item">
                <span class="detail-label">Popularidad</span>
                <span class="detail-value"><%= movie.getPopularidad() != null ? String.format("%.1f", movie.getPopularidad()) : "N/A" %></span>
            </div>
            
            <div class="detail-item">
                <span class="detail-label">Votos</span>
                <span class="detail-value"><%= movie.getVotosApi() %></span>
            </div>
            
            <div class="detail-item">
                <span class="detail-label">Contenido Adulto</span>
                <span class="detail-value"><%= movie.getAdulto() != null && movie.getAdulto() ? "Sí" : "No" %></span>
            </div>
            
            <% if (movie.getId_imdb() != null) { %>
            <div class="detail-item">
                <span class="detail-label">ID IMDB</span>
                <span class="detail-value"><%= movie.getId_imdb() %></span>
            </div>
            <% } %>
        </div>
    </div>
    
    <%
        @SuppressWarnings("unchecked")
        List<ActorWithCharacter> actors = (List<ActorWithCharacter>) request.getAttribute("actors");
        if (actors != null && !actors.isEmpty()) {
    %>
    <div class="actors-section">
        <h2 class="section-title">Reparto</h2>
        <div class="actors-grid">
            <%
                for (ActorWithCharacter actorWithChar : actors) {
            %>
            <div class="actor-card">
                <div class="actor-name"><%= actorWithChar.getActor().getName() %></div>
                <% if (actorWithChar.getCharacterName() != null && !actorWithChar.getCharacterName().trim().isEmpty()) { %>
                    <div class="actor-character"><%= actorWithChar.getCharacterName() %></div>
                <% } %>
            </div>
            <%
                }
            %>
        </div>
    </div>
    <%
        }
    %>
    
    <%
        @SuppressWarnings("unchecked")
        List<Person> directors = (List<Person>) request.getAttribute("directors");
    %>
    
    <!-- Sección de Dirección -->
    <div class="directors-section">
        <h2 class="section-title">Dirección</h2>
        <div class="directors-grid">
            <%
                if (directors != null && !directors.isEmpty()) {
                    for (Person director : directors) {
            %>
            <div class="director-card">
                <div class="director-name"><%= director.getName() %></div>
            </div>
            <%
                    }
                } else {
            %>
            <div class="director-card">
                <div class="director-name">No hay directores registrados</div>
            </div>
            <%
                }
            %>
        </div>
    </div>
    
    <!-- Sección de Reseñas -->
    <div class="reviews-section">
        <h2 class="section-title">Reseñas de la Comunidad</h2>
        
        <div class="reviews-list">
            <%
                @SuppressWarnings("unchecked")
                List<Review> reviews = (List<Review>) request.getAttribute("reviews");
                if (reviews != null && !reviews.isEmpty()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    for (Review review : reviews) {
            %>
            <div class="review-card <%= review.getContieneSpoiler() != null && review.getContieneSpoiler() ? "review-spoiler" : "" %>">
                <% if (review.getContieneSpoiler() != null && review.getContieneSpoiler()) { %>
                    <input type="checkbox" class="spoiler-checkbox" id="spoiler-<%= review.getId() %>">
                <% } %>
                <div class="review-header">
                    <div>
                        <strong><%= review.getUsername() != null ? review.getUsername() : "Usuario #" + review.getId_user() %></strong>
                        <span class="review-meta"> • Visto el <%= review.getWatched_on().format(formatter) %></span>
                        <% if (review.getContieneSpoiler() != null && review.getContieneSpoiler()) { %>
                            <span class="spoiler-badge">SPOILER</span>
                        <% } %>
                    </div>
                    <div class="review-stars" style="display: inline-flex; gap: 5px;">
                        <% 
                            double rating = review.getRating();
                            String iconPath;
                            if (rating <= 2) {
                                iconPath = request.getContextPath() + "/utils/bad_movie.svg";
                            } else if (rating <= 4) {
                                iconPath = request.getContextPath() + "/utils/good_movie.svg";
                            } else {
                                iconPath = request.getContextPath() + "/utils/great_movie.svg";
                            }
                            
                            for (int i = 1; i <= 5; i++) {
                                String opacity = "0.3";
                                String display = "inline-block";
                                String clipPath = "";
                                
                                if (rating >= i) {
                                    opacity = "1";
                                } else if (rating >= i - 0.5) {
                                    opacity = "1";
                                    clipPath = "clip-path: inset(0 50% 0 0);";
                                } else {
                                    opacity = "0.3";
                                }
                                
                                String scale = iconPath.contains("great_movie") ? "transform: scale(1.25);" : "";
                        %>
                        <img src="<%= iconPath %>" style="width: 30px; height: 30px; opacity: <%= opacity %>; <%= clipPath %> <%= scale %> object-fit: contain;" alt="rating">
                        <% } %>
                    </div>
                </div>
                <div class="review-text"><%= review.getReview_text() %></div>
                <div class="review-meta">Publicado el <%= review.getCreated_at() != null ? review.getCreated_at().format(formatter) : "N/A" %></div>
                <% if (review.getContieneSpoiler() != null && review.getContieneSpoiler()) { %>
                    <label for="spoiler-<%= review.getId() %>" class="spoiler-overlay">
                        <div class="spoiler-icon">👁️</div>
                        <div class="spoiler-text">Click para ver spoiler</div>
                    </label>
                <% } %>
            </div>
            <%
                    }
                } else {
            %>
            <div class="review-card" style="text-align: center; padding: 40px;">
                <p style="color: #888;">Aún no hay reseñas para esta película. ¡Sé el primero en escribir una!</p>
            </div>
            <%
                }
            %>
        </div>
    </div>
    
    <%
        } else {
    %>
        <div style="text-align: center; margin-top: 100px;">
            <h1>Película no encontrada</h1>
            <a href="${pageContext.request.contextPath}/">Volver al inicio</a>
        </div>
    <%
        }
    %>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const starContainers = document.querySelectorAll('.star-container');
            const ratingInput = document.getElementById('ratingInput');
            const ratingDisplay = document.getElementById('ratingDisplay');
            
            if (starContainers.length > 0 && ratingInput) {
                const currentValue = parseFloat(ratingInput.value);
                if (currentValue > 0) {
                    updateStars(currentValue);
                }
                
                starContainers.forEach(container => {
                    const index = parseInt(container.getAttribute('data-index'));
                    const starFull = container.querySelector('.popcorn-full');
                    const starHalf = container.querySelector('.popcorn-half');
                    
                    container.addEventListener('click', function(e) {
                        const rect = container.getBoundingClientRect();
                        const clickX = e.clientX - rect.left;
                        const isHalf = clickX < rect.width / 2;
                        const value = isHalf ? index - 0.5 : index;
                        ratingInput.value = value;
                        ratingDisplay.textContent = value + ' kCal';
                        updateStars(value);
                    });
                    
                    container.addEventListener('mousemove', function(e) {
                        const rect = container.getBoundingClientRect();
                        const mouseX = e.clientX - rect.left;
                        const isHalf = mouseX < rect.width / 2;
                        const value = isHalf ? index - 0.5 : index;
                        updateIconType(value);
                        highlightStars(value);
                    });
                });
                
                document.getElementById('starRating').addEventListener('mouseleave', function() {
                    const currentValue = parseFloat(ratingInput.value);
                    updateStars(currentValue);
                });
                
                function highlightStars(value) {
                    starContainers.forEach(container => {
                        const index = parseInt(container.getAttribute('data-index'));
                        const starFull = container.querySelector('.popcorn-full');
                        const starHalf = container.querySelector('.popcorn-half');
                        
                        if (index <= Math.floor(value)) {
                            starFull.classList.add('active');
                            starFull.classList.remove('half-hidden');
                            starHalf.classList.remove('active');
                        } else if (index - 0.5 === value) {
                            starFull.classList.add('half-hidden');
                            starHalf.classList.add('active');
                        } else {
                            starFull.classList.remove('active');
                            starFull.classList.remove('half-hidden');
                            starHalf.classList.remove('active');
                        }
                    });
                }
                
                function updateStars(value) {
                    updateIconType(value);
                    starContainers.forEach(container => {
                        const index = parseInt(container.getAttribute('data-index'));
                        const starFull = container.querySelector('.popcorn-full');
                        const starHalf = container.querySelector('.popcorn-half');
                        
                        if (index <= Math.floor(value)) {
                            starFull.classList.add('active');
                            starFull.classList.remove('half-hidden');
                            starHalf.classList.remove('active');
                        } else if (index - 0.5 === value) {
                            starFull.classList.add('half-hidden');
                            starHalf.classList.add('active');
                        } else {
                            starFull.classList.remove('active');
                            starFull.classList.remove('half-hidden');
                            starHalf.classList.remove('active');
                        }
                    });
                }
                
                function updateIconType(value) {
                    const contextPath = '${pageContext.request.contextPath}';
                    let iconPath;
                    if (value <= 2) {
                        iconPath = contextPath + '/utils/bad_movie.svg';
                    } else if (value <= 4) {
                        iconPath = contextPath + '/utils/good_movie.svg';
                    } else {
                        iconPath = contextPath + '/utils/great_movie.svg';
                    }
                    
                    starContainers.forEach(container => {
                        const starFull = container.querySelector('.popcorn-full');
                        const starHalf = container.querySelector('.popcorn-half');
                        starFull.src = iconPath;
                        starHalf.src = iconPath;
                    });
                }
            }
        });
    </script>
</body>
</html>