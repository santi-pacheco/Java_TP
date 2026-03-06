<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Carga de Datos TMDB - FatMovies</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        
        /* --- ESTILOS BASE COPIADOS DE SYSTEM SETTINGS --- */
        body {
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(135deg, #FAF8F3 0%, #F0EDE6 100%);
            min-height: 100vh;
            padding: 15px;
        }
        
        .container { 
            max-width: 1200px; 
            margin: 0 auto; 
            display: flex; 
            flex-direction: column; 
        }
        
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
            flex-wrap: wrap;
            gap: 10px;
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
        
        .btn-back { background: #666; color: white; }
        .btn-back:hover { background: #555; transform: translateY(-2px); }
        
        .alert {
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 25px;
            font-weight: 500;
        }
        .alert-success { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .alert-danger { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }

        /* --- ESTILOS ADAPTADOS PARA LAS TARJETAS DE TMDB --- */
        .admin-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
            gap: 25px;
            margin-top: 10px;
        }

		.card-icon {
            width: 65px;
            height: 65px;
            margin: 0 auto 20px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #F8F9FA;
            border-radius: 15px;
            padding: 12px;
        }

        .card-icon svg {
            width: 100%;
            height: 100%;
        }
        .card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            padding: 30px 25px;
            display: flex;
            flex-direction: column;
            text-align: center;
            transition: transform 0.3s, box-shadow 0.3s;
            border: 1px solid rgba(0,0,0,0.02);
        }

        .card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 20px rgba(0,0,0,0.1);
        }

        .card-icon {
            font-size: 3.5rem;
            margin-bottom: 15px;
            display: block;
        }

        .card-title {
            font-size: 1.2rem;
            font-weight: 600;
            color: #1a1a1a;
            margin-bottom: 10px;
        }

        .card-desc {
            font-size: 0.9rem;
            color: #666;
            line-height: 1.5;
            margin-bottom: 25px;
            flex-grow: 1; /* Empuja el botón hacia abajo */
        }

        .btn-action {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 8px;
            font-family: inherit;
            font-weight: 600;
            color: white;
            cursor: pointer;
            transition: background 0.3s, transform 0.2s;
        }

        .btn-primary-custom { background-color: #8B7355; }
        .btn-primary-custom:hover { background-color: #6e5b42; }

        .btn-warning-custom { background-color: #D4A017; }
        .btn-warning-custom:hover { background-color: #b88a12; }

        .btn-info-custom { background-color: #5D8AA8; }
        .btn-info-custom:hover { background-color: #4a6f8a; }

    </style>
</head>
<body>

    <div class="container">
        <div class="header">
            <div class="header-left">
                <a href="<%= request.getContextPath() %>/admin" class="btn btn-back">&larr; Volver al Panel</a>
                <h1 class="page-title">Carga de Datos TMDB</h1>
            </div>
        </div>

        <p style="color: #555; font-size: 1.05rem; margin-bottom: 20px; font-weight: 500;">
            Administración de carga y sincronización de base de datos.
        </p>

        <% 
            String msg = (String) session.getAttribute("flashMessage");
            String type = (String) session.getAttribute("flashType");
            if (msg != null) {
                String alertClass = "alert-success";
                if ("danger".equals(type)) {
                    alertClass = "alert-danger";
                }
        %>
            <div class="alert <%= alertClass %>">
                <strong>Estado:</strong> <%= msg %>
            </div>
        <% 
                session.removeAttribute("flashMessage");
                session.removeAttribute("flashType");
            } 
        %>

        <div class="admin-grid">

            <div class="card">
                <div>
                    <div class="card-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="#8B7355" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"></path>
                            <line x1="7" y1="7" x2="7.01" y2="7"></line>
                        </svg>
                    </div>
                    <div class="card-title">1. Géneros</div>
                    <p class="card-desc">
                        Descarga la lista oficial de géneros desde TMDB. 
                        Ejecutar esto primero si la base de datos está vacía.
                    </p>
                </div>
                <form action="${pageContext.request.contextPath}/data-load" method="post" style="margin-top: auto;">
                    <input type="hidden" name="accion" value="loadGenres">
                    <button type="submit" class="btn-action btn-primary-custom">Cargar Géneros</button>
                </form>
            </div>

            <div class="card">
                <div>
                    <div class="card-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="#8B7355" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <rect x="2" y="2" width="20" height="20" rx="2.18" ry="2.18"></rect>
                            <line x1="7" y1="2" x2="7" y2="22"></line>
                            <line x1="17" y1="2" x2="17" y2="22"></line>
                            <line x1="2" y1="12" x2="22" y2="12"></line>
                            <line x1="2" y1="7" x2="7" y2="7"></line>
                            <line x1="2" y1="17" x2="7" y2="17"></line>
                            <line x1="17" y1="17" x2="22" y2="17"></line>
                            <line x1="17" y1="7" x2="22" y2="7"></line>
                        </svg>
                    </div>
                    <div class="card-title">2. Películas</div>
                    <p class="card-desc">
                        Descarga el catálogo base de películas populares. 
                        Crea los registros iniciales (título, año, poster).
                    </p>
                </div>
                <form action="${pageContext.request.contextPath}/data-load" method="post" style="margin-top: auto;">
                    <input type="hidden" name="accion" value="loadMovies">
                    <button type="submit" class="btn-action btn-primary-custom">Cargar Películas</button>
                </form>
            </div>

            <div class="card">
                <div>
                    <div class="card-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="#D4A017" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                            <polyline points="14 2 14 8 20 8"></polyline>
                            <line x1="16" y1="13" x2="8" y2="13"></line>
                            <line x1="16" y1="17" x2="8" y2="17"></line>
                            <polyline points="10 9 9 9 8 9"></polyline>
                        </svg>
                    </div>
                    <div class="card-title">3. Detalles Completos</div>
                    <p class="card-desc">
                        Actualiza Actores, Directores, Duración y Países para todas las películas existentes.
                        <br><strong style="color:#D4A017; font-size: 0.85em; display:block; margin-top: 5px;">⚠️ Tarda unos minutos</strong>
                    </p>
                </div>
                <form action="${pageContext.request.contextPath}/data-load" method="post" style="margin-top: auto;">
                    <input type="hidden" name="accion" value="loadDetails">
                    <button type="submit" class="btn-action btn-warning-custom">Sincronizar Detalles</button>
                </form>
            </div>

            <div class="card">
                <div>
                    <div class="card-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="#5D8AA8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                            <circle cx="12" cy="7" r="4"></circle>
                        </svg>
                    </div>
                    <div class="card-title">4. Info Personas</div>
                    <p class="card-desc">
                        Descarga fotos de perfil, fechas y lugar de nacimiento de todos los actores/directores.
                        <br><strong style="color:#5D8AA8; font-size: 0.85em; display:block; margin-top: 5px;">⚠️ Proceso intensivo</strong>
                    </p>
                </div>
                <form action="${pageContext.request.contextPath}/data-load" method="post" style="margin-top: auto;">
                    <input type="hidden" name="accion" value="loadPersons">
                    <button type="submit" class="btn-action btn-info-custom">Cargar Fotos Personas</button>
                </form>
            </div>

        </div>
    </div>

</body>
</html>