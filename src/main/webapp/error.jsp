<%--
  Fichero: /webapp/error.jsp
  Propósito: Página de error genérica. Es llamada por el GlobalErrorHandlerFilter.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" 
    pageEncoding="UTF-8" isErrorPage="true" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style>
        body { background: #f5f5f5; font-family: 'Varela Round', sans-serif; }
        .error-container {
            margin: 100px auto;
            max-width: 600px;
            text-align: center;
            padding: 30px;
            background: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .error-container h1 {
            color: #d9534f; /* Color rojo de Bootstrap 'danger' */
            margin-bottom: 20px;
        }
        .error-code {
            font-size: 24px;
            color: #777;
        }
    </style>
</head>
<body>
    <div class="container error-container">
        <span class="glyphicon glyphicon-fire" style="font-size: 48px; color: #d9534f;"></span>
        <h1>¡Ups! Algo salió mal</h1>
        
        <p>Lo sentimos, el servidor encontró un error y no pudo completar tu solicitud.</p>
        
        <%-- 
          Aquí leemos los atributos que el GlobalErrorHandlerFilter puso 
          en el request.
          Usamos "requestScope" para ser explícitos.
        --%>
        <div class="alert alert-danger">
            <strong>Mensaje:</strong> 
            ${requestScope.errorMessage}
        </div>
        
        <div class="error-code">
            Código de estado: ${requestScope.statusCode}
        </div>
        
        <br>
        
        <%-- 
          Un enlace para volver al inicio. 
          ${pageContext.request.contextPath} es la forma segura de 
          obtener la URL raíz de tu aplicación. 
        --%>
        <a href="${pageContext.request.contextPath}/index.html" class="btn btn-primary">
            Volver a la página principal
        </a>
    </div>
</body>
</html>