<%@ page import="entity.User" %>
<%
    // Le sacamos el "entity." porque ya está importado arriba
    User user = (User) session.getAttribute("usuarioLogueado");
    if (user != null && "admin".equals(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/index.html");
    } else {
        response.sendRedirect(request.getContextPath() + "/home");
    }
%>