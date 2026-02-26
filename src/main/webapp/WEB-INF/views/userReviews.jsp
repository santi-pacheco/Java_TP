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
            margin-bottom: 30px;
            display: flex;
            justify-content: space-between;
            align-items: flex-end;
            flex-wrap: wrap;
            gap: 20px;
        }
        
        .header-title h1 {
            font-size: 36px;
            font-weight: 700;
            color: #333;
            margin-bottom: 10px;
        }
        
        .header-title p {
            font-size: 16px;
            color: #666;
        }


        .controls-panel {
            background: white;
            padding: 15px 25px;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
            display: flex;
            gap: 20px;
            align-items: center;
            flex-wrap: wrap;
        }

        .control-group {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .control-group label {
            font-size: 14px;
            font-weight: 500;
            color: #666;
        }

        .control-select {
            padding: 8px 15px;
            border-radius: 8px;
            border: 2px solid #eee;
            font-family: inherit;
            font-size: 14px;
            color: #333;
            background-color: white;
            cursor: pointer;
            outline: none;
            transition: border-color 0.3s;
        }

        .control-select:focus, .control-select:hover {
            border-color: #8B7355;
        }
        /* ------------------------------------------- */
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
            transition: transform 0.2s, box-shadow 0.2s, opacity 0.3s ease;
            cursor: pointer; /* AÑADIDO PARA QUE PAREZCA CLICKEABLE */
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
        
        <%
            List<Review> reviews = (List<Review>) request.getAttribute("reviews");
            Map<Integer, Movie> moviesMap = (Map<Integer, Movie>) request.getAttribute("moviesMap");
            User user = (User) session.getAttribute("usuarioLogueado");
            
            if (reviews == null || reviews.isEmpty()) {
        %>
            <div class="header">
                <div class="header-title">
                    <h1>Mis Reseñas</h1>
                    <p>Todas tus reseñas en un solo lugar</p>
                </div>
            </div>
            <div class="empty-state">
                <h2>Aún no has escrito ninguna reseña</h2>
                <p>Explora películas y comparte tu opinión con la comunidad</p>
                <a href="${pageContext.request.contextPath}/movies-page" class="btn-primary">Explorar Películas</a>
            </div>
        <%
            } else {
        %>
            <div class="header">
                <div class="header-title">
                    <h1>Mis Reseñas</h1>
                    <p>Todas tus reseñas en un solo lugar</p>
                </div>
                
                <div class="controls-panel">
                    <div class="control-group">
                        <label for="sortSelect">Ordenar por:</label>
                        <select id="sortSelect" class="control-select" onchange="applyFilters()">
                            <option value="date_desc"> Más recientes primero</option>
                            <option value="date_asc"> Más antiguas primero</option>
                            <option value="rating_desc"> Mejor calificadas</option>
                            <option value="rating_asc"> Peor calificadas</option>
                        </select>
                    </div>
                    <div class="control-group">
                        <label for="filterSelect">Mostrar:</label>
                        <select id="filterSelect" class="control-select" onchange="applyFilters()">
                            <option value="all">Todas las reseñas</option>
                            <option value="no_spoiler">Sin spoilers</option>
                            <option value="spoiler">Con spoilers</option>
                        </select>
                    </div>
                </div>
                </div>

            <div class="reviews-grid" id="reviewsContainer">
                <%
                    for (Review review : reviews) {
                        Movie movie = moviesMap.get(review.getId_movie());
                        if (movie != null) {
                            String posterUrl = movie.getPosterPath() != null 
                                ? "https://image.tmdb.org/t/p/w500" + movie.getPosterPath()
                                : request.getContextPath() + "/utils/no-poster.png";
                                
                            String status = (review.getModerationStatus() != null) ? review.getModerationStatus().toString() : "PENDING_MODERATION";
                            

                            long timestamp = review.getCreated_at() != null ? review.getCreated_at().toEpochDay() : 0;
                %>

                    <div class="review-card" 
					     data-rating="<%= review.getRating() %>" 
					     data-timestamp="<%= timestamp %>"
					     data-status="<%= status %>"
					     onclick="window.location.href='${pageContext.request.contextPath}/movie/<%= movie.getId() %>'"> 
                         
                        <img src="<%= posterUrl %>" 
                             alt="<%= movie.getTitulo() %>" 
                             class="movie-poster"
                             onerror="this.src='${pageContext.request.contextPath}/utils/no-poster.png'">
                        
                        <div class="review-content">

                            <span class="movie-title">
						    	<%= movie.getTitulo() %>
							</span>
                            
                            <div class="rating-date">
                                <div class="rating">
                                    🍿 <%= String.format("%.1f", review.getRating()) %>/5.0 Kcals
                                </div>
                                <div class="date">
                                    <%= review.getCreated_at() != null ? review.getCreated_at().toString() : "" %>
                                </div>
                                
                                <%
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
            
            <div id="noResultsMsg" class="empty-state" style="display: none;">
                <h2>No se encontraron reseñas</h2>
                <p>Ninguna de tus reseñas coincide con los filtros seleccionados.</p>
                <button onclick="resetFilters()" class="btn-primary" style="border: none; cursor: pointer;">Limpiar Filtros</button>
            </div>
        <%
            }
        %>
    </div>

    <script>
        function applyFilters() {
            const container = document.getElementById('reviewsContainer');
            if (!container) return;

            const sortValue = document.getElementById('sortSelect').value;
            const filterValue = document.getElementById('filterSelect').value;
            

            let cards = Array.from(container.getElementsByClassName('review-card'));
            let visibleCount = 0;


            cards.forEach(card => {
                const status = card.getAttribute('data-status');
                let shouldShow = true;

                if (filterValue === 'spoiler' && status !== 'SPOILER') {
                    shouldShow = false;
                } else if (filterValue === 'no_spoiler' && status === 'SPOILER') {
                    shouldShow = false;
                }

                if (shouldShow) {
                    card.style.display = 'flex';
                    visibleCount++;
                } else {
                    card.style.display = 'none';
                }
            });


            document.getElementById('noResultsMsg').style.display = visibleCount === 0 ? 'block' : 'none';
            container.style.display = visibleCount === 0 ? 'none' : 'grid';

            if (visibleCount === 0) return;


            cards.sort((a, b) => {
                const ratingA = parseFloat(a.getAttribute('data-rating'));
                const ratingB = parseFloat(b.getAttribute('data-rating'));
                const timeA = parseInt(a.getAttribute('data-timestamp'));
                const timeB = parseInt(b.getAttribute('data-timestamp'));

                switch (sortValue) {
                    case 'date_desc': return timeB - timeA;
                    case 'date_asc': return timeA - timeB;
                    case 'rating_desc': 
                        if (ratingB !== ratingA) return ratingB - ratingA;
                        return timeB - timeA; // Desempate por fecha
                    case 'rating_asc': 
                        if (ratingB !== ratingA) return ratingA - ratingB;
                        return timeB - timeA; // Desempate por fecha
                    default: return 0;
                }
            });

            cards.forEach(card => {

                if (card.style.display !== 'none') {
                    card.style.opacity = '0';
                    container.appendChild(card);
                    
                    
                    setTimeout(() => {
                        card.style.opacity = '1';
                    }, 50);
                } else {
                    container.appendChild(card);
                }
            });
        }

        function resetFilters() {
            document.getElementById('sortSelect').value = 'date_desc';
            document.getElementById('filterSelect').value = 'all';
            applyFilters();
        }
    </script>
</body>
</html>