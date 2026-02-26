<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page pageEncoding="UTF-8" %>
<style>
    .navbar {
        background-color: #FAF8F3;
        padding: 15px 40px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        position: sticky;
        top: 0;
        z-index: 100;
    }
    
    .navbar-left {
        display: flex;
        align-items: center;
        gap: 30px;
    }
    
    .navbar-logo {
        width: 60px;
        height: 60px;
        cursor: pointer;
        object-fit: cover;
        object-position: center;
        transform: scale(1.8);
    }
    
    .navbar-links {
        display: flex;
        gap: 15px;
    }
    
    .navbar-links a {
        text-decoration: none;
        color: #333;
        font-weight: 500;
        font-size: 16px;
        padding: 8px 16px;
        border-radius: 8px;
        transition: color 0.3s;
    }
    
    .navbar-links a:hover {
        color: #333;
        background-color: #F0F0F0;
    }
    
    .navbar-links a.active {
        color: #111;
        background-color: #E0E0E0;
        font-weight: 600;
    }
    
    .navbar-center {
        flex: 1;
        display: flex;
        justify-content: center;
        max-width: 500px;
        margin: 0 20px;
    }
    
    .navbar-center form {
        width: 100%;
        position: relative;
    }
    
    .search-bar {
        width: 100%;
        padding: 10px 20px;
        border: 2px solid #E0E0E0;
        border-radius: 25px;
        font-family: 'Poppins', sans-serif;
        font-size: 14px;
        outline: none;
        transition: border-color 0.3s;
    }
    
    .search-bar:focus {
        border-color: #999;
    }
    
    .navbar-right {
        display: flex;
        align-items: center;
        gap: 15px;
        position: relative;
    }
    
    .btn-login {
        padding: 10px 24px;
        background-color: #333;
        color: #FAF8F3;
        border: none;
        border-radius: 20px;
        font-family: 'Poppins', sans-serif;
        font-weight: 400;
        font-size: 16px;
        cursor: pointer;
        transition: background-color 0.3s;
    }
    
    .btn-login:hover {
        background-color: #555;
    }
    
    /* --- ESTILOS DEL AVATAR NAVBAR --- */
    .navbar-avatar-wrapper {
        border-radius: 50%;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        text-decoration: none;
    }

    .navbar-avatar-img {
        width: 45px;
        height: 45px;
        border-radius: 50%;
        object-fit: cover;
        border: 2px solid #E0E0E0;
        cursor: pointer;
        transition: all 0.3s;
        vertical-align: middle;
    }

    /* Borde de Hamburguesa Exclusivo para Navbar */
    .burger-avatar-border {
        border-radius: 50% !important;
        padding: 3px;
        background: linear-gradient(180deg, 
            #F5B041 0%, #F5B041 30%,   
            #58D68D 30%, #58D68D 40%,   
            #873600 40%, #873600 70%,   
            #F4D03F 70%, #F4D03F 100%   
        ) !important;
        box-shadow: 0 2px 6px rgba(0,0,0,0.2) !important;
        border: none !important;
    }

    .burger-avatar-border .navbar-avatar-img {
        border-radius: 50% !important;
        border: 2px solid #FFF !important; /* Separa el borde interno para que la hamburguesa respire */
        position: relative;
        z-index: 2;
    }
    
    .navbar-avatar-wrapper:hover .navbar-avatar-img {
        transform: scale(1.05);
    }

    /* --- ESTILOS DE NOTIFICACIONES --- */
    .nav-icon-wrapper { position: relative; cursor: pointer; margin-right: 5px; display: flex; align-items: center; justify-content: center; width: 40px; height: 40px; border-radius: 50%; transition: background 0.2s; }
    .nav-icon-wrapper:hover { background: #EAE5DB; }
    .nav-icon { width: 24px; height: 24px; color: #333; }
    .notification-badge { position: absolute; top: 6px; right: 6px; width: 10px; height: 10px; background-color: #ff3b30; border-radius: 50%; border: 2px solid #FAF8F3; display: none; }
    
    .notif-dropdown { position: absolute; top: 60px; right: 0; width: 380px; background: white; border-radius: 12px; box-shadow: 0 10px 25px rgba(0,0,0,0.15); display: none; flex-direction: column; overflow: hidden; z-index: 1000; border: 1px solid #eee; }
    
    .notif-header { padding: 15px 20px; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center; }
    .notif-header h3 { margin: 0; font-size: 1.1rem; color: #333; font-weight: 600; }
    
    .notif-tabs { display: flex; border-bottom: 1px solid #eee; background: #fdfdfd; }
    .notif-tab { flex: 1; text-align: center; padding: 10px 0; font-size: 0.85rem; font-weight: 600; color: #888; cursor: pointer; border-bottom: 2px solid transparent; transition: all 0.2s; }
    .notif-tab.active { color: #333; border-bottom-color: #333; }
    .notif-tab:hover:not(.active) { color: #555; background: #f5f5f5; }

    .notif-body { max-height: 400px; overflow-y: auto; }
    
    .notif-item { display: flex; padding: 15px 20px; border-bottom: 1px solid #f5f5f5; gap: 15px; align-items: center; transition: background 0.2s; text-decoration: none; color: inherit; }
    .notif-item:hover { background: #fcfcfc; }
    .notif-item.unread { background: #F4F8FA; }
    
    .notif-avatar { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; border: 1px solid #eee; flex-shrink: 0; }
    .notif-content { flex: 1; font-size: 0.9rem; line-height: 1.4; color: #444; }
    .notif-content strong { color: #111; }
    .notif-time { font-size: 0.75rem; color: #999; margin-top: 4px; }
    .notif-indicator {
        width: 10px;
        height: 10px;
        background-color: #0095f6; 
        border-radius: 50%;
        margin-left: auto;
        flex-shrink: 0;
    }
    
    .empty-notifs { padding: 30px; text-align: center; color: #888; font-size: 0.95rem; }
    .tab-indicator {
        display: inline-block;
        width: 6px;
        height: 6px;
        background-color: #0095f6;
        border-radius: 50%;
        vertical-align: top;
        margin-left: 3px;
        margin-top: -2px;
    }
</style>

<nav class="navbar">
    <div class="navbar-left">
        <a href="${pageContext.request.contextPath}/home">
            <img src="${pageContext.request.contextPath}/utils/export50.svg" alt="Fat Movies" class="navbar-logo">
        </a>
        
        <c:set var="realUrl" value="${requestScope['javax.servlet.forward.request_uri'] != null ? requestScope['javax.servlet.forward.request_uri'] : pageContext.request.requestURI}" />

        <div class="navbar-links">
            <a href="${pageContext.request.contextPath}/movies-page" 
               class="${fn:contains(realUrl, 'movies-page') ? 'active' : ''}">Películas</a>
            <a href="${pageContext.request.contextPath}/watchlist" 
               class="${fn:contains(realUrl, 'watchlist') ? 'active' : ''}">Watchlist</a>
            <a href="${pageContext.request.contextPath}/resenas" 
               class="${fn:contains(realUrl, 'userReviews') || fn:contains(realUrl, '/resenas') ? 'active' : ''}">Reseñas</a>
            <a href="${pageContext.request.contextPath}/comunidad" 
               class="${fn:contains(realUrl, 'community') || fn:contains(realUrl, '/comunidad') ? 'active' : ''}">Comunidad</a>
        </div>
    </div>
    
    <div class="navbar-center">
        <form action="${pageContext.request.contextPath}/search" method="get">
            <input type="text" class="search-bar" placeholder="Buscar películas, actores, directores..." name="q" id="searchInput" autocomplete="off">
            <div id="searchResults" style="position: absolute; top: 100%; left: 0; right: 0; background: white; border: 1px solid #ddd;
            border-radius: 5px; max-height: 300px; overflow-y: auto; display: none; z-index: 1000;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);"></div>
        </form>
    </div>
    
    <div class="navbar-right">
        <%
            Object usuarioLogueado = session.getAttribute("usuarioLogueado");
            if (usuarioLogueado != null) {
                entity.User user = (entity.User) usuarioLogueado;
                
                String navAvatar = request.getContextPath() + "/utils/default_profile.png";
                if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                    navAvatar = "/fatmovies_uploads/" + user.getProfileImage();
                }
        %>
            <% if ("admin".equals(user.getRole())) { %>
                <button class="btn-login" onclick="window.location.href='${pageContext.request.contextPath}/admin'" style="background:#8B7355;">Admin</button>
            <% } %>
            
            <div class="nav-icon-wrapper" id="notifTrigger">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                </svg>
                <div class="notification-badge" id="notifBadge"></div>
                
                <div class="notif-dropdown" id="notifDropdown">
                    <div class="notif-header">
                        <h3>Notificaciones</h3>
                    </div>
                    <div class="notif-tabs">
                        <div class="notif-tab active" id="tab-ALL" onclick="filterNotifs('ALL')">Todas</div>
                        <div class="notif-tab" id="tab-LIKE" onclick="filterNotifs('LIKE')">Likes</div>
                        <div class="notif-tab" id="tab-COMMENT" onclick="filterNotifs('COMMENT')">Comentarios</div>
                        <div class="notif-tab" id="tab-FOLLOW" onclick="filterNotifs('FOLLOW')">Seguidores</div>
                    </div>
                    <div class="notif-body" id="notifBody">
                        <div class="empty-notifs">Cargando...</div>
                    </div>
                </div>
            </div>
            
            <a href="${pageContext.request.contextPath}/profile" title="<%= user.getUsername() %>" 
               class="navbar-avatar-wrapper <%= user.getNivelUsuario() >= 3 ? "burger-avatar-border" : "" %>">
                <img src="<%= navAvatar %>" alt="Perfil" class="navbar-avatar-img" onerror="this.src='${pageContext.request.contextPath}/utils/default_profile.png'">
            </a>
            
            <button class="btn-login" onclick="window.location.href='${pageContext.request.contextPath}/logout'" style="background:#666;">Cerrar Sesión</button>
        <%
            } else {
        %>
            <button class="btn-login" onclick="window.location.href='${pageContext.request.contextPath}/login'">Iniciar Sesión</button>
        <%
            }
        %>
    </div>
    <%-- LÓGICA DE CARTEL DE SUBIDA DE NIVEL --%>
    <%
        if (usuarioLogueado != null) {
            entity.User userLvl = (entity.User) usuarioLogueado;
            if (userLvl.getNivelUsuario() > userLvl.getNivelNotificado()) {
                int newLevel = userLvl.getNivelUsuario();
                String modalTitle = "";
                String modalBody = "";
                String modalIcon = request.getContextPath() + "/utils/level" + newLevel + ".svg";

                if (newLevel == 2) {
                    modalTitle = "¡Nivel 2: Cinefilo en Volumen!";
                    modalBody = "¡Vemos que estás engordando a base de buenas críticas! Para saciar ese apetito, hemos expandido el límite de tu Watchlist.";
                } else if (newLevel == 3) {
                    modalTitle = "¡Nivel 3: Peso Pesado!";
                    modalBody = "Tus opiniones están ganando peso en la comunidad. Literalmente. Ahora tu perfil tiene un marco exclusivo que demuestra tu nivel y podés elegir tu Plato Principal en tu perfil.";
                } else if (newLevel >= 4) {
                    modalTitle = "¡Nivel 4: Crítico Michelin!";
                    modalBody = "Te has convertido en un catador de cine. Tu paladar es ley, tus reseñas tienen un diseño destacado y tu voto afecta más al promedio general.";
                }
    %>
                <style>
                    .levelup-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.8); z-index: 10000; display: flex; justify-content: center; align-items: center; animation: fadeInBg 0.5s; }
                    .levelup-card { background: linear-gradient(145deg, #ffffff, #f0ede6); padding: 40px; border-radius: 20px; text-align: center; max-width: 450px; width: 90%; box-shadow: 0 15px 35px rgba(0,0,0,0.3); border: 3px solid #8B7355; animation: bounceIn 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
                    .levelup-img { width: 120px; height: 120px; margin-bottom: 20px; animation: pulseImg 2s infinite; }
                    .levelup-title { color: #8B7355; font-size: 1.8rem; font-weight: 700; margin-bottom: 15px; text-transform: uppercase; letter-spacing: 1px;}
                    .levelup-body { color: #555; font-size: 1.1rem; line-height: 1.6; margin-bottom: 30px; }
                    .levelup-btn { background: #8B7355; color: white; border: none; padding: 12px 40px; border-radius: 50px; font-size: 1.1rem; font-weight: 600; cursor: pointer; transition: all 0.3s; }
                    .levelup-btn:hover { background: #6b5840; transform: scale(1.05); }
                    @keyframes fadeInBg { from { opacity: 0; } to { opacity: 1; } }
                    @keyframes bounceIn { 0% { transform: scale(0.3); opacity: 0; } 50% { transform: scale(1.05); } 70% { transform: scale(0.9); } 100% { transform: scale(1); opacity: 1; } }
                    @keyframes pulseImg { 0% { transform: scale(1); } 50% { transform: scale(1.1); } 100% { transform: scale(1); } }
                </style>

                <div class="levelup-overlay" id="levelUpModal">
                    <div class="levelup-card">
                        <img src="<%= modalIcon %>" alt="Level Up" class="levelup-img" onerror="this.src='${pageContext.request.contextPath}/utils/export50.svg'">
                        <div class="levelup-title">🚀 <%= modalTitle %></div>
                        <div class="levelup-body"><%= modalBody %></div>
                        <button class="levelup-btn" onclick="closeLevelUpModal(<%= newLevel %>)">¡A comer!</button>
                    </div>
                </div>

                <script>
                    function closeLevelUpModal(level) {
                        document.getElementById('levelUpModal').style.display = 'none';
                        // Llamamos al server para que no lo vuelva a mostrar
                        fetch('${pageContext.request.contextPath}/api/level-notified', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                            body: 'level=' + level
                        }).catch(err => console.error(err));
                    }
                </script>
    <%
            }
        }
    %>
</nav>

<script>
    // --- LÓGICA DEL BUSCADOR ---
    var searchInput = document.getElementById('searchInput');
    var searchResults = document.getElementById('searchResults');
    var searchTimeout;
    
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            var query = this.value.trim();
            
            if (query.length < 2) {
                searchResults.style.display = 'none';
                return;
            }
            
            searchTimeout = setTimeout(function() {
                var xhr = new XMLHttpRequest();
                xhr.open('GET', '${pageContext.request.contextPath}/search-api?q=' + encodeURIComponent(query), true);
                
                xhr.onreadystatechange = function() {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        try {
                            var movies = JSON.parse(xhr.responseText);
                            displaySearchResults(movies);
                        } catch (e) {
                            console.error('Error parsing JSON:', e);
                        }
                    }
                };
                xhr.send();
            }, 300);
        });
    }
    
    function displaySearchResults(movies) {
        if (movies.length === 0) {
            searchResults.innerHTML = '<div style="padding: 15px; text-align: center; color: #666;">No se encontraron películas</div>';
        } else {
            var html = '';
            for (var i = 0; i < Math.min(movies.length, 8); i++) {
                var movie = movies[i];
                html += '<div style="padding: 12px; border-bottom: 1px solid #eee; cursor: pointer; display: flex; align-items: center;" onclick="selectMovie(' + movie.id + ')">';
                if (movie.posterPath) {
                    html += '<img src="https://image.tmdb.org/t/p/w92' + movie.posterPath + '" style="width: 35px; height: 52px; margin-right: 12px; border-radius: 4px;" onerror="this.style.display=\'none\'">';
                }
                
                html += '<div><div style="font-weight: 500;">' + movie.titulo + '</div>';
                if (movie.estrenoYear) {
                    html += '<div style="font-size: 12px; color: #666;">' + movie.estrenoYear + '</div>';
                }
                html += '</div></div>';
            }
            searchResults.innerHTML = html;
        }
        searchResults.style.display = 'block';
    }
    
    function selectMovie(movieId) {
        window.location.href = '${pageContext.request.contextPath}/movie/' + movieId;
    }


 // --- LÓGICA DE NOTIFICACIONES ---
    const notifTrigger = document.getElementById('notifTrigger');
    const notifDropdown = document.getElementById('notifDropdown');
    const notifBadge = document.getElementById('notifBadge');
    const notifBody = document.getElementById('notifBody');
    let allNotifications = [];
    let isDropdownOpen = false;
    let currentFilter = 'ALL';

    function timeAgo(dateString) {
        const date = new Date(dateString);
        const seconds = Math.floor((new Date() - date) / 1000);
        let interval = seconds / 31536000;
        if (interval > 1) return Math.floor(interval) + "a";
        interval = seconds / 2592000;
        if (interval > 1) return Math.floor(interval) + "m";
        interval = seconds / 86400;
        if (interval > 1) return Math.floor(interval) + "d";
        interval = seconds / 3600;
        if (interval > 1) return Math.floor(interval) + "h";
        interval = seconds / 60;
        if (interval > 1) return Math.floor(interval) + " min";
        return Math.floor(seconds) + " seg";
    }


    function updateTabIndicators() {
        const hasNewLikes = allNotifications.some(n => n.unread && n.tipo === 'LIKE');
        const hasNewComments = allNotifications.some(n => n.unread && n.tipo === 'COMMENT');
        const hasNewFollows = allNotifications.some(n => n.unread && n.tipo === 'FOLLOW');
        const hasAnyNew = hasNewLikes || hasNewComments || hasNewFollows;

        const dotHtml = '<div class="tab-indicator"></div>';

        document.getElementById('tab-ALL').innerHTML = 'Todas' + (hasAnyNew ? dotHtml : '');
        document.getElementById('tab-LIKE').innerHTML = 'Likes' + (hasNewLikes ? dotHtml : '');
        document.getElementById('tab-COMMENT').innerHTML = 'Comentarios' + (hasNewComments ? dotHtml : '');
        document.getElementById('tab-FOLLOW').innerHTML = 'Seguidores' + (hasNewFollows ? dotHtml : '');
    }

    function loadNotifications() {
        if(!notifTrigger) return;
        
        fetch('${pageContext.request.contextPath}/notifications-api?t=' + new Date().getTime())
            .then(res => res.json())
            .then(data => {
                allNotifications = data;
                
                const hasNew = data.some(n => n.unread);
                if (hasNew) {
                    notifBadge.style.display = 'block';
                } else {
                    notifBadge.style.display = 'none';
                }
                
                updateTabIndicators(); 
                renderNotifs();
            })
            .catch(e => console.error("Error loading notifs", e));
    }

    function renderNotifs() {
        const filtered = currentFilter === 'ALL' ? allNotifications : allNotifications.filter(n => n.tipo === currentFilter);
        
        if (filtered.length === 0) {
            notifBody.innerHTML = '<div class="empty-notifs">No hay nada por aquí aún.</div>';
            return;
        }

        let html = '';
        filtered.forEach(n => {
            const avatarPath = n.actorProfileImage ? ('/fatmovies_uploads/' + n.actorProfileImage) : '${pageContext.request.contextPath}/utils/default_profile.png';
            
            const unreadClass = n.unread ? 'unread' : '';
            const indicator = n.unread ? '<div class="notif-indicator"></div>' : ''; 
            
            let text = '';
            let link = '#';
            let icon = '';

            if (n.tipo === 'LIKE') {
                link = '${pageContext.request.contextPath}/movie/' + n.reviewId;
                icon = '❤️';
                if (n.extraCount > 0) {
                    text = 'A <strong>' + n.actorUsername + '</strong> y <strong>' + n.extraCount + ' personas más</strong> les gustó tu reseña de ' + n.movieTitle + '.';
                } else {
                    text = 'A <strong>' + n.actorUsername + '</strong> le gustó tu reseña de ' + n.movieTitle + '.';
                }
            } 
            else if (n.tipo === 'COMMENT') {
                link = '${pageContext.request.contextPath}/movie/' + n.reviewId;
                icon = '💬';
                const snippet = n.commentText.length > 30 ? n.commentText.substring(0,30) + '...' : n.commentText;
                text = '<strong>' + n.actorUsername + '</strong> comentó en tu reseña de ' + n.movieTitle + ': "' + snippet + '"';
            }
            else if (n.tipo === 'FOLLOW') {
                link = '${pageContext.request.contextPath}/profile?id=' + n.actorId;
                icon = '👤';
                text = '<strong>' + n.actorUsername + '</strong> comenzó a seguirte.';
            }

            html += '<a href="' + link + '" class="notif-item ' + unreadClass + '">' +
                        '<img src="' + avatarPath + '" class="notif-avatar" onerror="this.src=\'${pageContext.request.contextPath}/utils/default_profile.png\'">' +
                        '<div class="notif-content">' +
                            text +
                            '<div class="notif-time">' + icon + ' ' + timeAgo(n.fecha) + '</div>' +
                        '</div>' +
                        indicator +
                    '</a>';
        });
        notifBody.innerHTML = html;
    }

    function filterNotifs(type) {
        currentFilter = type;
        document.querySelectorAll('.notif-tab').forEach(tab => tab.classList.remove('active'));
        

        document.getElementById('tab-' + type).classList.add('active');
        
        renderNotifs();
    }

    if(notifTrigger) {
        loadNotifications();

        notifTrigger.addEventListener('click', function(e) {
            e.stopPropagation();
            
            if (isDropdownOpen) {

                notifDropdown.style.display = 'none';
                isDropdownOpen = false;
                

                allNotifications.forEach(n => n.unread = false); 
                updateTabIndicators(); 
            } else {

                if(searchResults) searchResults.style.display = 'none';
                notifDropdown.style.display = 'flex';
                isDropdownOpen = true;
                
                const unreadCount = allNotifications.filter(n => n.unread).length;
                if (unreadCount > 0) {
                    notifBadge.style.display = 'none'; 
                    
                    fetch('${pageContext.request.contextPath}/notifications-api', { method: 'POST' })
                        .catch(err => console.error("Error al actualizar notificaciones", err));
                }
            }
        });
        notifDropdown.addEventListener('click', function(e) {
            e.stopPropagation(); 
        });
    }

    document.addEventListener('click', function(e) {
        if (searchResults && !e.target.closest('.navbar-center')) {
            searchResults.style.display = 'none';
        }
        if (notifDropdown && isDropdownOpen && !e.target.closest('#notifTrigger')) {
            notifDropdown.style.display = 'none';
            isDropdownOpen = false;
            
            allNotifications.forEach(n => n.unread = false);
            updateTabIndicators(); 
        }
    });
</script>