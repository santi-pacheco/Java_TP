<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="entity.Movie" %>
<%

    List<Movie> originalList = (List<Movie>) request.getAttribute("movies");
    List<String> fastLoadUrls = new ArrayList<>();
    
    if (originalList != null && !originalList.isEmpty()) {
        int limit = Math.min(72, originalList.size()); 
        for (int i = 0; i < limit; i++) {
            String posterPath = originalList.get(i).getPosterPath();
            if (posterPath != null && !posterPath.trim().isEmpty()) {
                fastLoadUrls.add("https://image.tmdb.org/t/p/w185" + posterPath);
            }
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fat Movies</title>
    
    <% for(String url : fastLoadUrls) { %>
        <link rel="preload" as="image" href="<%= url %>">
    <% } %>
    
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
        

        .splash-screen {
            position: fixed;
            top: 0;
            left: 0;
            width: 100vw;
            height: 100vh;
            background-color: #FAF8F3;
            z-index: 9999;
            display: flex;
            justify-content: center;
            align-items: center;
            animation: hideSplash 0.5s ease-in-out 1.5s forwards;
        }
        
        .splash-logo {
            width: 150px;
            animation: pulseSplash 1s infinite alternate;
        }
        
        @keyframes hideSplash {
            0% { opacity: 1; visibility: visible; }
            100% { opacity: 0; visibility: hidden; }
        }
        
        @keyframes pulseSplash {
            0% { transform: scale(1); opacity: 0.8; }
            100% { transform: scale(1.1); opacity: 1; }
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
        
        .row-wrapper {
            position: absolute;
            width: 100%;
            opacity: 0; 
        }
        
        .intro-from-right {
            animation: slideInFromRight 2s cubic-bezier(0.16, 1, 0.3, 1) 1.5s forwards;
        }
        
        .intro-from-left {
            animation: slideInFromLeft 2s cubic-bezier(0.16, 1, 0.3, 1) 1.8s forwards;
        }
        
        .intro-from-right-late {
            animation: slideInFromRight 2s cubic-bezier(0.16, 1, 0.3, 1) 2.1s forwards;
        }
        
        .poster-row {
            display: flex;
            gap: 20px;
            width: max-content;
            will-change: transform;
        }
        
        .scroll-left { animation: scrollLeft 60s linear infinite; }
        .scroll-right { animation: scrollRight 80s linear infinite; }
        .scroll-left-slow { animation: scrollLeft 100s linear infinite; }
        
        .poster {
            width: 150px;
            height: 225px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
            flex-shrink: 0;
            background-size: cover;
            background-position: center;
            background-color: transparent; 
        }
        
        @keyframes slideInFromRight {
            0% { transform: translateX(300px); opacity: 0; }
            100% { transform: translateX(0); opacity: 1; }
        }
        
        @keyframes slideInFromLeft {
            0% { transform: translateX(-300px); opacity: 0; }
            100% { transform: translateX(0); opacity: 1; }
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
    <div class="splash-screen">
        <img src="utils/export50.svg" alt="Cargando FatMovies..." class="splash-logo">
    </div>

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
    
    <div class="poster-rows">
        <%
            if (!fastLoadUrls.isEmpty()) {
                int totalMovies = fastLoadUrls.size();
                
                String[] wrapperClasses = {"intro-from-right", "intro-from-left", "intro-from-right-late"};
                String[] rowClasses = {"scroll-left", "scroll-right", "scroll-left-slow"};
                String[] topPositions = {"20%", "45%", "70%"};
                
                int globalIdx = 0; 
                
                for (int i = 0; i < 3; i++) {
                    out.print("<div class=\"row-wrapper " + wrapperClasses[i] + "\" style=\"top: " + topPositions[i] + ";\">");
                    out.print("<div class=\"poster-row " + rowClasses[i] + "\">");
                    
                    for (int j = 0; j < 24; j++) {
                        String bgUrl = fastLoadUrls.get(globalIdx % totalMovies);
                        out.print("<div class=\"poster\" style=\"background-image: url('" + bgUrl + "');\"></div>");
                        
                        globalIdx++; 
                    }
                    
                    out.print("</div></div>");
                }
            }
        %>
    </div>
</body>
</html>