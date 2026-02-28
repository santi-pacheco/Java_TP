<%@ page import="java.util.List"%>
<%@ page import="entity.Movie"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<title>FatMovies - Gestión de Películas</title>
<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<style>
body { color: #333; background: #FAF8F3; font-family: 'Poppins', sans-serif; }
.table-wrapper { background: #fff; padding: 20px; margin: 30px 0; border-radius: 5px; box-shadow: 0 1px 1px rgba(0,0,0,.05); max-height: 600px; overflow-y: auto; }
.table-title h2 { margin: 0; font-size: 24px; }
.search-box { margin: 15px 0; }
table.table tr th, table.table tr td { border-color: #e9e9e9; padding: 8px; vertical-align: middle; font-size: 12px; }
.movie-poster { width: 40px; height: 60px; object-fit: cover; }
.loading { text-align: center; padding: 10px; display: none; }
</style>
</head>
<body>
<div class="container">
    <a href="<%= request.getContextPath() %>/admin.jsp" class="btn btn-primary" style="margin: 20px 0; background-color: #8B7355; border-color: #8B7355;">
        <i class="glyphicon glyphicon-arrow-left"></i> Volver
    </a>
    <div class="table-wrapper">
        <div class="table-title">
            <div class="row">
                <div class="col-sm-6"><h2>Gestión de <b>Películas</b></h2></div>
                <div class="col-sm-6">
                    <a href="<%= request.getContextPath() %>/movies?accion=mostrarFormCrear" class="btn btn-success" style="float: right;">
                        <span class="glyphicon glyphicon-plus"></span> Añadir Nueva
                    </a>
                </div>
            </div>
            <div class="row" style="margin-bottom: 15px; margin-top: 15px;">
                <div class="col-sm-4">
                    <div class="input-group">
                        <span class="input-group-addon" style="background-color: #8B7355; color: white; border-color: #8B7355;">
                            <i class="glyphicon glyphicon-search"></i>
                        </span>
                        <input type="text" id="searchInput" class="form-control" placeholder="Buscar por ID, título, año...">
                    </div>
                </div>
            </div>
        </div>
        
        <% if ("true".equals(request.getParameter("exito"))) { %>
            <div class="alert alert-success">¡Operación realizada con éxito!</div>
        <% } %>

        <table class="table table-striped table-hover">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Póster</th>
                    <th>Título</th>
                    <th>Año</th>
                    <th>Puntuación</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody id="movieTableBody">
                <% 
                    List<Movie> movies = (List<Movie>) request.getAttribute("movies");
                    if (movies != null && !movies.isEmpty()) {
                        for (int i = 0; i < movies.size(); i++) {
                            Movie movie = movies.get(i);
                            String displayStyle = i < 50 ? "" : "style='display:none;'";
                %>
                <tr class="movie-row" data-title="<%= movie.getTitulo().toLowerCase() %>" <%= displayStyle %>>
                    <td><%= movie.getId() %></td>
                    <td>
                        <% if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) { %>
                            <img src="https://image.tmdb.org/t/p/w92<%= movie.getPosterPath() %>" class="movie-poster">
                        <% } else { %>
                            <span>-</span>
                        <% } %>
                    </td>
                    <td><%= movie.getTitulo() %></td>
                    <td><%= movie.getEstrenoYear() %></td>
                    <td><%= String.format("%.1f", movie.getPuntuacionApi()) %></td>
                    <td>
                        <a href="<%= request.getContextPath() %>/movies?accion=mostrarFormEditar&id=<%= movie.getId() %>" class="btn btn-warning btn-xs">Editar</a>
                        <form action="<%= request.getContextPath() %>/movies" method="POST" style="display:inline;">
                            <input type="hidden" name="accion" value="eliminar">
                            <input type="hidden" name="id" value="<%= movie.getId() %>">
                            <button type="submit" class="btn btn-danger btn-xs" onclick="return confirm('¿Eliminar?')">Eliminar</button>
                        </form>
                    </td>
                </tr>
                <%
                        }
                    }
                %>
            </tbody>
        </table>
        <div class="loading"><i class="glyphicon glyphicon-refresh glyphicon-spin"></i> Cargando...</div>
    </div>
</div>
<script>
$(document).ready(function() {
    var displayedCount = 50;
    
    $('.table-wrapper').on('scroll', function() {
        if ($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight - 50) {
            loadMore();
        }
    });
    
    function loadMore() {
        var hiddenRows = $('.movie-row:hidden');
        if (hiddenRows.length === 0) return;
        
        $('.loading').show();
        setTimeout(function() {
            hiddenRows.slice(0, 20).show();
            displayedCount += 20;
            $('.loading').hide();
        }, 300);
    }
    
    $('#searchInput').on('keyup', function() {
        var value = $(this).val().toLowerCase();
        $("#movieTableBody tr").filter(function() {
            $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1);
        });
    });
});
</script>
</body>
</html>
