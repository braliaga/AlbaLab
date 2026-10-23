# AlbaLab

App Android para **CORPALIV / Alba Lab**, organización sin fines de lucro que promueve la autonomía y la inclusión social y laboral de personas con discapacidad múltiple. La app reúne en un solo lugar el propósito de la organización, sus programas y su catálogo, y permite que una persona o empresa interesada envíe un **registro de interés** (donación, alianza, voluntariado, compra de productos o capacitación). Los registros quedan guardados en el teléfono para el equipo de vinculación.

Proyecto de la asignatura DSY1105 · Desarrollo de Aplicaciones Móviles (Duoc UC).

## Integrantes

- Branco Aliaga
- Paulo Catalán

## Funcionalidades implementadas

- **Pantallas informativas:** Inicio (propósito, indicadores y llamada a la acción), Programas y Catálogo.
- **Navegación** entre pantallas con transiciones de deslizamiento y desvanecimiento de 300 ms, y flecha para volver en la barra superior.
- **Formulario de registro de interés** con:
  - campos de texto: nombre, teléfono, correo, dirección, cantidad de personas y fecha de contacto (`dd/MM/aaaa`);
  - opciones únicas (radios): contacto preferido y tipo de ayuda;
  - `Checkbox` de aceptación de contacto y `Switch` para recibir novedades.
- **Validaciones por campo** con ícono y mensaje de error animado. Las reglas viven en `Validaciones`, separadas de la interfaz.
- **Estado con `StateFlow`:** el botón "Enviar registro" muestra un indicador de carga y, al guardar, un mensaje de éxito animado.
- **Persistencia local con Room:** los registros se guardan y se listan en la pantalla de registros.

<!-- Paulo: agregar aquí la edición de registros cuando esté terminada y verificada. -->

## Arquitectura

Patrón **MVVM**: la interfaz observa el estado del `ViewModel`, este valida con `Validaciones` y guarda mediante el repositorio, que accede a Room.

```
cl/duoc/albalab/
├── MainActivity.kt          solo AppNavegacion() dentro del tema
├── model/                   TipoAyuda, ContactoPreferido, RegistroInteres, Contenido
├── data/                    RegistroDao, AppDatabase, RegistroRepository, ContenidoFicticio
├── viewmodel/               Validaciones, RegistroViewModel
└── ui/
    ├── theme/               colores, tipografía y tema
    ├── navegacion/          AppNavegacion (rutas y transiciones)
    ├── componentes/         CampoValidado
    ├── PantallaInicio, PantallaProgramas, PantallaCatalogo
    ├── PantallaRegistro
    └── PantallaMisRegistros
```

- **Model:** entidad `RegistroInteres` y enums `TipoAyuda` y `ContactoPreferido`.
- **Repository:** `RegistroRepository` es el único punto de acceso a los datos. Es donde se conectaría la **API REST** de CORPALIV, que en este proyecto es solo conceptual.
- **ViewModel:** `RegistroViewModel` expone `formulario` y `registros` como `StateFlow`.
- **Screens y Composables:** reciben el estado y envían eventos; no contienen reglas de validación.

## Recursos nativos y permisos

<!-- Paulo: completar cuando la cámara y la galería estén integradas (recursos usados, permisos y comportamiento si se deniegan). -->

## Cómo ejecutar

1. Abrir **Android Studio**.
2. `File > Open` y elegir la carpeta del proyecto.
3. Esperar el **Sync de Gradle**.
4. Crear o iniciar un emulador con **API 24 o superior**.
5. Presionar **Run**.

## Cómo correr los tests

Desde Android Studio, click derecho sobre la carpeta de tests y `Run Tests`, o por terminal:

```
./gradlew testDebugUnitTest          # pruebas unitarias en JVM: ValidacionesTest y RegistroViewModelTest
./gradlew connectedDebugAndroidTest  # prueba instrumentada de Room (requiere emulador): RegistroDaoTest
```

En Windows, si la terminal indica que `JAVA_HOME` no está definido, apuntarlo al JDK incluido en Android Studio (`<carpeta de Android Studio>/jbr`).

## Nota sobre los datos

Todos los datos de la app y de los tests son **ficticios**. No se usan datos personales reales. La API REST mencionada es solo conceptual: la app funciona sin conexión y guarda todo en Room.
