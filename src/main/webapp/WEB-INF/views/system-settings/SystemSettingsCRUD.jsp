<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Configuración de Reglas - FatMovies</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        
        body {
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(135deg, #FAF8F3 0%, #F0EDE6 100%);
            height: 100vh;
            padding: 15px;
            overflow: hidden;
        }
        
        .container { max-width: 1200px; margin: 0 auto; height: 100%; display: flex; flex-direction: column; }
        
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            flex-wrap: wrap;
            gap: 10px;
            flex-shrink: 0;
        }
        
        .header-left { display: flex; align-items: center; gap: 20px; }
        .page-title { font-size: 1.5rem; font-weight: 700; color: #1a1a1a; }
        
        .btn {
            padding: 10px 20px;
            border-radius: 10px;
            font-weight: 500;
            font-size: 0.95rem;
            text-decoration: none;
            border: none;
            cursor: pointer;
            transition: all 0.3s;
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }
        
        .btn-back { background: #666; color: white; }
        .btn-back:hover { background: #555; transform: translateY(-2px); text-decoration: none; color: white; }
        .btn-success { background: #28a745; color: white; }
        .btn-success:hover { background: #218838; transform: translateY(-2px); }
        
        .alert {
            padding: 10px 15px;
            border-radius: 8px;
            margin-bottom: 15px;
            font-weight: 500;
            flex-shrink: 0;
        }
        
        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .alert-danger {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            overflow: hidden;
            margin-bottom: 15px;
            flex-shrink: 0;
        }
        
        .card-header {
            padding: 15px 20px;
            border-bottom: 1px solid #f0f0f0;
        }
        
        .card-title {
            font-size: 1.2rem;
            font-weight: 600;
            color: #1a1a1a;
        }
        
        .card-body {
            padding: 20px;
            max-height: 300px;
            overflow-y: auto;
        }
        
        .form-group {
            margin-bottom: 12px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: 500;
            color: #333;
            font-size: 0.85rem;
        }
        
        .form-control {
            width: 100%;
            padding: 8px 12px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-family: 'Poppins', sans-serif;
            font-size: 0.85rem;
            outline: none;
            transition: border-color 0.3s;
        }
        
        .form-control:focus {
            border-color: #999;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
        }
        
        thead {
            background: #f8f9fa;
        }
        
        th {
            padding: 10px 15px;
            text-align: left;
            font-weight: 600;
            color: #333;
            font-size: 0.8rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        td {
            padding: 10px 15px;
            border-bottom: 1px solid #f0f0f0;
            color: #555;
            font-size: 0.85rem;
        }
        
        tbody tr {
            transition: background 0.2s;
        }
        
        tbody tr:hover {
            background: #fafafa;
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }
        
        @media (max-width: 768px) {
            .page-title { font-size: 1.5rem; }
            .card-header, .card-body { padding: 20px; }
            th, td { padding: 12px 15px; font-size: 0.85rem; }
        }
    </style>
</head>
<body>

<div class="container">
    <div class="header">
        <div class="header-left">
            <a href="<%= request.getContextPath() %>/admin" class="btn btn-back">&larr; Volver al Panel</a>
            <h1 class="page-title">Configuración de Reglas</h1>
        </div>
    </div>
    
    <c:if test="${param.exito == 'true'}">
        <div class="alert alert-success">
            &check; ¡Configuración guardada con éxito!
        </div>
    </c:if>
    
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Nueva Configuración</h2>
        </div>
        <div class="card-body">
            <c:if test="${not empty errors}">
                <div class="alert alert-danger">
                    <strong>Error(es) en el formulario:</strong>
                    <ul style="margin: 10px 0 0 20px;">
                        <c:forEach var="error" items="${errors}">
                            <li>${error}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>
            
            <form action="<%= request.getContextPath() %>/system-settings" method="POST">
                <div class="form-group">
                    <label>Kcals para Nivel 2:</label>
                    <input type="number" name="kcalsToLevel2" class="form-control" required min="1"
                           value="${(configForm != null) ? configForm.kcalsToLevel2 : ''}">
                </div>
                <div class="form-group">
                    <label>Kcals para Nivel 3:</label>
                    <input type="number" name="kcalsToLevel3" class="form-control" required min="1"
                           value="${(configForm != null) ? configForm.kcalsToLevel3 : ''}">
                </div>
                <div class="form-group">
                    <label>Kcals para Nivel 4:</label>
                    <input type="number" name="kcalsToLevel4" class="form-control" required min="1"
                           value="${(configForm != null) ? configForm.kcalsToLevel4 : ''}">
                </div>
                <div class="form-group">
                    <label>Límite Watchlist Normal:</label>
                    <input type="number" name="normalWatchlistLimit" class="form-control" required min="1"
                           value="${(configForm != null) ? configForm.normalWatchlistLimit : ''}">
                </div>
                <div class="form-group">
                    <label>Límite Watchlist Activo:</label>
                    <input type="number" name="activeWatchlistLimit" class="form-control" required min="1"
                           value="${(configForm != null) ? configForm.activeWatchlistLimit : ''}">
                </div>
                <button type="submit" class="btn btn-success">Guardar Configuración</button>
            </form>
        </div>
    </div>

    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Historial de Configuraciones</h2>
        </div>
        
        <c:choose>
            <c:when test="${empty configuraciones}">
                <div class="empty-state">
                    <p>No hay configuraciones registradas</p>
                </div>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Kcals Nivel 2</th>
                            <th>Kcals Nivel 3</th>
                            <th>Kcals Nivel 4</th>
                            <th>Límite Normal</th>
                            <th>Límite Activo</th>
                            <th>Fecha Vigencia</th>
                            <th>Admin ID</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="config" items="${configuraciones}">
                            <tr>
                                <td><strong>#${config.configId}</strong></td>
                                <td>${config.kcalsToLevel2}</td>
                                <td>${config.kcalsToLevel3}</td>
                                <td>${config.kcalsToLevel4}</td>
                                <td>${config.normalWatchlistLimit}</td>
                                <td>${config.activeWatchlistLimit}</td>
                                <td>${config.effectiveDate}</td>
                                <td>${config.adminUserId}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

</body>
</html>
