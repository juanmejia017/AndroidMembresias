# Módulo de Gestión de Membresías — Zona Fit Evolution

Backend (FastAPI) y frontend (Streamlit) del **Módulo de Gestión de
Membresías**, desarrollado como parte del sistema de gestión de gimnasio
**Zona Fit Evolution** (SENA — ADSO, ficha 3223873).

Cubre los siguientes requerimientos del documento maestro del proyecto:

- **RF02 — Gestionar Membresías**: planes tarifarios flexibles (diario,
  quincenal, mensual, trimestral), parametrizables y editables.
- **CU-03 — Gestionar Membresías**: crear, actualizar y eliminar planes.
  No se permite eliminar un plan con socios activos asociados.
- **CU-04 — Validar Acceso**: verifica en tiempo real si la membresía de
  un cliente está vigente para permitir o denegar el ingreso.
- **RF03 — Validar Acceso**: denegación automática si la fecha actual es
  posterior a la fecha de vencimiento.

## 1. Arquitectura y patrones de diseño

El backend está organizado en capas, siguiendo el principio de
responsabilidad única:

```
routers      → reciben la petición HTTP y delegan al servicio
services     → lógica de negocio (Factory, State, orquestación)
repositories → acceso a datos vía SQLAlchemy (aísla el ORM)
models       → entidades de base de datos
schemas      → validación de entrada/salida con Pydantic v2
```

Se aplican dos de los patrones de diseño descritos en el documento
maestro del proyecto (sección 5):

- **Factory Method** (`app/services/membresia_factory.py`): la clase
  `MembresiaFactory` construye los datos de una nueva membresía
  (precio final y fecha de vencimiento) aplicando reglas de negocio
  distintas según la categoría del plan (Normal, Estudiante, VIP,
  Corporativo), sin duplicar esa lógica en los routers.
- **State** (`app/services/estado_membresia.py`): cada estado
  (Activa, Vencida, Morosa, Cancelada) encapsula su propio
  comportamiento de `permite_acceso()` y su mensaje, evitando
  condicionales dispersos por el código.

## 2. Estructura del proyecto

```
zona-fit-membresias/
├── backend/
│   ├── app/
│   │   ├── main.py                 # Ensambla la app FastAPI
│   │   ├── config.py                # Configuración vía variables de entorno
│   │   ├── database.py              # Engine y sesión de SQLAlchemy (Singleton)
│   │   ├── models/                  # Entidades ORM (Cliente, Plan, Membresia)
│   │   ├── schemas/                 # Esquemas Pydantic
│   │   ├── repositories/            # Acceso a datos
│   │   ├── services/                # Lógica de negocio, Factory y State
│   │   └── routers/                 # Endpoints HTTP
│   ├── .env.example
│   ├── requirements.txt
│   └── README.md                    # Este archivo
└── frontend/
    ├── app.py                       # Punto de entrada Streamlit
    ├── config.py
    ├── services/api_client.py       # Cliente HTTP hacia el backend
    ├── ui/                          # Vistas: clientes, planes, membresías, acceso
    ├── .env.example
    └── requirements.txt
```

## 3. Requisitos previos

- Python 3.10 o superior instalado (`python3 --version`).
- Sistema operativo Linux (los comandos siguientes usan `bash`).
- Git (opcional, para clonar el repositorio).

## 4. Puesta en marcha del backend (Linux)

Todos los comandos se ejecutan **desde la carpeta `backend/`**.

### 4.1 Crear el entorno virtual

```bash
cd backend
python3 -m venv venv
```

### 4.2 Activar el entorno virtual

```bash
source venv/bin/activate
```

Al activarse correctamente, el prompt de la terminal debe mostrar el
prefijo `(venv)`.

### 4.3 Instalar las dependencias

```bash
pip install --upgrade pip
pip install -r requirements.txt
```

### 4.4 Configurar las variables de entorno

```bash
cp .env.example .env
```

El archivo `.env` define, entre otras cosas, la cadena de conexión a la
base de datos (SQLite por defecto, sin configuración adicional) y los
orígenes permitidos por CORS.

### 4.5 Ejecutar el servidor

```bash
uvicorn app.main:app --reload
```

El backend queda disponible en `http://127.0.0.1:8000`. La
documentación interactiva (Swagger UI) se genera automáticamente en:

```
http://127.0.0.1:8000/docs
```

### 4.6 Desactivar el entorno virtual (al terminar)

```bash
deactivate
```

## 5. Puesta en marcha del frontend (Linux)

El frontend es una aplicación **100 % Python** construida con
Streamlit; consume el backend mediante peticiones HTTP mediante la
librería `requests`, sin una sola línea de HTML/JS escrita a mano.

Todos los comandos se ejecutan **desde la carpeta `frontend/`**, en una
terminal distinta a la del backend (el backend debe seguir corriendo).

```bash
cd frontend
python3 -m venv venv
source venv/bin/activate
pip install --upgrade pip
pip install -r requirements.txt
cp .env.example .env
streamlit run app.py
```

Streamlit abrirá automáticamente el navegador en
`http://localhost:8501`. Si el backend corre en una URL distinta a
`http://127.0.0.1:8000`, edita la variable `BACKEND_URL` en el archivo
`frontend/.env`.

## 6. Flujo de uso sugerido para la sustentación

1. **Clientes**: registrar al menos un cliente (nombre, cédula, correo).
2. **Planes**: crear un plan por cada tipo de duración/categoría que se
   quiera demostrar (por ejemplo, "Mensual Normal" y "Mensual
   Estudiante" para evidenciar el descuento del Factory Method).
3. **Membresías**: asignar un plan al cliente registrado. El sistema
   calcula automáticamente el precio (según categoría) y la fecha de
   vencimiento (según duración).
4. **Validar acceso**: seleccionar el cliente y pulsar "Validar
   ingreso" — el sistema debe responder "ACCESO PERMITIDO".
5. **Cancelar la membresía** desde la pestaña Membresías y volver a
   validar el acceso: el sistema ahora debe responder "ACCESO
   DENEGADO", evidenciando el patrón State.
6. Intentar **eliminar un plan** que tenga una membresía activa
   asociada: el sistema debe rechazar la operación (HTTP 409),
   evidenciando la postcondición del CU-03.

## 7. Endpoints principales del backend

| Método | Ruta                                     | Descripción                                  |
|--------|-------------------------------------------|-----------------------------------------------|
| POST   | `/clientes`                               | Registrar un cliente                          |
| GET    | `/clientes`                               | Listar clientes                               |
| POST   | `/planes`                                 | Crear un plan de membresía                    |
| GET    | `/planes`                                 | Listar planes                                 |
| PUT    | `/planes/{plan_id}`                       | Actualizar un plan                            |
| DELETE | `/planes/{plan_id}`                       | Eliminar un plan (bloqueado si tiene socios activos) |
| POST   | `/membresias`                             | Asignar un plan a un cliente                  |
| GET    | `/membresias`                             | Listar membresías (filtro opcional por estado) |
| GET    | `/membresias/cliente/{cliente_id}`        | Membresías de un cliente                      |
| PUT    | `/membresias/{membresia_id}/renovar`      | Renovar una membresía                         |
| PUT    | `/membresias/{membresia_id}/cancelar`     | Cancelar una membresía                        |
| GET    | `/membresias/validar-acceso/{cliente_id}` | Validar acceso (CU-04)                        |

## 8. Buenas prácticas aplicadas

- Separación de responsabilidades en capas (routers / services /
  repositories / models / schemas), tanto en el backend como en el
  frontend (`services/api_client.py` centraliza todas las llamadas
  HTTP; ninguna vista llama a `requests` directamente).
- Validación estricta de datos de entrada con Pydantic v2 (tipos,
  longitudes mínimas, `EmailStr`).
- Variables de entorno gestionadas con `pydantic-settings` /
  `python-dotenv`, nunca credenciales ni configuración *hardcodeadas*.
- Inyección de dependencias de FastAPI (`Depends(get_db)`) para el
  manejo del ciclo de vida de la sesión de base de datos.
- Manejo explícito de errores HTTP (`HTTPException`) con códigos de
  estado semánticamente correctos (404, 409, 400).
- Reglas de negocio (descuentos, vigencia, transición de estados)
  centralizadas en la capa de servicio, no repetidas en los routers.

## 9. Autor

Juan Diego Mejía Llano — Aprendiz ADSO, ficha 3223873, SENA Regional
Antioquia.
