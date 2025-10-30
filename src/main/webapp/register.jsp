<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8" />
  <title>Registro — FatMovies</title>
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <style>
    body { font-family: Arial, Helvetica, sans-serif;
display:flex; align-items:center; justify-content:center; height:100vh; background:#f5f5f5; margin:0; }
    .card { background:white; padding:24px; border-radius:8px; box-shadow:0 6px 20px rgba(0,0,0,0.08); width:360px;
}
    h1 { margin:0 0 12px 0; font-size:20px; }
    .field { margin-bottom:12px;
}
    label{ display:block; margin-bottom:6px; font-size:14px; }
    input[type="text"], input[type="password"], input[type="email"], input[type="date"] {
      width:100%;
padding:8px 10px; border:1px solid #ddd; border-radius:4px; box-sizing:border-box;
    }
    .btn { width:100%; padding:10px; border:none; border-radius:6px; background:#2b7cff; color:white; font-weight:600;
cursor:pointer; }
    .small { font-size:13px; color:#666; margin-top:10px; text-align:center; }
    .err { background:#ffe6e6; color:#a80000; padding:8px;
border-radius:4px; margin-bottom:12px; }
    .ok { background:#e6ffec; color:#116611; padding:8px; border-radius:4px; margin-bottom:12px; }
    .links { margin-top:12px;
text-align:center; }
    a { color:#2b7cff; text-decoration:none; }
    .hint { font-size:12px; color:#888; margin-top:6px;
}
  </style>

  <script>
    function validateAndSubmit(e) {
      e.preventDefault();
const username = document.getElementById('username').value.trim();
      const email = document.getElementById('email').value.trim();
      const pwd = document.getElementById('password').value;
      const pwd2 = document.getElementById('confirmPassword').value;
      const birth = document.getElementById('birthDate').value;
let msg = '';

      // Regex de la entidad User.java
      const pwdRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).*$/;

      if (username.length < 3) msg = 'El usuario debe tener entre 3 y 50 caracteres.';
else if (!email || !/^\S+@\S+\.\S+$/.test(email)) msg = 'Ingresá un email válido.';
else if (!birth) msg = 'Ingresá tu fecha de nacimiento.'; // Descomentado, ya que es @NotNull
else if (pwd.length < 8) msg = 'La contraseña debe tener al menos 8 caracteres.'; // Cambiado de 6 a 8
      else if (!pwdRegex.test(pwd)) msg = 'La contraseña debe tener mayúscula, minúscula, número y un carácter especial.';
      else if (pwd !== pwd2) msg = 'Las contraseñas no coinciden.';
      
const clientErr = document.getElementById('clientError');
      if (msg) {
        clientErr.textContent = msg;
        clientErr.style.display = 'block';
return false;
      } else {
        clientErr.style.display = 'none';
        document.getElementById('registerForm').submit();
}
    }

    window.addEventListener('DOMContentLoaded', () => {
      document.getElementById('registerForm').addEventListener('submit', validateAndSubmit);
    });
</script>
</head>
<body>
  <div class="card">
    <h1>Crear cuenta</h1>

    <%-- 1. Error de Aplicación (ej: "Usuario duplicado") --%>
    <c:if test="${not empty appError}">
      <div class="err">${appError}</div>
    </c:if>

    <%-- 2. Errores de Validación (ej: "Email inválido") --%>
    <c:if test="${not empty errors}">
        <div class="err">
            <strong>Por favor corregí los siguientes errores:</strong>
            <ul>
                <c:forEach var="e" items="${errors}">
                    <li>${e}</li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <div id="clientError" class="err" style="display:none;"></div>

    <form id="registerForm" method="post" action="${pageContext.request.contextPath}/register">
      
      <div class="field">
        <label for="username">Usuario</label>
        <input id="username" name="username" type="text" value="${user.username}" required autofocus />
      </div>

      <div class="field">
        <label for="email">Email</label>
        <input id="email" name="email" type="email" 
value="${user.email}" required />
      </div>

      <div class="field">
        <label for="password">Contraseña</label>
        <input id="password" name="password" type="password" required />
        <div class="hint">Mín. 8 caracteres, con mayúscula, minúscula, número y símbolo.</div>
      </div>

      <div class="field">
        <label for="confirmPassword">Confirmar contraseña</label>
        <input id="confirmPassword" name="confirmPassword" type="password" required />
      </div>

    
  <div class="field">
        <label for="birthDate">Fecha de nacimiento</label>
        <input id="birthDate" name="birthDate" type="date" value="${user.birthDate}" />

      </div>

      <button class="btn" type="submit">Registrarme</button>
    </form>

    <div class="links">
      <div class="small">¿Ya tenés cuenta? <a href="${pageContext.request.contextPath}/login">Ingresá</a></div>
    </div>
  </div>
</body>
</html>