<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Administración de Datos - Fat Movies</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <style>
        body {
            background-color: #FAF8F3;
            font-family: 'Poppins', sans-serif;
            margin: 0;
            padding: 0;
        }
        
        .container-custom {
            max-width: 1200px;
            margin: 0 auto;
            padding: 40px 20px;
        }
        
        h1 {
            font-size: 2.5rem;
            font-weight: 700;
            color: #333;
            margin-bottom: 10px;
        }

        .subtitle {
            color: #666;
            margin-bottom: 40px;
            font-size: 1.1rem;
        }
        .admin-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 30px;
        }
        .admin-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            padding: 30px;
            transition: transform 0.3s, box-shadow 0.3s;
            text-align: center;
            border: 1px solid rgba(0,0,0,0.02);
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            height: 100%;
        }
        .admin-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 20px rgba(0,0,0,0.12);
        }
        .card-icon {
            font-size: 3rem;
            margin-bottom: 20px;
            display: block;
        }
        .card-title {
            font-weight: 600;
            font-size: 1.25rem;
            color: #333;
            margin-bottom: 10px;
        }
        .card-desc {
            font-size: 0.9rem;
            color: #666;
            margin-bottom: 25px;
            flex-grow: 1;
        }
        .btn-action {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 25px;
            font-family: 'Poppins', sans-serif;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.3s, transform 0.2s;
            color: white;
        }
        .btn-primary-custom { background-color: #8B7355; }
        .btn-primary-custom:hover { background-color: #6e5b42; }

        .btn-warning-custom { background-color: #D4A017; color: white; }
        .btn-warning-custom:hover { background-color: #b88a12; }

        .btn-info-custom { background-color: #5D8AA8; color: white;}
        .btn-info-custom:hover { background-color: #4a6f8a; }
        .alert-custom {
            border-radius: 10px;
            padding: 15px 20px;
            margin-bottom: 30px;
            border: none;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
        }
        
        .navbar { margin-bottom: 0 !important; }
    </style>
</head>
<body>

    <%@ include file="/WEB-INF/components/navbar-new.jsp" %>
    
    <div class="container-custom">
        
        <div>
            <h1>Panel de Datos</h1>
            <p class="subtitle">Administración de carga y sincronización con TMDB</p>
        </div>

        <% 
            String msg = (String) session.getAttribute("flashMessage");
            String type = (String) session.getAttribute("flashType");
            if (msg != null) {
                String alertClass = "alert-success";
                if ("danger".equals(type)) {
                    alertClass = "alert-danger";
                }
        %>
            <div class="alert <%= alertClass %> alert-custom alert-dismissible fade show" role="alert">
                <strong>Estado:</strong> <%= msg %>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        <% 
                session.removeAttribute("flashMessage");
                session.removeAttribute("flashType");
            } 
        %>

        <div class="admin-grid">

            <div class="admin-card">
                <div>
                    <span class="card-icon">🏷️</span>
                    <div class="card-title">1. Géneros</div>
                    <p class="card-desc">
                        Descarga la lista oficial de géneros desde TMDB. Ejecutar esto primero si la base de datos está vacía.
                    </p>
                </div>
                <form action="${pageContext.request.contextPath}/data-load" method="post">
                    <input type="hidden" name="accion" value="loadGenres">
                    <button type="submit" class="btn-action btn-primary-custom">Cargar Géneros</button>
                </form>
            </div>

            <div class="admin-card">
                <div>
                    <span class="card-icon">🎬</span>
                    <div class="card-title">2. Películas</div>
                    <p class="card-desc">
                        Descarga el catálogo base de películas populares. Crea los registros iniciales (título, año, poster).
                    </p>
                </div>
                <form action="${pageContext.request.contextPath}/data-load" method="post">
                    <input type="hidden" name="accion" value="loadMovies">
                    <button type="submit" class="btn-action btn-primary-custom">Cargar Películas</button>
                </form>
            </div>

            <div class="admin-card">
                <div>
                    <span class="card-icon">📝</span>
                    <div class="card-title">3. Detalles Completos</div>
                    <p class="card-desc">
                        Actualiza Actores, Directores, Duración y Países para todas las películas existentes.
                        <br><strong style="color:#D4A017; font-size: 0.85em;">⚠️ Tarda unos minutos</strong>
                    </p>
                </div>
                <form action="${pageContext.request.contextPath}/data-load" method="post">
                    <input type="hidden" name="accion" value="loadDetails">
                    <button type="submit" class="btn-action btn-warning-custom">Sincronizar Detalles</button>
                </form>
            </div>

            <div class="admin-card">
                <div>
                    <span class="card-icon">👤</span>
                    <div class="card-title">4. Info Personas</div>
                    <p class="card-desc">
                        Descarga fotos de perfil, fechas y lugar de nacimiento de todos los actores/directores.
                        <br><strong style="color:#5D8AA8; font-size: 0.85em;">⚠️ Proceso intensivo</strong>
                    </p>
                </div>
                <form action="${pageContext.request.contextPath}/data-load" method="post">
                    <input type="hidden" name="accion" value="loadPersons">
                    <button type="submit" class="btn-action btn-info-custom">Cargar Fotos Personas</button>
                </form>
            </div>

        </div> <div style="margin-top: 40px; text-align: center;">
            <a href="${pageContext.request.contextPath}/home" style="color: #666; text-decoration: none; font-weight: 600;">← Volver al Home</a>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>