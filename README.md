# Conoce El Salvador 🇸🇻 (App Educativa para Niños)

Una aplicación Android interactiva y divertida construida con **Kotlin** y **Jetpack Compose**, diseñada para que niños y niñas aprendan sobre la geografía, cultura, tradiciones y símbolos patrios de El Salvador mediante minijuegos educativos.

---

## 🎮 Características Implementadas

### 1. Mascota Guía: "Torogocito" (Ave Nacional)
- Mascota animada ilustrada en Canvas (con sus icónicas plumas de la cola en forma de raqueta que oscilan en péndulo).
- Expresiones y estados dinámicos (feliz, emocionado, pensativo y victoria).
- Burbuja de diálogo que acompaña al niño dándole instrucciones, motivación y **datos curiosos reales** sobre cada departamento.

### 2. Perfil e Identificación del Niño (Nickname)
- Pantalla inicial colorida para que el niño ingrese su nombre o alias.
- Persistencia local mediante `SharedPreferences`: la app recuerda su nombre, total de estrellas y récord de puntuación entre sesiones.
- Posibilidad de editar el apodo en cualquier momento desde el menú.

### 3. Menú Principal (Hub de Minijuegos)
- Saludo personalizado: *"¡Hola, [Nombre]!"*.
- Contador visual de estrellas ganadas ⭐ y récord personal.
- Selector de minijuegos con diseño extensible para agregar más actividades culturales.

### 4. Minijuego: "El Mapa de El Salvador" (14 Departamentos)
- **Mapa interactivo**: Renderiza los 14 departamentos de El Salvador con sus siluetas y posiciones geográficas correspondientes.
- **Mecánica Drag & Drop (Arrastrar y Soltar)**:
  - Ficha interactiva con el nombre y cabecera departamental.
  - El niño arrastra la etiqueta hacia su lugar en el mapa.
  - Detección de impacto con tolerancia táctil adaptada para niños.
- **Feedback Audiovisual Inmediato**:
  - **Acierto**: El departamento se ilumina con su color vibrante, muestra su nombre y Torogocito celebra compartiendo un dato curioso.
  - **Fallo**: Efecto sonoro suave y mensaje de aliento para volver a intentarlo sin frustración.
- **Métricas y Puntuación**:
  - Cronómetro digital en vivo (`00:00`).
  - Contador de errores.
  - Barra de progreso interactiva (Ej: `8 / 14`).
  - Opción de saltar etiqueta si el niño tiene dudas.
- **Celebración de Victoria**:
  - Diálogo de victoria con animación de confeti festivo.
  - Fanfarria musical.
  - Calificación de 1 a 3 estrellas según el tiempo y los errores.
  - Botones para volver a jugar o regresar al menú.

---

## 🛠️ Tecnologías y Arquitectura

- **Lenguaje**: Kotlin 2.2+
- **UI & Animaciones**: Jetpack Compose (Material 3)
- **Navegación**: Navigation3 para Compose
- **Arquitectura**: MVVM (Model-View-ViewModel) con `StateFlow`
- **Sonidos**: Sintetizador senoidal nativo de baja latencia (`AudioTrack`), sin dependencias pesadas de audio externo
- **Persistencia**: `SharedPreferences` con reactividad mediante Coroutines Flow

---

## 📱 Cómo Compilar y Ejecutar

### Generar el archivo APK:
```powershell
.\gradlew.bat assembleDebug
```
El archivo ejecutable se genera en:
`app\build\outputs\apk\debug\app-debug.apk`

### Instalar en un emulador o teléfono conectado vía USB:
```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
```
