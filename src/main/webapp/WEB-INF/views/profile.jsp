<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Perfil - FatMovies</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style>
        body { background: #FAF8F3; font-family: 'Poppins', sans-serif; }
        .profile-container { max-width: 1000px; margin: 30px auto; }
        .profile-header { background: #fff; padding: 30px; border-radius: 5px; box-shadow: 0 1px 3px rgba(0,0,0,.1); margin-bottom: 20px; text-align: center; }
        .profile-header h1 { margin: 0 0 10px 0; color: #333; }
        .stats-box { background: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 1px 3px rgba(0,0,0,.1); margin-bottom: 20px; }
        .chart-container { background: #fff; padding: 20px 20px 50px 20px; border-radius: 5px; box-shadow: 0 1px 3px rgba(0,0,0,.1); margin-bottom: 20px; }
        .bar-chart { display: flex; align-items: flex-end; height: 250px; gap: 10px; margin-top: 40px; padding: 0 10px; position: relative; }
        .bar { flex: 1; background: linear-gradient(180deg, #a08570 0%, #8B7355 100%); border-radius: 8px 8px 0 0; position: relative; transition: all 0.4s ease; box-shadow: 0 2px 4px rgba(0,0,0,0.1); min-height: 5px; }
        .bar:hover { background: linear-gradient(180deg, #8B7355 0%, #6d5a43 100%); transform: translateY(-3px); box-shadow: 0 4px 8px rgba(0,0,0,0.15); }
        .bar-label { position: absolute; bottom: -30px; width: 100%; text-align: center; font-weight: 500; font-size: 13px; color: #666; }
        .bar-value { position: absolute; top: -30px; width: 100%; text-align: center; font-weight: 600; color: #8B7355; font-size: 14px; }
        .reviews-list { background: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 1px 3px rgba(0,0,0,.1); }
        .review-item { border-bottom: 1px solid #eee; padding: 15px 0; }
        .review-item:last-child { border-bottom: none; }
        .rating-stars { color: #f39c12; }
        .spoiler-wrapper { position: relative; cursor: pointer; }
        .spoiler-content { filter: blur(4px); user-select: none; transition: filter 0.3s ease; background-color: #fff; }
        .spoiler-visible { filter: none; user-select: text; background-color: transparent; }
        .spoiler-overlay-label { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: #333; color: white; padding: 5px 15px; border-radius: 20px; font-size: 12px; font-weight: bold; pointer-events: none; box-shadow: 0 2px 5px rgba(0,0,0,0.2); z-index: 10; }
        .spoiler-trigger { display: none; }
        .spoiler-trigger:checked + .spoiler-wrapper .spoiler-content { filter: none; user-select: text; }
        .spoiler-trigger:checked + .spoiler-wrapper .spoiler-overlay-label { display: none; }
        .spoiler-badge { padding: 4px 12px; border-radius: 12px; font-size: 11px; font-weight: 600; display: inline-block; margin-left: 10px; vertical-align: middle; }
        .spoiler-yes { background: #FFE5E5; color: #D32F2F; border: 1px solid #ffcccc; }
        .stat-item:hover { background-color: #f9f9f9; border-radius: 5px; }
        .modal-body ul { padding: 0; list-style: none; }
        
        .profile-avatar-container {
            position: relative;
            width: 150px;
            height: 150px;
            margin: 0 auto 15px auto;
            border-radius: 50%;
            overflow: hidden;
            border: 4px solid #fff;
            box-shadow: 0 4px 10px rgba(0,0,0,0.15);
            background-color: #eee;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        /* --- AVATAR NIVEL 3/4: LA HAMBURGUESA INFALIBLE (SIN EMOJI) --- */
        .burger-avatar-border {
            border-radius: 50% !important;
            padding: 8px;
            background: linear-gradient(180deg, 
                #F5B041 0%, #F5B041 30%,   
                #58D68D 30%, #58D68D 40%,   
                #873600 40%, #873600 70%,   
                #F4D03F 70%, #F4D03F 100%   
            ) !important;
            box-shadow: 0 6px 15px rgba(0,0,0,0.2) !important;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            position: relative;
            border: none !important;
        }

        .burger-avatar-border .profile-avatar {
            border-radius: 50% !important;
            border: 4px solid #FFF !important; 
            position: relative;
            z-index: 2;
        }

        .profile-avatar {
            width: 100%;
            height: 100%;
            object-fit: cover;
            border-radius: 50%;
        }
        
        .avatar-overlay {
            position: absolute;
            bottom: 0;
            left: 0;
            width: 100%;
            height: 40px;
            background: rgba(0,0,0,0.7);
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 15px;
            opacity: 0;
            transition: opacity 0.3s;
            z-index: 3;
            border-bottom-left-radius: 150px;
            border-bottom-right-radius: 150px;
        }
        .profile-avatar-container:hover .avatar-overlay {
            opacity: 1;
        }
        .overlay-btn {
            color: white;
            font-size: 18px;
            cursor: pointer;
            background: none;
            border: none;
            padding: 0;
            transition: color 0.2s;
        }
        .overlay-btn:hover {
            color: #ddd;
        }
        .overlay-btn-danger:hover {
            color: #ff6b6b;
        }

        #loadingOverlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(250, 248, 243, 0.9); 
            z-index: 1050; 
            display: none; 
            flex-direction: column;
            justify-content: center;
            align-items: center;
            text-align: center;
        }

        .spinner {
            width: 50px;
            height: 50px;
            border: 5px solid rgba(139, 115, 85, 0.3); 
            border-radius: 50%;
            border-top-color: #8B7355; 
            animation: spin 1s ease-in-out infinite;
            margin-bottom: 20px;
        }

        @keyframes spin {
            to { transform: rotate(360deg); }
        }

        .loading-text {
            color: #333;
            font-size: 1.2rem;
            font-weight: 600;
        }

        .loading-subtext {
            color: #666;
            font-size: 0.9rem;
            margin-top: 5px;
        }
    </style>
</head>
<body>

<div id="loadingOverlay">
    <div class="spinner"></div>
    <div class="loading-text">Subiendo y analizando imagen...</div>
    <div class="loading-subtext">Nuestra IA está verificando que el contenido sea apropiado.<br>Esto puede tardar unos segundos, por favor no cierres la página.</div>
</div>

<div class="container profile-container">

    <c:if test="${not empty sessionScope.flashMessage}">
        <div class="alert alert-${empty sessionScope.flashType ? 'info' : sessionScope.flashType} alert-dismissible" role="alert">
            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            ${sessionScope.flashMessage}
        </div>
        <c:remove var="flashMessage" scope="session"/>
        <c:remove var="flashType" scope="session"/>
    </c:if>

    <button onclick="volverInteligente()" class="btn btn-primary" style="margin-bottom: 20px; background-color: #8B7355; border-color: #8B7355; outline: none;">
        <i class="glyphicon glyphicon-arrow-left"></i> Volver
    </button>
    
    <div class="profile-header">  
        <div class="profile-avatar-container ${userLevel >= 3 ? 'burger-avatar-border' : ''}">
            <% 
                entity.User uProfile = (entity.User) request.getAttribute("user");
                String pImage = uProfile.getProfileImage();
                String displayImg = (pImage != null && !pImage.isEmpty()) 
                        ? request.getContextPath() + "/uploads/" + pImage 
                        : request.getContextPath() + "/utils/default_profile.png";
                
                boolean hasPhoto = (pImage != null && !pImage.isEmpty());
            %>
            <img src="<%= displayImg %>" alt="Foto de perfil" class="profile-avatar">
            
            <c:if test="${isMyProfile}">
                <div class="avatar-overlay">
                    <button type="button" class="overlay-btn" title="Cambiar foto" data-toggle="modal" data-target="#uploadPhotoModal">
                        <span class="glyphicon glyphicon-camera"></span>
                    </button>

                    <% if (hasPhoto) { %>
                        <form action="<%= request.getContextPath() %>/profile-image?accion=eliminar" method="post" style="display:inline;">
                            <button type="submit" class="overlay-btn overlay-btn-danger" title="Eliminar foto" onclick="return confirm('¿Seguro que quieres quitar tu foto?');">
                                <span class="glyphicon glyphicon-trash"></span>
                            </button>
                        </form>
                    <% } %>
                </div>
            </c:if>
        </div>
        
        <h1><i class="glyphicon glyphicon-user"></i> ${user.username}</h1>
        <p style="color: #666; margin: 0;">${user.email}</p>

        <c:choose>
            <c:when test="${!isMyProfile}">
                <div style="margin-top: 15px; border-top: 1px solid #eee; padding-top: 15px; display: flex; justify-content: center; gap: 10px;">
                    <form action="<%= request.getContextPath() %>/follow" method="POST" style="margin: 0;">
                        <input type="hidden" name="idUsuario" value="${user.id}">
                        <c:choose>
                            <c:when test="${isFollowing}">
                                <button type="submit" class="btn btn-danger">
                                    <i class="glyphicon glyphicon-remove-circle"></i> Dejar de Seguir
                                </button>
                            </c:when>
                            <c:otherwise>
                                <button type="submit" class="btn btn-primary" style="background-color: #8B7355; border-color: #8B7355;">
                                    <i class="glyphicon glyphicon-heart"></i> Seguir
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </form>
                    <button type="button" class="btn btn-default" onclick="toggleBlock(${user.id}, true)" style="color: #d9534f; border-color: #d4cfc7;">
                        <i class="glyphicon glyphicon-ban-circle"></i> Bloquear
                    </button>
                </div>
            </c:when>
            <c:otherwise>
                <div style="margin-top: 15px;">
                    <button type="button" class="btn btn-default btn-sm" data-toggle="modal" data-target="#blockedUsersModal" style="color: #666;">
                        <i class="glyphicon glyphicon-ban-circle"></i> Usuarios Bloqueados
                    </button>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="stats-box" style="text-align: center; position: relative;">
        <h3 style="margin-bottom: 20px;">Nivel de Apetito: <span style="color: #8B7355;">Nivel ${userLevel}</span></h3>
        
        <div style="display: flex; flex-direction: column; align-items: center; margin: 10px 0 20px 0;">
            
            <div style="height: 140px; width: 100%; display: flex; align-items: flex-end; justify-content: center; margin-bottom: 15px;">
                <img src="${pageContext.request.contextPath}/utils/level${userLevel}.svg" 
                     alt="Personaje Nivel ${userLevel}" 
                     style="max-height: 100%; width: auto; object-fit: contain; filter: drop-shadow(0px 4px 6px rgba(0,0,0,0.1));" 
                     onerror="this.style.display='none'">
            </div>

            <div class="progress" style="height: 30px; border-radius: 20px; background-color: #e9ecef; width: 80%; box-shadow: inset 0 2px 5px rgba(0,0,0,.05);">
                <div class="progress-bar progress-bar-striped active" role="progressbar" 
                     style="width: ${progressPercentage}%; background-color: #8B7355; line-height: 30px; font-size: 15px; font-weight: 600; transition: width 1s ease-in-out;">
                    <c:choose>
                        <c:when test="${userLevel == 4}">¡Apetito Máximo!</c:when>
                        <c:otherwise>${userKcals} / ${nextLevelMax} Kcals</c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        
        <p style="font-size: 13px; color: #777;">
            <i class="glyphicon glyphicon-info-sign"></i> ¡Cada Like que reciban tus reseñas te suma 500 Kcals!
        </p>
    </div>

    <c:if test="${userLevel >= 2}">
        <div class="stats-box" style="margin-top: 20px; text-align: center; border: 2px dashed #8B7355; background: #faf8f3; padding: 30px;">
            <h3 style="color: #8B7355; margin-bottom: 15px;">🍽️ Plato Principal</h3>
            
            <c:choose>
                <c:when test="${not empty platoPrincipalMovie}">
                    <div style="display: flex; flex-direction: column; align-items: center; gap: 15px;">
                        <a href="${pageContext.request.contextPath}/movie/${platoPrincipalMovie.id}">
                            <img src="https://image.tmdb.org/t/p/w300${platoPrincipalMovie.posterPath}" alt="${platoPrincipalMovie.titulo}" style="width: 150px; border-radius: 10px; box-shadow: 0 5px 15px rgba(0,0,0,0.2); transition: transform 0.3s;" onmouseover="this.style.transform='scale(1.05)'" onmouseout="this.style.transform='scale(1)'" onerror="this.src='${pageContext.request.contextPath}/utils/no-poster.png'">
                        </a>
                        <h4 style="margin: 0; font-weight: 600; color: #333;">${platoPrincipalMovie.titulo}</h4>
                        
                        <c:if test="${isMyProfile}">
                            <div style="display: flex; gap: 10px; justify-content: center; margin-top: 10px;">
                                <button class="btn btn-default" style="border-color: #8B7355; color: #8B7355; font-size: 0.85rem;" data-toggle="modal" data-target="#platoPrincipalModal">Cambiar Plato</button>
                                <form action="${pageContext.request.contextPath}/plato-principal" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="remove">
                                    <button type="submit" class="btn btn-danger" style="font-size: 0.85rem;" onclick="return confirm('¿Seguro que quieres quitar tu Plato Principal?');">Quitar</button>
                                </form>
                            </div>
                        </c:if>
                    </div>
                </c:when>
                <c:otherwise>
                    <p style="color: #666; font-size: 0.95rem; margin-bottom: 20px;">Espacio reservado para destacar tu película favorita de todos los tiempos.</p>
                    <c:if test="${isMyProfile}">
                        <button class="btn btn-primary" style="background-color: #8B7355; border-color: #8B7355; padding: 10px 25px; font-weight: 600;" data-toggle="modal" data-target="#platoPrincipalModal">Elegir Plato Principal</button>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>
    
    <div class="stats-box">
        <h3>Estadísticas</h3>
        <div class="row text-center" style="margin-top: 20px;">
            <div class="col-xs-4">
                <span style="font-size: 24px; font-weight: bold; display: block; color: #8B7355;">${totalReviews}</span>
                <span style="color: #666; font-size: 12px; text-transform: uppercase; letter-spacing: 1px;">Reseñas</span>
            </div>
            
            <div class="col-xs-4 stat-item" style="cursor: pointer;" data-toggle="modal" data-target="#followersModal">
                <span style="font-size: 24px; font-weight: bold; display: block; color: #8B7355;">${realFollowersCount}</span>
                <span style="color: #666; font-size: 12px; text-transform: uppercase; letter-spacing: 1px;">Seguidores</span>
            </div>
            
            <div class="col-xs-4 stat-item" style="cursor: pointer;" data-toggle="modal" data-target="#followingModal">
                <span style="font-size: 24px; font-weight: bold; display: block; color: #8B7355;">${realFollowingCount}</span>
                <span style="color: #666; font-size: 12px; text-transform: uppercase; letter-spacing: 1px;">Seguidos</span>
            </div>
        </div>
    </div>
    
    <div class="chart-container">
        <h3>Distribución de Calificaciones</h3>
        <div class="bar-chart">
            <c:set var="ratings" value="0.5,1.0,1.5,2.0,2.5,3.0,3.5,4.0,4.5,5.0" />
            <c:forEach var="rating" items="${fn:split(ratings, ',')}">
                <c:set var="count" value="${ratingDistribution[rating]}" />
                <c:if test="${empty count}"><c:set var="count" value="0" /></c:if>
                <c:set var="maxHeight" value="220" />
                <c:choose>
                    <c:when test="${totalReviews > 0}">
                        <c:set var="height" value="${(count * maxHeight) / totalReviews}" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="height" value="5" />
                    </c:otherwise>
                </c:choose>
                <div class="bar" style="height: ${height}px;">
                    <c:if test="${count > 0}">
                        <div class="bar-value">${count}</div>
                    </c:if>
                    <div class="bar-label">${rating} ★</div>
                </div>
            </c:forEach>
        </div>
    </div>
    
    <div class="reviews-list">
        <h3>Últimas Reseñas</h3>
        <c:choose>
            <c:when test="${empty recentReviews}">
                <p style="color: #999; text-align: center; padding: 20px;">No has escrito reseñas aún.</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="review" items="${recentReviews}">
                    <div class="review-item">
                        <div style="display: flex; justify-content: space-between; align-items: center;">
                            <div>
                                <a href="<%= request.getContextPath() %>/movie/${review.id_movie}" style="text-decoration: none; color: #8B7355; font-weight: 600; font-size: 16px;">
                                    <c:choose>
                                        <c:when test="${not empty review.movieTitle}">
                                            ${review.movieTitle}
                                        </c:when>
                                        <c:otherwise>
                                            Película ID: ${review.id_movie}
                                        </c:otherwise>
                                    </c:choose>
                                </a>
                                <div class="rating-stars" style="margin-top: 5px;">
                                    <c:forEach begin="1" end="${review.rating}">★</c:forEach>
                                    <c:forEach begin="${review.rating + 1}" end="5">☆</c:forEach>
                                    <c:if test="${review.moderationStatus == 'SPOILER'}">
                                        <span class="spoiler-badge spoiler-yes">Contiene Spoiler</span>
                                    </c:if>
                                </div>
                            </div>
                            <small style="color: #999;">${review.created_at}</small>
                        </div>
                        
                        <c:choose>
                            <c:when test="${review.moderationStatus == 'SPOILER'}">
                                <input type="checkbox" id="spoiler-check-${review.id}" class="spoiler-trigger">
                                <label for="spoiler-check-${review.id}" class="spoiler-wrapper" style="display: block; margin: 10px 0 0 0;">
                                    <div class="spoiler-overlay-label">Mostrar reseña</div>
                                    <p class="spoiler-content" style="margin: 0; color: #666;">
                                        ${review.review_text}
                                    </p>
                                </label>
                            </c:when>
                            <c:otherwise>
                                <p style="margin: 10px 0 0 0; color: #666;">${review.review_text}</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="modal fade" id="followersModal" tabindex="-1" role="dialog">
        <div class="modal-dialog modal-sm" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Seguidores</h4>
                </div>
                <div class="modal-body" style="max-height: 400px; overflow-y: auto;">
                    <ul class="list-group">
                        <c:forEach var="f" items="${followers}">
                            <li class="list-group-item">
                                <a href="<%= request.getContextPath() %>/profile?id=${f.id}" style="color: #333; font-weight: 600; text-decoration: none; display: block;">
                                    <i class="glyphicon glyphicon-user"></i> ${f.username}
                                </a>
                            </li>
                        </c:forEach>
                        <c:if test="${empty followers}">
                            <p class="text-center text-muted" style="padding: 10px;">Aún no tiene seguidores.</p>
                        </c:if>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    
    <div class="modal fade" id="blockedUsersModal" tabindex="-1" role="dialog">
        <div class="modal-dialog modal-sm" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Usuarios Bloqueados</h4>
                </div>
                <div class="modal-body" style="max-height: 400px; overflow-y: auto;">
                    <ul class="list-group">
                        <c:forEach var="b" items="${blockedUsers}">
                            <li class="list-group-item" style="display: flex; justify-content: space-between; align-items: center;">
                                <span><i class="glyphicon glyphicon-user" style="color: #999;"></i> ${b.username}</span>
                                <button class="btn btn-xs btn-default" onclick="toggleBlock(${b.id}, false)">Desbloquear</button>
                            </li>
                        </c:forEach>
                        <c:if test="${empty blockedUsers}">
                            <p class="text-center text-muted" style="padding: 10px; margin:0;">No tienes usuarios bloqueados.</p>
                        </c:if>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="followingModal" tabindex="-1" role="dialog">
        <div class="modal-dialog modal-sm" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Siguiendo</h4>
                </div>
                <div class="modal-body" style="max-height: 400px; overflow-y: auto;">
                    <ul class="list-group">
                        <c:forEach var="f" items="${following}">
                            <li class="list-group-item">
                                <a href="<%= request.getContextPath() %>/profile?id=${f.id}" style="color: #333; font-weight: 600; text-decoration: none; display: block;">
                                    <i class="glyphicon glyphicon-user"></i> ${f.username}
                                </a>
                            </li>
                        </c:forEach>
                        <c:if test="${empty following}">
                            <p class="text-center text-muted" style="padding: 10px;">Aún no sigue a nadie.</p>
                        </c:if>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="uploadPhotoModal" tabindex="-1" role="dialog">
        <div class="modal-dialog modal-sm" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Cambiar Foto de Perfil</h4>
                </div>
                
                <form id="uploadPhotoForm" action="<%= request.getContextPath() %>/profile-image?accion=subir" method="post" enctype="multipart/form-data">
                    <div class="modal-body">
                        <div class="form-group">
                            <label>Selecciona una imagen (Máx 10MB)</label>
                            <input type="file" name="photo" class="form-control" accept="image/*" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-primary" style="background-color: #8B7355; border:none;" id="btnSubmitUpload">Guardar Foto</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <c:if test="${isMyProfile}">
        <div class="modal fade" id="platoPrincipalModal" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                        <h4 class="modal-title">Buscar Película</h4>
                    </div>
                    <div class="modal-body">
                        <input type="text" id="platoSearchInput" class="form-control" placeholder="Escribe el nombre de la película..." autocomplete="off">
                        <div id="platoSearchResults" style="margin-top: 15px; max-height: 400px; overflow-y: auto;">
                        </div>
                        <form id="setPlatoForm" action="${pageContext.request.contextPath}/plato-principal" method="post" style="display:none;">
                            <input type="hidden" name="action" value="set">
                            <input type="hidden" name="movieId" id="platoMovieId">
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </c:if>

</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const uploadForm = document.getElementById('uploadPhotoForm');
        const loadingOverlay = document.getElementById('loadingOverlay');
        const btnSubmit = document.getElementById('btnSubmitUpload');

        if (uploadForm) {
            uploadForm.addEventListener('submit', function(event) {
                const fileInput = uploadForm.querySelector('input[type="file"]');
                if (fileInput && fileInput.files.length > 0) {
                    $('#uploadPhotoModal').modal('hide');
                    loadingOverlay.style.display = 'flex';
                    btnSubmit.disabled = true;
                }
            });
        }
        
        const platoInput = document.getElementById('platoSearchInput');
        const platoResults = document.getElementById('platoSearchResults');
        let platoTimeout;

        if (platoInput) {
            platoInput.addEventListener('input', function() {
                clearTimeout(platoTimeout);
                const query = this.value.trim();
                
                if (query.length < 2) {
                    platoResults.innerHTML = '';
                    return;
                }
                
                platoTimeout = setTimeout(function() {
                    fetch('${pageContext.request.contextPath}/search-api?q=' + encodeURIComponent(query))
                        .then(res => res.json())
                        .then(movies => {
                            if (movies.length === 0) {
                                platoResults.innerHTML = '<p class="text-center text-muted">No se encontraron películas.</p>';
                                return;
                            }
                            
                            let html = '<div class="list-group">';
                            movies.forEach(m => {
                                const poster = m.posterPath ? 'https://image.tmdb.org/t/p/w92' + m.posterPath : '${pageContext.request.contextPath}/utils/no-poster.png';
                                const year = m.estrenoYear ? m.estrenoYear : '';
                                html += '<a href="javascript:void(0)" class="list-group-item" onclick="selectPlatoPrincipal(' + m.id + ')" style="display:flex; align-items:center; gap:15px;">' +
                                            '<img src="' + poster + '" style="width: 40px; height: 60px; object-fit: cover; border-radius: 4px;">' +
                                            '<div>' +
                                                '<h5 style="margin:0; font-weight:600;">' + m.titulo + '</h5>' +
                                                '<small class="text-muted">' + year + '</small>' +
                                            '</div>' +
                                        '</a>';
                            });
                            html += '</div>';
                            platoResults.innerHTML = html;
                        })
                        .catch(err => console.error(err));
                }, 300);
            });
        }
    });

    function selectPlatoPrincipal(id) {
        document.getElementById('platoMovieId').value = id;
        document.getElementById('setPlatoForm').submit();
    }
    
    // --- LÓGICA DEL BOTÓN VOLVER INTELIGENTE ---
    document.addEventListener('DOMContentLoaded', function() {
        let retrovisor = document.referrer;
        if (retrovisor && !retrovisor.includes('/profile') && !retrovisor.includes('/profile-image')) {
            sessionStorage.setItem('rutaOriginal', retrovisor);
        }
    });

    function volverInteligente() {
        let rutaGuardada = sessionStorage.getItem('rutaOriginal');
        if (rutaGuardada) {
            window.location.href = rutaGuardada;
        } else {
            window.location.href = '<%= request.getContextPath() %>/home';
        }
    }
    
    // --- LÓGICA DE BLOQUEAR USUARIO ---
    function toggleBlock(targetId, isBlockingAction) {
        let mensaje = isBlockingAction ? '¿Estás seguro de que deseas bloquear a este usuario? Dejarás de ver sus reseñas y él no podrá ver tu perfil.' : '¿Deseas desbloquear a este usuario?';
        
        if (confirm(mensaje)) {
            $.ajax({
                url: '<%= request.getContextPath() %>/block',
                type: 'POST',
                data: { targetId: targetId },
                success: function(response) {
                    if(response.success) {
                        if (isBlockingAction) {
                            window.location.href = '<%= request.getContextPath() %>/home';
                        } else {
                            location.reload();
                        }
                    }
                },
                error: function() {
                    alert('Hubo un error al procesar la solicitud.');
                }
            });
        }
    }
</script>

</body>
</html>