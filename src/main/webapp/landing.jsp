<%@ page contentType="text/html;charset=UTF-8" %>
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
        const API_TOKEN = 'eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJhNDdiYTBiMTI3NDk5YjBlMWIyOGNlYjBhMTgzZWM1NyIsIm5iZiI6MTc1NTYwOTMwOC44NzIsInN1YiI6IjY4YTQ3OGRjNWJkMTI3ZjcyY2RhNThjYSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ._mkAgrQSPf-YCaYm1TFxuNDEgAtESQEaBOPI5t-8i8Q';
        const IMAGE_BASE = 'https://image.tmdb.org/t/p/w500';
        
        async function fetchMovies() {
            try {
                const response = await fetch('https://api.themoviedb.org/3/movie/popular?language=es-ES&page=1', {
                    headers: {
                        'Authorization': 'Bearer ' + API_TOKEN,
                        'accept': 'application/json'
                    }
                });
                const data = await response.json();
                return data.results;
            } catch (error) {
                console.error('Error fetching movies:', error);
                return [];
            }
        }
        
        function shuffleArray(array) {
            const shuffled = [...array];
            for (let i = shuffled.length - 1; i > 0; i--) {
                const j = Math.floor(Math.random() * (i + 1));
                [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
            }
            return shuffled;
        }
        
        async function initPosters() {
            const movies = await fetchMovies();
            const posterRows = document.getElementById('posterRows');
            
            for (let i = 0; i < 3; i++) {
                const row = document.createElement('div');
                row.className = 'poster-row';
                const shuffledMovies = shuffleArray(movies);
                
                for (let j = 0; j < 24; j++) {
                    const movie = shuffledMovies[j % shuffledMovies.length];
                    const poster = document.createElement('div');
                    poster.className = 'poster';
                    if (movie && movie.poster_path) {
                        poster.style.backgroundImage = 'url(' + IMAGE_BASE + movie.poster_path + ')';
                    }
                    row.appendChild(poster);
                }
                
                posterRows.appendChild(row);
            }
        }
        
        initPosters();
    </script>
</body>
</html>