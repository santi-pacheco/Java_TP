<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8" />
  <title>Ingresar — FatMovies</title>
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
  <style>
    body { font-family: 'Poppins', sans-serif; display:flex; flex-direction:column; min-height:100vh; background:#FAF8F3; margin:0; }
    .content { flex:1; display:flex; align-items:center; justify-content:center; padding:40px 20px; }
    .card { background:white; padding:40px; border-radius:15px; box-shadow:0 8px 30px rgba(0,0,0,0.12); width:400px; }
    .logo-container { text-align:center; margin-bottom:30px; }
    .logo { width:120px; height:90px; object-fit:cover; transform:scale(1.5); }
    h1 { margin:0 0 24px 0; font-size:28px; font-weight:700; text-align:center; color:#333; }
    .field { margin-bottom:16px; }
    label{ display:block; margin-bottom:8px; font-size:14px; font-weight:500; color:#333; }
    input[type="text"], input[type="password"] {
      width:100%; padding:12px 16px; border:2px solid #E0E0E0; border-radius:8px; box-sizing:border-box; font-family:'Poppins',sans-serif; font-size:14px; transition:border-color 0.3s;
    }
    input:focus { outline:none; border-color:#333; }
    .btn { width:100%; padding:14px; border:none; border-radius:8px; background:#333; color:white; font-weight:600; font-size:16px; cursor:pointer; margin-top:8px; transition:background 0.3s; }
    .btn:hover { background:#555; }
    .small { font-size:14px; color:#666; margin-top:20px; text-align:center; }
    .err { background:#ffe6e6; color:#a80000; padding:12px; border-radius:8px; margin-bottom:16px; font-size:14px; }
    .ok { background:#e6ffec; color:#116611; padding:12px; border-radius:8px; margin-bottom:16px; font-size:14px; }
    .links { margin-top:16px; text-align:center; }
    a { color:#333; text-decoration:none; font-weight:600; }
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
      <h1>Ingresar</h1>

      <c:if test="${param.success == 'true'}">
        <div class="ok">Registro exitoso. Ya podés ingresar.</div>
      </c:if>

      <c:if test="${not empty error}">
        <div class="err">${error}</div>
      </c:if>

      <form method="post" action="${pageContext.request.contextPath}/login">
        <div class="field">
          <label for="username">Usuario</label>
          <input id="username" name="username" type="text" value="${param.username}" required autofocus />
        </div>

        <div class="field">
          <label for="password">Contraseña</label>
          <input id="password" name="password" type="password" required />
        </div>

        <button class="btn" type="submit">Entrar</button>
      </form>

      <div class="links">
        <div class="small">¿No tenés cuenta? <a href="${pageContext.request.contextPath}/register">Registrate</a></div>
      </div>
    </div>
  </div>
</body>
</html>
