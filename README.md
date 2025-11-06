LetterBox look-a-like made in Java

**Integrantes:**<br> 
Giacone, Alessandro \- 52664  
Mazalán, Ariel \- 52867  
Pacheco, Santiago \- 52831  
Ribotta, Tomás \- 52309  
**Enunciado:**  
 El sistema va a consistir en una aplicación web en la cual los usuarios podrán llevar un registro de las películas y/o series que hayan visto brindándole la posibilidad de darle una puntuación y una reseña. De cada película los usuarios tendrán la opción de visualizar las diferentes reseñas teniendo la oportunidad de darle me gusta. También podrán ver la calificación de un medio especializado.   
 Los usuarios podrán agregar películas a una lista llamada “Watchlist” a modo de recordatorio para su posterior visualización.  
 En la principal se ubicará un dashboard que en el cual usuario podrá ver las películas mejor puntuadas, sus últimas reseñas y recomendaciones específicas para cada usuario.


**Regularidad:**

| Requerimiento | Detalle/Listado de casos incluidos  |
| :---- | :---- |
| ABMC simple | País CRUD \- Persona CRUD \- Usuario CRUD \- Género CRUD \ |
| ABMC dependiente | CRUD \- Película (depende de Género, Persona y País) CRUD \- Configuracion(depende de usuario) |
| CU NO-ABMC | **C.U.U. Hacer una reseña a una película** (Verificando que sea la primer reseña de ese usuario para esa película, es decir, no puede haber más de dos reseñas por usuario para cada película. Los usuarios tienen un estado que depende de la cantidad de reseñas que realizaron, si superan cierto limite indicado por el administrador pasan a un estado mayor que les da beneficios(Para la regularidad solo le permite poner mas peliculas en su watchlist)) <br>**C.U.U. Hacer una WatchList**  (El usuario puede agregar a su watchlist peliculas que desea ver mas tarde, el mismo podra añadir peliculas a las cuales ya le realizo una reseña ya que puede querer verlas denuevo. Se verifica que al agregar una pelicula el usuario no supere el limite de peliculas por watchlist que tiene dependiendo del estado del usuario. El usuario Momentaneamente solo tiene una watchlist) |
| Listado simple | \- |
| Listado complejo | Listado de películas por género,anio y nombre muestra nombre de película, valoracion \=\> detalle muestra la puntuacion de tmdb y la de nuestra api, mas las reseñas y otra informacion de la pelicula como la descripcion, actores,etc. |

**Aprobación Directa:**

| Requerimiento | Detalle/Listado de casos incluidos |
| :---- | :---- |
| ABMC | CRUD \- Persona CRUD \- Configuracion CRUD \- Usuario CRUD \- Género CRUD \- País CRUD \- Película |
| CU “Complejo” (nivel resumen) | **C.U.R. Feed:**  <br>C.U.U.1: Recomendación de películas: El sistema recomendará al usuario una cierta cantidad de películas en base a las películas que vió, su género más visto y las películas en tendencia o mejor rankeadas, con un algoritmo sencillo. El usuario podrá solicitar que el sistema le recomiende una película o podrá ver sus recomendaciones en una FYP(For you page) <br>C.U.U.2: Seguir Usuarios: El usuario podrá buscar usuarios por su nombre, ver sus últimas reseñas e información importante de ellos, y tendrá la posibilidad de seguirlos. Al seguir un usuario podrás ver en tu página de seguidores, cada vez que este publique una reseña, así como las reseñas que comparte(Cada usuario una vez haga una reseña tendrá la posibilidad de recomendar la película/serie a sus amigos mediante un botón, para que les aparezca en su página a sus seguidores <br>**C.U.R. Sistema de votos y moderación:** <br>C.U.U.1: Votación de reseñas El usuario podrá dar like a una reseña y las reseñas más likeadas tendrán más visibilidad <br>C.U.U.2: Recibo de Feedback El usuario recibirá notificaciones o mails cuando una reseña suya haya llegado a una cierta cantidad de likes <br>C.U.U.3:Moderación de reseñas  Las reseñas serán moderadas con IA para evitar reseñas con spoilers y/o lenguaje ofensivo y racista.  |
| Listado complejo | \-Listado de películas por género, muestra nombre de película, género  , nombre de director \=\> detalle muestra las diferentes puntuaciones de cada medio para esa película \-Listado de reseñas de una película por cantidad de likes, muestra la reseña, fecha, cantidad de likes \=\> detalle muestra info del usuario que hizo la reseña |
| Nivel de acceso | \-Admin \-Usuario |
| Manejo de errores | – |
| requerimiento extra obligatorio (\*\*) | \-Manejo de archivos. El sistema podrá controlar las imágenes de las portadas de las películas. \-Envío de mails.El usuario recibirá mail para notificar cosas importantes que suceden en su cuenta |
| publicar el sitio | – |
