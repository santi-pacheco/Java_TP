<%-- 
  Fichero: /WEB-INF/vistaUserCRUD/crearUsuario.jsp
  Propósito: Formulario para crear un nuevo usuario.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Crear Nuevo Usuario</title>
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
        }
        .form-control {
            border-radius: 3px;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="form-wrapper">
        <div class="form-title">
            <h2>Crear Nuevo Usuario</h2>
        </div>

        <%-- 
          1. LÓGICA DE ERRORES (Validación)
          Muestra los errores de Jakarta Validation si existen en el request.
        --%>
        <c:if test="${not empty errors}">
            <div class="alert alert-danger">
                <strong>¡Error de validación!</strong> Por favor, corrige los siguientes campos:
                <ul>
                    <c:forEach var="error" items="${errors}">
                        <li>${error}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>

        <%-- 
          2. LÓGICA DE ERRORES (Negocio)
          Muestra el error de negocio (ej. "DUPLICATE_ERROR") si existe.
        --%>
        <c:if test="${not empty appError}">
            <div class="alert alert-danger">
                <strong>¡Error!</strong> ${appError}
            </div>
        </c:if>

        <%-- 
          3. EL FORMULARIO
          - Envía un POST al servlet "/users".
          - 'user' es el objeto que el servlet reenvía si hay un error, 
             para rellenar los campos.
        --%>
        <c:url var="formAction" value="/users" />
        <form action="${formAction}" method="POST">
            
            <input type="hidden" name="accion" value="crear">

            <div class="form-group">
                <label>Nombre de Usuario (Username)</label>
                <input type="text" name="username" class="form-control" 
                       value="${not empty user ? user.username : ''}" required>
            </div>
            
            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" class="form-control" 
                       value="${not empty user ? user.email : ''}" required>
            </div>
            
            <div class="form-group">
                <label>Contraseña</label>
                <input type="password" name="password" class="form-control" required>
                <p class="help-block">Mín. 8 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 símbolo.</p>
            </div>
            
            <div class="form-group">
                <label>Rol</label>
                <select name="role" class="form-control" required>
                    <option value="" ${empty user.role ? 'selected' : ''} disabled>-- Selecciona un rol --</option>
                    <option value="admin" ${user.role == 'admin' ? 'selected' : ''}>Admin</option>
                    <option value="user" ${user.role == 'user' ? 'selected' : ''}>User</option>
                </select>
            </div>
            
            <div class="form-group">
                <label>Fecha de Nacimiento</label>
                <input type="date" name="birthDate" class="form-control" 
                       value="${not empty user ? user.birthDate : ''}" required>
            </div>
            
            <hr>
            
            <button type="submit" class="btn btn-success">Crear Usuario</button>
            
            <c:url var="cancelUrl" value="/users?accion=listar" />
            <a href="${cancelUrl}" class="btn btn-default">Cancelar</a>
            
        </form>
    </div>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>