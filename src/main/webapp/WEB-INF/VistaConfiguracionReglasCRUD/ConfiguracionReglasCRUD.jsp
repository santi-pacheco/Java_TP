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
        <form action="<%= request.getContextPath() %>/configuracion-reglas" method="POST">
            <div class="form-group">
                <label>Umbral Reseñas Activo:</label>
                <input type="number" name="umbralResenasActivo" class="form-control" required min="0">
            </div>
            <div class="form-group">
                <label>Límite Watchlist Normal:</label>
                <input type="number" name="limiteWatchlistNormal" class="form-control" required min="0">
            </div>
            <div class="form-group">
                <label>Límite Watchlist Activo:</label>
                <input type="number" name="limiteWatchlistActivo" class="form-control" required min="0">
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
                    <th>Umbral Reseñas Activo</th>
                    <th>Límite Watchlist Normal</th>
                    <th>Límite Watchlist Activo</th>
                    <th>Fecha Vigencia</th>
                    <th>Usuario Admin ID</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="config" items="${configuraciones}">
                    <tr>
                        <td>${config.configID}</td>
                        <td>${config.umbralResenasActivo}</td>
                        <td>${config.limiteWatchlistNormal}</td>
                        <td>${config.limiteWatchlistActivo}</td>
                        <td>${config.fechaVigencia}</td>
                        <td>${config.usuarioAdminID}</td>
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