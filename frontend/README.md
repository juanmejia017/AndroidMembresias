# Zona Fit Membresías — Frontend Android (Kotlin + Jetpack Compose)

Cliente Android nativo para el Módulo de Gestión de Membresías de Zona
Fit Evolution. Construido en Kotlin con Jetpack Compose y Retrofit;
consume el backend FastAPI incluido en el paquete `backend.zip`.

## Abrir el proyecto

1. Abre Android Studio.
2. `File → Open...` y selecciona esta carpeta (`zona-fit-android`).
3. Espera a que Gradle sincronice las dependencias (requiere conexión a
   internet la primera vez, para descargar Retrofit, Compose, etc.).

## Antes de ejecutar

1. Levanta el backend (ver `backend.zip` / README del backend) con:
   ```bat
   uvicorn app.main:app --reload --host 0.0.0.0
   ```
2. Ejecuta la app (▶) sobre un **emulador** de Android — la URL del
   backend ya está configurada en
   `app/src/main/java/com/example/zonafitmembresias/data/remote/RetrofitClient.kt`
   como `http://10.0.2.2:8000/`, que apunta automáticamente al
   `localhost` de tu PC desde el emulador.
3. Si vas a probar en un **celular físico** en la misma red Wi-Fi,
   cambia `BASE_URL` en `RetrofitClient.kt` por la IP de tu PC (por
   ejemplo `http://192.168.1.50:8000/`) y agrega esa IP también en
   `app/src/main/res/xml/network_security_config.xml`.

## Estructura

```
app/src/main/java/com/example/zonafitmembresias/
├── MainActivity.kt
├── data/
│   ├── model/        # DTOs (Cliente, Plan, Membresia)
│   ├── remote/        # ApiService (Retrofit) + RetrofitClient
│   └── repository/    # MembresiaRepository (única puerta a la API)
├── navigation/         # Rutas + NavGraph (barra inferior)
└── ui/
    ├── theme/          # Tema Compose (Color, Type, Theme)
    ├── acceso/         # Pantalla "Validar acceso" + ViewModel
    ├── membresias/     # Pantalla "Membresías" + ViewModel
    ├── planes/         # Pantalla "Planes" + ViewModel
    └── clientes/       # Pantalla "Clientes" + ViewModel
```

## Notas

- El ícono de la app usa temporalmente un ícono del sistema
  (`@android:drawable/sym_def_app_icon`) para que el proyecto compile
  sin necesidad de recursos gráficos adicionales. Reemplázalo desde
  Android Studio con `File → New → Image Asset` cuando quieras el
  logo real de Zona Fit Evolution.
- Este proyecto no pudo compilarse ni ejecutarse en el entorno donde se
  generó (sin SDK de Android), así que revísalo con calma la primera
  vez que sincronices Gradle. La guía en PDF entregada antes documenta
  el paso a paso completo y tres correcciones que ya están aplicadas
  aquí.
