<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Comunidad - FatMovies</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    
    <style>
        body { background: #FAF8F3; font-family: 'Poppins', sans-serif; margin: 0; padding: 0; }
        .community-container { 
            max-width: 1100px;
            margin: 40px auto; 
            padding: 0 20px; 
            display: grid;
            grid-template-columns: 1fr 350px;
            gap: 40px;
            align-items: start;
        }

        .sidebar-section {
            position: sticky;
            top: 100px;
            z-index: 10;
        }

        @media (max-width: 850px) {
            .community-container {
                grid-template-columns: 1fr;
            }
            .sidebar-section {
                position: static;
                order: -1;
            }
        }

        .search-section { background: #fff; padding: 30px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); margin-bottom: 30px; position: relative; }
        .search-section h3 { margin-top: 0; margin-bottom: 20px; font-weight: 700; color: #333; font-size: 1.5rem; }
        .user-search-input { width: 100%; padding: 15px 20px; font-size: 16px; border: 2px solid #eee; border-radius: 8px; outline: none; transition: all 0.3s; box-sizing: border-box; }
        .user-search-input:focus { border-color: #8B7355; box-shadow: 0 0 8px rgba(139, 115, 85, 0.2); }
        .user-results { position: absolute; top: 100%; left: 30px; right: 30px; background: white; border: 1px solid #eee; border-radius: 0 0 8px 8px; box-shadow: 0 10px 20px rgba(0,0,0,0.1); z-index: 1000; display: none; max-height: 350px; overflow-y: auto; }
        .user-result-item { display: flex; align-items: center; padding: 15px 20px; cursor: pointer; border-bottom: 1px solid #f5f5f5; text-decoration: none; color: #333; transition: background 0.2s; }
        .user-result-item:hover { background-color: #fcfcfc; text-decoration: none; color: #8B7355; }
        .result-avatar { width: 45px; height: 45px; border-radius: 50%; object-fit: cover; margin-right: 15px; border: 2px solid #eee; }
        .result-username { font-weight: 600; font-size: 16px; }

        .feed-header { border-bottom: 2px solid #eee; padding-bottom: 15px; margin-bottom: 25px; }
        .feed-header h2 { font-size: 1.8rem; font-weight: 700; color: #333; margin: 0; }
        
        .review-card {
            background: white;
            border-radius: 12px;
            padding: 24px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            display: flex;
            gap: 20px;
            transition: transform 0.2s, box-shadow 0.2s;
            margin-bottom: 24px;
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
        .user-info { display: flex; align-items: center; gap: 12px; }
        .user-avatar { width: 42px; height: 42px; border-radius: 50%; object-fit: cover; border: 2px solid #f0f0f0; }
        .user-details { display: flex; flex-direction: column; }
        .user-name { font-weight: 700; font-size: 15px; color: #333; text-decoration: none; }
        .user-name:hover { text-decoration: underline; color: #8B7355; }
        .review-date { font-size: 13px; color: #999; }
        
        .movie-title-header { font-size: 22px; font-weight: 600; color: #333; margin: 0; }
        .rating-date { display: flex; align-items: center; gap: 16px; flex-wrap: wrap; }
        .rating { display: flex; align-items: center; gap: 6px; font-size: 16px; font-weight: 600; color: #8B7355; }
        .review-text { font-size: 15px; line-height: 1.6; color: #555; margin: 0; }

        .loading-sentinel { display: flex; justify-content: center; padding: 30px 0; }
        .spinner { width: 40px; height: 40px; border: 4px solid #f3f3f3; border-top: 4px solid #8B7355; border-radius: 50%; animation: spin 1s linear infinite; }
        @keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
        
        .no-more-data { text-align: center; color: #888; padding: 20px; display: none; font-weight: 500;}
    </style>
</head>
<body>

<%@ include file="/WEB-INF/components/navbar-new.jsp" %>

<div class="container community-container">
    
    <div class="feed-section">
        <div class="feed-header">
            <h2>Últimas Reseñas</h2>
        </div>
        
        <div id="feed-container"></div>

        <div id="loading-sentinel" class="loading-sentinel">
            <div class="spinner"></div>
        </div>
        
        <div id="no-more-data" class="no-more-data">
            Has llegado al final. ¡No hay más reseñas! 🎬
        </div>
    </div>

    <div class="sidebar-section">
        <div class="search-section">
            <h3>🔍 Buscar Cinéfilos</h3>
            <input type="text" id="communitySearchInput" class="user-search-input" placeholder="Escribe un nombre (ej. mariano)..." autocomplete="off">
            <div id="communitySearchResults" class="user-results"></div>
        </div>
    </div>

</div>

<script>
	function escapeHTML(str) {
	    if (!str) return '';
	    return String(str)
	        .replace(/&/g, '&amp;')
	        .replace(/</g, '&lt;')
	        .replace(/>/g, '&gt;')
	        .replace(/"/g, '&quot;')
	        .replace(/'/g, '&#039;');
	}
    const userSearchInput = document.getElementById('communitySearchInput');
    const userSearchResults = document.getElementById('communitySearchResults');
    let userSearchTimeout;

    userSearchInput.addEventListener('input', function() {
        clearTimeout(userSearchTimeout);
        const query = this.value.trim();
        if (query.length < 1) { userSearchResults.style.display = 'none'; return; }
        
        userSearchTimeout = setTimeout(function() {
            fetch('${pageContext.request.contextPath}/api/search-users?q=' + encodeURIComponent(query))
                .then(response => response.json())
                .then(users => displayUserResults(users))
                .catch(error => console.error('Error buscando usuarios:', error));
        }, 300);
    });

    function displayUserResults(users) {
        if (users.length === 0) {
            userSearchResults.innerHTML = '<div style="padding: 15px; text-align: center; color: #999;">No encontramos a nadie con ese nombre 🕵️‍♂️</div>';
        } else {
            let html = '';
            users.forEach(user => {
                let safeImage = escapeHTML(user.profileImage);
                let safeUsername = escapeHTML(user.username);
                let avatarUrl = user.profileImage ? `${pageContext.request.contextPath}/uploads/\${safeImage}` : `${pageContext.request.contextPath}/utils/no-user.png`;
                
                html += `<a href="${pageContext.request.contextPath}/profile?id=\${user.id}" class="user-result-item">
                            <img src="\${avatarUrl}" class="result-avatar" alt="\${safeUsername}">
                            <span class="result-username">\${safeUsername}</span>
                        </a>`;
            });
            userSearchResults.innerHTML = html;
        }
        userSearchResults.style.display = 'block';
    }

    document.addEventListener('click', function(event) {
        if (!event.target.closest('.search-section')) userSearchResults.style.display = 'none';
    });

    let offset = 0;
    let isFetching = false;
    let hasMoreData = true;

    const feedContainer = document.getElementById('feed-container');
    const sentinel = document.getElementById('loading-sentinel');
    const noMoreDataMsg = document.getElementById('no-more-data');

    const observer = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && !isFetching && hasMoreData) {
            fetchMoreReviews();
        }
    });

    observer.observe(sentinel);

    function fetchMoreReviews() {
        isFetching = true;
        sentinel.style.display = 'flex';

        fetch(`${pageContext.request.contextPath}/api/feed?offset=\${offset}`)
            .then(response => response.json())
            .then(data => {
                
                if (data.length === 0) {
                    hasMoreData = false;
                    sentinel.style.display = 'none';
                    if (offset === 0) {
                        feedContainer.innerHTML = `
                            <div style="background: #fff; padding: 60px 20px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); text-align: center; color: #666;">
                                <h4>Aún no hay actividad en tu comunidad</h4>
                                <p>Busca usuarios arriba, síguelos, y aquí aparecerán sus reseñas.</p>
                            </div>`;
                    } else {
                        noMoreDataMsg.style.display = 'block';
                    }
                    return;
                }

                data.forEach(review => {
                    const reviewHTML = createReviewCard(review);
                    feedContainer.insertAdjacentHTML('beforeend', reviewHTML);
                });
                offset += data.length;
                isFetching = false;
            })
            .catch(error => {
                console.error("Error cargando el feed:", error);
                isFetching = false;
                sentinel.style.display = 'none';
            });
    }

    function createReviewCard(review) {
    	let safeUsername = escapeHTML(review.username);
        let safeMovieTitle = escapeHTML(review.movieTitle);
        let safeText = escapeHTML(review.text);
        
        let avatarUrl = review.userAvatar ? `${pageContext.request.contextPath}/uploads/\${escapeHTML(review.userAvatar)}` : `${pageContext.request.contextPath}/utils/no-user.png`;
        let posterUrl = review.posterPath ? `https://image.tmdb.org/t/p/w500\${escapeHTML(review.posterPath)}` : `${pageContext.request.contextPath}/utils/no-poster.png`;
        
        return `
        <div class="review-card" style="cursor: pointer;" onclick="window.location.href='${pageContext.request.contextPath}/movie/\${review.movieId}'">
            <img src="\${posterUrl}" class="movie-poster" alt="\${safeMovieTitle}" onerror="this.src='${pageContext.request.contextPath}/utils/no-poster.png'">
            <div class="review-content">
                <div class="user-info">
                    <img src="\${avatarUrl}" class="user-avatar" alt="\${safeUsername}">
                    <div class="user-details">
                        <a href="${pageContext.request.contextPath}/profile?id=\${review.userId}" class="user-name" onclick="event.stopPropagation()">\${safeUsername}</a>
                        <span class="review-date">\${escapeHTML(review.dateFormatted)}</span>
                    </div>
                </div>
                <h3 class="movie-title-header">\${safeMovieTitle}</h3>
                <div class="rating-date">
                    <div class="rating">🍿 \${parseFloat(review.rating).toFixed(1)} / 5.0 Kcals</div>
                </div>
                <div class="review-text">
                    \${safeText}
                </div>
            </div>
        </div>
    `;
    }
</script>

</body>
</html>