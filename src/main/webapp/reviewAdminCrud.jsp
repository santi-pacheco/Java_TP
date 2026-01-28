<%@ page import="java.util.List"%>
<%@ page import="entity.Review"%>
<%@ page import="entity.ModerationStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<title>Gestión de Reseñas</title>
<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<style>
body { background: #FAF8F3; font-family: 'Poppins', sans-serif; }
.table-wrapper { background: #fff; padding: 20px; margin: 30px 0; border-radius: 5px; box-shadow: 0 1px 1px rgba(0,0,0,.05); max-height: 600px; overflow-y: auto; }
.table-title h2 { margin: 0 0 15px 0; font-size: 24px; }
table.table tr th, table.table tr td { border-color: #e9e9e9; padding: 12px; vertical-align: middle; }
.badge-approved { background-color: #5cb85c; color: white; }
.badge-rejected { background-color: #d9534f; color: white; }
.badge-pending { background-color: #f0ad4e; color: white; }
.filter-section { background: #f8f9fa; padding: 15px; margin-bottom: 20px; border-radius: 5px; }
.filter-section .btn { margin: 5px; }
</style>
</head>
<body>
<div class="container">
    <a href="<%= request.getContextPath() %>/home" class="btn btn-primary" style="margin: 20px 0; background-color: #8B7355; border-color: #8B7355;">Volver</a>
    
    <div class="table-wrapper">
        <div class="table-title">
            <h2>Gestión de <b>Reseñas</b></h2>
        </div>
        
        <!-- Filtros por estado de moderación -->
        <div class="filter-section">
            <strong>Filtrar por estado:</strong>
            <a href="<%= request.getContextPath() %>/reviews-admin?accion=listar" class="btn btn-default btn-sm">Todas</a>
            <a href="<%= request.getContextPath() %>/reviews-admin?accion=listar&status=PENDING_MODERATION" class="btn btn-warning btn-sm">⏳ Pendientes</a>
            <a href="<%= request.getContextPath() %>/reviews-admin?accion=listar&status=APPROVED" class="btn btn-success btn-sm">✅ Aprobadas</a>
            <a href="<%= request.getContextPath() %>/reviews-admin?accion=listar&status=REJECTED" class="btn btn-danger btn-sm">❌ Rechazadas</a>
        </div>
        
        <% if ("true".equals(request.getParameter("exito"))) { %>
        <div class="alert alert-success">¡Operación realizada con éxito!</div>
        <% } %>
        
        <table class="table table-striped table-hover">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Usuario</th>
                    <th>Película</th>
                    <th>Rating</th>
                    <th>Fecha</th>
                    <th>Estado de Moderación</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <% 
                    List<Review> reviews = (List<Review>) request.getAttribute("reviews");
                    if (reviews != null && !reviews.isEmpty()) {
                        for (Review review : reviews) {
                            String statusBadge = "";
                            String statusClass = "";
                            
                            if (review.getModerationStatus() == ModerationStatus.APPROVED) {
                                statusBadge = "✅ Aprobada";
                                statusClass = "badge-approved";
                            } else if (review.getModerationStatus() == ModerationStatus.REJECTED) {
                                statusBadge = "❌ Rechazada por Spoiler";
                                statusClass = "badge-rejected";
                            } else {
                                statusBadge = "⏳ Pendiente";
                                statusClass = "badge-pending";
                            }
                %>
                <tr>
                    <td><%= review.getId() %></td>
                    <td><%= review.getUsername() != null ? review.getUsername() : "Usuario " + review.getId_user() %></td>
                    <td><%= review.getMovieTitle() != null ? review.getMovieTitle() : "Película #" + review.getId_movie() %></td>
                    <td><%= String.format("%.1f", review.getRating()) %> / 5.0</td>
                    <td><%= review.getCreated_at() %></td>
                    <td><span class="badge <%= statusClass %>"><%= statusBadge %></span></td>
                    <td>
                        <a href="<%= request.getContextPath() %>/reviews-admin?accion=detalle&id=<%= review.getId() %>" class="btn btn-info btn-xs">Ver Detalle</a>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="7" class="text-center">No hay reseñas registradas</td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
