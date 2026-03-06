<%@ page import="entity.Movie"%>
<%@ page import="java.util.Set" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= request.getAttribute("movie") != null ? "Editar" : "Crear" %> Película - FatMovies</title>
<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
<style>
* { margin: 0; padding: 0; box-sizing: border-box; }

body {
    font-family: 'Poppins', sans-serif;
    background: linear-gradient(135deg, #FAF8F3 0%, #F0EDE6 100%);
    min-height: 100vh;
    padding: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.form-container {
    max-width: 800px;
    width: 100%;
}

.card {
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    overflow: hidden;
}

.card-header {
    padding: 20px 25px;
    border-bottom: 1px solid #f0f0f0;
    background: #f8f9fa;
}

.card-title {
    font-size: 1.5rem;
    font-weight: 600;
    color: #1a1a1a;
}

.card-body {
    padding: 25px;
}

.alert {
    padding: 12px 15px;
    border-radius: 8px;
    margin-bottom: 20px;
    font-size: 0.9rem;
}

.alert-danger {
    background: #f8d7da;
    color: #721c24;
    border: 1px solid #f5c6cb;
}

.alert ul {
    margin: 8px 0 0 20px;
    padding: 0;
}

.form-row {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 15px;
    margin-bottom: 20px;
}

.form-group {
    margin-bottom: 20px;
}

.form-group label {
    display: block;
    margin-bottom: 6px;
    font-weight: 500;
    color: #333;
    font-size: 0.9rem;
}

.form-control {
    width: 100%;
    padding: 10px 12px;
    border: 2px solid #e0e0e0;
    border-radius: 8px;
    font-family: 'Poppins', sans-serif;
    font-size: 0.95rem;
    outline: none;
    transition: border-color 0.3s;
}

.form-control:focus {
    border-color: #999;
}

textarea.form-control {
    resize: vertical;
    min-height: 80px;
}

.form-actions {
    display: flex;
    gap: 10px;
    margin-top: 25px;
}

.btn {
    padding: 10px 20px;
    border-radius: 8px;
    font-weight: 500;
    font-size: 0.95rem;
    text-decoration: none;
    border: none;
    cursor: pointer;
    transition: all 0.3s;
    display: inline-flex;
    align-items: center;
    justify-content: center;
}

.btn-primary {
    background: #333;
    color: white;
    flex: 1;
}

.btn-primary:hover {
    background: #555;
    transform: translateY(-2px);
}

.btn-secondary {
    background: #e0e0e0;
    color: #333;
    flex: 1;
}

.btn-secondary:hover {
    background: #d0d0d0;
    text-decoration: none;
    color: #333;
}

@media (max-width: 768px) {
    body { padding: 15px; }
    .card-title { font-size: 1.3rem; }
    .form-row { grid-template-columns: 1fr; }
}
</style>
</head>
<body>
<div class="form-container">
    <div class="card">
        <div class="card-header">
            <h2 class="card-title"><%= request.getAttribute("movie") != null ? "Editar" : "Crear" %> Película</h2>
        </div>
        <div class="card-body">
            <% Movie movie = (Movie) request.getAttribute("movie"); %>
            <% 
                Set<String> errors = (Set<String>) request.getAttribute("errors");
                if (errors != null && !errors.isEmpty()) { 
            %>
                <div class="alert alert-danger">
                    <strong>Error(es) en el formulario:</strong>
                    <ul>
                        <% for (String error : errors) { %>
                            <li><%= error %></li>
                        <% } %>
                    </ul>
                </div>
            <% } %>
            
            <form action="<%= request.getContextPath() %>/movies" method="POST">
                <input type="hidden" name="accion" value="<%= (movie != null && movie.getMovieId() != 0) ? "actualizar" : "crear" %>">
                <% if (movie != null) { %>
                    <input type="hidden" name="id" value="<%= movie.getMovieId() %>">
                <% } %>
                
                <div class="form-row">
                    <div class="form-group">
                        <label>ID API:</label>
                        <input type="number" name="id_api" class="form-control" value="<%= movie != null ? movie.getApiId() : "" %>" required>
                    </div>
                    <div class="form-group">
                        <label>Año Estreno:</label>
                        <input type="number" name="estrenoYear" class="form-control" value="<%= movie != null ? movie.getReleaseYear() : "" %>" required>
                    </div>
                </div>
                
                <div class="form-group">
                    <label>Título:</label>
                    <input type="text" name="titulo" class="form-control" value="<%= movie != null ? movie.getTitle() : "" %>" required>
                </div>
                
                <div class="form-group">
                    <label>Título Original:</label>
                    <input type="text" name="tituloOriginal" class="form-control" value="<%= movie != null ? movie.getOriginalTitle() : "" %>" required>
                </div>
                
                <div class="form-group">
                    <label>Sinopsis:</label>
                    <textarea name="sinopsis" class="form-control" required><%= movie != null ? movie.getSynopsis() : "" %></textarea>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label>Duración (HH:MM):</label>
                        <input type="time" name="duracion" class="form-control" value="<%= movie != null && movie.getDuration() != null ? movie.getDuration().toString().substring(0,5) : "" %>" required>
                    </div>
                    <div class="form-group">
                        <label>Puntuación API:</label>
                        <input type="number" step="0.1" name="puntuacionApi" class="form-control" value="<%= movie != null ? movie.getApiRating() : "" %>" required>
                    </div>
                    <div class="form-group">
                        <label>Popularidad:</label>
                        <input type="number" step="0.1" name="popularidad" class="form-control" value="<%= movie != null ? movie.getPopularity() : "" %>" required>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label>Idioma Original:</label>
                        <input type="text" name="idiomaOriginal" class="form-control" maxlength="2" value="<%= movie != null ? movie.getOriginalLanguage() : "" %>" required>
                    </div>
                    <div class="form-group">
                        <label>Votos API:</label>
                        <input type="number" name="votosApi" class="form-control" value="<%= movie != null ? movie.getApiVotes() : "" %>" required>
                    </div>
                    <div class="form-group">
                        <label>Adulto:</label>
                        <select name="adulto" class="form-control" required>
                            <option value="false" <%= movie != null && !movie.getIsAdult() ? "selected" : "" %>>No</option>
                            <option value="true" <%= movie != null && movie.getIsAdult() ? "selected" : "" %>>Sí</option>
                        </select>
                    </div>
                </div>
                
                <div class="form-group">
                    <label>Poster Path:</label>
                    <input type="text" name="posterPath" class="form-control" value="<%= movie != null ? movie.getPosterPath() : "" %>" required>
                </div>
                
                <div class="form-group">
                    <label>ID IMDB:</label>
                    <input type="text" name="id_imdb" class="form-control" value="<%= movie != null && movie.getImdbId() != null ? movie.getImdbId() : "" %>">
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Guardar</button>
                    <a href="<%= request.getContextPath() %>/movies" class="btn btn-secondary">Cancelar</a>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>
