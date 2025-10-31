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
          
          <label>Rating (1-5):</label>
          <input type="number" id="rating" value="4.5" min="1" max="5" step="0.1" required>
          
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
          showResult('📋 Reseñas de la película ' + movieId + ':\n' + JSON.stringify(data, null, 2));
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
