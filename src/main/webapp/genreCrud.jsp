<%@ page import="java.util.List"%>
<%@ page import="entity.Genre"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Gestión de Géneros - FatMovies</title>
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

.container { max-width: 1000px; margin: 0 auto; height: 100%; display: flex; flex-direction: column; }

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
.btn-success { background: #28a745; color: white; }
.btn-success:hover { background: #218838; transform: translateY(-2px); }
.btn-warning { background: #ffc107; color: #333; }
.btn-warning:hover { background: #e0a800; }
.btn-danger { background: #dc3545; color: white; }
.btn-danger:hover { background: #c82333; }
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
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 10px;
    flex-shrink: 0;
}

.card-title { font-size: 1.2rem; font-weight: 600; color: #1a1a1a; }

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

.search-box:focus { border-color: #999; }

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

.actions { display: flex; gap: 8px; flex-wrap: wrap; }

.stat-card {
    background: white;
    padding: 15px;
    border-radius: 10px;
    box-shadow: 0 2px 6px rgba(0,0,0,0.06);
    text-align: center;
    margin-bottom: 15px;
    flex-shrink: 0;
}

.stat-value { font-size: 1.5rem; font-weight: 700; color: #333; margin-bottom: 3px; }
.stat-label { font-size: 0.8rem; color: #666; }

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
    .search-box { min-width: 100%; }
}
</style>
</head>
<body>
<div class="container">
    <div class="header">
        <div class="header-left">
            <a href="<%= request.getContextPath() %>/admin" class="btn btn-back">&larr; Volver al Panel</a>
            <h1 class="page-title">Gestión de Géneros</h1>
        </div>
    </div>
    
    <% if ("true".equals(request.getParameter("exito"))) { %>
        <div class="alert">&check; ¡Operación realizada con éxito!</div>
    <% } %>
    
    <% 
        List<Genre> genres = (List<Genre>) request.getAttribute("genres");
        int totalGenres = (genres != null) ? genres.size() : 0;
    %>
    
    <div class="stat-card">
        <div class="stat-value"><%= totalGenres %></div>
        <div class="stat-label">Géneros Registrados</div>
    </div>
    
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Lista de Géneros</h2>
            <div style="display: flex; gap: 15px; align-items: center; flex-wrap: wrap;">
                <input type="text" id="searchInput" class="search-box" placeholder="Buscar género..." onkeyup="filterTable()">
                <a href="<%= request.getContextPath() %>/genres?accion=mostrarFormCrear" class="btn btn-success">+ Añadir Género</a>
            </div>
        </div>
        
        <% if (genres == null || genres.isEmpty()) { %>
            <div class="empty-state">
                <div class="empty-state-icon">&#127917;</div>
                <p>No hay géneros registrados</p>
            </div>
        <% } else { %>
            <div class="table-container">
                <table id="genresTable">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>ID API</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Genre genre : genres) { %>
                        <tr>
                            <td><strong>#<%= genre.getGenreId() %></strong></td>
                            <td><strong><%= genre.getName() %></strong></td>
                            <td><%= genre.getApiId() != null ? genre.getApiId() : "-" %></td>
                            <td>
                                <div class="actions">
                                    <a href="<%= request.getContextPath() %>/genres?accion=mostrarFormEditar&id=<%= genre.getGenreId() %>" class="btn btn-warning btn-sm">Editar</a>
                                    <form action="<%= request.getContextPath() %>/genres" method="POST" style="display:inline;">
                                        <input type="hidden" name="accion" value="eliminar">
                                        <input type="hidden" name="id" value="<%= genre.getGenreId() %>">
                                        <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('¿Eliminar <%= genre.getName() %>?')">Eliminar</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } %>
    </div>
</div>

<script>
function filterTable() {
    const input = document.getElementById('searchInput');
    const filter = input.value.toLowerCase();
    const table = document.getElementById('genresTable');
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
