<%@ page import="entity.Country"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<title>Formulario de País</title>
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
        <h2><%= request.getAttribute("country") != null ? "Editar" : "Crear" %> País</h2>
        <% Country country = (Country) request.getAttribute("country"); %>
        <form action="<%= request.getContextPath() %>/countries" method="POST">
            <input type="hidden" name="accion" value="<%= country != null ? "actualizar" : "crear" %>">
            <% if (country != null) { %>
                <input type="hidden" name="id" value="<%= country.getId() %>">
            <% } %>
            <% if (country == null) { %>
            <div class="form-group">
                <label>Código ISO:</label>
                <input type="text" name="iso" class="form-control" maxlength="2" required>
            </div>
            <% } %>
            <div class="form-group">
                <label>Nombre:</label>
                <input type="text" name="name" class="form-control" value="<%= country != null ? country.getEnglish_name() : "" %>" required>
            </div>
            <button type="submit" class="btn btn-primary">Guardar</button>
            <a href="<%= request.getContextPath() %>/countries" class="btn btn-default">Cancelar</a>
        </form>
    </div>
</div>
</body>
</html>
