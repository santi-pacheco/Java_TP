<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="entity.Movie" %>
<%@ page import="entity.Person" %>
<%@ page import="entity.ActorWithCharacter" %>
<%@ page import="java.util.List" %>
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
            margin-bottom: 30px;
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
                </div>
                
                <% if (movie.getSinopsis() != null && !movie.getSinopsis().trim().isEmpty()) { %>
                    <div class="movie-synopsis">
                        <%= movie.getSinopsis() %>
                    </div>
                <% } %>
                
                <% if (movie.getPuntuacionApi() != null && movie.getPuntuacionApi() > 0) { %>
                    <div class="rating-badge">
                        ⭐ <%= String.format("%.1f", movie.getPuntuacionApi()) %>/10
                    </div>
                <% } %>
            </div>
        </div>
    </div>
    
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
</body>
</html>