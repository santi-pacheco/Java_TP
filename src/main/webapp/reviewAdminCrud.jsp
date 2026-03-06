<%@ page import="java.util.List"%>
<%@ page import="entity.Review"%>
<%@ page import="entity.ModerationStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Gestión de Reseñas - FatMovies</title>
<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
<style>
* { margin: 0; padding: 0; box-sizing: border-box; }

body {
    font-family: 'Poppins', sans-serif;
    background: linear-gradient(135deg, #FAF8F3 0%, #F0EDE6 100%);
    height: 100vh;
    padding: 15px;
    overflow: hidden;
}

.container { max-width: 1400px; margin: 0 auto; height: 100%; display: flex; flex-direction: column; }

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
    flex-wrap: wrap;
    gap: 10px;
    flex-shrink: 0;
}

.header-left { display: flex; align-items: center; gap: 20px; }
.page-title { font-size: 1.5rem; font-weight: 700; color: #1a1a1a; }

.btn {
    padding: 10px 20px;
    border-radius: 10px;
    font-weight: 500;
    font-size: 0.95rem;
    text-decoration: none;
    border: none;
    cursor: pointer;
    transition: all 0.3s;
    display: inline-flex;
    align-items: center;
    gap: 8px;
}

.btn-back { background: #666; color: white; }
.btn-back:hover { background: #555; transform: translateY(-2px); text-decoration: none; color: white; }
.btn-default { background: #e0e0e0; color: #333; }
.btn-default:hover { background: #d0d0d0; }
.btn-warning { background: #ffc107; color: #333; }
.btn-warning:hover { background: #e0a800; }
.btn-success { background: #28a745; color: white; }
.btn-success:hover { background: #218838; }
.btn-danger { background: #dc3545; color: white; }
.btn-danger:hover { background: #c82333; }
.btn-info { background: #17a2b8; color: white; }
.btn-info:hover { background: #138496; }
.btn-sm { padding: 6px 12px; font-size: 0.85rem; }

.alert {
    padding: 10px 15px;
    border-radius: 8px;
    margin-bottom: 15px;
    font-weight: 500;
    background: #d4edda;
    color: #155724;
    border: 1px solid #c3e6cb;
    flex-shrink: 0;
}

.card {
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
    overflow: hidden;
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;
}

.card-header {
    padding: 15px 20px;
    border-bottom: 1px solid #f0f0f0;
    flex-shrink: 0;
}

.card-title {
    font-size: 1.2rem;
    font-weight: 600;
    color: #1a1a1a;
}

.filter-section {
    background: #f8f9fa;
    padding: 12px;
    border-radius: 10px;
    margin-bottom: 12px;
    flex-shrink: 0;
}

.filter-section strong {
    display: block;
    margin-bottom: 8px;
    color: #333;
    font-size: 0.85rem;
}

.filter-buttons {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
}

.table-container {
    overflow-y: auto;
    flex: 1;
    min-height: 0;
}

table { width: 100%; border-collapse: collapse; }
thead { background: #f8f9fa; position: sticky; top: 0; z-index: 10; }

th {
    padding: 10px 15px;
    text-align: left;
    font-weight: 600;
    color: #333;
    font-size: 0.8rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

td {
    padding: 10px 15px;
    border-bottom: 1px solid #f0f0f0;
    color: #555;
    font-size: 0.85rem;
}

tbody tr { transition: background 0.2s; }
tbody tr:hover { background: #fafafa; }

.badge {
    padding: 3px 8px;
    border-radius: 12px;
    font-size: 0.7rem;
    font-weight: 600;
    display: inline-block;
}

.badge-approved {
    background: #28a745;
    color: white;
}

.badge-rejected {
    background: #dc3545;
    color: white;
}

.badge-pending {
    background: #ffc107;
    color: #333;
}

.empty-state {
    text-align: center;
    padding: 60px 20px;
    color: #999;
}

.empty-state-icon { font-size: 4rem; margin-bottom: 20px; }

@media (max-width: 768px) {
    .page-title { font-size: 1.5rem; }
    .card-header { padding: 20px; }
    th, td { padding: 12px 15px; font-size: 0.85rem; }
}
</style>
</head>
<body>
<div class="container">
    <div class="header">
        <div class="header-left">
            <a href="<%= request.getContextPath() %>/admin" class="btn btn-back">&larr; Volver al Panel</a>
            <h1 class="page-title">Gestión de Reseñas</h1>
        </div>
    </div>
    
    <% if ("true".equals(request.getParameter("exito"))) { %>
        <div class="alert">&check; ¡Operación realizada con éxito!</div>
    <% } %>
    
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Moderación de Reseñas</h2>
        </div>
        
        <div style="padding: 20px;">
            <div class="filter-section">
                <strong>Filtrar por estado:</strong>
                <div class="filter-buttons">
                    <a href="<%= request.getContextPath() %>/reviews-admin?accion=listar" class="btn btn-default btn-sm">Todas</a>
                    <a href="<%= request.getContextPath() %>/reviews-admin?accion=listar&status=PENDING_MODERATION" class="btn btn-warning btn-sm">Pendientes</a>
                    <a href="<%= request.getContextPath() %>/reviews-admin?accion=listar&status=APPROVED" class="btn btn-success btn-sm">Aprobadas</a>
                    <a href="<%= request.getContextPath() %>/reviews-admin?accion=listar&status=REJECTED" class="btn btn-danger btn-sm">Rechazadas</a>
                </div>
            </div>
        </div>
        
        <div class="table-container">
            <% 
                List<Review> reviews = (List<Review>) request.getAttribute("reviews");
                if (reviews == null || reviews.isEmpty()) {
            %>
                <div class="empty-state">
                    <div class="empty-state-icon">&#128221;</div>
                    <p>No hay reseñas registradas</p>
                </div>
            <% } else { %>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Usuario</th>
                            <th>Película</th>
                            <th>Rating</th>
                            <th>Fecha</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                            for (Review review : reviews) {
                                String statusBadge = "";
                                String statusClass = "";
                                
                                if (review.getModerationStatus() == ModerationStatus.APPROVED) {
                                    statusBadge = "Aprobada";
                                    statusClass = "badge-approved";
                                } else if (review.getModerationStatus() == ModerationStatus.REJECTED) {
                                    statusBadge = "Rechazada";
                                    statusClass = "badge-rejected";
                                } else {
                                    statusBadge = "Pendiente";
                                    statusClass = "badge-pending";
                                }
                        %>
                        <tr>
                            <td><strong>#<%= review.getReviewId() %></strong></td>
                            <td><%= review.getUsername() != null ? review.getUsername() : "Usuario " + review.getUserId() %></td>
                            <td><%= review.getMovieTitle() != null ? review.getMovieTitle() : "Película #" + review.getMovieId() %></td>
                            <td><%= String.format("%.1f", review.getRating()) %> / 5.0</td>
                            <td><%= review.getCreatedAt() %></td>
                            <td><span class="badge <%= statusClass %>"><%= statusBadge %></span></td>
                            <td>
                                <a href="<%= request.getContextPath() %>/reviews-admin?accion=detalle&id=<%= review.getReviewId() %>" class="btn btn-info btn-sm">Ver Detalle</a>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } %>
        </div>
    </div>
</div>
</body>
</html>
