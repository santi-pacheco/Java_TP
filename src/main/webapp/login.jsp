<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8" />
  <title>Ingresar — FatMovies</title>
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <style>
    body { font-family: Arial, Helvetica, sans-serif; display:flex; align-items:center; justify-content:center; height:100vh; background:#f5f5f5; margin:0; }
    .card { background:white; padding:24px; border-radius:8px; box-shadow:0 6px 20px rgba(0,0,0,0.08); width:320px; }
    h1 { margin:0 0 12px 0; font-size:20px; }
    .field { margin-bottom:12px; }
    label{ display:block; margin-bottom:6px; font-size:14px; }
    input[type="text"], input[type="password"], input[type="email"], input[type="date"] {
      width:100%; padding:8px 10px; border:1px solid #ddd; border-radius:4px; box-sizing:border-box;
    }
    .btn { width:100%; padding:10px; border:none; border-radius:6px; background:#2b7cff; color:white; font-weight:600; cursor:pointer; }
    .small { font-size:13px; color:#666; margin-top:10px; text-align:center; }
    .err { background:#ffe6e6; color:#a80000; padding:8px; border-radius:4px; margin-bottom:12px; }
    .ok { background:#e6ffec; color:#116611; padding:8px; border-radius:4px; margin-bottom:12px; }
    .links { margin-top:12px; text-align:center; }
    a { color:#2b7cff; text-decoration:none; }
  </style>
</head>
<body>
  <div class="card">
    <h1>Ingresar</h1>

    <!-- Mensaje de éxito desde registro: /login.jsp?success=true -->
    <c:if test="${param.success == 'true'}">
      <div class="ok">Registro exitoso. Ya podés ingresar.</div>
    </c:if>

    <!-- Mensaje de error (LoginServlet hace forward con request.setAttribute("error", ...)) -->
    <c:if test="${not empty error}">
      <div class="err">${error}</div>
    </c:if>

    <!-- Formulario: POST a /login -->
    <form method="post" action="${pageContext.request.contextPath}/login">
      <div class="field">
        <label for="username">Usuario</label>
        <!-- repobla si vino en param (por ejemplo al hacer forward) -->
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
</body>
</html>
