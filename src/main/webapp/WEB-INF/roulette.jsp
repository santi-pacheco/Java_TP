<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="entity.Movie" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ruleta de Películas - Fat Movies</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            background-color: #FAF8F3;
            font-family: 'Poppins', sans-serif;
            margin: 0;
            padding: 0;
        }
        
        .container {
            max-width: 900px;
            margin: 0 auto;
            padding: 40px 20px;
            text-align: center;
        }
        
        h1 {
            font-size: 2.5rem;
            font-weight: 700;
            color: #333;
            margin-bottom: 40px;
        }
        
        .roulette-container {
            position: relative;
            width: 600px;
            height: 600px;
            margin: 0 auto 40px;
            border-radius: 50%;
            border: 8px solid #333;
            overflow: hidden;
        }
        
        .roulette-wheel {
            width: 100%;
            height: 100%;
            position: relative;
            border-radius: 50%;
        }
        
        @keyframes spin {
            from { transform: rotate(0deg); }
        }
        

        
        .popup-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.7);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 1000;
            animation: fadeIn 0.5s ease-in 3s both;
        }
        
        .selected-movie {
            background: white;
            border-radius: 20px;
            padding: 30px;
            box-shadow: 0 8px 24px rgba(0,0,0,0.3);
            max-width: 500px;
            animation: popIn 0.5s ease-out;
        }
        
        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }
        
        @keyframes popIn {
            from { transform: scale(0.5); opacity: 0; }
            to { transform: scale(1); opacity: 1; }
        }
        
        .selected-movie img {
            width: 200px;
            height: 300px;
            object-fit: cover;
            border-radius: 15px;
            margin-bottom: 20px;
        }
        
        .selected-movie h2 {
            font-size: 1.8rem;
            color: #333;
            margin-bottom: 10px;
        }
        
        .selected-movie p {
            font-size: 1.1rem;
            color: #666;
            margin-bottom: 20px;
        }
        
        .btn {
            padding: 12px 30px;
            background-color: #333;
            color: white;
            border: none;
            border-radius: 25px;
            font-family: 'Poppins', sans-serif;
            font-weight: 500;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s;
            text-decoration: none;
            display: inline-block;
            margin: 5px;
        }
        
        .btn:hover {
            background-color: #555;
        }
        
        .btn-secondary {
            background-color: #666;
        }
        
        .btn-secondary:hover {
            background-color: #888;
        }
        
        .roulette-pointer {
            position: absolute;
            top: -20px;
            left: 50%;
            transform: translateX(-50%);
            width: 0;
            height: 0;
            border-left: 30px solid transparent;
            border-right: 30px solid transparent;
            border-top: 50px solid #FF0000;
            z-index: 10;
            filter: drop-shadow(0 4px 8px rgba(0,0,0,0.6));
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/components/navbar-new.jsp" %>
    
    <div class="container">
        <h1>🎰 Ruleta de Películas</h1>
        
        <%
            @SuppressWarnings("unchecked")
            List<Movie> movies = (List<Movie>) request.getAttribute("movies");
            int totalMovies = movies.size();
            int selectedIndex = (Integer) request.getAttribute("selectedIndex");
            int extraSpins = (Integer) request.getAttribute("extraSpins");
            double randomOffset = (Double) request.getAttribute("randomOffset");
            
            double degreesPerSlice = 360.0 / totalMovies;
            double targetAngle = selectedIndex * degreesPerSlice + (degreesPerSlice / 2);
            double finalRotation = (360 * extraSpins) + targetAngle + randomOffset;
        %>
        
        <div style="position: relative; display: inline-block;">
            <div class="roulette-pointer"></div>
            <div class="roulette-container">
            <div class="roulette-wheel" style="animation: spin 3s cubic-bezier(0.17, 0.67, 0.12, 0.99); animation-fill-mode: forwards; transform: rotate(<%= finalRotation %>deg); background: conic-gradient(
                <%
                    String[] colors = {"#FF6B6B", "#4ECDC4", "#45B7D1", "#FFA07A", "#98D8C8", "#F7DC6F", "#BB8FCE", "#85C1E2"};
                    for (int i = 0; i < totalMovies; i++) {
                        double startAngle = degreesPerSlice * i;
                        double endAngle = degreesPerSlice * (i + 1);
                        String color = colors[i % colors.length];
                        out.print(color + " " + startAngle + "deg " + endAngle + "deg");
                        if (i < totalMovies - 1) out.print(", ");
                    }
                %>);">
                <%
                    for (int i = 0; i < totalMovies; i++) {
                        Movie movie = movies.get(totalMovies - 1 - i);
                        double angle = degreesPerSlice * i + (degreesPerSlice / 2);
                %>
                <div style="position: absolute; width: 100%; height: 100%; transform: rotate(<%= angle %>deg);">
                    <img src="https://image.tmdb.org/t/p/w200<%= movie.getPosterPath() %>" 
                         alt="<%= movie.getTitulo() %>"
                         style="position: absolute; width: 100px; height: 150px; top: 30px; left: 50%; transform: translateX(-50%); object-fit: cover; border-radius: 8px; border: 3px solid white; box-shadow: 0 4px 8px rgba(0,0,0,0.6);"
                         onerror="this.src='https://via.placeholder.com/100x150?text=?'">
                </div>
                <%
                    }
                %>
            </div>
            </div>
        </div>
        
        <%
            Movie selectedMovie = (Movie) request.getAttribute("selectedMovie");
            if (selectedMovie != null) {
        %>
        <div class="popup-overlay">
            <div class="selected-movie">
            <img src="https://image.tmdb.org/t/p/w300<%= selectedMovie.getPosterPath() %>" 
                 alt="<%= selectedMovie.getTitulo() %>"
                 onerror="this.src='https://via.placeholder.com/200x300?text=Sin+Imagen'">
            <h2><%= selectedMovie.getTitulo() %></h2>
            <p><%= selectedMovie.getEstrenoYear() %></p>
            <a href="${pageContext.request.contextPath}/movie/<%= selectedMovie.getId() %>" class="btn">Ver Detalles</a>
            <a href="${pageContext.request.contextPath}/roulette" class="btn btn-secondary">Girar de Nuevo</a>
            <a href="${pageContext.request.contextPath}/watchlist" class="btn btn-secondary">Volver a Watchlist</a>
            </div>
        </div>
        <%
            }
        %>
    </div>
</body>
</html>
