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
        .table > tbody > tr > td {
            vertical-align: middle !important;
        }
        .table > thead > tr > th:first-child,
        .table > tbody > tr > td:first-child {
            text-align: center;
        }
        .table-title h2 {
            margin: 0;
            font-size: 24px;
        }
        .table-title .btn {
            float: right;
        }
        /* Estilo para la miniatura del avatar en la tabla */
        .avatar-thumb {
            width: 35px;
            height: 35px;
            border-radius: 50%;
            object-fit: cover;
            border: 2px solid #eee;
        }
        .burger-avatar-thumb {
            border-radius: 50% !important;
            padding: 3px;
            background: linear-gradient(180deg, 
                #F5B041 0%, #F5B041 30%,   
                #58D68D 30%, #58D68D 40%,   
                #873600 40%, #873600 70%,   
                #F4D03F 70%, #F4D03F 100%   
            ) !important;
            box-shadow: 0 2px 5px rgba(0,0,0,0.2) !important;
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }
        .burger-avatar-thumb .avatar-thumb {
            border: 2px solid #FFF !important; 
        }
    </style>
</head>
<body>

<div class="container">
    <a href="${pageContext.request.contextPath}/admin.jsp" class="btn btn-primary" style="margin: 20px 0; background-color: #8B7355; border-color: #8B7355;"><i class="glyphicon glyphicon-arrow-left"></i> Volver</a>
    
    <div class="table-wrapper">
        <div class="table-title">
            <div class="table-title">
            <div class="row">
                <div class="col-sm-6">
                    <h2>Gestión de <b>Usuarios</b></h2>
                </div>
                <div class="col-sm-6">
                    <c:url var="addUrl" value="/users?accion=mostrarFormCrear" />
                    <a href="${addUrl}" class="btn btn-success" style="float: right;">
                        <span class="glyphicon glyphicon-plus"></span> Añadir Nuevo
                    </a>
                </div>
            </div>
            <div class="row" style="margin-bottom: 15px; margin-top: 15px;">
                <div class="col-sm-4">
                    <div class="input-group">
                        <span class="input-group-addon" style="background-color: #8B7355; color: white; border-color: #8B7355;">
                            <i class="glyphicon glyphicon-search"></i>
                        </span>
                        <input type="text" id="searchUserInput" class="form-control" placeholder="Buscar por ID, username, email...">
                    </div>
                </div>
            </div>
        </div>

        <c:if test="${param.exito == 'true'}">
            <div class="alert alert-success" style="margin-top: 15px;">
                ¡Operación realizada con éxito!
            </div>
        </c:if>

        <table class="table table-striped table-hover">
            <thead>
                <tr>
                    <th>Avatar</th> 
                    <th>ID</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Nivel</th> <th>Rol</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody id="userTableBody">
                <c:forEach var="user" items="${users}">
                    <tr>
                        <td>
                            <div class="${user.nivelUsuario >= 3 ? 'burger-avatar-thumb' : ''}">
                                <c:choose>
                                    <c:when test="${not empty user.profileImage}">
                                        <img src="${pageContext.request.contextPath}/uploads/${user.profileImage}" class="avatar-thumb" alt="Avatar">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/utils/default_profile.png" class="avatar-thumb" alt="Sin foto" style="opacity: 0.5;">
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </td>
                        
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${user.email}</td>
                        <td>
                            <span class="label" style="background-color: #8B7355; color: white;">Nvl ${user.nivelUsuario}</span>
                        </td>
                        <td>
                            <c:if test="${user.role == 'admin'}">
                                <span class="label label-danger">Admin</span>
                            </c:if>
                            <c:if test="${user.role == 'user'}">
                                <span class="label label-info">User</span>
                            </c:if>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${user.banned}">
                                    <span class="label label-danger" title="Baneado hasta: ${user.bannedUntil}">Baneado</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="label label-success">Activo</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:url var="editUrl" value="/users">
                                <c:param name="accion" value="mostrarFormEditar" />
                                <c:param name="id" value="${user.id}" />
                            </c:url>
                            <a href="${editUrl}" class="btn btn-warning btn-xs" title="Editar">
                                Modificar
                            </a>
                            
                            <c:url var="deleteActionUrl" value="/users" />
                            <form action="${deleteActionUrl}" method="POST" style="display:inline;">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="${user.id}">
                                <button type="submit" class="btn btn-danger btn-xs" title="Eliminar"
                                        onclick="return confirm('¿Está seguro de que desea eliminar a ${user.username}? Esta acción borrará también sus reseñas y foto.')">
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
<script>
		$(document).ready(function() {
		    $("#searchUserInput").on("keyup", function() {
		        var value = $(this).val().toLowerCase();
		        $("#userTableBody tr").filter(function() {
		            $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1);
		        });
		    });
		});
</script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>