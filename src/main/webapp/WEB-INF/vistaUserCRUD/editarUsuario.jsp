<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar Usuario - FatMovies</title>
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
        
        .form-container { max-width: 600px; width: 100%; }
        
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
            text-align: center;
        }
        
        .card-title { font-size: 1.5rem; font-weight: 600; color: #1a1a1a; }
        .card-body { padding: 25px; }
        
        .avatar-section {
            text-align: center;
            margin-bottom: 25px;
        }
        
        .avatar-preview {
            width: 100px;
            height: 100px;
            border-radius: 50%;
            object-fit: cover;
            border: 3px solid #e0e0e0;
            margin-bottom: 8px;
        }
        
        .user-info {
            font-size: 0.85rem;
            color: #666;
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
        
        .alert ul { margin: 8px 0 0 20px; padding: 0; }
        
        .form-group { margin-bottom: 18px; }
        
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
        
        .form-control:focus { border-color: #999; }
        
        .password-section {
            background: #fff9e6;
            padding: 15px;
            border: 1px solid #ffe066;
            border-radius: 8px;
            margin-bottom: 18px;
        }
        
        .password-section label {
            color: #d9534f;
            margin-bottom: 8px;
        }
        
        .help-text {
            font-size: 0.8rem;
            color: #666;
            margin-top: 4px;
        }
        
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
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
            .form-row { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>
<div class="form-container">
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Editar Usuario</h2>
        </div>
        <div class="card-body">
            <div class="avatar-section">
                <c:choose>
                    <c:when test="${not empty user.profileImage}">
                        <img src="${pageContext.request.contextPath}/uploads/${user.profileImage}" class="avatar-preview" alt="Foto actual" onerror="this.src='${pageContext.request.contextPath}/utils/default_profile.png'">
                    </c:when>
                    <c:otherwise>
                        <img src="${pageContext.request.contextPath}/utils/default_profile.png" class="avatar-preview" alt="Sin foto">
                    </c:otherwise>
                </c:choose>
                <div class="user-info">ID: ${user.userId} - ${user.username}</div>
            </div>

            <c:if test="${not empty errors}">
                <div class="alert alert-danger">
                    <strong>Error de validación:</strong>
                    <ul>
                        <c:forEach var="error" items="${errors}">
                            <li>${error}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>

            <c:if test="${not empty appError}">
                <div class="alert alert-danger">
                    <strong>Error:</strong> ${appError}
                </div>
            </c:if>

            <c:url var="formAction" value="/users" />
            <form action="${formAction}" method="POST">
                <input type="hidden" name="accion" value="actualizar">
                <input type="hidden" name="id" value="${user.userId}">

                <div class="form-group">
                    <label>Nombre de Usuario:</label>
                    <input type="text" name="username" class="form-control" value="${user.username}" required>
                </div>
                
                <div class="form-group">
                    <label>Email:</label>
                    <input type="email" name="email" class="form-control" value="${user.email}" required>
                </div>
                
                <div class="password-section">
                    <label>Cambiar Contraseña (Opcional):</label>
                    <input type="password" name="password" class="form-control" 
                           placeholder="Dejar vacío para mantener la actual">
                    <div class="help-text">
                        Si escribes aquí, la contraseña será reemplazada
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label>Rol:</label>
                        <select name="role" class="form-control" required>
                            <option value="admin" ${user.role == 'admin' ? 'selected' : ''}>Admin</option>
                            <option value="user" ${user.role == 'user' ? 'selected' : ''}>User</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Fecha de Nacimiento:</label>
                        <input type="date" name="birthDate" class="form-control" value="${user.birthDate}" required>
                    </div>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Guardar Cambios</button>
                    <c:url var="cancelUrl" value="/users?accion=listar" />
                    <a href="${cancelUrl}" class="btn btn-secondary">Cancelar</a>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>
