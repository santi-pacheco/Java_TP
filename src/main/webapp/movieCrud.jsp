<%@ page import="java.util.List"%>
<%@ page import="entity.Movie"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Gestión de Películas - FatMovies</title>
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
    min-width: 300px;
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

.movie-poster {
    width: 40px;
    height: 60px;
    object-fit: cover;
    border-radius: 6px;
    box-shadow: 0 2px 6px rgba(0,0,0,0.15);
}

.rating-badge {
    background: #ffc107;
    color: #333;
    padding: 4px 10px;
    border-radius: 15px;
    font-weight: 600;
    font-size: 0.85rem;
}

.actions { display: flex; gap: 8px; flex-wrap: wrap; }

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

.stat-value { font-size: 1.5rem; font-weight: 700; color: #333; margin-bottom: 3px; }
.stat-label { font-size: 0.8rem; color: #666; }

.loading {
    text-align: center;
    padding: 20px;
    color: #666;
    display: none;
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
    .search-box { min-width: 100%; }
}
</style>
</head>
<body>
<div class="container">
    <div class="header">
        <div class="header-left">
            <a href="<%= request.getContextPath() %>/admin" class="btn btn-back">← Volver al Panel</a>
            <h1 class="page-title">Gestión de Películas</h1>
        </div>
    </div>
    
    <% if ("true".equals(request.getParameter("exito"))) { %>
        <div class="alert">&check; ¡Operación realizada con éxito!</div>
    <% } %>
    
    <% 
        List<Movie> movies = (List<Movie>) request.getAttribute("movies");
        int totalMovies = (movies != null) ? movies.size() : 0;
        double avgRating = 0;
        if (movies != null && !movies.isEmpty()) {
            for (Movie m : movies) {
                avgRating += m.getApiRating();
            }
            avgRating /= movies.size();
        }
    %>
    
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-value"><%= totalMovies %></div>
            <div class="stat-label">Total Películas</div>
        </div>
        <div class="stat-card">
            <div class="stat-value"><%= String.format("%.1f", avgRating) %></div>
            <div class="stat-label">Puntuación Promedio</div>
        </div>
    </div>
    
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Catálogo de Películas</h2>
            <div style="display: flex; gap: 15px; align-items: center; flex-wrap: wrap;">
                <input type="text" id="searchInput" class="search-box" placeholder="Buscar película...">
                <a href="<%= request.getContextPath() %>/movies?accion=mostrarFormCrear" class="btn btn-success">+ Añadir Película</a>
            </div>
        </div>
        
        <div class="table-container">
            <% if (movies == null || movies.isEmpty()) { %>
                <div class="empty-state">
                    <div class="empty-state-icon">&#127909;</div>
                    <p>No hay películas en el catálogo</p>
                </div>
            <% } else { %>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Póster</th>
                            <th>Título</th>
                            <th>Año</th>
                            <th>Puntuación</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody id="movieTableBody">
                        <% 
                            for (int i = 0; i < movies.size(); i++) {
                                Movie movie = movies.get(i);
                                String displayStyle = i < 50 ? "" : "style='display:none;'";
                        %>
                        <tr class="movie-row" data-title="<%= movie.getTitle().toLowerCase() %>" <%= displayStyle %>>
                            <td><strong>#<%= movie.getMovieId() %></strong></td>
                            <td>
                                <% if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) { %>
                                    <img src="https://image.tmdb.org/t/p/w92<%= movie.getPosterPath() %>" class="movie-poster" alt="<%= movie.getTitle() %>">
                                <% } else { %>
                                    <div style="width:50px;height:75px;background:#e0e0e0;border-radius:6px;display:flex;align-items:center;justify-content:center;color:#999;">&#128253;</div>
                                <% } %>
                            </td>
                            <td><strong><%= movie.getTitle() %></strong></td>
                            <td><%= movie.getReleaseYear() %></td>
                            <td><span class="rating-badge">&star; <%= String.format("%.1f", movie.getApiRating()) %></span></td>
                            <td>
                                <div class="actions">
                                    <a href="<%= request.getContextPath() %>/movies?accion=mostrarFormEditar&id=<%= movie.getMovieId() %>" class="btn btn-warning btn-sm">Editar</a>
                                    <form action="<%= request.getContextPath() %>/movies" method="POST" style="display:inline;">
                                        <input type="hidden" name="accion" value="eliminar">
                                        <input type="hidden" name="id" value="<%= movie.getMovieId() %>">
                                        <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('¿Eliminar <%= movie.getTitle() %>?')">Eliminar</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <div class="loading">&#8987; Cargando más películas...</div>
            <% } %>
        </div>
    </div>
</div>

<script>
let displayedCount = 50;

const tableContainer = document.querySelector('.table-container');
if (tableContainer) {
    tableContainer.addEventListener('scroll', function() {
        if (this.scrollTop + this.clientHeight >= this.scrollHeight - 50) {
            loadMore();
        }
    });
}

function loadMore() {
    const hiddenRows = document.querySelectorAll('.movie-row[style*="display:none"], .movie-row[style*="display: none"]');
    if (hiddenRows.length === 0) return;
    
    document.querySelector('.loading').style.display = 'block';
    
    setTimeout(() => {
        for (let i = 0; i < Math.min(20, hiddenRows.length); i++) {
            hiddenRows[i].style.display = '';
        }
        displayedCount += 20;
        document.querySelector('.loading').style.display = 'none';
    }, 300);
}

const searchInput = document.getElementById('searchInput');
if (searchInput) {
    searchInput.addEventListener('keyup', function() {
        const searchTerm = this.value.toLowerCase();
        const rows = document.querySelectorAll('.movie-row');
        
        rows.forEach(row => {
            const title = row.getAttribute('data-title');
            row.style.display = title.includes(searchTerm) ? '' : 'none';
        });
    });
}
</script>
</body>
</html>