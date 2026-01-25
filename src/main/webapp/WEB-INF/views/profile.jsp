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
        .profile-header { background: #fff; padding: 30px; border-radius: 5px; box-shadow: 0 1px 3px rgba(0,0,0,.1); margin-bottom: 20px; }
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
		.spoiler-wrapper {
		    position: relative;
		    cursor: pointer;
		}
		.spoiler-content {
		    filter: blur(4px);
		    user-select: none;
		    transition: filter 0.3s ease;
		    background-color: #fff;
		}
		.spoiler-visible {
		    filter: none;
		    user-select: text;
		    background-color: transparent;
		}
		.spoiler-overlay-label {
		    position: absolute;
		    top: 50%;
		    left: 50%;
		    transform: translate(-50%, -50%);
		    background: #333; /*#d9534f;*/
		    color: white;
		    padding: 5px 15px;
		    border-radius: 20px;
		    font-size: 12px;
		    font-weight: bold;
		    pointer-events: none;
		    box-shadow: 0 2px 5px rgba(0,0,0,0.2);
		    z-index: 10;
		}
		.spoiler-trigger {
		    display: none;
		}
		.spoiler-trigger:checked + .spoiler-wrapper .spoiler-content {
		    filter: none;
		    user-select: text;
		}
		.spoiler-trigger:checked + .spoiler-wrapper .spoiler-overlay-label {
		    display: none;
		}
		.spoiler-badge {
		    padding: 4px 12px;
		    border-radius: 12px;
		    font-size: 11px;
		    font-weight: 600;
		    display: inline-block;
		    margin-left: 10px;
		    vertical-align: middle;
		}
		.spoiler-yes {
		    background: #FFE5E5;
		    color: #D32F2F;
		    border: 1px solid #ffcccc;
		}
        .stat-item:hover { background-color: #f9f9f9; border-radius: 5px; }
        .modal-body ul { padding: 0; list-style: none; }
    </style>
</head>
<body>
<div class="container profile-container">

    <c:if test="${not empty sessionScope.flashMessage}">
        <div class="alert alert-${empty sessionScope.flashType ? 'info' : sessionScope.flashType} alert-dismissible" role="alert">
            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            ${sessionScope.flashMessage}
        </div>
        <c:remove var="flashMessage" scope="session"/>
        <c:remove var="flashType" scope="session"/>
    </c:if>

    <a href="<%= request.getContextPath() %>/home" class="btn btn-primary" style="margin-bottom: 20px; background-color: #8B7355; border-color: #8B7355;">
        <i class="glyphicon glyphicon-arrow-left"></i> Volver
    </a>
    
    <div class="profile-header">
        <h1><i class="glyphicon glyphicon-user"></i> ${user.username}</h1>
        <p style="color: #666; margin: 0;">${user.email}</p>

        <c:if test="${!isMyProfile}">
            <div style="margin-top: 15px; border-top: 1px solid #eee; padding-top: 15px;">
                <form action="<%= request.getContextPath() %>/follow" method="POST">
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
            </div>
        </c:if>
    </div>
    
    <div class="stats-box">
        <h3>Estadísticas</h3>
        <div class="row text-center" style="margin-top: 20px;">
            <div class="col-xs-4">
                <span style="font-size: 24px; font-weight: bold; display: block; color: #8B7355;">${totalReviews}</span>
                <span style="color: #666; font-size: 12px; text-transform: uppercase; letter-spacing: 1px;">Reseñas</span>
            </div>
            
            <div class="col-xs-4 stat-item" style="cursor: pointer;" data-toggle="modal" data-target="#followersModal">
                <span style="font-size: 24px; font-weight: bold; display: block; color: #8B7355;">${fn:length(followers)}</span>
                <span style="color: #666; font-size: 12px; text-transform: uppercase; letter-spacing: 1px;">Seguidores</span>
            </div>
            
            <div class="col-xs-4 stat-item" style="cursor: pointer;" data-toggle="modal" data-target="#followingModal">
                <span style="font-size: 24px; font-weight: bold; display: block; color: #8B7355;">${fn:length(following)}</span>
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
                            <p class="text-center text-muted" style="padding: 10px;">No sigue a nadie aún.</p>
                        </c:if>
                    </ul>
                </div>
            </div>
        </div>
    </div>

</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

</body>
</html>