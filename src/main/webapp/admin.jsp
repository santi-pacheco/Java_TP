<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>FatMovies - Sistema de Gestión</title>

    <%
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    %>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style>
        body { color: #333; background: #FAF8F3; font-family: 'Poppins', sans-serif; font-size: 14px; }
        .main-container { margin: 80px auto; max-width: 900px; text-align: center; padding: 40px; background: #fff; border-radius: 20px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
        .logo-container { width: 200px; height: 150px; margin: 0 auto 20px; overflow: hidden; display: flex; align-items: center; justify-content: center; transition: transform 0.3s; }
        .logo-container:hover { transform: scale(1.05); }
        .logo { width: 100%; height: 100%; object-fit: cover; transform: scale(1.6); cursor: pointer; }
        .option-btn { display: inline-flex; flex-direction: column; align-items: center; justify-content: center; width: 160px; height: 160px; margin: 15px; text-align: center; border-radius: 15px; background-color: #333; color: #FAF8F3; transition: all 0.3s; padding: 20px; text-decoration: none; font-size: 18px; font-weight: 500; box-shadow: 0 4px 8px rgba(0,0,0,0.15); line-height: 1.4; vertical-align: top; }
        .btn-api { background-color: #8B7355; }
        .btn-home { background-color: #666; border: 2px solid #666; }
        .option-btn:hover { background-color: #555; transform: translateY(-5px); color: #FAF8F3; text-decoration: none; box-shadow: 0 6px 16px rgba(0,0,0,0.2); }
        .btn-api:hover { background-color: #6e5b42; }
        .btn-home:hover { background-color: #888; border-color: #888; }
        h1 { color: #1a1a1a; margin-bottom: 40px; font-weight: 700; font-size: 2.5rem; }
        .btn-icon { font-size: 24px; margin-bottom: 10px; display: block; }
    </style>
</head>
<body>
    <div class="container main-container">
        <a href="${pageContext.request.contextPath}/home" title="Ir al Sitio Web">
            <div class="logo-container">
                <img src="${pageContext.request.contextPath}/utils/export50.svg" alt="Fat Movies Logo" class="logo">
            </div>
        </a>
        
        <h1>Sistema de Gestión</h1>
        
        <div>
            <a href="${pageContext.request.contextPath}/users" class="option-btn">Gestión de Usuarios</a>
            <a href="${pageContext.request.contextPath}/movies" class="option-btn">Gestión de Películas</a>
            <a href="${pageContext.request.contextPath}/genres" class="option-btn">Gestión de Géneros</a>
            <a href="${pageContext.request.contextPath}/countries" class="option-btn">Gestión de Países</a>
            <a href="${pageContext.request.contextPath}/reviews-admin" class="option-btn">Gestión de Reseñas</a>
            <a href="${pageContext.request.contextPath}/configuracion-reglas" class="option-btn">Configuración de Reglas</a>
            <a href="${pageContext.request.contextPath}/admin/data-load" class="option-btn btn-api">Carga de Datos (TMDB)</a>
            
            <a href="${pageContext.request.contextPath}/home" class="option-btn btn-home">
                <span class="glyphicon glyphicon-home btn-icon"></span>
                Ir al Sitio Web
            </a>
        </div>
    </div>
</body>
</html>