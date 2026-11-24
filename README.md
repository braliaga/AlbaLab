# AlbaLab

App Android para **CORPALIV / Alba Lab**, organización sin fines de lucro que promueve la autonomía y la inclusión social y laboral de personas con discapacidad múltiple. La app reúne en un solo lugar el propósito de la organización, sus programas y su catálogo, y permite que una persona o empresa interesada envíe un **registro de interés** (donación, alianza, voluntariado, compra de productos o capacitación). Los registros quedan guardados en el teléfono para el equipo de vinculación.

Proyecto de la asignatura DSY1105 · Desarrollo de Aplicaciones Móviles (Duoc UC).

## Integrantes

- Branco Aliaga
- Paulo Catalán

## Funcionalidades implementadas

- **Pantallas informativas:** Inicio (propósito, indicadores y llamada a la acción), Programas y Catálogo.
- **Identidad visual de CORPALIV / Alba Lab:** paleta tomada de [albalab.corpaliv.cl](https://albalab.corpaliv.cl/) (rojo `#DD3117`, amarillo `#FDC609`, crema `#F7F0E7`, texto `#1D1D1B`) en modo claro y oscuro, y las mismas fuentes del sitio: **Josefin Sans** en títulos y **DM Sans** en textos (incluidas en `res/font`, licencia SIL OFL).
- **Barra de navegación inferior** con cinco pestañas (Inicio, Programas, Catálogo, Colaborar, Registros). En horizontal se transforma en un `NavigationRail` lateral. Entre pestañas la transición es un fundido; hacia la pantalla de edición, un deslizamiento de 300 ms con flecha para volver.
- **Formulario de registro de interés** con:
  - campos de texto: nombre, teléfono, correo, dirección, cantidad de personas y fecha de contacto (`dd/MM/aaaa`);
  - opciones únicas (radios): contacto preferido y tipo de ayuda;
  - `Checkbox` de aceptación de contacto y `Switch` para recibir novedades.
- **Validaciones por campo** con ícono y mensaje de error animado. Las reglas viven en `Validaciones`, separadas de la interfaz. Si el envío falla, la pantalla se desplaza sola hasta el primer campo con error.
- **Estado con `StateFlow`:** el botón "Enviar registro" muestra un indicador de carga y, al guardar, un mensaje de éxito animado con ícono.
- **Persistencia local con Room:** los registros se guardan y se listan en la pantalla de registros, con la miniatura de la foto si la tienen.
- **Filtro dinámico de registros:** buscador por nombre que filtra mientras se escribe y chips por tipo de ayuda. Las tarjetas entran, salen y se reacomodan con animación, y hay estados vacíos distintos para "Aún no hay registros" y "Sin resultados" (con botón para limpiar filtros).
- **Notificación local** al guardar un registro ("Registro enviado · Gracias, …").
- **Autenticación biométrica** (huella o PIN del dispositivo) para entrar a los registros, desde cualquier camino: pestaña, botón de Inicio o botón tras enviar el formulario.
- **Animaciones de entrada** escalonadas (fundido + 8 dp hacia arriba, 200 ms) en Inicio, Programas y Catálogo. Todas son cortas, sin rebotes, y respetan la opción del sistema "Quitar animaciones".
- **Edición de registros:** al tocar un registro de la lista se abre un segundo formulario para cambiar teléfono, contacto preferido y recepción de novedades. Se valida igual que el registro y, al guardar, se actualiza en Room y se vuelve a la lista.
- **Foto de perfil opcional** en el registro, tomada con la cámara o elegida desde la galería.
- **Imágenes remotas** (banner de Inicio e imágenes del Catálogo) descargadas con Coil. Si no hay conexión se muestra un ícono en su lugar y el resto de la app sigue funcionando.

## Arquitectura

Patrón **MVVM**: la interfaz observa el estado del `ViewModel` (`StateFlow`), este valida con `Validaciones` y guarda mediante el repositorio, que accede a Room. Hay un ViewModel por pantalla.

```
cl/duoc/albalab/
├── MainActivity.kt          solo AppNavegacion() dentro del tema
├── data/
│   ├── model/               TipoAyuda, ContactoPreferido, RegistroInteres, Contenido
│   └── local/               RegistroDao, AppDatabase, ContenidoFicticio
├── repository/              RegistroRepository, ContenidoRepository
├── viewmodel/               Validaciones, Comun (Campo, Notificador),
│                            RegistroViewModel, RegistrosViewModel, EdicionViewModel, ContenidoViewModel
├── utils/                   ImagenStorage, Biometria, Notificaciones, FormatoClp
└── ui/
    ├── theme/               paleta de marca, tipografía (Josefin Sans + DM Sans) y tema
    ├── navegacion/          AppNavegacion (barra inferior / rail, rutas, transiciones, biometría)
    ├── componentes/         ContenedorPantalla, CampoValidado, Formulario, SelectorFoto,
    │                        ImagenRemota, Animaciones
    └── screens/             PantallaInicio, PantallaProgramas, PantallaCatalogo,
                             PantallaRegistro, PantallaMisRegistros, PantallaEditarRegistro
```

- **Model:** entidad `RegistroInteres`, enums `TipoAyuda` y `ContactoPreferido`, y los modelos de contenido.
- **Repository:** `RegistroRepository` y `ContenidoRepository` son el único punto de acceso a los datos. Ahí se conectará la **API REST** (Retrofit → microservicios Spring Boot) en la siguiente etapa.
- **ViewModel:**
  - `RegistroViewModel`: formulario 1, `guardar()` y aviso por `Notificador`.
  - `RegistrosViewModel`: lista con búsqueda y tipo combinados (`combine` → filtrados).
  - `EdicionViewModel`: formulario 2, recibe el id por `SavedStateHandle`.
  - `ContenidoViewModel`: programas, catálogo e indicadores.
- **`Notificador`** es una interfaz: el ViewModel no depende de Android y en los tests se usa un `FakeNotificador`.
- **Screens y Composables:** reciben el estado y envían eventos; no contienen reglas de validación.

## Recursos nativos y permisos

| Recurso | Uso | Permiso |
|---|---|---|
| Cámara | Tomar la foto de perfil del registro (`TakePicture`) | `CAMERA`, solicitado en tiempo de ejecución al tocar el botón |
| Galería | Elegir una foto existente (`GetContent`) | Ninguno: el selector del sistema entrega solo la imagen elegida |
| Internet | Descargar las imágenes remotas con Coil | `INTERNET` (permiso normal, sin diálogo) |
| Biometría | Proteger el acceso a los registros (`BiometricPrompt`, huella o PIN/patrón) | Ninguno extra: usa el bloqueo configurado en el dispositivo |
| Notificaciones | Avisar que el registro se guardó | `POST_NOTIFICATIONS` (Android 13+), solicitado al abrir el formulario |

- Las fotos se copian a la carpeta privada de la app (`files/imagenes/`) y se comparten mediante `FileProvider`, por lo que siguen disponibles aunque se reinicie la app.
- **Si se deniega el permiso de cámara**, el formulario muestra un mensaje bajo los botones y la galería sigue disponible; el resto del registro no se ve afectado. La foto es opcional.
- Sin conexión a internet, las imágenes remotas muestran un ícono de respaldo.
- **Si se deniega el permiso de notificaciones**, el registro se guarda igual; solo no aparece el aviso.
- **Si el dispositivo no tiene huella ni PIN**, se muestra un mensaje pidiendo configurarlo y no se abre la pantalla de registros.

## Bonus tracks

| Bonus | Dónde |
|---|---|
| Rotación de pantalla (portrait / landscape) | Todas las pantallas: 2 columnas en Inicio y Catálogo, `NavigationRail` en horizontal, estado conservado al rotar |
| Autenticación biométrica | `utils/Biometria.kt`, exigida en todo acceso a Registros |
| Notificaciones locales | `utils/Notificaciones.kt`, al guardar un registro |
| Filtros dinámicos | `RegistrosViewModel` + buscador y chips en `PantallaMisRegistros` |

## Cómo ejecutar

1. Abrir **Android Studio**.
2. `File > Open` y elegir la carpeta del proyecto.
3. Esperar el **Sync de Gradle**.
4. Crear o iniciar un emulador con **API 24 o superior**.
5. Presionar **Run**.

## Cómo correr los tests

Desde Android Studio, click derecho sobre la carpeta de tests y `Run Tests`, o por terminal:

```
./gradlew testDebugUnitTest          # pruebas unitarias en JVM: ValidacionesTest, RegistroViewModelTest,
                                     # RegistrosViewModelTest (filtros) y EdicionViewModelTest
./gradlew connectedDebugAndroidTest  # prueba instrumentada de Room (requiere emulador): RegistroDaoTest
```

En Windows, si la terminal indica que `JAVA_HOME` no está definido, apuntarlo al JDK incluido en Android Studio (`<carpeta de Android Studio>/jbr`).

## Nota sobre los datos

Todos los datos de la app y de los tests son **ficticios**. No se usan datos personales reales. Las imágenes remotas provienen de un servicio público de fotos de ejemplo (objetos y paisajes sin relación con la organización ni con personas reales). La API REST mencionada es solo conceptual: los registros se guardan siempre en Room, sin servidor.
