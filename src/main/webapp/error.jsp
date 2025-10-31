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
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style>
        body { background: #FAF8F3; font-family: 'Poppins', sans-serif; }
        .error-container {
            margin: 100px auto;
            max-width: 800px;
            text-align: center;
            padding: 50px;
            background: #fff;
            border-radius: 20px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        .error-container h1 {
            color: #d9534f;
            margin-bottom: 10px;
            font-size: 3rem;
            font-weight: 700;
        }
        .error-container p {
            font-size: 18px;
            margin-bottom: 10px;
        }
        .error-code {
            font-size: 28px;
            color: #777;
            margin: 10px 0;
        }
        .alert {
            font-size: 18px;
            margin: 10px 0;
        }
        .btn {
            font-size: 18px;
            padding: 12px 30px;
            margin-top: 10px;
        }
        .btn-primary {
            background-color: #8B7355;
            border-color: #8B7355;
        }
        .btn-primary:hover {
            background-color: #6F5B45;
            border-color: #6F5B45;
        }
    </style>
</head>
<body>
    <div class="container error-container">
        <img src="utils/error.svg" alt="Error" style="width: 300px; height: 300px; margin-bottom: 0px; filter: invert(27%) sepia(51%) saturate(2878%) hue-rotate(346deg) brightness(104%) contrast(97%);">
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
        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
            Volver a la página principal
        </a>
    </div>
</body>
</html>