<%@ page import="entity.Country"%>
<%@ page import="java.util.Set" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= request.getAttribute("country") != null ? "Editar" : "Crear" %> País - FatMovies</title>
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
    max-width: 500px;
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

.form-control[readonly] {
    background-color: #f5f5f5;
    cursor: not-allowed;
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
}
</style>
</head>
<body>
<div class="form-container">
    <div class="card">
        <div class="card-header">
            <% Country country = (Country) request.getAttribute("country"); %>
            <h2 class="card-title"><%= (country != null && country.getCountryId() != 0) ? "Editar" : "Crear" %> País</h2>
        </div>
        <div class="card-body">
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
                <input type="hidden" name="accion" value="<%= (country != null && country.getCountryId() != 0) ? "actualizar" : "crear" %>">
                <% if (country != null) { %>
                    <input type="hidden" name="id" value="<%= country.getCountryId() %>">
                <% } %>
                
                <div class="form-group">
                    <label>Código ISO (2 caracteres):</label>
                    <input type="text" name="iso" class="form-control" maxlength="2" required
                           value="<%= (country != null && country.getIsoCode() != null) ? country.getIsoCode() : "" %>"
                           <%= (country != null && country.getCountryId() != 0) ? "readonly" : "" %>>
                </div>
                
                <div class="form-group">
                    <label>Nombre del País:</label>
                    <input type="text" name="name" class="form-control" 
                           value="<%= (country != null && country.getName() != null) ? country.getName() : "" %>" required>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Guardar</button>
                    <a href="<%= request.getContextPath() %>/countries" class="btn btn-secondary">Cancelar</a>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>
