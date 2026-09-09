"""
Capa de conexión a la base de datos.

Aplica el patrón Singleton descrito en el documento maestro del proyecto
(sección 5.1): se crea un único `engine` y una única fábrica de sesiones
`SessionLocal` para toda la aplicación, evitando abrir conexiones
redundantes a la base de datos en cada petición.
"""
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base

from app.config import settings

connect_args = {"check_same_thread": False} if "sqlite" in settings.database_url else {}

# Único engine para toda la aplicación (Singleton).
engine = create_engine(settings.database_url, connect_args=connect_args)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()


def get_db():
    """
    Dependencia de FastAPI que entrega una sesión de BD por petición
    y garantiza su cierre incluso si ocurre un error (try/finally).
    """
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
