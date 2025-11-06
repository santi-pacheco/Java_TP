<%-- 
  Fichero: /WEB-INF/vistaUserCRUD/userCrud.jsp
  Propósito: Muestra la lista de usuarios y permite acciones CRUD.
--%>

<%-- 1. Directivas JSP: Importamos JSTL (core) para bucles y URLs --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Usuarios</title>
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
        .table-title .btn {
            float: right;
        }
    </style>
</head>
<body>

<div class="container">
    <a href="<%= request.getContextPath() %>/home" class="btn btn-primary" style="margin: 20px 0; background-color: #8B7355; border-color: #8B7355;"><i class="glyphicon glyphicon-home"></i> Volver al Inicio</a>
    <div class="table-wrapper">
        <div class="table-title">
            <div class="row">
                <div class="col-sm-6">
                    <h2>Gestión de <b>Usuarios</b></h2>
                </div>
                <div class="col-sm-6">
                    <%-- 
                      2. BOTÓN AÑADIR (Tu Requerimiento)
                      Llama al UserServlet con accion=mostrarFormCrear.
                      Usamos <c:url> para construir la URL correctamente.
                    --%>
                    <c:url var="addUrl" value="/users?accion=mostrarFormCrear" />
                    <a href="${addUrl}" class="btn btn-success">
                        <span class="glyphicon glyphicon-plus"></span> Añadir Nuevo Usuario
                    </a>
                </div>
            </div>
        </div>

        <%-- 
          3. MENSAJE DE ÉXITO (Opcional)
          Lee el parámetro 'exito=true' que pusimos en el sendRedirect
        --%>
        <c:if test="${param.exito == 'true'}">
            <div class="alert alert-success" style="margin-top: 15px;">
                ¡Operación realizada con éxito!
            </div>
        </c:if>

        <table class="table table-striped table-hover">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Rol</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <%-- 
                  4. LISTA DE USUARIOS (Tu Requerimiento)
                  Usamos un bucle <c:forEach> para iterar sobre la lista "users"
                  que nuestro UserServlet puso en el request.
                --%>
                <c:forEach var="user" items="${users}">
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${user.email}</td>
                        <td>${user.role}</td>
                        <td>
                            <%-- 
                              5. ACCIONES (Modificar y Borrar)
                              
                              MODIFICAR (y "Mostrar Info"):
                              Llama al servlet con accion=mostrarFormEditar y el ID.
                              Esta es la acción que "muestra más info" para editarla.
                            --%>
                            <c:url var="editUrl" value="/users">
                                <c:param name="accion" value="mostrarFormEditar" />
                                <c:param name="id" value="${user.id}" />
                            </c:url>
                            <a href="${editUrl}" class="btn btn-warning btn-xs">Modificar</a>
                            
                            <%-- 
                              BORRAR:
                              Esto es un mini-formulario que envía un POST.
                              Es más seguro que un enlace GET para borrar.
                            --%>
                            <c:url var="deleteActionUrl" value="/users" />
                            <form action="${deleteActionUrl}" method="POST" style="display:inline;">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="${user.id}">
                                <button type="submit" class="btn btn-danger btn-xs" 
                                        onclick="return confirm('¿Está seguro de que desea eliminar a ${user.username}?')">
                                    Eliminar
                                </button>
                            </form>
                        </td>
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