# Instalación y Configuración del Proyecto (Setup Guide)

Sigue estos pasos para configurar y ejecutar **FatMovies** en tu entorno local.

## 1. Prerrequisitos

Antes de empezar, asegúrate de tener instalado el siguiente software en tu computadora:
* **Java Development Kit (JDK):** Versión 11 o superior (Recomendado JDK 21).
* **Servidor Web:** Apache Tomcat (Versión 10 o superior).
* **Base de Datos:** MySQL Server 8.0+.
* **IDE (Entorno de Desarrollo):** Eclipse Enterprise, IntelliJ IDEA Ultimate o VS Code.
* **Gestor de dependencias:** Maven.

---

## 2. Configuración de la Base de Datos

1. Inicia tu servidor MySQL .
2. Abre tu cliente SQL y ejecuta el siguiente script para crear la base de datos con datos: [Script DB](./db/Dump_DB_AD.sql)

---

## 3. Despliegue (Deploy) en el IDE

**Si usas Eclipse:**
1. Ve a `File` -> `Import` -> `Existing Maven Projects` (o `Existing Dynamic Web Projects`) y selecciona la carpeta del proyecto.
2. Haz clic derecho sobre el proyecto en el explorador -> `Build Path` -> `Configure Build Path` y asegúrate de que el **Server Runtime** de Apache Tomcat esté agregado en las librerías.
3. Haz clic derecho sobre el proyecto -> `Run As` -> `Run on Server`.
4. Selecciona tu servidor Tomcat y dale a "Finish".
5. En la ruta `src/main/resources` crea un archivo `config.properties`, siguiendo el siguiente ejemplo [Config.properties.example ](./src/main/resources/config.properties.example)
6. Haz click derecho en la carpeta del proyecto ve a `run as` -> `run on server`
---

## 4. Acceso a la aplicación

Una vez que el servidor esté corriendo sin errores en la consola, abre tu navegador web favorito y accede a:

**`http://localhost:8080/FatMovies`**


**Puedes registrarte o logearte con el usuario administrador utilizando:**
```text
Username: Admin
Password: Admin123$
```

## 5. Disfruta de la aplicacion <br>

Crea usuarios , haz reviews e invita a tus amigos para que suban kcals con vos. 
