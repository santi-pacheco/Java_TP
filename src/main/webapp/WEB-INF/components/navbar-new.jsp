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
        position: relative;
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
        gap: 25px;
    }
    
    .navbar-links a {
        text-decoration: none;
        color: #333;
        font-weight: 500;
        font-size: 18px;
        transition: color 0.3s;
    }
    
    .navbar-links a:hover {
        color: #666;
    }
    
    .navbar-center {
        position: absolute;
        left: 50%;
        transform: translateX(-50%);
        max-width: 500px;
        width: 500px;
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
    }
    
    .btn-login {
        padding: 10px 24px;
        background-color: #333;
        color: #FAF8F3;
        border: none;
        border-radius: 20px;
        font-family: 'Poppins', sans-serif;
        font-weight: 500;
        font-size: 16px;
        cursor: pointer;
        transition: background-color 0.3s;
    }
    
    .btn-login:hover {
        background-color: #555;
    }
    
    .btn-profile {
        width: 45px;
        height: 45px;
        border-radius: 50%;
        background-color: #E0E0E0;
        border: none;
        cursor: pointer;
        font-weight: 600;
        font-size: 18px;
        color: #333;
        transition: background-color 0.3s;
    }
    
    .btn-profile:hover {
        background-color: #D0D0D0;
    }
</style>

<nav class="navbar">
    <div class="navbar-left">
        <a href="${pageContext.request.contextPath}/">
            <img src="${pageContext.request.contextPath}/utils/export50.svg" alt="Fat Movies" class="navbar-logo">
        </a>
        <div class="navbar-links">
            <a href="${pageContext.request.contextPath}/peliculas">Películas</a>
            <a href="${pageContext.request.contextPath}/watchlist">Watchlist</a>
            <a href="${pageContext.request.contextPath}/reseñas">Reseñas</a>
        </div>
    </div>
    
    <div class="navbar-center">
        <form action="${pageContext.request.contextPath}/search" method="get">
            <input type="text" class="search-bar" placeholder="Buscar películas..." name="q" id="searchInput" autocomplete="off">
            <div id="searchResults" style="position: absolute; top: 100%; left: 0; right: 0; background: white; border: 1px solid #ddd; border-radius: 5px; max-height: 300px; overflow-y: auto; display: none; z-index: 1000; box-shadow: 0 4px 8px rgba(0,0,0,0.1);"></div>
        </form>
    </div>
    
    <div class="navbar-right">
        <button class="btn-login" onclick="window.location.href='${pageContext.request.contextPath}/login'">Iniciar Sesión</button>
        <button class="btn-profile" onclick="window.location.href='${pageContext.request.contextPath}/perfil'">P</button>
    </div>
</nav>

<script>
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
                
                // CORREGIDO (el comentario)
                // IMPORTANTE: Asegúrate de que el servlet responda UTF-8 (Solución 2)
                
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
            // CORREGIDO
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
    
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.navbar-center')) {
            searchResults.style.display = 'none';
        }
    });
</script>