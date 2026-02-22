<%@ page import="entity.Review"%>
<%@ page import="entity.ModerationStatus"%>
<%@ page import="entity.User" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<title>Detalle de Reseña — FatMovies</title>
<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<style>
    body { background: #FAF8F3; font-family: 'Poppins', sans-serif; color: #333; }
    .container { max-width: 900px; }
    .detail-wrapper { background: #fff; padding: 40px; margin: 50px auto; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.08); }
    h2, h4 { font-weight: 700; color: #2c3e50; }
    .review-text { background: #f9f9f9; padding: 20px; border-radius: 5px; margin: 20px 0; line-height: 1.7; font-size: 1.1em; border-left: 4px solid #2b7cff; }
    .info-row { margin: 15px 0; font-size: 1.1em; }
    .info-label { font-weight: 600; display: inline-block; width: 180px; color: #555; }
    .status-badge { padding: 6px 12px; border-radius: 15px; font-weight: 600; display: inline-block; font-size: 0.9em; }
    .status-pending { background-color: #fff3cd; color: #856404; }
    .status-approved { background-color: #d4edda; color: #155724; }
    .status-rejected { background-color: #f8d7da; color: #721c24; }

    /* Likes y Comentarios */
    .actions-bar { margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; display: flex; align-items: center; gap: 30px; }
    .like-section { display: flex; align-items: center; gap: 10px; }
    .like-btn { background: none; border: 2px solid #ddd; border-radius: 20px; padding: 8px 15px; cursor: pointer; display: flex; align-items: center; gap: 8px; font-weight: 600; transition: all 0.3s ease; }
    .like-btn:hover { border-color: #2b7cff; color: #2b7cff; }
    .like-btn.liked { background-color: #2b7cff; color: white; border-color: #2b7cff; }
    .like-btn img { width: 20px; height: 20px; transition: transform 0.2s; }
    .like-btn.liked img { filter: brightness(0) invert(1); }
    .like-btn:active img { transform: scale(1.2); }
    #likeCount { font-weight: 700; font-size: 1.2em; }

    .comments-section { margin-top: 40px; }
    .comment { background: #f8f9fa; padding: 15px; border-radius: 5px; margin-bottom: 15px; border: 1px solid #e9ecef; }
    .comment-author { font-weight: 600; color: #2b7cff; }
    .comment-date { font-size: 0.9em; color: #888; margin-left: 10px; }
    .comment-text { margin-top: 5px; }
    #commentForm textarea { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; resize: vertical; }
    #commentForm button { margin-top: 10px; }
    .btn-primary { background-color: #2b7cff; border-color: #2b7cff; }
    .btn-primary:hover { background-color: #1a5cd8; border-color: #1a5cd8; }
</style>
</head>
<body>
<div class="container">
    <div class="detail-wrapper">
        <h2><span class="glyphicon glyphicon-list-alt"></span> Detalle de Reseña</h2>
        <% 
            Review review = (Review) request.getAttribute("review");
            User loggedUser = (User) session.getAttribute("usuarioLogueado");
            boolean isAdmin = loggedUser != null && "admin".equals(loggedUser.getRole());
        %>
        <% if (review != null) { %>
        
        <div class="info-row"><span class="info-label">ID:</span><span><%= review.getId() %></span></div>
        <div class="info-row"><span class="info-label">Usuario:</span><span><%= review.getUsername() != null ? review.getUsername() : "Usuario " + review.getId_user() %></span></div>
        <div class="info-row"><span class="info-label">Película:</span><span><%= review.getMovieTitle() != null ? review.getMovieTitle() : "Película #" + review.getId_movie() %></span></div>
        <div class="info-row"><span class="info-label">Rating (kcal):</span><span><%= String.format("%.1f", review.getRating()) %> / 5.0</span></div>
        <div class="info-row"><span class="info-label">Fecha de creación:</span><span><%= review.getCreated_at() %></span></div>
        <div class="info-row"><span class="info-label">Visto el:</span><span><%= review.getWatched_on() %></span></div>
        
        <h4 style="margin-top: 30px;">Texto de la Reseña:</h4>
        <div class="review-text"><%= review.getReview_text() %></div>

        <!-- Barra de Acciones: Likes y Comentarios -->
        <div class="actions-bar">
            <div class="like-section">
                <button class="like-btn" id="likeBtn" onclick="toggleLike()">
                    <img src="<%= request.getContextPath() %>/utils/like_icon.svg" alt="Like">
                    <span id="likeBtnText">Like</span>
                </button>
                <span id="likeCount">0 Likes</span>
            </div>
            <a href="#comments-section" class="btn btn-default">Ver Comentarios</a>
        </div>

        <!-- Sección de Comentarios -->
        <div id="comments-section" class="comments-section">
            <h4><span class="glyphicon glyphicon-comment"></span> Comentarios</h4>
            <div id="commentsList">
                <!-- Los comentarios se cargarán aquí dinámicamente -->
            </div>
            <% if (loggedUser != null) { %>
                <form id="commentForm" style="margin-top: 20px;">
                    <div class="form-group">
                        <label for="commentText">Añadir un comentario:</label>
                        <textarea class="form-control" id="commentText" rows="3" placeholder="Escribe tu comentario..."></textarea>
                    </div>
                    <button type="button" class="btn btn-primary" onclick="postComment()">Publicar Comentario</button>
                </form>
            <% } else { %>
                <p><a href="<%= request.getContextPath() %>/login">Inicia sesión</a> para dejar un comentario.</p>
            <% } %>
        </div>

        <% if (isAdmin) { %>
            <div style="margin-top: 40px; padding-top: 30px; border-top: 1px solid #eee;">
                <h4><span class="glyphicon glyphicon-cog"></span> Panel de Moderación</h4>
                <div class="info-row">
                    <span class="info-label">Estado Actual:</span>
                    <% 
                        String statusClass = "";
                        String statusText = "";
                        if (review.getModerationStatus() == ModerationStatus.PENDING_MODERATION) {
                            statusClass = "status-pending";
                            statusText = "Pendiente de Moderación";
                        } else if (review.getModerationStatus() == ModerationStatus.APPROVED) {
                            statusClass = "status-approved";
                            statusText = "Aprobada";
                        } else if (review.getModerationStatus() == ModerationStatus.REJECTED) {
                            statusClass = "status-rejected";
                            statusText = "Rechazada";
                        }
                    %>
                    <span class="status-badge <%= statusClass %>"><%= statusText %></span>
                </div>
                <% if (review.getModerationStatus() == ModerationStatus.REJECTED && review.getModerationReason() != null) { %>
                <div class="info-row">
                    <span class="info-label">Razón del rechazo:</span>
                    <span class="text-danger"><%= review.getModerationReason() %></span>
                </div>
                <% } %>
                
                <form action="<%= request.getContextPath() %>/reviews-admin" method="POST" style="margin-top: 20px;">
                    <input type="hidden" name="accion" value="actualizarModeracion">
                    <input type="hidden" name="id" value="<%= review.getId() %>">
                    
                    <div class="form-group">
                        <label><input type="radio" name="status" value="PENDING_MODERATION" <%= review.getModerationStatus() == ModerationStatus.PENDING_MODERATION ? "checked" : "" %>> Pendiente</label>
                        <label><input type="radio" name="status" value="APPROVED" <%= review.getModerationStatus() == ModerationStatus.APPROVED ? "checked" : "" %>> Aprobar</label>
                        <label><input type="radio" name="status" value="REJECTED" <%= review.getModerationStatus() == ModerationStatus.REJECTED ? "checked" : "" %>> Rechazar</label>
                    </div>
                    
                    <div class="form-group">
                        <label for="reason">Razón (si aplica):</label>
                        <textarea class="form-control" name="reason" id="reason" rows="2"><%= review.getModerationReason() != null ? review.getModerationReason() : "" %></textarea>
                    </div>
                    
                    <button type="submit" class="btn btn-primary">Guardar Estado</button>
                    <a href="<%= request.getContextPath() %>/reviews-admin" class="btn btn-default">Volver al Listado</a>
                </form>
            </div>
        <% } %>
        
        <% } else { %>
            <div class="alert alert-warning">Reseña no encontrada</div>
            <a href="<%= request.getContextPath() %>/reviews-admin" class="btn btn-default">Volver al Listado</a>
        <% } %>
    </div>
</div>

<script>
    const contextPath = '<%= request.getContextPath() %>';
    const reviewId = <%= review != null ? review.getId() : "null" %>;
    const userId = <%= loggedUser != null ? loggedUser.getId() : "null" %>;

    document.addEventListener('DOMContentLoaded', function() {
        if (reviewId) {
            fetchLikes();
            fetchComments();
        }
    });

    async function fetchLikes() {
        try {
            const response = await fetch(`${contextPath}/likes?reviewId=${reviewId}`);
            const data = await response.json();
            if (response.ok) {
                updateLikeState(data.likes, data.userHasLiked);
            }
        } catch (error) {
            console.error('Error fetching likes:', error);
        }
    }

    async function fetchComments() {
        try {
            const response = await fetch(`${contextPath}/comments?reviewId=${reviewId}`);
            const comments = await response.json();
            if (response.ok) {
                const commentsList = document.getElementById('commentsList');
                commentsList.innerHTML = '';
                comments.forEach(comment => {
                    const commentEl = document.createElement('div');
                    commentEl.className = 'comment';
                    commentEl.innerHTML = `
                        <p class="comment-author">${comment.username || 'Usuario'}</p>
                        <p class="comment-text">${comment.comment_text}</p>
                        <small class="comment-date">${new Date(comment.created_at).toLocaleString()}</small>
                    `;
                    commentsList.appendChild(commentEl);
                });
            }
        } catch (error) {
            console.error('Error fetching comments:', error);
        }
    }

    function updateLikeState(likeCount, userHasLiked) {
        document.getElementById('likeCount').textContent = `${likeCount} Likes`;
        const likeBtn = document.getElementById('likeBtn');
        const likeBtnText = document.getElementById('likeBtnText');
        if (userHasLiked) {
            likeBtn.classList.add('liked');
            likeBtnText.textContent = 'Liked';
        } else {
            likeBtn.classList.remove('liked');
            likeBtnText.textContent = 'Like';
        }
    }

    async function toggleLike() {
        if (!userId) {
            window.location.href = `${contextPath}/login`;
            return;
        }
        try {
            const response = await fetch(`${contextPath}/likes`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ reviewId: reviewId, userId: userId })
            });
            const data = await response.json();
            if (response.ok) {
                updateLikeState(data.likes, data.userHasLiked);
            } else {
                alert('Error al procesar el like: ' + data.message);
            }
        } catch (error) {
            console.error('Error toggling like:', error);
        }
    }

    async function postComment() {
        const commentText = document.getElementById('commentText').value;
        if (!commentText.trim()) {
            alert('El comentario no puede estar vacío.');
            return;
        }
        try {
            const response = await fetch(`${contextPath}/comments`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    reviewId: reviewId,
                    userId: userId,
                    commentText: commentText
                })
            });
            if (response.ok) {
                document.getElementById('commentText').value = '';
                fetchComments();
            } else {
                const data = await response.json();
                alert('Error al publicar comentario: ' + data.message);
            }
        } catch (error) {
            console.error('Error posting comment:', error);
        }
    }
</script>

</body>
</html>