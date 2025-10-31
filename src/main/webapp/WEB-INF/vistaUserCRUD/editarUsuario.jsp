<%-- 
  Fichero: /WEB-INF/vistaUserCRUD/editarUsuario.jsp
  Propósito: Formulario para editar un usuario existente.
--%>
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
        }
    </style>
</head>
<body>

<div class="container">
    <div class="form-wrapper">
        <div class="form-title">
            <%-- El ${user.username} viene del servlet --%>
            <h2>Editar Usuario: <b>${user.username}</b></h2>
        </div>

        <%-- 
          1. LÓGICA DE ERRORES (Validación)
          Muestra los errores de Jakarta Validation si el POST falla.
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
          Muestra el error de negocio (ej. "DUPLICATE_ERROR") si el POST falla.
        --%>
        <c:if test="${not empty appError}">
            <div class="alert alert-danger">
                <strong>¡Error!</strong> ${appError}
            </div>
        </c:if>

        <%-- 
          3. EL FORMULARIO
          - Envía un POST al servlet "/users".
          - Los campos se rellenan con los valores del objeto 'user'.
        --%>
        <c:url var="formAction" value="/users" />
        <form action="${formAction}" method="POST">
            
            <input type="hidden" name="accion" value="actualizar">
            
            <input type="hidden" name="id" value="${user.id}">

            <div class="form-group">
                <label>Nombre de Usuario (Username)</label>
                <input type="text" name="username" class="form-control" 
                       value="${user.username}" required>
            </div>
            
            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" class="form-control" 
                       value="${user.email}" required>
            </div>
            
            <%-- ¡NO HAY CAMPO DE CONTRASEÑA! --%>
            
            <div class="form-group">
                <label>Rol</label>
                <select name="role" class="form-control" required>
                    <option value="admin" ${user.role == 'admin' ? 'selected' : ''}>Admin</option>
                    <option value="user" ${user.role == 'user' ? 'selected' : ''}>User</option>
                </select>
            </div>
            
            <div class="form-group">
                <label>Fecha de Nacimiento</label>
                <input type="date" name="birthDate" class="form-control" 
                       value="${user.birthDate}" required>
            </div>
            
            <hr>
            
            <button type="submit" class="btn btn-primary">Guardar Cambios</button>
            
            <%-- Botón para volver a la lista principal --%>
            <c:url var="cancelUrl" value="/users?accion=listar" />
            <a href="${cancelUrl}" class="btn btn-default">Cancelar</a>
            
        </form>
    </div>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>