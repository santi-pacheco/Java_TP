<%@ page import="entity.Genre"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<title>Formulario de Género</title>
<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<style>
body { background: #FAF8F3; font-family: 'Poppins', sans-serif; }
.form-wrapper { background: #fff; padding: 30px; margin: 50px auto; max-width: 600px; border-radius: 5px; box-shadow: 0 1px 1px rgba(0,0,0,.05); }
</style>
</head>
<body>
<div class="container">
    <div class="form-wrapper">
        <h2><%= request.getAttribute("genre") != null ? "Editar" : "Crear" %> Género</h2>
        <% Genre genre = (Genre) request.getAttribute("genre"); %>
        <form action="<%= request.getContextPath() %>/genres" method="POST">
            <input type="hidden" name="accion" value="<%= genre != null ? "actualizar" : "crear" %>">
            <% if (genre != null) { %>
                <input type="hidden" name="id" value="<%= genre.getId() %>">
            <% } %>
            <div class="form-group">
                <label>Nombre:</label>
                <input type="text" name="name" class="form-control" value="<%= genre != null ? genre.getName() : "" %>" required>
            </div>
            <div class="form-group">
                <label>ID API:</label>
                <input type="number" name="id_api" class="form-control" value="<%= genre != null ? genre.getId_api() : "" %>" required>
            </div>
            <button type="submit" class="btn btn-primary">Guardar</button>
            <a href="<%= request.getContextPath() %>/genres" class="btn btn-default">Cancelar</a>
        </form>
    </div>
</div>
</body>
</html>
