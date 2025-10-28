<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="entity.Genre" %>
<%@ page import="controller.GenreController" %>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>FatMovies - Gestión de Géneros CRUD</title>
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto|Varela+Round">
<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<style>
    body {
        color: #566787;
		background: #f5f5f5;
		font-family: 'Varela Round', sans-serif;
		font-size: 13px;
	}
	.table-responsive {
        margin: 30px 0;
    }
	.table-wrapper {
		min-width: 1000px;
        background: #fff;
        padding: 20px 25px;
		border-radius: 3px;
        box-shadow: 0 1px 1px rgba(0,0,0,.05);
    }
	.table-title {        
		padding-bottom: 15px;
		background: #435d7d;
		color: #fff;
		padding: 16px 30px;
		margin: -20px -25px 10px;
		border-radius: 3px 3px 0 0;
    }
    .table-title h2 {
		margin: 5px 0 0;
		font-size: 24px;
	}
	.table-title .btn-group {
		float: right;
	}
	.table-title .btn {
		color: #fff;
		float: right;
		font-size: 13px;
		border: none;
		min-width: 50px;
		border-radius: 2px;
		border: none;
		outline: none !important;
		margin-left: 10px;
	}
	.table-title .btn i {
		float: left;
		font-size: 21px;
		margin-right: 5px;
	}
	.table-title .btn span {
		float: left;
		margin-top: 2px;
	}
    table.table tr th, table.table tr td {
        border-color: #e9e9e9;
		padding: 12px 15px;
		vertical-align: middle;
    }
	table.table tr th:first-child {
		width: 60px;
	}
	table.table tr th:last-child {
		width: 100px;
	}
    table.table-striped tbody tr:nth-of-type(odd) {
    	background-color: #fcfcfc;
	}
	table.table-striped.table-hover tbody tr:hover {
		background: #f5f5f5;
	}
    table.table th i {
        font-size: 13px;
        margin: 0 5px;
        cursor: pointer;
    }	
    table.table td:last-child i {
		opacity: 0.9;
		font-size: 22px;
        margin: 0 5px;
    }
	table.table td a {
		font-weight: bold;
		color: #566787;
		display: inline-block;
		text-decoration: none;
		outline: none !important;
	}
	table.table td a:hover {
		color: #2196F3;
	}
	table.table td a.edit {
        color: #FFC107;
    }
    table.table td a.delete {
        color: #F44336;
    }
    table.table td i {
        font-size: 19px;
    }
    .custom-checkbox {
		position: relative;
	}
	.custom-checkbox input[type="checkbox"] {    
		opacity: 0;
		position: absolute;
		margin: 5px 0 0 3px;
		z-index: 9;
	}
	.custom-checkbox label:before{
		width: 18px;
		height: 18px;
	}
	.custom-checkbox label:before {
		content: '';
		margin-right: 10px;
		display: inline-block;
		vertical-align: text-top;
		background: white;
		border: 1px solid #bbb;
		border-radius: 2px;
		box-sizing: border-box;
		z-index: 2;
	}
	.custom-checkbox input[type="checkbox"]:checked + label:after {
		content: '';
		position: absolute;
		left: 6px;
		top: 3px;
		width: 6px;
		height: 11px;
		border: solid #000;
		border-width: 0 3px 3px 0;
		transform: inherit;
		z-index: 3;
		transform: rotateZ(45deg);
	}
	.custom-checkbox input[type="checkbox"]:checked + label:before {
		border-color: #03A9F4;
		background: #03A9F4;
	}
	.custom-checkbox input[type="checkbox"]:checked + label:after {
		border-color: #fff;
	}
    .hint-text {
        float: left;
        margin-top: 10px;
        font-size: 13px;
    }
</style>
<script>
$(document).ready(function(){
	// Activate tooltip
	$('[data-toggle="tooltip"]').tooltip();
	
	// Select/Deselect checkboxes
	var checkbox = $('table tbody input[type="checkbox"]');
	$("#selectAll").click(function(){
		if(this.checked){
			checkbox.each(function(){
				this.checked = true;                        
			});
		} else{
			checkbox.each(function(){
				this.checked = false;                        
			});
		} 
	});
	checkbox.click(function(){
		if(!this.checked){
			$("#selectAll").prop("checked", false);
		}
	});
});
</script>
</head>
<body>
<%
    // Obtener los géneros usando el controlador
    String apiKey = "a47ba0b127499b0e1b28ceb0a183ec57";
    List<Genre> genres = null;
    String errorMessage = null;
    boolean dbConnectionError = false;
    
    try {
        GenreController genreController = new GenreController();
        genres = genreController.getGenres();
    } catch (RuntimeException e) {
        if (e.getMessage().contains("database connection") || e.getCause() instanceof java.sql.SQLException) {
            dbConnectionError = true;
            errorMessage = "Error de conexión a la base de datos. Verifique que MySQL esté ejecutándose y que las credenciales sean correctas.";
        } else {
            errorMessage = "Error al obtener los géneros: " + e.getMessage();
        }
        e.printStackTrace();
    } catch (Exception e) {
        errorMessage = "Error inesperado: " + e.getMessage();
        e.printStackTrace();
    }
%>

<div class="container">
    <div class="table-responsive">
        <div class="table-wrapper">
            <div class="table-title">
                <div class="row">
                    <div class="col-xs-6">
                        <h2>FatMovies - <b>Gestión de Géneros (CRUD)</b></h2>
                    </div>
                    <div class="col-xs-6">
                        <a href="genreCrud.html" class="btn btn-success">
                            <i class="material-icons">&#xE89C;</i> <span>Página Principal</span>
                        </a>
                    </div>
                </div>
            </div>
            
            <% if (errorMessage != null) { %>
                <div class="alert alert-danger">
                    <strong>Error:</strong> <%= errorMessage %>
                </div>
            <% } else { %>
                <div class="alert alert-success">
                    <strong>Éxito:</strong> Se han cargado <%= genres != null ? genres.size() : 0 %> géneros desde The Movie Database (TMDB).
                </div>
                
                <table class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th>
                                <span class="custom-checkbox">
                                    <input type="checkbox" id="selectAll">
                                    <label for="selectAll"></label>
                                </span>
                            </th>
                            <th>ID</th>
                            <th>Nombre del Género</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (genres != null && !genres.isEmpty()) { %>
                            <% for (Genre genre : genres) { %>
                                <tr>
                                    <td>
                                        <span class="custom-checkbox">
                                            <input type="checkbox" id="checkbox<%= genre.getId() %>" name="options[]" value="<%= genre.getId() %>">
                                            <label for="checkbox<%= genre.getId() %>"></label>
                                        </span>
                                    </td>
                                    <td><%= genre.getId() %></td>
                                    <td><%= genre.getName() %></td>
                                    <td>
                                        <a href="#editGenreModal" class="edit" data-toggle="modal">
                                            <i class="material-icons" data-toggle="tooltip" title="Editar">&#xE254;</i>
                                        </a>
                                        <a href="#deleteGenreModal" class="delete" data-toggle="modal">
                                            <i class="material-icons" data-toggle="tooltip" title="Eliminar">&#xE872;</i>
                                        </a>
                                    </td>
                                </tr>
                            <% } %>
                        <% } else { %>
                            <tr>
                                <td colspan="4" class="text-center">No se encontraron géneros.</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
                
                <div class="clearfix">
                    <div class="hint-text">
                        Mostrando <b><%= genres != null ? genres.size() : 0 %></b> de <b><%= genres != null ? genres.size() : 0 %></b> entradas
                    </div>
                </div>
            <% } %>
        </div>
    </div>        
</div>

</body>
</html>