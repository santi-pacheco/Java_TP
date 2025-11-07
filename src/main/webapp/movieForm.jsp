<%@ page import="entity.Movie"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Set" %>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<title>Formulario de Película</title>
<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<style>
body { background: #FAF8F3;
	font-family: 'Poppins', sans-serif; }
.form-wrapper { background: #fff; padding: 30px; margin: 50px auto; max-width: 800px; border-radius: 5px; 
	box-shadow: 0 1px 1px rgba(0,0,0,.05); }
</style>
</head>
<body>
<div class="container">
    <div class="form-wrapper">

        <% Movie movie = (Movie) request.getAttribute("movie"); %>
        <h2><%= (movie != null && movie.getId() != 0) ? "Editar" : "Crear" %> Película</h2>
        
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
		<% 
		} 
		%>
        
        
        <form action="<%= request.getContextPath() %>/movies" method="POST">

            <input type="hidden" name="accion" value="<%= (movie != null && movie.getId() != 0) ? "actualizar" : "crear" %>">

            <% if (movie != null) { %>
                <input type="hidden" name="id" value="<%= movie.getId() %>">
            <% } %>
            
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label>ID API:</label>
                        <input type="number" name="id_api" class="form-control" value="<%= movie != null ? movie.getId_api() : "" %>" required>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label>Año Estreno:</label>
                        <input type="number" name="estrenoYear" class="form-control" value="<%= movie != null ? movie.getEstrenoYear() : "" %>" required>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <label>Título:</label>
                <input type="text" name="titulo" class="form-control" value="<%= movie != null ? movie.getTitulo() : "" %>" required>
            </div>
            <div class="form-group">
                <label>Título Original:</label>
                <input type="text" name="tituloOriginal" class="form-control" value="<%= movie != null ? movie.getTituloOriginal() : "" %>" required>
            </div>
            <div class="form-group">
                <label>Sinopsis:</label>
                <textarea name="sinopsis" class="form-control" rows="3" required><%= movie != null ? movie.getSinopsis() : "" %></textarea>
            </div>
            <div class="row">
                <div class="col-md-4">
                    <div class="form-group">
                        <label>Duración (HH:MM):</label>
                        <input type="time" name="duracion" class="form-control" value="<%= movie != null && movie.getDuracion() != null ? movie.getDuracion().toString().substring(0,5) : "" %>" required>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                         <label>Puntuación API:</label>
                        <input type="number" step="0.1" name="puntuacionApi" class="form-control" value="<%= movie != null ? movie.getPuntuacionApi() : "" %>" required>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                         <label>Popularidad:</label>
                        <input type="number" step="0.1" name="popularidad" class="form-control" value="<%= movie != null ? movie.getPopularidad() : "" %>" required> 
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-4">
                     <div class="form-group">
                        <label>Idioma Original:</label>
                        <input type="text" name="idiomaOriginal" class="form-control" maxlength="2" value="<%= movie != null ? movie.getIdiomaOriginal() : "" %>" required>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                         <label>Votos API:</label>
                        <input type="number" name="votosApi" class="form-control" value="<%= movie != null ? movie.getVotosApi() : "" %>" required>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                         <label>Adulto:</label>
                        <select name="adulto" class="form-control" required>
                            <option value="false" <%= movie != null && !movie.getAdulto() ? "selected" : "" %>>No</option>
                            <option value="true" <%= movie != null && movie.getAdulto() ? "selected" : "" %>>Sí</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="form-group">
                 <label>Poster Path:</label>
                <input type="text" name="posterPath" class="form-control" value="<%= movie != null ? movie.getPosterPath() : "" %>" required>
            </div>
            <div class="form-group">
                <label>ID IMDB:</label>
                <input type="text" name="id_imdb" class="form-control" value="<%= movie != null && movie.getId_imdb() != null ? movie.getId_imdb() : "" %>">
            </div>
            <button type="submit" class="btn btn-primary">Guardar</button>
            <a href="<%= request.getContextPath() %>/movies" class="btn btn-default">Cancelar</a>
        </form>
    </div>
</div>
</body>
</html>