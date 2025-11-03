<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="entity.Movie" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fat Movies</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            background-color: #FAF8F3;
            font-family: 'Poppins', sans-serif;
            overflow: hidden;
            height: 100vh;
        }
        
        .container {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: calc(100vh - 117px);
            position: relative;
            z-index: 2;
        }
        
        .logo-container {
            position: relative;
            z-index: 3;
            margin-bottom: 30px;
        }
        
        .logo-bg {
            position: fixed;
            width: 550px;
            height: 100vh;
            background-color: #FAF8F3;
            top: 0;
            left: 50%;
            transform: translateX(-50%);
            filter: blur(15px);
            box-shadow: 0 0 60px 30px rgba(250,248,243,0.8);
            z-index: 0;
        }
        
        .logo {
            position: relative;
            width: 400px;
            height: 300px;
            overflow: hidden;
            border-radius: 10px;
        }
        
        .logo img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            object-position: center;
            transform: scale(1.5);
            position: relative;
            z-index: 1;
        }
        
        h1 {
            font-size: 4rem;
            color: #1a1a1a;
            font-weight: 700;
            letter-spacing: -2px;
            z-index: 4;
            position: relative;
        }
        
        .poster-rows {
            position: absolute;
            width: 100%;
            height: 100%;
            top: 0;
            left: 0;
            z-index: 1;
            overflow: hidden;
        }
        
        .poster-row {
            position: absolute;
            display: flex;
            gap: 20px;
        }
        
        .poster-row:nth-child(1) {
            top: 20%;
            animation: scrollLeft 60s linear infinite;
        }
        
        .poster-row:nth-child(2) {
            top: 45%;
            animation: scrollRight 80s linear infinite;
        }
        
        .poster-row:nth-child(3) {
            top: 70%;
            animation: scrollLeft 100s linear infinite;
        }
        
        .poster {
            width: 150px;
            height: 225px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
            flex-shrink: 0;
            background-size: cover;
            background-position: center;
            background-color: #ddd;
        }
        
        @keyframes scrollLeft {
            0% { transform: translateX(0); }
            100% { transform: translateX(-50%); }
        }
        
        @keyframes scrollRight {
            0% { transform: translateX(-50%); }
            100% { transform: translateX(0); }
        }
    </style>
</head>
<body>
    <%
        @SuppressWarnings("unchecked")
        List<Movie> debugMovies = (List<Movie>) request.getAttribute("movies");
        System.out.println("[JSP DEBUG] Movies attribute: " + debugMovies);
        System.out.println("[JSP DEBUG] Movies size: " + (debugMovies != null ? debugMovies.size() : "null"));
    %>
    <%@ include file="/WEB-INF/components/navbar-new.jsp" %>
    <div class="container">
        <div class="logo-container">
            <div class="logo-bg"></div>
            <div class="logo">
                <img src="utils/export50.svg" alt="Fat Movies Logo" id="logoImg">
            </div>
        </div>
        <h1>Fat Movies</h1>
    </div>
    
    <div class="poster-rows" id="posterRows"></div>
    
    <script>
        const IMAGE_BASE = 'https://image.tmdb.org/t/p/w500';
        const movies = [
            <%
                @SuppressWarnings("unchecked")
                List<Movie> movieList = (List<Movie>) request.getAttribute("movies");
                System.out.println("[JSP SCRIPT] Building movies array, list is: " + (movieList != null ? "not null, size=" + movieList.size() : "NULL"));
                if (movieList != null && !movieList.isEmpty()) {
                    for (int i = 0; i < movieList.size(); i++) {
                        Movie m = movieList.get(i);
                        out.print("{posterPath: '" + (m.getPosterPath() != null ? m.getPosterPath() : "") + "'}");
                        if (i < movieList.size() - 1) out.print(",");
                    }
                } else {
                    System.out.println("[JSP SCRIPT] Movie list is empty or null!");
                }
            %>
        ];
        
        console.log('Total movies:', movies.length);
        console.log('Sample movie:', movies[0]);
        
        function initPosters() {
            if (movies.length === 0) {
                console.error('No movies available');
                return;
            }
            
            const posterRows = document.getElementById('posterRows');
            let idx = 0;
            
            for (let i = 0; i < 3; i++) {
                const row = document.createElement('div');
                row.className = 'poster-row';
                
                for (let j = 0; j < 24; j++) {
                    const movie = movies[idx % movies.length];
                    const poster = document.createElement('div');
                    poster.className = 'poster';
                    if (movie && movie.posterPath) {
                        poster.style.backgroundImage = 'url(' + IMAGE_BASE + movie.posterPath + ')';
                    }
                    row.appendChild(poster);
                    idx++;
                }
                
                posterRows.appendChild(row);
            }
        }
        
        initPosters();
    </script>
</body>
</html>