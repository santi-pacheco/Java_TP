<%@ page import="java.util.List" %>
<%@ page import="entity.User" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>FatMovies - Gestión de Usuarios</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto|Varela+Round">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style>
        body { color: #566787; background: #f5f5f5; font-family: 'Varela Round', sans-serif; font-size: 13px; }
        .table-responsive { margin: 30px 0; }
        .table-wrapper { min-width: 1000px; background: #fff; padding: 20px 25px; border-radius: 3px; box-shadow: 0 1px 1px rgba(0,0,0,.05); }
        .table-title { padding-bottom: 15px; background: #435d7d; color: #fff; padding: 16px 30px; margin: -20px -25px 10px; border-radius: 3px 3px 0 0; }
        .table-title h2 { margin: 5px 0 0; font-size: 24px; }
        .table-title .btn { color: #fff; float: right; font-size: 13px; border: none; min-width: 50px; border-radius: 2px; outline: none !important; margin-left: 10px; }
        .table-title .btn i { float: left; font-size: 21px; margin-right: 5px; }
        .table-title .btn span { float: left; margin-top: 2px; }
        table.table tr th, table.table tr td { border-color: #e9e9e9; padding: 12px 15px; vertical-align: middle; }
        table.table td a.edit { color: #FFC107; }
        table.table td a.delete { color: #F44336; }
        table.table td i { font-size: 19px; }
    </style>
</head>
<body>
<div class="container">
    <a href="<%= request.getContextPath() %>/index.html" class="btn btn-primary" style="margin-top:10px"><i class="fa fa-home"></i> Volver al Inicio</a>
    <div class="table-responsive">
        <div class="table-wrapper">
            <div class="table-title">
                <div class="row">
                    <div class="col-sm-6">
                        <h2>Gestión de <b>Usuarios</b></h2>
                    </div>
                    <div class="col-sm-6">
                        <a href="#addUserModal" class="btn btn-success" data-toggle="modal"><i class="material-icons">&#xE147;</i> <span>Agregar Usuario</span></a>
                    </div>
                </div>
            </div>
            <table class="table table-striped table-hover">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Email</th>
                        <th>Rol</th>
                        <th>Fecha de nacimiento</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        List<User> users = (List<User>) request.getAttribute("users");
                        if (users != null) {
                            for (User user : users) {
                    %>
                    <tr>
                        <td><%= user.getId() %></td>
                        <td><%= user.getUsername() %></td>
                        <td><%= user.getEmail() %></td>
                        <td><%= user.getRole() %></td>
                        <td><%= user.getBirthDate() %></td>
                        <td>
                            <a href="#editUserModal" class="edit" data-toggle="modal" 
                               data-id="<%= user.getId() %>"
                               data-name="<%= user.getUsername() %>"
                               data-email="<%= user.getEmail() %>"
                               data-role="<%= user.getRole() %>"
                               data-birthdate="<%= user.getBirthDate() != null ? user.getBirthDate().toString() : "" %>">
                               <i class="material-icons" data-toggle="tooltip" title="Editar">&#xE254;</i>
                            </a>
                            <a href="#deleteUserModal" class="delete" data-toggle="modal" data-id="<%= user.getId() %>">
                                <i class="material-icons" data-toggle="tooltip" title="Eliminar">&#xE872;</i>
                            </a>
                        </td>
                    </tr>
                    <%      }
                        }
                    %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div id="addUserModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="addUserForm">
                <div class="modal-header">
                    <h4 class="modal-title">Agregar Usuario</h4>
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                </div>
                <div class="modal-body">
                    <div id="addUserError" class="alert alert-danger" style="display: none;"></div>
                    <div class="form-group">
                        <label>Nombre</label>
                        <input type="text" name="name" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" name="email" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>Contraseña</label>
                        <input type="password" name="password" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>Rol</label>
                        <input type="text" name="role" class="form-control" value="user" required>
                    </div>
                    <div class="form-group">
                        <label>Fecha de nacimiento</label>
                        <input type="date" name="birthDate" class="form-control" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <input type="button" class="btn btn-default" data-dismiss="modal" value="Cancelar">
                    <input type="submit" class="btn btn-success" value="Agregar">
                </div>
            </form>
        </div>
    </div>
</div>

<div id="editUserModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="editUserForm">
                <div class="modal-header">
                    <h4 class="modal-title">Editar Usuario</h4>
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                </div>
                <div class="modal-body">
                    <div id="editUserError" class="alert alert-danger" style="display: none;"></div>
                    <input type="hidden" name="id" class="edit_id">
                    <div class="form-group">
                        <label>Nombre</label>
                        <input type="text" name="name" class="form-control edit_name" required>
                    </div>
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" name="email" class="form-control edit_email" required>
                    </div>
                    <div class="form-group">
                        <label>Contraseña (dejar en blanco para no cambiar)</label>
                        <input type="password" name="password" class="form-control">
                    </div>
                    <div class="form-group">
                        <label>Rol</label>
                        <input type="text" name="role" class="form-control edit_role" required>
                    </div>
                    <div class="form-group">
                        <label>Fecha de nacimiento</label>
                        <input type="date" name="birthDate" class="form-control edit_birthdate" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <input type="button" class="btn btn-default" data-dismiss="modal" value="Cancelar">
                    <input type="submit" class="btn btn-info" value="Guardar Cambios">
                </div>
            </form>
        </div>
    </div>
</div>

<div id="deleteUserModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="deleteUserForm">
                <input type="hidden" name="id" class="delete_id">
                <div class="modal-header">
                    <h4 class="modal-title">Eliminar Usuario</h4>
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                </div>
                <div class="modal-body">
                     <div id="deleteUserError" class="alert alert-danger" style="display: none;"></div>
                    <p>¿Estás seguro de que quieres eliminar este registro?</p>
                    <p class="text-warning"><small>Esta acción no se puede deshacer.</small></p>
                </div>
                <div class="modal-footer">
                    <input type="button" class="btn btn-default" data-dismiss="modal" value="Cancelar">
                    <input type="submit" class="btn btn-danger" value="Eliminar">
                </div>
            </form>
        </div>
    </div>
</div>

<script>
$(document).ready(function(){
    // Activa los tooltips
    $('[data-toggle="tooltip"]').tooltip();

    // ---- Lógica para el modal de EDITAR (poblar datos) ----
    $('.edit').on('click', function(){
        var id = $(this).data('id');
        var name = $(this).data('name');
        var email = $(this).data('email');
        var role = $(this).data('role');
        var birthdate = $(this).data('birthdate');
        
        $('#editUserModal .edit_id').val(id);
        $('#editUserModal .edit_name').val(name);
        $('#editUserModal .edit_email').val(email);
        $('#editUserModal .edit_role').val(role);
        $('#editUserModal .edit_birthdate').val(birthdate);
    });

    // ---- Lógica para el modal de ELIMINAR (poblar datos) ----
    $('.delete').on('click', function(){
        var id = $(this).data('id');
        $('#deleteUserModal .delete_id').val(id);
    });

    // ---- MANEJADORES DE SUBMIT AJAX ----

// 1. Agregar Usuario (ESTE ES EL CÓDIGO QUE FALTA)
$('#addUserForm').on('submit', function(event) {
    // Prevenimos el comportamiento por defecto del navegador (que es recargar la página con GET)
    event.preventDefault();
    $('#addUserError').hide();

    // Creamos un objeto JavaScript con los datos del formulario de "Agregar"
    var userData = {
        username: $(this).find('input[name="nam	e"]').val(),
        email: $(this).find('input[name="email"]').val(),
        password: $(this).find('input[name="password"]').val(),
        role: $(this).find('input[name="role"]').val(),
        birthDate: $(this).find('input[name="birthDate"]').val()
    };
    console.log("Datos de fecha a ENVIAR (Agregar):", $(this).find('input[name="birthDate"]').val());
    console.log("Objeto completo a ENVIAR (Agregar):", userData);
    // Hacemos la llamada AJAX usando el método POST
    $.ajax({
        type: 'POST',
        url: 'users', // Para crear, no se necesita un ID en la URL
        contentType: 'application/json; charset=UTF-8',
        data: JSON.stringify(userData), // Convertimos el objeto a un string JSON
        success: function(response) {
            // Si todo sale bien, recargamos la página para ver el nuevo usuario
            location.reload();
        },
        error: function(xhr) {
            // Si el servlet devuelve un error, lo mostramos en el modal
            var errorResponse = JSON.parse(xhr.responseText);
            $('#addUserError').text(errorResponse.message).show();
        }
    });
});
// 2. Editar Usuario
    $('#editUserForm').on('submit', function(event) {
        event.preventDefault();
        var id = $('.edit_id').val();
        $('#editUserError').hide();

        // PASO 1: Creamos un objeto JavaScript con los datos del formulario.
        var userData = {
            username: $(this).find('.edit_name').val(),
            email: $(this).find('.edit_email').val(),
            password: $(this).find('input[name="password"]').val(),
            role: $(this).find('.edit_role').val(),
            birthDate: $(this).find('.edit_birthdate').val()
        };

        $.ajax({
            type: 'PUT',
            url: 'users?id=' + id, // Mandamos el ID en la URL
            
            // PASO 2: Le decimos a JQuery que el cuerpo de la petición es JSON.
            contentType: 'application/json; charset=UTF-8',
            
            // PASO 3: Convertimos el objeto JavaScript a una cadena de texto JSON.
            data: JSON.stringify(userData),
            
            success: function(response) {
                location.reload(); [cite_start]// [cite: 53]
            },
            error: function(xhr) {
                var errorResponse = JSON.parse(xhr.responseText);
                $('#editUserError').text(errorResponse.message).show(); [cite_start]// [cite: 54]
            }
        });
    });

    // 3. Eliminar Usuario
    $('#deleteUserForm').on('submit', function(event) {
        event.preventDefault();
        var id = $('.delete_id').val();
        $('#deleteUserError').hide();

        $.ajax({
            type: 'DELETE',
            url: 'users?id=' + id,
            success: function(response) {
                location.reload();
            },
            error: function(xhr) {
                var errorResponse = JSON.parse(xhr.responseText);
                $('#deleteUserError').text(errorResponse.message).show();
            }
        });
    });
});
</script>

</body>
</html>