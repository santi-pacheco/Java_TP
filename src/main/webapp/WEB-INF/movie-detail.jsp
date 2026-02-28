<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="entity.Movie" %>
<%@ page import="entity.Person" %>
<%@ page import="entity.ActorWithCharacter" %>
<%@ page import="entity.Review" %>
<%@ page import="entity.User" %>
<%@ page import="entity.ModerationStatus" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="entity.Country" %>
<%@ page import="java.util.Map" %>

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
        body { background-color: #FAF8F3; font-family: 'Poppins', sans-serif; margin: 0; padding: 0; overflow-x: hidden; }
        
        .movie-hero { position: relative; height: 70vh; background: linear-gradient(135deg, #FAF8F3 0%, #F0EDE6 100%); display: flex; align-items: center; justify-content: center; overflow: hidden; }
        .movie-bg { position: fixed; width: 550px; height: 100vh; background-color: #FAF8F3; top: 0; left: 50%; transform: translateX(-50%); filter: blur(15px); box-shadow: 0 0 60px 30px rgba(250,248,243,0.8); z-index: 0; }
        .movie-content { display: flex; gap: 40px; max-width: 1200px; margin: 0 auto; padding: 0 40px; position: relative; z-index: 2; }
        .movie-poster { width: 300px; height: 450px; border-radius: 15px; box-shadow: 0 8px 24px rgba(0,0,0,0.2); object-fit: cover; }
        .movie-info { flex: 1; color: #333; display: flex; flex-direction: column; max-height: 450px; }
        .movie-title { font-size: 3rem; font-weight: 700; margin-bottom: 10px; letter-spacing: -1px; }
        .movie-original-title { font-size: 1.2rem; color: #666; margin-bottom: 20px; font-style: italic; }
        
        .movie-meta { display: flex; gap: 30px; margin-bottom: 30px; flex-wrap: wrap; }
        .meta-item { display: flex; flex-direction: column; }
        .meta-label { font-size: 0.9rem; color: #666; font-weight: 500; margin-bottom: 5px; }
        .meta-value { font-size: 1.1rem; font-weight: 600; color: #333; }
        
        .movie-synopsis { font-size: 1.1rem; line-height: 1.6; color: #444; margin-bottom: 20px; max-height: 120px; overflow-y: auto; flex-shrink: 1; }
        .rating-badge { display: inline-block; background: #333; color: #FAF8F3; padding: 8px 16px; border-radius: 20px; font-weight: 600; font-size: 1rem; }
        .btn-watchlist { background: #333; color: white; border: none; padding: 12px 24px; border-radius: 8px; font-family: inherit; font-weight: 600; font-size: 1rem; cursor: pointer; margin-top: 20px; transition: background 0.3s; }
        .btn-watchlist:hover { background: #555; }
        
        .section-container { max-width: 1200px; margin: 60px auto; padding: 0 40px; position: relative; z-index: 2; }
        .section-title { font-size: 2rem; font-weight: 700; color: #333; margin-bottom: 30px; text-align: center; }
        
        .details-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 30px; background: white; padding: 40px; border-radius: 20px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
        .detail-item { display: flex; justify-content: space-between; padding: 15px 0; border-bottom: 1px solid #eee; }
        .detail-label { font-weight: 600; color: #666; }
        .detail-value { color: #333; text-align: right; }
        
        .persons-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 20px; background: white; padding: 30px; border-radius: 20px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
        .person-card { text-align: center; padding: 15px; border-radius: 10px; background: #FAF8F3; transition: transform 0.3s; display: flex; flex-direction: column; align-items: center; justify-content: flex-start; }
        .person-card:hover { transform: translateY(-5px); box-shadow: 0 6px 15px rgba(0,0,0,0.1); }
        .person-photo { width: 100%; max-width: 120px; height: 180px; object-fit: cover; border-radius: 8px; margin-bottom: 12px; background-color: #eee; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
        .person-name { font-weight: 600; color: #333; margin-bottom: 5px; font-size: 1rem; }
        .person-role { font-size: 0.85rem; color: #888; font-style: italic; margin-top: 3px; }
        
        .reviews-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; flex-wrap: wrap; gap: 15px; }
        .reviews-header h2 { margin-bottom: 0; }
        .sort-select { padding: 8px 15px; border-radius: 8px; border: 2px solid #eee; font-family: inherit; font-size: 0.95rem; color: #333; background-color: white; cursor: pointer; outline: none; transition: border-color 0.3s; }
        .sort-select:focus, .sort-select:hover { border-color: #333; }
        
        .review-form { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); margin-bottom: 30px; }
        .review-form textarea { width: calc(100% - 30px); min-height: 120px; padding: 15px; border: 2px solid #eee; border-radius: 8px; font-family: inherit; font-size: 1rem; resize: vertical; margin-bottom: 15px; box-sizing: border-box; }
        .review-form input[type="date"] { padding: 10px 15px; border: 2px solid #eee; border-radius: 8px; font-family: inherit; font-size: 1rem; margin-right: 15px; }
        .review-form button { background: #333; color: white; border: none; padding: 12px 30px; border-radius: 8px; font-family: inherit; font-weight: 600; cursor: pointer; transition: background 0.3s; }
        .review-form button:hover { background: #555; }
        
        .reviews-list { display: flex; flex-direction: column; gap: 20px; }
        .review-card { background: white; padding: 25px; border-radius: 15px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); position: relative; }

        .cheddar-review-card {
            position: relative;
            border: 3px solid #FFB800 !important;
            border-radius: 15px !important;
            background: #FFFdf5 !important;
            margin-top: 30px !important;
            margin-bottom: 40px !important;
            padding: 25px !important; 
            overflow: visible !important; 
        }

        .cheddar-review-card::after {
            content: '';
            position: absolute;
            top: -3px;
            left: -3px;
            right: -3px;
            height: 40px;
            background-image: url('${pageContext.request.contextPath}/utils/cheddar.png');
            background-repeat: repeat-x;
            background-size: auto 100%;
            background-position: top left;

            z-index: 10;
            pointer-events: none; 
            border-top-left-radius: 15px; 
            border-top-right-radius: 15px; 
        }

        .cheddar-review-card::before {
            content: '👑 Crítico Michelin';
            position: absolute;
            top: -15px;
            right: 20px;
            background: #222;
            color: #FFB800;
            font-size: 0.8rem;
            font-weight: 800;
            padding: 5px 15px;
            border-radius: 20px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.3);
            z-index: 11;
            border: 2px solid #FFB800;
        }

        .review-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 15px; }
        .review-text { color: #444; line-height: 1.6; margin-bottom: 10px; }
        .review-meta { font-size: 0.9rem; color: #888; display: flex; align-items: center; justify-content: space-between; width: 100%; }
        
        .author-container { display: flex; align-items: center; gap: 12px; }
        
        .author-avatar-wrapper { border-radius: 50%; display: flex; align-items: center; justify-content: center; }
        .author-avatar { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; border: 2px solid #eee; background-color: #f5f5f5; }

        .burger-avatar-border {
            border-radius: 50% !important;
            padding: 4px;
            background: linear-gradient(180deg, 
                #F5B041 0%, #F5B041 30%,   
                #58D68D 30%, #58D68D 40%,   
                #873600 40%, #873600 70%,   
                #F4D03F 70%, #F4D03F 100%   
            ) !important;
            box-shadow: 0 4px 8px rgba(0,0,0,0.2) !important;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            position: relative;
            border: none !important;
        }

        .burger-avatar-border .author-avatar {
            border-radius: 50% !important;
            border: 2px solid #FFF !important;
            position: relative;
            z-index: 2;
        }

        .author-details { display: flex; flex-direction: column; }
        .author-name-row { display: flex; align-items: center; gap: 8px; }
        
        .follow-btn { background: transparent; border: 2px solid #8B7355; color: #8B7355; border-radius: 15px; padding: 2px 10px; font-size: 0.75rem; font-weight: 600; cursor: pointer; transition: all 0.2s; }
        .follow-btn:hover { background: #8B7355; color: white; }
        .follow-btn.following { background: #8B7355; color: white; border-color: #8B7355; }
        .follow-btn.following:hover { background: #6b5840; border-color: #6b5840; }

        .user-profile-link { text-decoration: none; color: #333; transition: color 0.2s; font-size: 1rem; }
        .user-profile-link:hover { color: #8B7355; text-decoration: underline; }

        .spoiler-checkbox { display: none; }
        .review-spoiler-text { filter: blur(8px); user-select: none; transition: filter 0.3s; }
        .spoiler-checkbox:checked ~ .review-content .review-spoiler-text { filter: none; user-select: text; }
        .spoiler-checkbox:checked ~ .spoiler-overlay { display: none; }
        .spoiler-overlay { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: rgba(255, 255, 255, 0.95); padding: 15px 25px; border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.2); cursor: pointer; transition: all 0.3s; z-index: 10; text-align: center; }
        .spoiler-overlay:hover { background: rgba(255, 255, 255, 1); transform: translate(-50%, -50%) scale(1.05); }
        .spoiler-text { font-weight: 600; color: #333; text-align: center; }
        .spoiler-badge { background: #ff6b6b; color: white; padding: 3px 8px; border-radius: 5px; font-size: 0.75rem; font-weight: 600; margin-left: 5px; vertical-align: middle; }
        
        .star-rating { display: inline-flex; cursor: pointer; gap: 8px; margin: 15px 0; }
        .star-container { position: relative; display: inline-block; width: 60px; height: 60px; }
        .popcorn-full { width: 60px; height: 60px; opacity: 0.3; transition: opacity 0.2s; display: block; object-fit: contain; }
        .popcorn-full.active { opacity: 1; }
        .popcorn-half { position: absolute; left: 0; top: 0; width: 60px; height: 60px; clip-path: inset(0 50% 0 0); opacity: 0.3; transition: opacity 0.2s; display: none; object-fit: contain; }
        .popcorn-half.active { opacity: 1; display: block; }
        .popcorn-full.half-hidden { display: none; }
        .popcorn-full[src*="great_movie"], .popcorn-half[src*="great_movie"] { transform: scale(1.25); }
        .rating-display { margin-left: 15px; font-weight: 600; color: #333; font-size: 1.2rem; }
        
        .btn-write-review { position: fixed; bottom: 30px; right: 30px; background: #333; color: white; border: none; padding: 15px 25px; border-radius: 50px; font-family: inherit; font-weight: 600; font-size: 1rem; cursor: pointer; box-shadow: 0 4px 12px rgba(0,0,0,0.3); transition: all 0.3s; z-index: 1000; text-decoration: none; display: flex; align-items: center; gap: 10px; }
        .btn-write-review:hover { background: #555; transform: translateY(-2px); box-shadow: 0 6px 16px rgba(0,0,0,0.4); }
        
        .like-section { display: inline-flex; align-items: center; margin-left: auto; }
        .like-btn { background: transparent; border: 2px solid #e0e0e0; cursor: pointer; padding: 6px 16px; border-radius: 50px; transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); display: flex; align-items: center; gap: 8px; position: relative; overflow: hidden; color: #555; }
        .like-btn:hover { border-color: #333; background-color: #f5f5f5; transform: translateY(-2px); }
        .like-btn.liked { background: #333; border-color: #333; color: #fff; }
        .like-btn.liked .soda-stroke { stroke: #fff; }
        .like-btn.liked .liquid-fill { fill: #fff; clip-path: inset(0 0 0 0); }
        .like-btn.liked .like-label { font-weight: 600; }
        .soda-svg { width: 22px; height: 22px; display: block; overflow: visible; }
        .liquid-fill { fill: #333; clip-path: inset(100% 0 0 0); transition: clip-path 0.6s cubic-bezier(0.4, 0, 0.2, 1); }
        .soda-stroke { fill: none; stroke: #333; stroke-width: 3; stroke-linecap: round; stroke-linejoin: round; transition: stroke 0.3s; }
        .like-count { font-weight: 700; font-size: 0.95rem; }
        .like-label { font-size: 0.85rem; font-weight: 500; text-transform: uppercase; letter-spacing: 0.5px; }
        @media (max-width: 400px) { .like-label { display: none; } }
        
        .comments-section { margin-top: 15px; width: 100%; }
        .comments-toggle { background: none; border: none; color: #666; font-size: 0.9rem; font-weight: 600; cursor: pointer; display: inline-flex; align-items: center; gap: 5px; padding: 5px 0; transition: color 0.2s; }
        .comments-toggle:hover { color: #333; }
        .comments-container { display: none; margin-top: 15px; padding-top: 15px; border-top: 1px dashed #ddd; }
        .comment-list { max-height: 250px; overflow-y: auto; margin-bottom: 15px; padding-right: 5px; }
        
        .comment-item { background: #f9f9f9; padding: 12px; border-radius: 8px; margin-bottom: 10px; font-size: 0.9rem; animation: fadeIn 0.3s ease; position: relative; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }
        .comment-header { display: flex; justify-content: space-between; margin-bottom: 5px; align-items: flex-start; }
        .comment-author-avatar { width: 30px; height: 30px; border-radius: 50%; object-fit: cover; border: 1px solid #ddd; }
        .comment-text { color: #555; line-height: 1.4; word-wrap: break-word; }
        
        .comment-actions { display: flex; gap: 8px; margin-left: auto; }
        .btn-action { background: transparent; border: 1px solid #333; color: #333; font-family: inherit; font-size: 0.75rem; font-weight: 600; padding: 4px 10px; border-radius: 6px; cursor: pointer; transition: all 0.2s; text-transform: uppercase; letter-spacing: 0.5px; }
        .btn-action:hover { background: #333; color: #fff; }
        .btn-action.delete { border-color: #d32f2f; color: #d32f2f; }
        .btn-action.delete:hover { background: #d32f2f; color: #fff; }
        
        .comment-form { display: flex; gap: 10px; }
        .comment-input { flex: 1; padding: 10px; border: 2px solid #eee; border-radius: 8px; font-family: inherit; font-size: 0.9rem; outline: none; transition: border-color 0.3s; }
        .comment-input:focus { border-color: #333; }
        .btn-submit-comment { background: #333; color: white; border: none; padding: 0 15px; border-radius: 8px; cursor: pointer; font-weight: 600; transition: background 0.3s; }
        .btn-submit-comment:hover { background: #000; }
        
        .comment-spoiler-checkbox { display: none; }
        .comment-spoiler-text { filter: blur(6px); user-select: none; transition: filter 0.3s; }
        .comment-spoiler-checkbox:checked ~ .comment-spoiler-text { filter: none; user-select: text; }
        .comment-spoiler-checkbox:checked ~ .comment-spoiler-overlay { display: none; }
        .comment-spoiler-overlay { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: rgba(255, 255, 255, 0.9); padding: 5px 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.2); cursor: pointer; transition: all 0.3s; z-index: 10; text-align: center; }
        
        #toast-container { position: fixed; bottom: 20px; right: 20px; z-index: 9999; display: flex; flex-direction: column; gap: 10px; pointer-events: none; }
        .toast { padding: 12px 20px; border-radius: 8px; color: white; font-family: inherit; font-size: 0.95rem; font-weight: 500; box-shadow: 0 4px 12px rgba(0,0,0,0.15); animation: slideInToast 0.3s ease, fadeOutToast 0.3s ease 3.7s forwards; pointer-events: all; }
        .toast.success { background: #333; }
        .toast.error { background: #d32f2f; }
        @keyframes slideInToast { from { transform: translateX(100%); opacity: 0; } to { transform: translateX(0); opacity: 1; } }
        @keyframes fadeOutToast { to { opacity: 0; visibility: hidden; margin-top: -50px; } }

        /* --- TRUCO UX: El overlay se enciende, pero el contenido espera 400ms --- */
        #aiLoadingOverlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(250, 248, 243, 0.95);
            z-index: 10000;
            display: none;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            text-align: center;
            opacity: 0; /* Empieza invisible */
            animation: fadeInOverlay 0.3s ease 0.4s forwards; /* Tarda 400ms en mostrarse */
        }

        @keyframes fadeInOverlay {
            to { opacity: 1; }
        }

        .spinner-ai {
            width: 60px;
            height: 60px;
            border: 6px solid rgba(139, 115, 85, 0.3);
            border-radius: 50%;
            border-top-color: #8B7355;
            animation: spin 1s linear infinite;
            margin-bottom: 20px;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        #resultOverlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.8);
            z-index: 10001;
            display: none;
            justify-content: center;
            align-items: center;
        }

        .result-card {
            background: white;
            padding: 40px;
            border-radius: 15px;
            text-align: center;
            max-width: 500px;
            width: 90%;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
            animation: slideInToast 0.3s ease;
        }

        .result-icon { font-size: 4rem; margin-bottom: 15px; }
        .result-title { color: #333; margin-bottom: 15px; font-weight: 700; font-size: 1.5rem; }
        .result-message { color: #666; margin-bottom: 25px; line-height: 1.6; font-size: 1.05rem; }
        .result-btn {
            background: #8B7355; color: white; border: none; padding: 12px 30px; 
            border-radius: 8px; font-weight: 600; cursor: pointer; font-size: 1rem;
            transition: background 0.3s;
        }
        .result-btn:hover { background: #6b5840; }
    </style>
</head>
<body>
    <div id="aiLoadingOverlay">
        <div class="spinner-ai"></div>
        <h3 style="color:#333; margin-top:20px;" id="aiLoadingText">Nuestra IA está analizando tu contenido...</h3>
        <p style="color:#666;">Verificando normas de la comunidad y posibles spoilers.</p>
    </div>

    <div id="resultOverlay">
        <div class="result-card">
            <div id="resultIcon" class="result-icon"></div>
            <div id="resultTitle" class="result-title"></div>
            <div id="resultMessage" class="result-message"></div>
            <button class="result-btn" id="resultBtn" onclick="window.location.reload()">Entendido</button>
        </div>
    </div>

    <%@ include file="/WEB-INF/components/navbar-new.jsp" %>
    
    <div id="toast-container"></div>
    
    <%
        Movie movie = (Movie) request.getAttribute("movie");
        @SuppressWarnings("unchecked")
        List<Integer> likedReviewIds = (List<Integer>) request.getAttribute("likedReviewIds");
        if (likedReviewIds == null) likedReviewIds = new ArrayList<>();
        
        User loggedUser = (User) session.getAttribute("usuarioLogueado");
        Integer currentUserId = loggedUser != null ? loggedUser.getId() : null;
        
        if (movie != null) {
    %>
    
    <div class="movie-bg"></div>
    <div class="movie-hero">
        <div class="movie-content">
            <img src="https://image.tmdb.org/t/p/w500<%= movie.getPosterPath() != null ? movie.getPosterPath() : "" %>" alt="<%= movie.getTitulo() %>" class="movie-poster" onerror="this.src='https://via.placeholder.com/300x450?text=Sin+Imagen'">
            <div class="movie-info">
                <h1 class="movie-title"><%= movie.getTitulo() %></h1>
                <% if (movie.getTituloOriginal() != null && !movie.getTituloOriginal().equals(movie.getTitulo())) { %>
                    <div class="movie-original-title"><%= movie.getTituloOriginal() %></div>
                <% } %>
                
                <div class="movie-meta">
                    <div class="meta-item"><span class="meta-label">Año</span><span class="meta-value"><%= movie.getEstrenoYear() %></span></div>
                    <% if (movie.getDuracion() != null) { %>
                        <div class="meta-item"><span class="meta-label">Duración</span><span class="meta-value"><%= movie.getDuracion() %></span></div>
                    <% } %>
                    <% if (movie.getIdiomaOriginal() != null) { %>
                        <div class="meta-item"><span class="meta-label">Idioma</span><span class="meta-value"><%= movie.getIdiomaOriginal().toUpperCase() %></span></div>
                    <% } %>
                    <% 
                        @SuppressWarnings("unchecked")
                        List<Country> countries = (List<Country>) request.getAttribute("countries");
                        if (countries != null && !countries.isEmpty()) { 
                    %>
                    <div class="meta-item">
                        <span class="meta-label">Países</span>
                        <span class="meta-value">
                            <% for (int i = 0; i < countries.size(); i++) {
                                out.print((i > 0 ? ", " : "") + countries.get(i).getIso_3166_1());
                            } %>
                        </span>
                    </div>
                    <% } %>
                </div>
                
                <% if (movie.getSinopsis() != null && !movie.getSinopsis().trim().isEmpty()) { %>
                    <div class="movie-synopsis"><%= movie.getSinopsis() %></div>
                <% } %>
                
                <div style="display: flex; gap: 15px; align-items: center;">
                    <% if (movie.getPuntuacionApi() != null && movie.getPuntuacionApi() > 0) { %>
                        <div class="rating-badge">⭐ TMDB: <%= String.format("%.1f", movie.getPuntuacionApi()) %>/10</div>
                    <% } %>
                    <% if (movie.getPromedioResenasLocal() != null && movie.getPromedioResenasLocal() > 0) { %>
                        <div class="rating-badge" style="background: #8B7355;">🍿 FatMovies: <%= String.format("%.1f", movie.getPromedioResenasLocal()) %>/5.0 (<%= movie.getCantidadResenasLocal() %> reseñas)</div>
                    <% } %>
                </div>
                
                <div style="margin-top: auto; padding-top: auto;">
                    <% 
                        if (loggedUser != null) {
                            Boolean isInWatchlist = (Boolean) request.getAttribute("isInWatchlist");
                            Boolean canAddToWatchlist = (Boolean) request.getAttribute("canAddToWatchlist");
                            if (isInWatchlist != null && isInWatchlist) {
                    %>
                        <button class="btn-watchlist" disabled style="background: #999; cursor: not-allowed;">✓ En Watchlist</button>
                    <% } else if (canAddToWatchlist != null && !canAddToWatchlist) { %>
                        <button class="btn-watchlist" disabled style="background: #999; cursor: not-allowed;" onclick="showToast('Has alcanzado el límite de películas en tu watchlist', 'error')">⚠️ Límite Alcanzado</button>
                    <% } else { %>
                        <form method="post" action="${pageContext.request.contextPath}/watchlist" style="display:inline;">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="movieId" value="<%= movie.getId() %>">
                            <button type="submit" class="btn-watchlist">+ Agregar a Watchlist</button>
                        </form>
                    <%      }
                        } 
                    %>
                </div>
            </div>
        </div>
    </div>
    
    <% if (loggedUser != null) { %>
        <a href="#review-form" class="btn-write-review">Escribir Reseña</a>
    <% } %>
    
    <% if (loggedUser != null) {
        Review userReview = (Review) request.getAttribute("userReview");
    %>
    <div class="section-container" id="review-form">
        <div class="review-form">
            <h3 style="margin-bottom: 20px;"><%= userReview != null ? "Editar tu reseña" : "Escribe tu reseña" %></h3>
            
            <form id="ajaxReviewForm">
                <input type="hidden" name="movieId" id="movieIdInput" value="<%= movie.getId() %>">
                <% 
                    String savedReviewText = "", savedRating = "0", savedWatchedOn = "";
                    if (userReview != null) {
                        savedReviewText = userReview.getReview_text();
                        savedRating = String.valueOf(userReview.getRating());
                        savedWatchedOn = userReview.getWatched_on().toString();
                    }
                %>
                <textarea name="reviewText" id="reviewTextInput" placeholder="Escribe tu reseña aquí..." required autocomplete="off"><%= savedReviewText %></textarea>
                <div style="display: flex; align-items: center; gap: 20px; flex-wrap: wrap;">
                    <div style="display: flex; align-items: center; gap: 10px;">
                        <label>Rating:</label>
                        <div class="star-rating" id="starRating">
                            <% for(int i=1; i<=5; i++) { %>
                                <div class="star-container" data-index="<%=i%>"><img src="${pageContext.request.contextPath}/utils/good_movie.svg" class="popcorn-full"><img src="${pageContext.request.contextPath}/utils/good_movie.svg" class="popcorn-half"></div>
                            <% } %>
                        </div>
                        <input type="hidden" name="rating" id="ratingInput" value="<%= savedRating %>" required>
                        <span class="rating-display" id="ratingDisplay"><%= Double.parseDouble(savedRating) > 0 ? savedRating + " kCal" : "0 kCal" %></span>
                    </div>
                    <div style="display: flex; align-items: center; gap: 10px;">
                        <label>Visto el:</label>
                        <% 
                            String minDate = "";
                            if (movie.getFechaEstreno() != null) {
                                minDate = movie.getFechaEstreno().toString(); 
                            } else if (movie.getEstrenoYear() > 0) {
                                minDate = movie.getEstrenoYear() + "-01-01";
                            }
                        %>
                        <input type="date" id="watchedOnInput" name="watchedOn" value="<%= savedWatchedOn %>" min="<%= minDate %>" max="<%= java.time.LocalDate.now() %>" required>
                    </div>
                    <button type="submit" id="submitReviewBtn"><%= userReview != null ? "Actualizar Reseña" : "Publicar Reseña" %></button>
                </div>
            </form>
        </div>
    </div>
    <% } %>
    
    <%
        @SuppressWarnings("unchecked")
        List<ActorWithCharacter> actors = (List<ActorWithCharacter>) request.getAttribute("actors");
        if (actors != null && !actors.isEmpty()) {
    %>
    <div class="section-container">
        <h2 class="section-title">Reparto</h2>
        <div class="persons-grid">
            <% for (ActorWithCharacter awc : actors) {
                Person actor = awc.getActor();
                if (actor != null) {
                    String path = actor.getProfile_path();
                    String photoUrl = (path != null && !path.trim().isEmpty() && !path.equals("null")) ? "https://image.tmdb.org/t/p/w185" + path : request.getContextPath() + "/utils/default_profile.png";
            %>
            <div class="person-card">
                <img src="<%= photoUrl %>" alt="<%= actor.getName() %>" class="person-photo" onerror="this.src='<%= request.getContextPath() %>/utils/default_profile.png'">
                <div class="person-name"><%= actor.getName() %></div>
                <% if (awc.getCharacterName() != null && !awc.getCharacterName().trim().isEmpty()) { %>
                    <div class="person-role"><%= awc.getCharacterName() %></div>
                <% } %>
            </div>
            <% } } %>
        </div>
    </div>
    <% } %>
    
    <%
        @SuppressWarnings("unchecked")
        List<Person> directors = (List<Person>) request.getAttribute("directors");
    %>
    <div class="section-container">
        <h2 class="section-title">Dirección</h2>
        <div class="persons-grid">
            <% if (directors != null && !directors.isEmpty()) {
                for (Person director : directors) {
                    String path = director.getProfile_path();
                    String photoUrl = (path != null && !path.trim().isEmpty() && !path.equals("null")) ? "https://image.tmdb.org/t/p/w185" + path : request.getContextPath() + "/utils/default_profile.png";
            %>
            <div class="person-card">
                <img src="<%= photoUrl %>" alt="<%= director.getName() %>" class="person-photo" onerror="this.src='<%= request.getContextPath() %>/utils/default_profile.png'">
                <div class="person-name"><%= director.getName() %></div>
            </div>
            <% } } else { %>
            <div class="person-card"><div class="person-name">No hay directores registrados</div></div>
            <% } %>
        </div>
    </div>
    
    <div class="section-container">
        <div class="reviews-header">
            <h2 class="section-title" style="margin-bottom: 0;">Reseñas de la Comunidad</h2>
            <div class="sort-container">
                <select class="sort-select" onchange="sortReviews(this.value)">
                    <option value="date">⏱️ Más recientes</option>
                    <option value="likes">🥤 Más refrescantes</option>
                    <option value="rating_desc">🍿 Mejor calificadas</option>
                    <option value="rating_asc">📉 Peor calificadas</option>
                </select>
            </div>
        </div>
        
        <div class="reviews-list">
             <%
                @SuppressWarnings("unchecked")
                List<Review> reviews = (List<Review>) request.getAttribute("reviews");
                @SuppressWarnings("unchecked")
                Map<Integer, Integer> likesCountMap = (Map<Integer, Integer>) request.getAttribute("likesCountMap");
                @SuppressWarnings("unchecked")
                Map<Integer, Boolean> userLikesMap = (Map<Integer, Boolean>) request.getAttribute("userLikesMap");
                @SuppressWarnings("unchecked")
                Map<Integer, Boolean> followedUsersMap = (Map<Integer, Boolean>) request.getAttribute("followedUsersMap");
                @SuppressWarnings("unchecked")
                Map<Integer, Integer> userLevelsMap = (Map<Integer, Integer>) request.getAttribute("userLevelsMap");
        
                if (reviews != null && !reviews.isEmpty()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    for (Review review : reviews) {
                        boolean isSpoiler = review.getModerationStatus() != null && ModerationStatus.SPOILER.equals(review.getModerationStatus());
                        boolean isFollowing = followedUsersMap != null && followedUsersMap.getOrDefault(review.getId_user(), false);
                        
                        int authorLevel = (userLevelsMap != null && userLevelsMap.containsKey(review.getId_user())) ? userLevelsMap.get(review.getId_user()) : 1;
                        String reviewCardClass = authorLevel == 4 ? "review-card cheddar-review-card" : "review-card";
                        String avatarWrapperClass = authorLevel >= 3 ? "author-avatar-wrapper burger-avatar-border" : "author-avatar-wrapper";

                        String userAvatarPath = request.getContextPath() + "/utils/default_profile.png"; 
                        if (review.getProfileImage() != null && !review.getProfileImage().trim().isEmpty()) {
                            userAvatarPath = request.getContextPath() + "/uploads/" + review.getProfileImage();
                        }
            %>
            <div class="<%= reviewCardClass %>" data-timestamp="<%= review.getCreated_at() != null ? review.getCreated_at().toEpochDay() : 0 %>" data-rating="<%= review.getRating() %>">
                <% if (isSpoiler) { %><input type="checkbox" class="spoiler-checkbox" id="spoiler-<%= review.getId() %>"><% } %>
                <div class="review-content">
                    <div class="review-header">
                        <div class="author-container">
                            <div class="<%= avatarWrapperClass %>">
                                <img src="<%= userAvatarPath %>" alt="Avatar" class="author-avatar" onerror="this.src='<%= request.getContextPath() %>/utils/default_profile.png'">
                            </div>
                            <div class="author-details">
                                <div class="author-name-row">
                                    <strong>
                                        <a href="${pageContext.request.contextPath}/profile?id=<%= review.getId_user() %>" class="user-profile-link">
                                            <%= review.getUsername() != null ? review.getUsername() : "Usuario #" + review.getId_user() %>
                                        </a>
                                    </strong>
                                    <% if (isSpoiler) { %><span class="spoiler-badge">SPOILER</span><% } %>
                                    
                                    <% if (currentUserId != null && currentUserId != review.getId_user()) { %>
                                        <button class="follow-btn <%= isFollowing ? "following" : "" %>" data-user-id="<%= review.getId_user() %>" onclick="toggleFollow(<%= review.getId_user() %>, this)">
                                            <%= isFollowing ? "Siguiendo" : "Seguir" %>
                                        </button>
                                    <% } %>
                                </div>
                                <span style="color: #888; font-size: 0.8rem;">Visto el <%= review.getWatched_on().format(formatter) %></span>
                            </div>
                        </div>

                        <div class="review-stars" style="display: inline-flex; gap: 5px;">
                            <% 
                                double rating = review.getRating();
                                String iconPath = request.getContextPath() + (rating <= 2 ? "/utils/bad_movie.svg" : rating <= 4 ? "/utils/good_movie.svg" : "/utils/great_movie.svg");
                                for (int i = 1; i <= 5; i++) {
                                    String opacity = (rating >= i || rating >= i - 0.5) ? "1" : "0.3";
                                    String clipPath = (rating >= i - 0.5 && rating < i) ? "clip-path: inset(0 50% 0 0);" : "";
                                    String scale = iconPath.contains("great_movie") ? "transform: scale(1.25);" : "";
                            %>
                            <img src="<%= iconPath %>" style="width: 30px; height: 30px; opacity: <%= opacity %>; <%= clipPath %> <%= scale %> object-fit: contain;" alt="rating">
                            <% } %>
                        </div>
                     </div>
                     
                    <div class="<%= isSpoiler ? "review-text review-spoiler-text" : "review-text" %>"><%= review.getReview_text() %></div>
                    
                    <div class="review-meta">
                        <span>Publicado el <%= review.getCreated_at() != null ? review.getCreated_at().format(formatter) : "N/A" %></span>
                        <div class="like-section">
                            <% 
                                boolean isLiked = userLikesMap != null && userLikesMap.getOrDefault(review.getId(), false);
                                int likesCount = likesCountMap != null ? likesCountMap.getOrDefault(review.getId(), 0) : 0;
                            %>
                            <button class="like-btn <%= isLiked ? "liked" : "" %>" onclick="toggleLike(<%= review.getId() %>, this)" title="<%= isLiked ? "Quitar voto" : "Votar esta reseña" %>">
                                <svg class="soda-svg" viewBox="0 0 64 64" xmlns="http://www.w3.org/2000/svg">
                                    <path class="liquid-fill" d="M20 20 L24 56 H40 L44 20 Z" />
                                    <path class="soda-stroke" d="M38 4 L38 12 M38 4 L46 4" />
                                    <path class="soda-stroke" d="M16 20 Q32 10 48 20 L44 56 H20 L16 20 Z" />
                                </svg>
                                <span class="like-label"><%= isLiked ? "" : "Refrescar" %></span>
                                <span class="like-count"><%= likesCount %></span>
                            </button>
                        </div>
                    </div>

                    <div class="comments-section">
                        <button class="comments-toggle" onclick="toggleComments(<%= review.getId() %>)">
                            💬 Ver Comentarios (<%= review.getCommentsCount() %>)
                        </button>
                        <div class="comments-container" id="comments-panel-<%= review.getId() %>">
                            <div class="comment-list" id="comment-list-<%= review.getId() %>"></div>
                            <% if (loggedUser != null) { %>
                                <div class="comment-form">
                                    <input type="text" class="comment-input" id="comment-input-<%= review.getId() %>" placeholder="Escribe un comentario respetuoso..." maxlength="500" autocomplete="off">
                                    <button class="btn-submit-comment" onclick="submitComment(<%= review.getId() %>)">Enviar</button>
                                </div>
                            <% } else { %>
                                <div style="font-size: 0.85rem; color: #888; text-align: center; margin-top: 10px;">Iniciá sesión para comentar.</div>
                            <% } %>
                        </div>
                    </div>
                </div> 
                <% if (isSpoiler) { %>
                    <label for="spoiler-<%= review.getId() %>" class="spoiler-overlay">
                        <div class="spoiler-text">Mostrar reseña</div>
                    </label>
                <% } %>
            </div> 
            <% } } else { %>
                <div class="review-card" style="text-align: center; padding: 40px;"><p style="color: #888;">Aún no hay reseñas.</p></div>
            <% } %>
        </div>
    </div>
    
    <% } else { %>
        <div style="text-align: center; margin-top: 100px;">
            <h1>Película no encontrada</h1>
            <a href="${pageContext.request.contextPath}/">Volver al inicio</a>
        </div>
    <% } %>
    
    <script>
    const currentUserId = <%= currentUserId != null ? currentUserId : "null" %>;
    const contextPath = '${pageContext.request.contextPath}';

    function showToast(message, type = 'success') {
        const container = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = 'toast ' + type;
        toast.textContent = message;
        container.appendChild(toast);
        setTimeout(() => toast.remove(), 4000);
    }

    const ajaxForm = document.getElementById('ajaxReviewForm');
    if (ajaxForm) {
        ajaxForm.addEventListener('submit', function(e) {
            e.preventDefault(); 
            
            document.getElementById('aiLoadingText').textContent = 'Nuestra IA está analizando tu reseña...';
            document.getElementById('aiLoadingOverlay').style.display = 'flex';
            
            const submitBtn = document.getElementById('submitReviewBtn');
            const origText = submitBtn.textContent;
            submitBtn.disabled = true;
            submitBtn.textContent = 'Enviando...';

            const payload = {
                id_movie: parseInt(document.getElementById('movieIdInput').value),
                review_text: document.getElementById('reviewTextInput').value,
                rating: parseFloat(document.getElementById('ratingInput').value),
                watched_on: document.getElementById('watchedOnInput').value
            };

            fetch(contextPath + '/reviews', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            })
            .then(res => {
                if(res.status === 403) {
                    return res.json().then(data => { throw data; });
                }
                return res.json();
            })
            .then(data => {
                if(data.id) {
                    pollReviewStatus(data.id);
                }
            })
            .catch(err => {
                document.getElementById('aiLoadingOverlay').style.display = 'none';
                submitBtn.disabled = false;
                submitBtn.textContent = origText;
                if(err.error) {
                    showModerationResult('🚫', 'Acción Denegada', err.error, true);
                } else {
                    showModerationResult('❌', 'Error', 'Ocurrió un error inesperado al procesar tu reseña.', false);
                }
            });
        });
    }

    function pollReviewStatus(reviewId) {
        let attempts = 0;
        
        const pollInterval = setInterval(() => {
            attempts++;
            
            fetch(contextPath + '/reviews?id=' + reviewId)
            .then(res => res.json())
            .then(review => {
                if (review && review.moderationStatus && review.moderationStatus !== 'PENDING_MODERATION') {
                    clearInterval(pollInterval);
                    document.getElementById('aiLoadingOverlay').style.display = 'none';
                    
                    if (review.moderationStatus === 'APPROVED') {
                        showModerationResult('✅', '¡Reseña Aprobada!', 'Tu reseña cumple con todas las normas y ya ha sido publicada.', true);
                    } else if (review.moderationStatus === 'SPOILER') {
                        showModerationResult('⚠️', 'Atención: Contiene Spoilers', 'Hemos detectado que tu reseña revela detalles clave de la trama. Se ha publicado, pero la hemos protegido para que otros usuarios no se arruinen la película accidentalmente.', true);
                    } else if (review.moderationStatus === 'REJECTED') {
                        showModerationResult('🚫', 'Reseña Rechazada', 'Tu reseña incumple nuestras normas de comunidad por contener lenguaje ofensivo o inapropiado. Tu cuenta ha sido baneada temporalmente.<br><br><strong>Motivo de la IA:</strong> ' + (review.moderationReason || 'Contenido tóxico.'), true);
                    }
                }
            })
            .catch(err => {
                clearInterval(pollInterval);
                document.getElementById('aiLoadingOverlay').style.display = 'none';
                showModerationResult('⚠️', 'Proceso Finalizado', 'La IA ha terminado de evaluar, la página se recargará para ver los cambios.', true);
            });

            if(attempts >= 20) {
                clearInterval(pollInterval);
                document.getElementById('aiLoadingOverlay').style.display = 'none';
                showModerationResult('⏱️', 'Demasiado tiempo', 'Nuestra IA está demorada, pero no te preocupes, tu reseña se está procesando en segundo plano.', true);
            }

        }, 1500); 
    }

    function showModerationResult(icon, title, message, reloadOnClose = false) {
        document.getElementById('resultIcon').textContent = icon;
        document.getElementById('resultTitle').textContent = title;
        document.getElementById('resultMessage').innerHTML = message;
        document.getElementById('resultOverlay').style.display = 'flex';

        const btn = document.getElementById('resultBtn');
        if (reloadOnClose) {
            btn.onclick = () => window.location.reload();
        } else {
            btn.onclick = () => {
                document.getElementById('resultOverlay').style.display = 'none';
            };
        }
    }

    function toggleFollow(targetUserId, btnElement) {
        if (currentUserId === null) {
            showToast('Debes iniciar sesión para seguir usuarios', 'error');
            return;
        }

        const isFollowing = btnElement.classList.contains('following');

        fetch(contextPath + '/follow', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'idUsuario=' + targetUserId + '&ajax=true'
        })
        .then(async res => {
            const text = await res.text();
            try { return JSON.parse(text); } 
            catch(e) { throw new Error("Respuesta inválida del servidor"); }
        })
        .then(data => {
            if (data && data.success) {
                if (isFollowing) {
                    btnElement.classList.remove('following');
                    btnElement.textContent = 'Seguir';
                } else {
                    btnElement.classList.add('following');
                    btnElement.textContent = 'Siguiendo';
                }
                
                const otherButtons = document.querySelectorAll('.follow-btn[data-user-id="' + targetUserId + '"]');
                otherButtons.forEach(btn => {
                    btn.className = btnElement.className;
                    btn.textContent = btnElement.textContent;
                });

                showToast(isFollowing ? 'Dejaste de seguir al usuario' : 'Comenzaste a seguir al usuario');
            } else {
                showToast('Error: ' + (data ? data.error : 'Desconocido'), 'error');
            }
        })
        .catch(err => {
            console.error(err);
            showToast('Error al procesar la acción', 'error');
        });
    }

    function sortReviews(criterion) {
        const container = document.querySelector('.reviews-list');
        const reviews = Array.from(container.querySelectorAll('.review-card'));
        if (reviews.length <= 1 && reviews[0].innerText.includes('Aún no hay reseñas')) return;

        reviews.sort((a, b) => {
            if (criterion === 'likes') {
                const likesA = parseInt(a.querySelector('.like-count').textContent) || 0;
                const likesB = parseInt(b.querySelector('.like-count').textContent) || 0;
                if (likesB !== likesA) return likesB - likesA;
                const dateA = parseInt(a.getAttribute('data-timestamp')) || 0;
                const dateB = parseInt(b.getAttribute('data-timestamp')) || 0;
                return dateB - dateA;
            } else if (criterion === 'rating_desc') {
                const ratingA = parseFloat(a.getAttribute('data-rating')) || 0;
                const ratingB = parseFloat(b.getAttribute('data-rating')) || 0;
                if (ratingB !== ratingA) return ratingB - ratingA;
                const dateA = parseInt(a.getAttribute('data-timestamp')) || 0;
                const dateB = parseInt(b.getAttribute('data-timestamp')) || 0;
                return dateB - dateA;
            } else if (criterion === 'rating_asc') {
                const ratingA = parseFloat(a.getAttribute('data-rating')) || 0;
                const ratingB = parseFloat(b.getAttribute('data-rating')) || 0;
                if (ratingB !== ratingA) return ratingA - ratingB; 
                const dateA = parseInt(a.getAttribute('data-timestamp')) || 0;
                const dateB = parseInt(b.getAttribute('data-timestamp')) || 0;
                return dateB - dateA;
            } else {
                const dateA = parseInt(a.getAttribute('data-timestamp')) || 0;
                const dateB = parseInt(b.getAttribute('data-timestamp')) || 0;
                return dateB - dateA;
            }
        });

        reviews.forEach(review => {
            review.style.opacity = '0';
            container.appendChild(review);
            setTimeout(() => review.style.opacity = '1', 50);
            review.style.transition = 'opacity 0.3s ease';
        });
    }

    document.addEventListener('DOMContentLoaded', function() {
        const starContainers = document.querySelectorAll('.star-container');
        const ratingInput = document.getElementById('ratingInput');
        const ratingDisplay = document.getElementById('ratingDisplay');
        if (starContainers.length > 0 && ratingInput) {
            const currentValue = parseFloat(ratingInput.value);
            if (currentValue > 0) updateStars(currentValue);
            starContainers.forEach(container => {
                const index = parseInt(container.getAttribute('data-index'));
                container.addEventListener('click', e => {
                    const rect = container.getBoundingClientRect();
                    const value = (e.clientX - rect.left) < rect.width / 2 ? index - 0.5 : index;
                    ratingInput.value = value;
                    ratingDisplay.textContent = value + ' kCal';
                    updateStars(value);
                });
                container.addEventListener('mousemove', e => {
                    const rect = container.getBoundingClientRect();
                    const value = (e.clientX - rect.left) < rect.width / 2 ? index - 0.5 : index;
                    updateIconType(value);
                    highlightStars(value);
                });
            });
            document.getElementById('starRating').addEventListener('mouseleave', () => { updateStars(parseFloat(ratingInput.value)); });
            
            function highlightStars(value) {
                starContainers.forEach(container => {
                    const index = parseInt(container.getAttribute('data-index'));
                    const full = container.querySelector('.popcorn-full'), half = container.querySelector('.popcorn-half');
                    if (index <= Math.floor(value)) { full.classList.add('active'); full.classList.remove('half-hidden'); half.classList.remove('active'); }
                    else if (index - 0.5 === value) { full.classList.add('half-hidden'); half.classList.add('active'); }
                    else { full.classList.remove('active'); full.classList.remove('half-hidden'); half.classList.remove('active'); }
                });
            }
            function updateStars(value) { updateIconType(value); highlightStars(value); }
            function updateIconType(value) {
                const path = contextPath + (value <= 2 ? '/utils/bad_movie.svg' : value <= 4 ? '/utils/good_movie.svg' : '/utils/great_movie.svg');
                starContainers.forEach(c => { c.querySelector('.popcorn-full').src = path; c.querySelector('.popcorn-half').src = path; });
            }
        }
    });

    function toggleLike(reviewId, button) {
        if (currentUserId === null) { showToast('Debes iniciar sesión para dar like', 'error'); return; }
        button.classList.add('animating'); setTimeout(() => button.classList.remove('animating'), 300);
        fetch('<%= request.getContextPath() %>/review-likes', {
            method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: 'action=like&reviewId=' + reviewId
        }).then(res => res.json()).then(data => {
            if (data.success) {
                const countSpan = button.querySelector('.like-count'), labelSpan = button.querySelector('.like-label');
                countSpan.style.opacity = '0';
                setTimeout(() => { countSpan.textContent = data.likesCount; countSpan.style.opacity = '1'; }, 150);
                if (data.liked) { button.classList.add('liked'); button.title = 'Quitar voto'; if(labelSpan) labelSpan.textContent = ""; }
                else { button.classList.remove('liked'); button.title = 'Votar reseña'; if(labelSpan) labelSpan.textContent = "Refrescar"; }
            } else { showToast(data.error, 'error'); }
        });
    }

    function toggleComments(reviewId) {
        const panel = document.getElementById('comments-panel-' + reviewId);
        if (panel.style.display === 'block') { panel.style.display = 'none'; } 
        else { panel.style.display = 'block'; loadComments(reviewId); }
    }

    function loadComments(reviewId) {
        const listContainer = document.getElementById('comment-list-' + reviewId);
        listContainer.innerHTML = '<div style="text-align: center; color: #888; padding: 10px;">Cargando...</div>';
        
        fetch('<%= request.getContextPath() %>/review-comments?reviewId=' + reviewId + '&t=' + new Date().getTime())
            .then(response => response.text())
            .then(text => {
                let data;
                try { data = JSON.parse(text); } catch (e) { listContainer.innerHTML = '<div style="color:red; text-align:center;">Error de servidor.</div>'; return; }
                listContainer.innerHTML = ''; 
                if (data.error) { listContainer.innerHTML = '<div style="color:red; text-align:center;">' + data.error + '</div>'; return; }
                if (!Array.isArray(data) || data.length === 0) { listContainer.innerHTML = '<div style="text-align:center; color:#888; padding: 10px;">Sin comentarios.</div>'; return; }
                
                data.forEach(comment => {
                    appendCommentToDOM(reviewId, comment.idComment, comment.idUsuario, comment.username, comment.createdAt, comment.commentText, comment.status, comment.isFollowing, comment.profilePicture);
                });
            });
    }

    function submitComment(reviewId) {
        const inputField = document.getElementById('comment-input-' + reviewId);
        const commentText = inputField.value.trim();
        const submitBtn = inputField.nextElementSibling;
        
        if (!commentText) return;
        
        document.getElementById('aiLoadingText').textContent = 'Nuestra IA está analizando tu comentario...';
        document.getElementById('aiLoadingOverlay').style.display = 'flex';
        
        inputField.disabled = true;
        submitBtn.disabled = true;
        const origText = submitBtn.textContent; 
        submitBtn.textContent = 'Enviando...';
        
        fetch('<%= request.getContextPath() %>/review-comments', {
            method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'action=create&reviewId=' + reviewId + '&commentText=' + encodeURIComponent(commentText)
        }).then(res => res.json()).then(data => {
            document.getElementById('aiLoadingOverlay').style.display = 'none';
            inputField.disabled = false; 
            submitBtn.disabled = false; 
            submitBtn.textContent = origText;
            
            if (data.success) {
                if (data.status === 'REJECTED') {
                    showModerationResult('🚫', 'Comentario Rechazado', 'Tu comentario incumple nuestras normas de comunidad por contener lenguaje ofensivo o inapropiado. Tu cuenta ha sido baneada temporalmente.', true);
                } else if (data.status === 'SPOILER') {
                    inputField.value = '';
                    loadComments(reviewId);
                    showModerationResult('⚠️', 'Atención: Contiene Spoilers', 'Hemos detectado que tu comentario revela detalles clave de la trama. Se ha publicado, pero oculto por defecto.', false);
                } else {
                    inputField.value = '';
                    loadComments(reviewId);
                    showModerationResult('✅', '¡Comentario Aprobado!', 'Tu comentario cumple con todas las normas y ya ha sido publicado.', false);
                }
            } else if (data.bannedUntil) {
                showModerationResult('🚫', 'Acción Denegada', 'No puedes comentar. Has sido baneado hasta: ' + data.bannedUntil, true);
            } else { 
                showModerationResult('❌', 'Error', data.error || 'Ocurrió un error inesperado al procesar tu comentario.', false);
            }
        }).catch(err => {
            document.getElementById('aiLoadingOverlay').style.display = 'none';
            inputField.disabled = false; 
            submitBtn.disabled = false; 
            submitBtn.textContent = origText;
            showModerationResult('❌', 'Error de conexión', 'No se pudo conectar con el servidor.', false);
        });
    }

    function startEditComment(commentId, btnElement) {
        const commentItem = btnElement.closest('.comment-item');
        const currentText = commentItem.getAttribute('data-text'); 
        const safeText = currentText.replace(/"/g, '&quot;');
        const textContainer = commentItem.querySelector('.comment-text-container');
        textContainer.innerHTML = '<input type="text" class="comment-input edit-input" value="' + safeText + '" style="width: 100%; margin-top: 5px;" autocomplete="off">' +
                            '<div style="margin-top: 5px; text-align: right;">' +
                                '<button onclick="saveEditComment(' + commentId + ', this)" class="btn-submit-comment" style="padding: 4px 10px; font-size: 0.8rem;">Guardar</button>' +
                                '<button onclick="cancelEditComment(this)" class="btn-submit-comment" style="background: #999; padding: 4px 10px; font-size: 0.8rem; margin-left: 5px;">Cancelar</button>' +
                            '</div>';
        commentItem.querySelector('.comment-actions').style.display = 'none';
    }

    function cancelEditComment(btnElement) {
        const reviewId = btnElement.closest('.comments-container').id.split('-')[2];
        loadComments(reviewId);
    }

    function saveEditComment(commentId, btnElement) {
        const commentItem = btnElement.closest('.comment-item');
        const newText = commentItem.querySelector('.edit-input').value.trim();
        const reviewId = btnElement.closest('.comments-container').id.split('-')[2];
        if (!newText) { showToast('Comentario vacío', 'error'); return; }
        
        document.getElementById('aiLoadingText').textContent = 'Nuestra IA está analizando tu edición...';
        document.getElementById('aiLoadingOverlay').style.display = 'flex';
        
        const btn = btnElement; btn.textContent = '...'; btn.disabled = true;

        fetch('<%= request.getContextPath() %>/review-comments', {
            method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'action=edit&commentId=' + commentId + '&commentText=' + encodeURIComponent(newText)
        }).then(r => r.json()).then(data => {
            document.getElementById('aiLoadingOverlay').style.display = 'none';
            
            if (data.success) {
                if (data.status === 'REJECTED') {
                    showModerationResult('🚫', 'Edición Rechazada', 'Tu edición incumple las normas. Tu cuenta ha sido baneada temporalmente.', true);
                } else if (data.status === 'SPOILER') {
                    loadComments(reviewId); 
                    showModerationResult('⚠️', 'Atención: Contiene Spoilers', 'Hemos detectado que tu edición revela detalles clave. Se ha actualizado, pero oculto por defecto.', false);
                } else {
                    loadComments(reviewId); 
                    showModerationResult('✅', '¡Edición Aprobada!', 'Tu comentario editado cumple con todas las normas y ya es visible.', false);
                }
            } else if (data.bannedUntil) {
                showModerationResult('🚫', 'Acción Denegada', 'No puedes comentar. Has sido baneado hasta: ' + data.bannedUntil, true);
                loadComments(reviewId);
            } else {
                showModerationResult('❌', 'Error', data.error || 'No se pudo editar el comentario.', false);
                btn.textContent = 'Guardar'; btn.disabled = false;
                loadComments(reviewId);
            }
        }).catch(err => {
            document.getElementById('aiLoadingOverlay').style.display = 'none';
            showModerationResult('❌', 'Error de conexión', 'No se pudo conectar con el servidor.', false);
            loadComments(reviewId);
        });
    }

    function deleteComment(commentId, reviewId) {
        if (!confirm("¿Eliminar comentario permanentemente?")) return;
        fetch('<%= request.getContextPath() %>/review-comments', {
            method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'action=delete&commentId=' + commentId
        }).then(r => r.json()).then(data => {
            if (data.success) { showToast('Eliminado'); loadComments(reviewId); } 
            else { showToast('Error al eliminar', 'error'); }
        });
    }

    function appendCommentToDOM(reviewId, commentId, commentUserId, username, date, text, status, isFollowing, profilePicture) {
        const list = document.getElementById('comment-list-' + reviewId);
        if (!list) return;
        
        const safeUser = username || 'Usuario';
        const safeDate = date || '';
        const safeText = text ? text.replace(/"/g, '&quot;') : ''; 
        const safeAvatar = profilePicture ? profilePicture : contextPath + '/utils/default_profile.png';

        let actionsHtml = '';
        if (currentUserId !== null && currentUserId === commentUserId) {
            actionsHtml = '<div class="comment-actions">' +
                            '<button class="btn-action" onclick="startEditComment(' + commentId + ', this)">Editar</button>' +
                            '<button class="btn-action delete" onclick="deleteComment(' + commentId + ', ' + reviewId + ')">Eliminar</button>' +
                          '</div>';
        }

        let badgeHtml = '';
        let contentHtml = '';

        if (status === 'SPOILER') {
            badgeHtml = '<span class="spoiler-badge">SPOILER</span>';
            contentHtml = '<input type="checkbox" class="comment-spoiler-checkbox" id="spoiler-comment-' + commentId + '">' +
                          '<div class="comment-text comment-spoiler-text">' + text + '</div>' +
                          '<label for="spoiler-comment-' + commentId + '" class="comment-spoiler-overlay">' +
                              '<span style="font-size: 0.75rem; font-weight: 600;">Mostrar comentario</span>' +
                          '</label>';
        } else {
            contentHtml = '<div class="comment-text">' + text + '</div>';
        }

        const userLinkHtml = '<a href="' + contextPath + '/profile?id=' + commentUserId + '" class="user-profile-link">' + safeUser + '</a>';

        let followBtnHtml = '';
        if (currentUserId !== null && currentUserId !== commentUserId) {
            const btnClass = isFollowing ? 'following' : '';
            const btnText = isFollowing ? 'Siguiendo' : 'Seguir';
            followBtnHtml = '<button class="follow-btn ' + btnClass + '" data-user-id="' + commentUserId + '" onclick="toggleFollow(' + commentUserId + ', this)">' + btnText + '</button>';
        }

        const html = '<div class="comment-item" data-text="' + safeText + '">' +
                        '<div class="comment-header">' +
                            '<div style="display: flex; align-items: center; gap: 10px;">' +
                                '<img src="' + safeAvatar + '" class="comment-author-avatar" onerror="this.src=\'' + contextPath + '/utils/default_profile.png\'">' +
                                '<div>' +
                                    '<div style="display: flex; align-items: center; gap: 8px;">' +
                                        '<strong>' + userLinkHtml + '</strong>' +
                                        badgeHtml +
                                        followBtnHtml +
                                    '</div>' +
                                    '<div class="comment-date" style="margin-left: 0;">' + safeDate + '</div>' +
                                '</div>' +
                            '</div>' +
                            actionsHtml +
                        '</div>' +
                        '<div class="comment-text-container" style="position: relative; margin-top: 8px;">' + contentHtml + '</div>' +
                      '</div>';
                      
        list.insertAdjacentHTML('beforeend', html);
    }
    </script>
</body>
</html>