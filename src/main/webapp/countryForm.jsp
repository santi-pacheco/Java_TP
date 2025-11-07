<%@ page import="entity.Country"%>
<%@ page import="java.util.Set" %> <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <title>Formulario de País</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style>
        body { background: #FAF8F3;
            font-family: 'Poppins', sans-serif;}
        .form-wrapper { background: #fff; padding: 30px; margin: 50px auto;
            max-width: 600px; border-radius: 5px;
            box-shadow: 0 1px 1px rgba(0,0,0,.05);}
        input[readonly] {
            background-color: #eee;
            cursor: not-allowed;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="form-wrapper">
    
        <% Country country = (Country) request.getAttribute("country"); %>
        <h2><%= (country != null && country.getId() != 0) ? "Editar" : "Crear" %> País</h2>
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
        <% 
            String appError = (String) request.getAttribute("appError");
            if (appError != null) {
        %>
            <div class="alert alert-danger">
                <strong>Error:</strong> <%= appError %>
            </div>
        <% } %>
        
        <form action="<%= request.getContextPath() %>/countries" method="POST">
         
             <input type="hidden" name="accion" value="<%= (country != null && country.getId() != 0) ? "actualizar" : "crear" %>">
            
            <% if (country != null) { %>
                <input type="hidden" name="id" value="<%= country.getId() %>">
            <% } %>
            
            <div class="form-group">
                <label>Código ISO:</label>
                <input type="text" name="iso" class="form-control" maxlength="2" required
                       value="<%= (country != null && country.getIso_3166_1() != null) ? country.getIso_3166_1() : "" %>"
                       <%= (country != null && country.getId() != 0) ? "readonly" : "" %>>
            </div>
            
            <div class="form-group">
                <label>Nombre:</label>
                <input type="text" name="name" class="form-control" 
                       value="<%= (country != null && country.getEnglish_name() != null) ? country.getEnglish_name() : "" %>" required>
            </div>

            <button type="submit" class="btn btn-primary">Guardar</button>
            <a href="<%= request.getContextPath() %>/countries" class="btn btn-default">Cancelar</a>
        </form>
    </div>
</div>
</body>
</html>