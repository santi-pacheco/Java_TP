<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Usuarios - FatMovies</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
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
        
        .container {
            max-width: 1400px;
            margin: 0 auto;
            height: 100%;
            display: flex;
            flex-direction: column;
        }
        
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            flex-wrap: wrap;
            gap: 10px;
            flex-shrink: 0;
        }
        
        .header-left {
            display: flex;
            align-items: center;
            gap: 20px;
        }
        
        .page-title {
            font-size: 1.5rem;
            font-weight: 700;
            color: #1a1a1a;
        }
        
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
        
        .btn-back {
            background: #666;
            color: white;
        }
        
        .btn-back:hover {
            background: #555;
            transform: translateY(-2px);
            text-decoration: none;
            color: white;
        }
        
        .btn-success {
            background: #28a745;
            color: white;
        }
        
        .btn-success:hover {
            background: #218838;
            transform: translateY(-2px);
        }
        
        .btn-warning {
            background: #ffc107;
            color: #333;
        }
        
        .btn-warning:hover {
            background: #e0a800;
        }
        
        .btn-danger {
            background: #dc3545;
            color: white;
        }
        
        .btn-danger:hover {
            background: #c82333;
        }
        
        .btn-sm {
            padding: 6px 12px;
            font-size: 0.85rem;
        }
        
        .alert {
            padding: 10px 15px;
            border-radius: 8px;
            margin-bottom: 15px;
            font-weight: 500;
            flex-shrink: 0;
        }
        
        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
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
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 10px;
            flex-shrink: 0;
        }
        
        .card-title {
            font-size: 1.2rem;
            font-weight: 600;
            color: #1a1a1a;
        }
        
        .search-box {
            padding: 8px 15px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-family: 'Poppins', sans-serif;
            font-size: 0.9rem;
            outline: none;
            transition: border-color 0.3s;
            min-width: 250px;
        }
        
        .search-box:focus {
            border-color: #999;
        }
        
        .table-container {
            overflow-y: auto;
            flex: 1;
            min-height: 0;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
        }
        
        thead {
            background: #f8f9fa;
        }
        
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
        
        tbody tr {
            transition: background 0.2s;
        }
        
        tbody tr:hover {
            background: #fafafa;
        }
        
        .avatar-thumb {
            width: 35px;
            height: 35px;
            border-radius: 50%;
            object-fit: cover;
            border: 2px solid #e0e0e0;
        }
        
        .badge {
            padding: 3px 8px;
            border-radius: 12px;
            font-size: 0.7rem;
            font-weight: 600;
            display: inline-block;
        }
        
        .badge-admin {
            background: #dc3545;
            color: white;
        }
        
        .badge-user {
            background: #17a2b8;
            color: white;
        }
        
        .badge-level {
            background: #ffc107;
            color: #333;
        }
        
        .badge-active {
            background: #28a745;
            color: white;
        }
        
        .badge-banned {
            background: #dc3545;
            color: white;
        }
        
        .actions {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 15px;
            margin-bottom: 15px;
            flex-shrink: 0;
        }
        
        .stat-card {
            background: white;
            padding: 15px;
            border-radius: 10px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.06);
            text-align: center;
        }
        
        .stat-value {
            font-size: 1.5rem;
            font-weight: 700;
            color: #333;
            margin-bottom: 3px;
        }
        
        .stat-label {
            font-size: 0.8rem;
            color: #666;
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }
        
        .empty-state-icon {
            font-size: 4rem;
            margin-bottom: 20px;
        }
        
        @media (max-width: 768px) {
            .page-title { font-size: 1.5rem; }
            .card-header { padding: 20px; }
            th, td { padding: 12px 15px; font-size: 0.85rem; }
            .search-box { min-width: 100%; }
        }
    </style>
</head>
<body>

<div class="container">
    <div class="header">
        <div class="header-left">
            <a href="<%= request.getContextPath() %>/admin" class="btn btn-back">&larr; Volver al Panel</a>
            <h1 class="page-title">Gestión de Usuarios</h1>
        </div>
    </div>
    
    <c:if test="${param.exito == 'true'}">
        <div class="alert alert-success">
            &check; ¡Operación realizada con éxito!
        </div>
    </c:if>
    
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-value">${users.size()}</div>
            <div class="stat-label">Total Usuarios</div>
        </div>
        <div class="stat-card">
            <div class="stat-value">
                <c:set var="adminCount" value="0" />
                <c:forEach var="u" items="${users}">
                    <c:if test="${u.role == 'admin'}">
                        <c:set var="adminCount" value="${adminCount + 1}" />
                    </c:if>
                </c:forEach>
                ${adminCount}
            </div>
            <div class="stat-label">Administradores</div>
        </div>
        <div class="stat-card">
            <div class="stat-value">${users.size() - adminCount}</div>
            <div class="stat-label">Usuarios Regulares</div>
        </div>
    </div>
    
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Lista de Usuarios</h2>
            <div style="display: flex; gap: 15px; align-items: center; flex-wrap: wrap;">
                <input type="text" id="searchInput" class="search-box" placeholder="Buscar usuario..." onkeyup="filterTable()">
                <c:url var="addUrl" value="/users?accion=mostrarFormCrear" />
                <a href="${addUrl}" class="btn btn-success">+ Añadir Usuario</a>
            </div>
        </div>
        
        <div class="table-container">
            <c:choose>
                <c:when test="${empty users}">
                    <div class="empty-state">
                        <div class="empty-state-icon">&#128100;</div>
                        <p>No hay usuarios registrados</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table id="usersTable">
                        <thead>
                            <tr>
                                <th>Avatar</th>
                                <th>ID</th>
                                <th>Username</th>
                                <th>Email</th>
                                <th>Rol</th>
                                <th>Nivel</th>
                                <th>Kcals</th>
                                <th>Estado</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="user" items="${users}">
                                <tr>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty user.profileImage}">
                                                <img src="${pageContext.request.contextPath}/uploads/${user.profileImage}" class="avatar-thumb" alt="Avatar" onerror="this.src='${pageContext.request.contextPath}/utils/default_profile.png'">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/utils/default_profile.png" class="avatar-thumb" alt="Sin foto">
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><strong>#${user.userId}</strong></td>
                                    <td><strong>${user.username}</strong></td>
                                    <td>${user.email}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${user.role == 'admin'}">
                                                <span class="badge badge-admin">Admin</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-user">User</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <span class="badge badge-level">Nivel ${user.userLevel}</span>
                                    </td>
                                    <td>${user.totalKcals}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${user.bannedUntil != null}">
                                                <span class="badge badge-banned">Baneado</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-active">Activo</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="actions">
                                            <c:url var="editUrl" value="/users">
                                                <c:param name="accion" value="mostrarFormEditar" />
                                                <c:param name="id" value="${user.userId}" />
                                            </c:url>
                                            <a href="${editUrl}" class="btn btn-warning btn-sm">Editar</a>
                                            
                                            <c:choose>
                                                <c:when test="${user.bannedUntil != null}">
                                                    <c:url var="unbanUrl" value="/users" />
                                                    <form action="${unbanUrl}" method="POST" style="display:inline;">
                                                        <input type="hidden" name="accion" value="desbanear">
                                                        <input type="hidden" name="id" value="${user.userId}">
                                                        <button type="submit" class="btn btn-success btn-sm">Desbanear</button>
                                                    </form>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:url var="banUrl" value="/users" />
                                                    <form action="${banUrl}" method="POST" style="display:inline;">
                                                        <input type="hidden" name="accion" value="banear">
                                                        <input type="hidden" name="id" value="${user.userId}">
                                                        <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('¿Banear a ${user.username}?')">Banear</button>
                                                    </form>
                                                </c:otherwise>
                                            </c:choose>
                                            
                                            <c:url var="deleteActionUrl" value="/users" />
                                            <form action="${deleteActionUrl}" method="POST" style="display:inline;">
                                                <input type="hidden" name="accion" value="eliminar">
                                                <input type="hidden" name="id" value="${user.userId}">
                                                <button type="submit" class="btn btn-danger btn-sm" title="Eliminar"
                                                        onclick="return confirm('¿Está seguro de eliminar a ${user.username}? Se borrarán sus reseñas y datos.')">
                                                    Eliminar
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<script>
    function filterTable() {
        const input = document.getElementById('searchInput');
        const filter = input.value.toLowerCase();
        const table = document.getElementById('usersTable');
        const rows = table.getElementsByTagName('tr');
        
        for (let i = 1; i < rows.length; i++) {
            const cells = rows[i].getElementsByTagName('td');
            let found = false;
            
            for (let j = 0; j < cells.length; j++) {
                const cell = cells[j];
                if (cell && cell.textContent.toLowerCase().indexOf(filter) > -1) {
                    found = true;
                    break;
                }
            }
            
            rows[i].style.display = found ? '' : 'none';
        }
    }
</script>

</body>
</html>
