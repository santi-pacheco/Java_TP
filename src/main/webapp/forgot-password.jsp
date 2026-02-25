<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8" />
  <title>Recuperar Contraseña — FatMovies</title>
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  <style>
    body { font-family: 'Poppins', sans-serif; display:flex; flex-direction:column; min-height:100vh; background:#FAF8F3; margin:0; }
    .content { flex:1; display:flex; align-items:center; justify-content:center; padding:40px 20px; }
    .card { background:white; padding:40px; border-radius:15px; box-shadow:0 8px 30px rgba(0,0,0,0.12); width:400px; text-align: center; }
    .logo-container { margin-bottom:20px; }
    .logo { width:120px; height:90px; object-fit:cover; transform:scale(1.5); }
    h1 { margin:0 0 10px 0; font-size:24px; font-weight:700; color:#333; }
    p { font-size: 14px; color: #666; margin-bottom: 24px; line-height: 1.5; }
    .field { margin-bottom:16px; text-align: left; }
    label { display:block; margin-bottom:8px; font-size:14px; font-weight:500; color:#333; }
    input[type="email"] { width:100%; padding:12px 16px; border:2px solid #E0E0E0; border-radius:8px; box-sizing:border-box; font-family:'Poppins',sans-serif; font-size:14px; transition:border-color 0.3s; }
    input:focus { outline:none; border-color:#8B7355; }
    .btn { width:100%; padding:14px; border:none; border-radius:8px; background:#8B7355; color:white; font-weight:600; font-size:16px; cursor:pointer; margin-top:8px; transition:background 0.3s; }
    .btn:hover { background:#6b5840; }
    .err { background:#ffe6e6; color:#a80000; padding:12px; border-radius:8px; margin-bottom:16px; font-size:14px; text-align: left; }
    .ok { background:#e6ffec; color:#116611; padding:12px; border-radius:8px; margin-bottom:16px; font-size:14px; text-align: left; }
    .links { margin-top:20px; }
    a { color:#8B7355; text-decoration:none; font-weight:600; font-size: 14px; }
    a:hover { text-decoration:underline; }
  </style>
</head>
<body>
  <%@ include file="/WEB-INF/components/navbar-new.jsp" %>
  
  <div class="content">
    <div class="card">
      <div class="logo-container">
        <img src="${pageContext.request.contextPath}/utils/export50.svg" alt="Fat Movies" class="logo">
      </div>
      <h1>¿Olvidaste tu contraseña?</h1>
      <p>Ingresa el correo electrónico asociado a tu cuenta y te enviaremos las instrucciones para restablecerla.</p>

      <c:if test="${param.success == 'true'}">
        <div class="ok">✔️ Si el correo está registrado, te hemos enviado un enlace para restablecer tu contraseña.</div>
      </c:if>

      <c:if test="${param.error == 'true'}">
        <div class="err">❌ Ocurrió un error al procesar tu solicitud. Inténtalo más tarde.</div>
      </c:if>
      
      <c:if test="${param.error == 'invalid_token'}">
        <div class="err">❌ El enlace de recuperación es inválido o ha expirado. Solicita uno nuevo.</div>
      </c:if>

      <form method="post" action="${pageContext.request.contextPath}/forgot-password">
        <div class="field">
          <label for="email">Correo Electrónico</label>
          <input id="email" name="email" type="email" placeholder="ejemplo@correo.com" required autofocus />
        </div>

        <button class="btn" type="submit">Enviar enlace de recuperación</button>
      </form>

      <div class="links">
        <a href="${pageContext.request.contextPath}/login">Volver a Ingresar</a>
      </div>
    </div>
  </div>
</body>
</html>