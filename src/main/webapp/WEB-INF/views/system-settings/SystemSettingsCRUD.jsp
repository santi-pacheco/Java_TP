<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Configuración de Reglas</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style>
        body { background: #FAF8F3; font-family: 'Poppins', sans-serif; }
        .table-wrapper {
            background: #fff;
            padding: 20px;
            margin: 30px 0;
            border-radius: 5px;
            box-shadow: 0 1px 1px rgba(0,0,0,.05);
        }
        .table-title h2 {
            margin: 0;
            font-size: 24px;
        }
        .form-wrapper {
            background: #fff;
            padding: 20px;
            margin: 30px 0;
            border-radius: 5px;
            box-shadow: 0 1px 1px rgba(0,0,0,.05);
        }
    </style>
</head>
<body>

<div class="container">
    <a href="<%= request.getContextPath() %>/home" class="btn btn-primary" style="margin: 20px 0; background-color: #8B7355; border-color: #8B7355;"><i class="glyphicon glyphicon-home"></i> Volver al Inicio</a>
    
    <div class="form-wrapper">
        <h3>Agregar Nueva Configuración</h3>
        
        <c:if test="${not empty errors}">
            <div class="alert alert-danger">
                <strong>Error(es) en el formulario:</strong>
                <ul>
                    <c:forEach var="error" items="${errors}">
                        <li>${error}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        
        <form action="<%= request.getContextPath() %>/system-settings" method="POST">
            <div class="form-group">
                <label>Kcals Nivel 2:</label>
                <input type="number" name="kcalsToLevel2" class="form-control" required min="1"
                       value="${(configForm != null) ? configForm.kcalsToLevel2 : ''}">
            </div>
            <div class="form-group">
                <label>Kcals Nivel 3:</label>
                <input type="number" name="kcalsToLevel3" class="form-control" required min="1"
                       value="${(configForm != null) ? configForm.kcalsToLevel3 : ''}">
            </div>
            <div class="form-group">
                <label>Kcals Nivel 4:</label>
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

    <div class="table-wrapper">
        <div class="table-title">
            <h2>Historial de <b>Configuraciones</b></h2>
        </div>

        <c:if test="${param.exito == 'true'}">
            <div class="alert alert-success" style="margin-top: 15px;">
                ¡Configuración guardada con éxito!
            </div>
        </c:if>

        <table class="table table-striped table-hover">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Kcals Nivel 2</th>
                    <th>Kcals Nivel 3</th>
                    <th>Kcals Nivel 4</th>
                    <th>Límite Watchlist Normal</th>
                    <th>Límite Watchlist Activo</th>
                    <th>Fecha Vigencia</th>
                    <th>Admin User ID</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="config" items="${configuraciones}">
                    <tr>
                        <td>${config.configId}</td>
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
    </div>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>
