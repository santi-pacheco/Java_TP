<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8" />
  <title>Registro — FatMovies</title>
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
  <style>
    body { font-family: 'Poppins', sans-serif; display:flex; flex-direction:column; min-height:100vh; background:#FAF8F3; margin:0; }
    .content { flex:1; display:flex; align-items:center; justify-content:center; padding:40px 20px; }
    .card { background:white; padding:40px; border-radius:15px; box-shadow:0 8px 30px rgba(0,0,0,0.12); width:420px; }
    .logo-container { text-align:center; margin-bottom:30px; }
    .logo { width:120px; height:90px; object-fit:cover; transform:scale(1.5); }
    h1 { margin:0 0 24px 0; font-size:28px; font-weight:700; text-align:center; color:#333; }
    .field { margin-bottom:16px; }
    label{ display:block; margin-bottom:8px; font-size:14px; font-weight:500; color:#333; }
    input[type="text"], input[type="password"], input[type="email"], input[type="date"] {
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
    .hint { font-size:12px; color:#888; margin-top:6px; }
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
      const pwdRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).*$/;

      if (username.length < 3) msg = 'El usuario debe tener entre 3 y 50 caracteres.';
else if (!email || !/^\S+@\S+\.\S+$/.test(email)) msg = 'Ingresá un email válido.';
else if (!birth) msg = 'Ingresá tu fecha de nacimiento.';
else if (pwd.length < 8) msg = 'La contraseña debe tener al menos 8 caracteres.';
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
  <%@ include file="/WEB-INF/components/navbar-new.jsp" %>
  
  <div class="content">
    <div class="card">
      <div class="logo-container">
        <img src="${pageContext.request.contextPath}/utils/export50.svg" alt="Fat Movies" class="logo">
      </div>
      <h1>Crear cuenta</h1>

    <c:if test="${not empty appError}">
      <div class="err">${appError}</div>
    </c:if>

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
          <div style="position: relative;">
              <input id="password" name="password" type="password" required style="padding-right: 40px; width: 100%; box-sizing: border-box;" />
              <span onclick="toggleVisibility('password', 'eyeOpen1', 'eyeClosed1')" style="position: absolute; right: 12px; top: 50%; transform: translateY(-50%); cursor: pointer; color: #666; display: flex;">
                  <svg id="eyeClosed1" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
                  <svg id="eyeOpen1" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="display: none;"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
              </span>
          </div>
          <span style="font-size: 11px; color: #888;">Mín. 8 caracteres, con mayúscula, minúscula, número y símbolo.</span>
        </div>

        <div class="field">
          <label for="confirmPassword">Confirmar contraseña</label>
          <div style="position: relative;">
              <input id="confirmPassword" name="confirmPassword" type="password" required style="padding-right: 40px; width: 100%; box-sizing: border-box;" />
              <span onclick="toggleVisibility('confirmPassword', 'eyeOpen2', 'eyeClosed2')" style="position: absolute; right: 12px; top: 50%; transform: translateY(-50%); cursor: pointer; color: #666; display: flex;">
                  <svg id="eyeClosed2" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
                  <svg id="eyeOpen2" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="display: none;"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
              </span>
          </div>
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
  </div>
  
  <script>
            function toggleVisibility(inputId, openIconId, closedIconId) {
                const pwd = document.getElementById(inputId);
                const eyeOpen = document.getElementById(openIconId);
                const eyeClosed = document.getElementById(closedIconId);

                if (pwd.type === 'password') {
                    pwd.type = 'text';
                    eyeClosed.style.display = 'none';
                    eyeOpen.style.display = 'block';
                } else {
                    pwd.type = 'password';
                    eyeOpen.style.display = 'none';
                    eyeClosed.style.display = 'block';
                }
            }
  </script>
</body>
</html>