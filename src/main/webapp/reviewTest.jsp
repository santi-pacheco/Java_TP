<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="entity.User" %>
<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8" />
  <title>Probar Reviews — FatMovies</title>
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; background: #f5f5f5; }
    .container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
    .section { margin-bottom: 30px; padding: 15px; border: 1px solid #ddd; border-radius: 6px; }
    .btn { padding: 10px 15px; background: #2b7cff; color: white; border: none; border-radius: 4px; cursor: pointer; margin: 5px; }
    .btn:hover { background: #1a5cd8; }
    .btn-danger { background: #dc3545; }
    .btn-danger:hover { background: #c82333; }
    textarea { width: 100%; height: 100px; padding: 8px; border: 1px solid #ddd; border-radius: 4px; resize: vertical; }
    input[type="number"], input[type="date"] { padding: 8px; border: 1px solid #ddd; border-radius: 4px; margin: 5px; }
    .result { background: #f8f9fa; padding: 15px; border-radius: 4px; margin-top: 10px; white-space: pre-wrap; font-family: monospace; }
    .error { background: #f8d7da; color: #721c24; }
    .success { background: #d4edda; color: #155724; }
    .user-info { background: #e3f2fd; padding: 10px; border-radius: 4px; margin-bottom: 20px; }
    .star-rating { display: inline-flex; cursor: pointer; gap: 8px; }
    .star-container { position: relative; display: inline-block; width: 60px; height: 60px; }
    .popcorn-full { width: 60px; height: 60px; opacity: 0.3; transition: opacity 0.2s; display: block; object-fit: contain; }
    .popcorn-full.active { opacity: 1; }
    .popcorn-half { position: absolute; left: 0; top: 0; width: 60px; height: 60px; clip-path: inset(0 50% 0 0); opacity: 0.3; transition: opacity 0.2s; display: none; object-fit: contain; }
    .popcorn-half.active { opacity: 1; display: block; }
    .popcorn-full.half-hidden { display: none; }
    .popcorn-full[src*="great_movie"], .popcorn-half[src*="great_movie"] { transform: scale(1.25); }
    #ratingDisplay { margin-left: 15px; font-weight: bold; color: #2b7cff; font-size: 1.2rem; }
  </style>
</head>
<body>
  <div class="container">
    <h1>🎬 Probar Sistema de Reviews</h1>
    
    <!-- Información del usuario logueado -->
    <div class="user-info">
      <%
        User loggedUser = (User) session.getAttribute("usuarioLogueado");
        if (loggedUser != null) {
      %>
        <strong>👤 Usuario logueado:</strong> <%= loggedUser.getUsername() %> 
        (ID: <%= loggedUser.getId() %>, Role: <%= loggedUser.getRole() %>)
      <% } else { %>
        <strong>⚠️ No hay usuario logueado.</strong> 
        <a href="<%= request.getContextPath() %>/login">Ir a Login</a>
      <% } %>
    </div>

    <!-- Sección 1: Crear Reseña -->
    <div class="section">
      <h3>📝 Crear Reseña</h3>
      <% if (loggedUser != null) { %>
        <form id="createReviewForm">
          <label>ID Película:</label>
          <input type="number" id="movieId" value="1" min="1" required><br><br>
          
          <label>Texto de la reseña:</label><br>
          <textarea id="reviewText" placeholder="Escribe tu reseña aquí..." required>Esta película es increíble! La recomiendo mucho. Los efectos especiales son espectaculares.</textarea><br><br>
          
          <label>Rating (0-5):</label>
          <div class="star-rating" id="starRating">
            <div class="star-container" data-index="1">
              <img src="<%= request.getContextPath() %>/utils/good_movie.svg" class="popcorn-full" alt="popcorn">
              <img src="<%= request.getContextPath() %>/utils/good_movie.svg" class="popcorn-half" alt="popcorn">
            </div>
            <div class="star-container" data-index="2">
              <img src="<%= request.getContextPath() %>/utils/good_movie.svg" class="popcorn-full" alt="popcorn">
              <img src="<%= request.getContextPath() %>/utils/good_movie.svg" class="popcorn-half" alt="popcorn">
            </div>
            <div class="star-container" data-index="3">
              <img src="<%= request.getContextPath() %>/utils/good_movie.svg" class="popcorn-full" alt="popcorn">
              <img src="<%= request.getContextPath() %>/utils/good_movie.svg" class="popcorn-half" alt="popcorn">
            </div>
            <div class="star-container" data-index="4">
              <img src="<%= request.getContextPath() %>/utils/good_movie.svg" class="popcorn-full" alt="popcorn">
              <img src="<%= request.getContextPath() %>/utils/good_movie.svg" class="popcorn-half" alt="popcorn">
            </div>
            <div class="star-container" data-index="5">
              <img src="<%= request.getContextPath() %>/utils/good_movie.svg" class="popcorn-full" alt="popcorn">
              <img src="<%= request.getContextPath() %>/utils/good_movie.svg" class="popcorn-half" alt="popcorn">
            </div>
          </div>
          <input type="hidden" id="rating" value="0" required>
          <span id="ratingDisplay">0 estrellas</span>
          
          <label>Fecha que viste la película:</label>
          <input type="date" id="watchedOn" value="2024-01-15" required><br><br>
          
          <button type="button" class="btn" onclick="createReview()">Crear Reseña</button>
        </form>
      <% } else { %>
        <p>Debes estar logueado para crear reseñas.</p>
      <% } %>
    </div>

    <!-- Sección 2: Ver Reseñas -->
    <div class="section">
      <h3>👀 Ver Reseñas</h3>
      <label>ID Película:</label>
      <input type="number" id="viewMovieId" value="1" min="1">
      <button type="button" class="btn" onclick="getReviewsByMovie()">Ver Reseñas de esta Película</button>
      <br><br>
      
      <label>ID Usuario:</label>
      <input type="number" id="viewUserId" value="1" min="1">
      <label>ID Película:</label>
      <input type="number" id="viewUserMovieId" value="1" min="1">
      <button type="button" class="btn" onclick="getReviewByUserAndMovie()">Ver Reseña Específica</button>
    </div>

    <!-- Sección 3: Administración (solo admins) -->
    <% if (loggedUser != null && "admin".equals(loggedUser.getRole())) { %>
      <div class="section">
        <h3>🛠️ Administración (Solo Admins)</h3>
        <button type="button" class="btn" onclick="getPendingSpoilerReviews()">Ver Reseñas Pendientes de Revisión</button>
        <br><br>
        
        <label>ID Reseña:</label>
        <input type="number" id="spoilerReviewId" value="1" min="1">
        <button type="button" class="btn" onclick="markAsSpoiler(true)">Marcar como Spoiler</button>
        <button type="button" class="btn" onclick="markAsSpoiler(false)">Marcar como No Spoiler</button>
      </div>
    <% } %>

    <!-- Sección 4: Resultados -->
    <div class="section">
      <h3>📊 Resultados</h3>
      <div id="result" class="result">Los resultados aparecerán aquí...</div>
    </div>
  </div>

  <script>
    const contextPath = '<%= request.getContextPath() %>';
    
    // Star rating system
    document.addEventListener('DOMContentLoaded', function() {
      const starContainers = document.querySelectorAll('.star-container');
      const ratingInput = document.getElementById('rating');
      const ratingDisplay = document.getElementById('ratingDisplay');
      
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
          ratingDisplay.textContent = value + ' estrellas';
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
        const contextPath = '<%= request.getContextPath() %>';
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
    });
    
    function showResult(data, isError = false) {
      const resultDiv = document.getElementById('result');
      resultDiv.className = 'result ' + (isError ? 'error' : 'success');
      resultDiv.textContent = typeof data === 'string' ? data : JSON.stringify(data, null, 2);
    }

    async function createReview() {
      try {
        const response = await fetch(contextPath + '/reviews', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify({
            id_movie: parseInt(document.getElementById('movieId').value),
            review_text: document.getElementById('reviewText').value,
            rating: parseFloat(document.getElementById('rating').value),
            watched_on: document.getElementById('watchedOn').value
          })
        });
        
        const data = await response.json();
        if (response.ok) {
          showResult('✅ Reseña creada exitosamente:\n' + JSON.stringify(data, null, 2));
        } else {
          showResult('❌ Error: ' + data.message, true);
        }
      } catch (error) {
        showResult('❌ Error de conexión: ' + error.message, true);
      }
    }

    async function getReviewsByMovie() {
      try {
        const movieId = document.getElementById('viewMovieId').value;
        const response = await fetch(contextPath + '/reviews?movieId=' + movieId, {
          credentials: 'include'
        });
        
        const data = await response.json();
        if (response.ok) {
          let result = '📋 Reseñas de la película ' + movieId + ':\n\n';
          data.forEach(review => {
            const stars = getStarDisplay(review.rating);
            result += `👤 ${review.username || 'Usuario #' + review.id_user}\n`;
            result += `${stars} (${review.rating})\n`;
            result += `💬 ${review.review_text}\n`;
            result += `📅 Visto: ${review.watched_on}\n\n`;
          });
          showResult(result);
        } else {
          showResult('❌ Error: ' + data.message, true);
        }
      } catch (error) {
        showResult('❌ Error de conexión: ' + error.message, true);
      }
    }

    async function getReviewByUserAndMovie() {
      try {
        const userId = document.getElementById('viewUserId').value;
        const movieId = document.getElementById('viewUserMovieId').value;
        const response = await fetch(contextPath + '/reviews?userId=' + userId + '&movieId=' + movieId, {
          credentials: 'include'
        });
        
        if (response.status === 404) {
          showResult('ℹ️ No se encontró reseña para el usuario ' + userId + ' y película ' + movieId);
          return;
        }
        
        const data = await response.json();
        if (response.ok) {
          showResult('🔍 Reseña específica:\n' + JSON.stringify(data, null, 2));
        } else {
          showResult('❌ Error: ' + data.message, true);
        }
      } catch (error) {
        showResult('❌ Error de conexión: ' + error.message, true);
      }
    }

    async function getPendingSpoilerReviews() {
      try {
        const response = await fetch(contextPath + '/reviews', {
          credentials: 'include'
        });
        
        const data = await response.json();
        if (response.ok) {
          showResult('⏳ Reseñas pendientes de revisión:\n' + JSON.stringify(data, null, 2));
        } else {
          showResult('❌ Error: ' + data.message, true);
        }
      } catch (error) {
        showResult('❌ Error de conexión: ' + error.message, true);
      }
    }

    function getStarDisplay(rating) {
      let stars = '';
      for (let i = 1; i <= 5; i++) {
        if (rating >= i) {
          stars += '★';
        } else if (rating >= i - 0.5) {
          stars += '½';
        } else {
          stars += '☆';
        }
      }
      return stars;
    }
    
    async function markAsSpoiler(containsSpoiler) {
      try {
        const reviewId = document.getElementById('spoilerReviewId').value;
        const response = await fetch(contextPath + '/reviews?id=' + reviewId, {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify({ contieneSpoiler: containsSpoiler })
        });
        
        const data = await response.json();
        if (response.ok) {
          showResult('✅ Estado de spoiler actualizado: ' + (containsSpoiler ? 'TIENE SPOILER' : 'NO TIENE SPOILER'));
        } else {
          showResult('❌ Error: ' + data.message, true);
        }
      } catch (error) {
        showResult('❌ Error de conexión: ' + error.message, true);
      }
    }
  </script>
</body>
</html>
