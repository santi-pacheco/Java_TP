<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar Usuario</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style>
        body { background: #f5f5f5; }
        .form-wrapper {
            max-width: 600px;
            margin: 50px auto;
            background: #fff;
            padding: 30px;
            border-radius: 5px;
            box-shadow: 0 1px 1px rgba(0,0,0,.05);
        }
        .form-title h2 {
            margin-top: 0;
            margin-bottom: 25px;
            text-align: center;
        }
        .avatar-preview {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            object-fit: cover;
            border: 4px solid #f0f0f0;
            margin-bottom: 10px;
        }
        .password-section {
            background: #fafafa;
            padding: 15px;
            border: 1px dashed #ddd;
            border-radius: 4px;
            margin-bottom: 15px;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="form-wrapper">
        <div class="form-title">
            <h2>Editar Usuario</h2>
        </div>

        <%-- LÓGICA DE ERRORES (Validación) --%>
        <c:if test="${not empty errors}">
            <div class="alert alert-danger">
                <strong>¡Error de validación!</strong>
                <ul>
                    <c:forEach var="error" items="${errors}">
                        <li>${error}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>

        <%-- LÓGICA DE ERRORES (Negocio) --%>
        <c:if test="${not empty appError}">
            <div class="alert alert-danger">
                <strong>¡Error!</strong> ${appError}
            </div>
        </c:if>

        <c:url var="formAction" value="/users" />
        <form action="${formAction}" method="POST">
            
            <input type="hidden" name="accion" value="actualizar">
            <input type="hidden" name="id" value="${user.id}">

            <div class="text-center" style="margin-bottom: 25px;">
                <c:choose>
                    <c:when test="${not empty user.profileImage}">
                        <img src="${pageContext.request.contextPath}/uploads/${user.profileImage}" class="avatar-preview" alt="Foto actual">
                    </c:when>
                    <c:otherwise>
                        <img src="${pageContext.request.contextPath}/utils/default_user.png" class="avatar-preview" alt="Sin foto" style="opacity: 0.5;">
                    </c:otherwise>
                </c:choose>
                <p class="text-muted small">ID: ${user.id} - ${user.username}</p>
            </div>

            <div class="form-group">
                <label>Nombre de Usuario (Username)</label>
                <input type="text" name="username" class="form-control" value="${user.username}" required>
            </div>
            
            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" class="form-control" value="${user.email}" required>
            </div>
            
          <div class="password-section">
                <label style="color: #d9534f;">Cambiar Contraseña (Opcional)</label>
                <input type="password" name="password" class="form-control" 
                       placeholder="Dejar vacío para mantener la actual">
                <small class="text-muted" style="display: block; margin-bottom: 10px;">
                    Si escribes aquí, la contraseña del usuario será reemplazada por esta nueva.
                </small>
            </div>
            
            <div class="password-section" style="border-color: #d9534f; background: #fff4f4;">
                <label style="color: #d9534f;"><span class="glyphicon glyphicon-ban-circle"></span> Suspensión de Cuenta (Regla de 7 Días)</label>
                
                <c:choose>
                    <c:when test="${user.banned}">
                        <p style="color: #a94442; margin-bottom: 10px; margin-top: 10px;">
                            <strong>Estado actual:</strong> 🚫 Baneado hasta el ${user.bannedUntil}
                        </p>
                        <div class="checkbox">
                            <label style="color: #3c763d; font-weight: bold; cursor: pointer;">
                                <input type="checkbox" name="unbanUser" value="true"> 
                                Levantar castigo (Desbanear inmediatamente)
                            </label>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p style="color: #3c763d; margin-bottom: 10px; margin-top: 10px;">
                            <strong>Estado actual:</strong> ✅ Activo
                        </p>
                        <div class="checkbox">
                            <label style="color: #a94442; font-weight: bold; cursor: pointer;">
                                <input type="checkbox" name="banUser7Days" value="true"> 
                                Aplicar castigo (Banear por 7 días desde hoy)
                            </label>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="form-group">
                <label>Fecha de Nacimiento</label>
                <input type="date" name="birthDate" class="form-control" value="${user.birthDate}" required>
            </div>
            <p>Rol actual: <strong>${user.role}</strong></p>              
            <hr>
            
            <button type="submit" class="btn btn-primary btn-block">Guardar Cambios</button>
            
            <c:url var="cancelUrl" value="/users?accion=listar" />
            <a href="${cancelUrl}" class="btn btn-default btn-block">Cancelar</a>
            
        </form>
    </div>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>