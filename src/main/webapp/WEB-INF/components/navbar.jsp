<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<style>
    .navbar {
        background-color: #FAF8F3;
        padding: 15px 40px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        position: sticky;
        top: 0;
        z-index: 100;
        position: relative;
    }
    
    .navbar-left {
        display: flex;
        align-items: center;
        gap: 30px;
    }
    
    .navbar-logo {
        width: 60px;
        height: 60px;
        cursor: pointer;
        object-fit: cover;
        object-position: center;
        transform: scale(1.8);
    }
    
    .navbar-links {
        display: flex;
        gap: 25px;
    }
    
    .navbar-links a {
        text-decoration: none;
        color: #333;
        font-weight: 500;
        font-size: 18px;
        transition: color 0.3s;
    }
    
    .navbar-links a:hover {
        color: #666;
    }
    
    .navbar-center {
        position: absolute;
        left: 50%;
        transform: translateX(-50%);
        max-width: 500px;
        width: 500px;
    }
    
    .search-bar {
        width: 100%;
        padding: 10px 20px;
        border: 2px solid #E0E0E0;
        border-radius: 25px;
        font-family: 'Poppins', sans-serif;
        font-size: 14px;
        outline: none;
        transition: border-color 0.3s;
    }
    
    .search-bar:focus {
        border-color: #999;
    }
    
    .navbar-right {
        display: flex;
        align-items: center;
        gap: 15px;
    }
    
    .btn-login {
        padding: 10px 24px;
        background-color: #333;
        color: #FAF8F3;
        border: none;
        border-radius: 20px;
        font-family: 'Poppins', sans-serif;
        font-weight: 500;
        font-size: 16px;
        cursor: pointer;
        transition: background-color 0.3s;
    }
    
    .btn-login:hover {
        background-color: #555;
    }
    
    .btn-profile {
        width: 45px;
        height: 45px;
        border-radius: 50%;
        background-color: #E0E0E0;
        border: none;
        cursor: pointer;
        font-weight: 600;
        font-size: 18px;
        color: #333;
        transition: background-color 0.3s;
    }
    
    .btn-profile:hover {
        background-color: #D0D0D0;
    }
</style>

<%-- 
  Este es el único HTML que debe estar en este archivo de inclusión.
  (Sin <html>, <head> o <body>)
--%>
<nav class="navbar">
    <div class="navbar-left">
        <a href="${pageContext.request.contextPath}/">
            <img src="${pageContext.request.contextPath}/utils/export50.svg" alt="Fat Movies" class="navbar-logo">
        </a>
        <div class="navbar-links">
            <a href="${pageContext.request.contextPath}/movies-page">Películas</a>
            <a href="${pageContext.request.contextPath}/watchlist">Watchlist</a>
            <a href="${pageContext.request.contextPath}/resenas">Reseñas</a>
        </div>
    </div>
    
    <div class="navbar-center">
        <form action="${pageContext.request.contextPath}/search" method="get">
            <input type="text" class="search-bar" placeholder="Buscar películas..." name="q">
        </form>
    </div>
    
    <div class="navbar-right">
        <button class="btn-login" onclick="window.location.href='${pageContext.request.contextPath}/login'">Iniciar Sesión</button>
        <button class="btn-profile" onclick="window.location.href='${pageContext.request.contextPath}/profile'">P</button>
    </div>
</nav>