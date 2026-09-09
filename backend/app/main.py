"""
Módulo de Gestión de Membresías - Zona Fit Evolution.

Punto de entrada de la aplicación FastAPI. Ensambla configuración,
base de datos y routers. La lógica de negocio NO vive aquí, sino en
app/services; este archivo solo cablea las piezas.
"""
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.config import settings
from app.database import Base, engine
from app.routers import clientes_routes, planes_routes, membresias_routes

# Crea las tablas si no existen. En un entorno productivo esto se
# reemplazaría por migraciones (p. ej. Alembic), pero para el alcance
# de este módulo académico basta con create_all al iniciar.
Base.metadata.create_all(bind=engine)

app = FastAPI(
    title=settings.app_name,
    version=settings.app_version,
    description=(
        "API REST para el Módulo de Gestión de Membresías del sistema "
        "Zona Fit Evolution: administración de planes (RF02), asignación "
        "y renovación de membresías, y validación de acceso (CU-04)."
    ),
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins_list,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(clientes_routes.router)
app.include_router(planes_routes.router)
app.include_router(membresias_routes.router)


@app.get("/", tags=["Salud"])
def raiz():
    return {"app": settings.app_name, "version": settings.app_version, "estado": "activo"}
