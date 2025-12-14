# Documentación Técnica de la App SoulScreen (Branch: main)

-----

## 1\. Descripción General

**SoulScreen** es una aplicación móvil desarrollada en **Kotlin** con **Jetpack Compose**, que funciona como el cliente oficial para la red social del mismo nombre. Esta red permite a los usuarios crear **publicaciones (posts)** categorizadas sobre diversos contenidos multimedia, como películas, series y videojuegos. Las publicaciones se clasifican en tipos como **Spoiler, Opinión, Meme, Consejo, Pregunta, Debate, Noticia, Curiosidad y Otros**.

La aplicación permite a los usuarios:

  * **Registrarse**, **iniciar sesión** y mantener la sesión activa mediante **JWT**.
  * **Crear**, **explorar** y **comentar** publicaciones.
  * **Buscar** usuarios y **seguir perfiles**.
  * **Personalizar** la experiencia mediante configuración de tema, tipo y tamaño de fuente.
  * **Editar** su perfil (correo, nombre de usuario, contraseña).

El sistema está estructurado bajo el patrón arquitectónico **Modelo-Vista-Controlador (MVC)** y se conecta a una base de datos remota **MongoDB** para gestionar los datos de usuarios, publicaciones y multimedia.

-----

## 2\. Requerimientos del Sistema

### 2.1 Requerimientos Funcionales

  * **RF01**: El sistema debe permitir la **autenticación de usuarios** mediante nombre de usuario y contraseña.
  * **RF02**: El sistema debe permitir a los usuarios **crear publicaciones** sobre contenido multimedia.
  * **RF03**: El sistema debe permitir **seguir o dejar de seguir** a otros usuarios.
  * **RF04**: El sistema debe mostrar una **fuente de publicaciones reciente**.
  * **RF05**: El sistema debe permitir **comentar publicaciones**.
  * **RF06**: El sistema debe almacenar y mostrar información sobre **películas, series y videojuegos**.
  * **RF07**: El sistema debe permitir la **personalización de fuente, tamaño de texto y tema visual**.
  * **RF08**: El sistema debe permitir la **navegación entre pantallas** mediante rutas definidas.

### 2.2 Requerimientos No Funcionales

  * **RNF01**: La app debe mantener **sesiones de usuario seguras mediante JWT**.
  * **RNF02**: El acceso a los tokens debe estar protegido mediante **EncryptedSharedPreferences**.
  * **RNF03**: El sistema debe estar **optimizado para dispositivos Android**.
  * **RNF04**: Las llamadas de red deben ser manejadas de forma **asíncrona usando coroutines**.

-----

## 3\. Diseño del Sistema

### 3.1 Arquitectura General

El sistema sigue un modelo **MVC**:

  * **Modelo**: Representado por clases de datos como `Usuario`, `Post`, `Multimedia`, conectadas a MongoDB mediante **Retrofit**.
  * **Vista**: Composables de Jetpack Compose (Home, LogIn, SignUp, etc.).
  * **Controlador**: Maneja navegación (NavHost), eventos de usuario y lógica de negocio con ViewModels.

### 3.2 Diagrama General (descripción textual)

  * La vista inicia desde `MainActivity`, que carga preferencias de usuario (tema y fuente).
  * Se verifica la validez del token, y según su estado, se dirige al usuario a la pantalla correspondiente (`welcomeScreen`, `Posts`, etc.).
  * Las vistas están compuestas por pantallas modulares, llamadas desde la navegación.

### 3.3 Diagrama de Clases (resumen)

  * **Usuario**: Contiene `userName`, `email`, `password`, `biography`, `avatar`, `genres`, etc.
  * **Post**: Contiene `title`, `content`, `mediaName`, `postType`, `date`, `userId`, etc.
  * **Multimedia**: Contiene `name`, `descripcion`, `director`, `cast`, `poster`, `rating`, etc.

-----

## 4\. Estructura de la Base de Datos (MongoDB)

### 4.1 Users

```json
{
  "_id": ObjectId("6858a476982493b03409fbe2"),
  "userName": "Niamky",
  "name": "(Encrypted)",
  "biography": "Nada que ver aqui",
  "genres": ["Acción", "Fantasía"],
  "birthDate": "(Encrypted)",
  "joiningDate": "2025-06-22",
  "password": "(Hasheado con bcrypt)",
  "email": "NiamkyTails@outlook.com",
  "avatar": "/Images/1750639733_Niamky_by_Alphy.jpg",
  "following": ["..."],
  "followers": ["..."]
}
```

### 4.2 Posts

```json
{
  "_id": ObjectId("685ac38026a9490d9010ace8"),
  "user": "Niamky",
  "userId": "6858a476982493b03409fbe2",
  "title": "La acabo de ver",
  "content": "7 / 10 rotten tomatoes como dicen los chavos.",
  "date": "2025-06-24",
  "time": "09:25:51",
  "mediaName": "Spider-Man: No Way Home",
  "mediaId": "685a3e583854eb0dcb309570",
  "mediaImg": "/Images/Movies/1750646699_81y0foYjoFL._UF894,1000_QL80_.jpg",
  "postType": "Opinion",
  "comments": []
}
```

### 4.3 Multimedia

```json
{
  "_id": ObjectId("685a3e583854eb0dcb309566"),
  "id": "6858b6d296bc1fd370095042",
  "name": "Cómo entrenar a tu dragón",
  "descripcion": "Un joven vikingo desafía las tradiciones de su tribu al entablar amistad con un dragón...",
  "duracion": "98 min",
  "director": "Dean DeBlois, Chris Sanders",
  "cast": ["Jay Baruchel", "Gerard Butler", "America Ferrera"],
  "gender": ["Animación"],
  "idMedia": "Movies",
  "company": ["DreamWorks"],
  "date": "2010-03-26",
  "poster": "/Images/Movies/1750644434_51uJGH71GsL.jpg",
  "rating": 8.1
}
```

-----

## 5\. Autenticación y Seguridad

La app utiliza **JWT** para autenticación, manejando `accessToken` y `refreshToken`.
Los tokens se almacenan en **EncryptedSharedPreferences**, proporcionando seguridad adicional.
Se verifica la validez de los tokens con `isTokenValid()`, basada en la expiración del payload JWT.
Si el token ha expirado y existe un `refreshToken`, se realiza una llamada al backend usando Retrofit para renovar el token.

-----

## 6\. Flujo de Navegación

Se usa **Jetpack Navigation Compose**. El `NavHost` define rutas como:

  * `welcomeScreen`, `logIn`, `signUp`, `signUp_UserName`, `signUp_BirthDate`, `signUp_Email`
  * `Posts`, `crearPostScreen`, `VerPostScreen/{postId}`
  * `profileScreen`, `seeprofileuser/{id}`, `seguidoresUser/{userId}`
  * `ConfiguracionScreen`, `changeEmail`, `changePassword`, `changeUsername`
  * `ExplorarScreen`, `GustosScreen`, `AyudaScreen`, `DetalleMediaScreen/{mediaId}`
  * `openCamera/{destination}`, `takenPhoto/{destination}`

-----

## 7\. Personalización del Usuario

La app permite seleccionar entre cuatro familias tipográficas: **Default, Monospace, Serif y Sans Serif**.
El tamaño de fuente también puede personalizarse.
Las configuraciones se guardan en **SharedPreferences** mediante funciones `saveFontConfig()` y `loadFontConfig()`.

-----

## 8\. UI y Composición

Toda la UI está diseñada con **Jetpack Compose**:

  * Composables principales: `Scaffold`, `Column`, `Row`, `LazyColumn`, `NavigationBar`, `PullRefresh`, `Box`.
  * Soporte para **tema claro y oscuro** (modo del sistema).
  * La barra inferior (`BottomBar`) gestiona navegación entre Home, Amigos y Explorar.

<!-- end list -->

```kotlin
NavigationBarItem(
    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
    label = { Text("Inicio") },
    selected = index == 0,
    onClick = { onItemSelected(0) }
)
```

-----

## 9\. Requisitos Técnicos y Sincronización

  * La app requiere **Android 13 o superior (SDK 33)**.
  * Dependencias principales incluyen **Jetpack Compose, Retrofit, Kotlin Coroutines, Navigation Compose y EncryptedSharedPreferences**.
  * La API backend se inicia con `gradle bootRun` y se conecta a la base de datos MongoDB.
  * Actualmente, el backend corre en un servidor local, limitando el acceso externo.
  * La sincronización con la base de datos es automática cada vez que se actualiza la pantalla Home, siguiendo la lógica de actualización típica de redes sociales.
  * Las imágenes se cargan y guardan correctamente en la base de datos y en la app.
  * Para la administración visual de la base de datos multimedia se usó inicialmente una aplicación PHP.

-----

## 10\. Librerías y Herramientas Utilizadas

  * **Jetpack Compose** (UI)
  * **Retrofit** (llamadas HTTP y refresh tokens)
  * **EncryptedSharedPreferences** (almacenamiento seguro)
  * **Media3** (multimedia avanzada)
  * **Navigation Compose** (ruteo)
  * **Kotlin Coroutines** (asincronía)

-----

## 11\. Buenas Prácticas Implementadas

  * **Separación clara** entre lógica de UI, datos y navegación.
  * **Manejo seguro de sesiones**.
  * **Persistencia de configuraciones** personalizadas del usuario.
  * **Modularización de pantallas** en archivos independientes.
  * **Evita bloqueos de UI** usando coroutines.
