<%
    entity.User user = (entity.User) session.getAttribute("usuarioLogueado");
    if (user != null && "admin".equals(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/index.html");
    } else {
        response.sendRedirect(request.getContextPath() + "/home");
    }
%>
