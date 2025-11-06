<%@ page import="java.util.List" %>
<%@ page import="entity.Country" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>FatMovies - Gestión de Países</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<style>
    body {
        color: #333;
        background: #FAF8F3;
        font-family: 'Poppins', sans-serif;
        font-size: 14px;
    }
    .table-responsive {
        margin: 30px 0;
    }
    .table-wrapper {
        background: #fff;
        padding: 20px;
        margin: 30px 0;
        border-radius: 5px;
        box-shadow: 0 1px 1px rgba(0,0,0,.05);
    }
    .table-title {
        margin-bottom: 15px;
    }
    .table-title h2 {
        margin: 0;
        font-size: 24px;
    }
    .table-title .btn {
        float: right;
    }
    table.table tr th, table.table tr td {
        border-color: #e9e9e9;
        padding: 12px 15px;
        vertical-align: middle;
    }
    table.table tr th:first-child {
        width: 60px;
    }
    table.table tr th:last-child {
        width: 100px;
    }
    table.table-striped tbody tr:nth-of-type(odd) {
        background-color: #fcfcfc;
    }
    table.table-striped.table-hover tbody tr:hover {
        background: #f5f5f5;
    }
    table.table th i {
        font-size: 13px;
        margin: 0 5px;
        cursor: pointer;
    }
    table.table td:last-child i {
        opacity: 0.9;
        font-size: 22px;
        margin: 0 5px;
    }

    table.table td i {
        font-size: 19px;
    }

</style>
</head>
<body>
<div class="container">
    <a href="<%= request.getContextPath() %>/home" class="btn btn-primary" style="margin: 20px 0; background-color: #8B7355; border-color: #8B7355;"><i class="fa fa-home"></i> Volver al Inicio</a>
    <div class="table-responsive">
        <div class="table-wrapper">
            <div class="table-title">
                <div class="row">
                    <div class="col-sm-6">
                        <h2>Gestión de <b>Países</b></h2>
                    </div>
                    <div class="col-sm-6">
                        <a href="<%= request.getContextPath() %>/countries?accion=mostrarFormCrear" class="btn btn-success" style="float: right;">
                            <span class="glyphicon glyphicon-plus"></span> Añadir País
                        </a>
                    </div>
                </div>
            </div>
            <% if ("true".equals(request.getParameter("exito"))) { %>
            <div class="alert alert-success">¡Operación realizada con éxito!</div>
            <% } %>
            <table class="table table-striped table-hover">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Código ISO</th>
                        <th>Nombre</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        List<Country> countries = (List<Country>) request.getAttribute("countries");
                        if (countries != null && !countries.isEmpty()) {
                            for (Country country : countries) {
                    %>
                    <tr>
                        <td><%= country.getId() %></td>
                        <td><%= country.getIso_3166_1() != null ? country.getIso_3166_1() : "-" %></td>
                        <td><%= country.getEnglish_name() != null ? country.getEnglish_name() : "-" %></td>
                        <td>
                            <a href="<%= request.getContextPath() %>/countries?accion=mostrarFormEditar&id=<%= country.getId() %>" class="btn btn-warning btn-xs">Editar</a>
                            <form action="<%= request.getContextPath() %>/countries" method="POST" style="display:inline;">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="<%= country.getId() %>">
                                <button type="submit" class="btn btn-danger btn-xs" onclick="return confirm('¿Eliminar este país?')">Eliminar</button>
                            </form>
                        </td>
                    </tr>
                    <%      
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="4" class="text-center">No hay países registrados</td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>