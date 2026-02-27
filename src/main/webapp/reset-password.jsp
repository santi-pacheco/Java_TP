<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8" />
  <title>Restablecer Contraseña — FatMovies</title>
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  <style>
    body { font-family: 'Poppins', sans-serif; display:flex; flex-direction:column; min-height:100vh; background:#FAF8F3; margin:0; }
    .content { flex:1; display:flex; align-items:center; justify-content:center; padding:40px 20px; }
    .card { background:white; padding:40px; border-radius:15px; box-shadow:0 8px 30px rgba(0,0,0,0.12); width:400px; }
    .logo-container { text-align:center; margin-bottom:20px; }
    .logo { width:120px; height:90px; object-fit:cover; transform:scale(1.5); }
    h1 { margin:0 0 10px 0; font-size:24px; font-weight:700; color:#333; text-align: center; }
    p { font-size: 14px; color: #666; margin-bottom: 24px; text-align: center; }
    .field { margin-bottom:16px; text-align: left; }
    label { display:block; margin-bottom:8px; font-size:14px; font-weight:500; color:#333; }
    input[type="password"] { width:100%; padding:12px 16px; border:2px solid #E0E0E0; border-radius:8px; box-sizing:border-box; font-family:'Poppins',sans-serif; font-size:14px; transition:border-color 0.3s; }
    input:focus { outline:none; border-color:#8B7355; }
    .btn { width:100%; padding:14px; border:none; border-radius:8px; background:#8B7355; color:white; font-weight:600; font-size:16px; cursor:pointer; margin-top:8px; transition:background 0.3s; }
    .btn:hover { background:#6b5840; }
    .err { background:#ffe6e6; color:#a80000; padding:12px; border-radius:8px; margin-bottom:16px; font-size:14px; text-align: left; display: none; }
  </style>
  <script>
    function validateReset(e) {
      e.preventDefault();
      const pwd = document.getElementById('password').value;
      const pwd2 = document.getElementById('confirmPassword').value; // Usamos camelCase aquí también
      const clientErr = document.getElementById('clientError');
      const pwdRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–\[{}\]:;',?\/*~$^+=<>]).*$/;
      
      let msg = '';
      if (pwd.length < 8) msg = 'La contraseña debe tener al menos 8 caracteres.';
      else if (!pwdRegex.test(pwd)) msg = 'La contraseña debe tener mayúscula, minúscula, número y un carácter especial.';
      else if (pwd !== pwd2) msg = 'Las contraseñas no coinciden.';
      
      if (msg) {
        clientErr.textContent = msg;
        clientErr.style.display = 'block';
        return false;
      } else {
        clientErr.style.display = 'none';
        document.getElementById('resetForm').submit();
      }
    }

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

    window.addEventListener('DOMContentLoaded', () => {
      document.getElementById('resetForm').addEventListener('submit', validateReset);
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
      <h1>Nueva Contraseña</h1>
      <p>Establece una nueva contraseña segura para tu cuenta.</p>

      <c:if test="${not empty error}">
        <div class="err" style="display: block;">❌ ${error}</div>
      </c:if>
      
      <div id="clientError" class="err"></div>

      <form id="resetForm" method="post" action="${pageContext.request.contextPath}/reset-password">
        <input type="hidden" name="token" value="${token}" />

        <div class="field">
          <label for="password">Nueva Contraseña</label>
          <div style="position: relative;">
              <input id="password" name="password" type="password" required style="padding-right: 40px;" autofocus />
              <span onclick="toggleVisibility('password', 'eyeOpen1', 'eyeClosed1')" style="position: absolute; right: 12px; top: 50%; transform: translateY(-50%); cursor: pointer; color: #666; display: flex;">
                  <svg id="eyeClosed1" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
                  <svg id="eyeOpen1" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="display: none;"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
              </span>
          </div>
          <span style="font-size: 11px; color: #888;">Mín. 8 caracteres, mayúscula, minúscula, número y símbolo.</span>
        </div>

        <div class="field">
          <label for="confirmPassword">Confirmar Contraseña</label>
          <div style="position: relative;">
              <input id="confirmPassword" name="confirmPassword" type="password" required style="padding-right: 40px;" />
              <span onclick="toggleVisibility('confirmPassword', 'eyeOpen2', 'eyeClosed2')" style="position: absolute; right: 12px; top: 50%; transform: translateY(-50%); cursor: pointer; color: #666; display: flex;">
                  <svg id="eyeClosed2" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
                  <svg id="eyeOpen2" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="display: none;"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
              </span>
          </div>
        </div>

        <button class="btn" type="submit">Guardar y Entrar</button>
      </form>
    </div>
  </div>
</body>
</html>